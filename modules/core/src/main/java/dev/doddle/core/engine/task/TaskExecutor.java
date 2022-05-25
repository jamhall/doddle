/*
 * MIT License
 *
 * Copyright (c) 2022 Jamie Hall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.doddle.core.engine.task;

import dev.doddle.core.engine.JobExecutionContext;
import dev.doddle.core.exceptions.TaskExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.lang.String.format;
import static dev.doddle.core.support.Objects.requireNonNull;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class TaskExecutor {

    private static final Logger                 logger = LoggerFactory.getLogger(TaskExecutor.class);
    private final        TaskDependencyResolver resolver;

    /**
     * Create a new task executor
     *
     * @param resolver the task dependency resolver
     */
    public TaskExecutor(final TaskDependencyResolver resolver) {
        this.resolver = requireNonNull(resolver, "resolver cannot be null");
    }

    /**
     * Execute a task
     * The caller wraps this in a runnable for asynchronous execution
     */
    public void execute(final TaskDescriptor task,
                        final Long timeout,
                        final JobExecutionContext context) throws TaskExecutionException {
        if (task == null) {
            throw new TaskExecutionException("The task cannot be null");
        }
        process(task, timeout, context);
    }

    /**
     * Builds the task's method parameters ready to be invoked
     *
     * @param task    the task
     * @param context the execution context
     * @return a list of parameters
     */
    private Object[] buildMethodParameters(final TaskDescriptor task, final JobExecutionContext context) {
        final List<Object> parameters = new ArrayList<>();
        if (task.getExecutionContextRequested()) {
            parameters.add(context);
        }
        return parameters.toArray();
    }

    /**
     * Create a runnable for invoking the task's method
     *
     * @param task    the task
     * @param context the execution context
     * @return the method runnable
     * @throws TaskExecutionException thrown if there was an error executing the task
     */
    private Runnable createInvokeMethodRunnable(final TaskDescriptor task, final JobExecutionContext context) throws TaskExecutionException {
        return () -> {
            try {
                final Method method = task.getMethod();
                final Object object = resolveDependency(task);

                if (object == null) {
                    throw new TaskExecutionException(format("Could not resolve the class for the task %s. Is it being injected correctly?", task.getName()));
                }

                final Object[] parameters = buildMethodParameters(task, context);
                if (parameters.length == 0) {
                    method.invoke(object);
                } else {
                    method.invoke(object, parameters);
                }
            } catch (IllegalAccessException exception) {
                throw new TaskExecutionException(exception.getMessage(), exception);
            } catch (InvocationTargetException exception) {
                final Throwable cause = exception.getCause();
                if (cause instanceof TaskExecutionException) {
                    throw (TaskExecutionException) cause;
                } else {
                    final Throwable target = exception.getTargetException();
                    if (target == null) {
                        throw new TaskExecutionException(exception.getMessage(), exception);
                    } else {
                        throw new TaskExecutionException(target.getMessage(), target);
                    }
                }
            }
        };
    }

    /**
     * Process the given task and wrap it in a future to manage timeouts
     *
     * @param task    the task to process
     * @param timeout when the job should timeout
     * @param context the execution context
     * @throws TaskExecutionException thrown if there was an error processing the task
     */
    private void process(final TaskDescriptor task,
                         final Long timeout,
                         final JobExecutionContext context) throws TaskExecutionException {
        logger.debug(format("Processing task: %s", task.getName()));
        final ExecutorService executor = newSingleThreadExecutor();
        try {
            final Future<?> executable = executor.submit(createInvokeMethodRunnable(task, context));
            try {
                executable.get(timeout, MILLISECONDS);
            } catch (InterruptedException | ExecutionException exception) {
                final Throwable cause = exception.getCause();
                if (cause instanceof TaskExecutionException) {
                    throw (TaskExecutionException) cause;
                } else {
                    throw new TaskExecutionException(exception.getMessage(), exception);
                }
            } catch (TimeoutException exception) {
                throw new TaskExecutionException(format("The task exceeded its timeout of %d milliseconds", timeout, exception));
            }
        } finally {
            executor.shutdown();
        }
    }

    /**
     * Resolve the tasks enclosing class dependencies
     *
     * @param task the task to resolve the dependencies for
     * @return the resolved dependency
     */
    private Object resolveDependency(TaskDescriptor task) {
        return resolver.resolve(task.getEnclosingClass());
    }

}
