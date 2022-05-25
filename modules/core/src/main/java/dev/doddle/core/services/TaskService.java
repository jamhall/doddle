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
package dev.doddle.core.services;

import dev.doddle.common.support.NotNull;
import dev.doddle.core.engine.JobExecutionContext;
import dev.doddle.core.engine.task.TaskDescriptor;
import dev.doddle.core.engine.task.TaskExecutor;
import dev.doddle.core.engine.task.TaskRegistry;
import dev.doddle.core.engine.task.states.ExecutingTaskState;
import dev.doddle.core.engine.task.states.FailedTaskState;
import dev.doddle.core.engine.task.states.SuccessfulTaskState;
import dev.doddle.core.engine.task.states.TaskState;
import dev.doddle.core.exceptions.TaskExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static dev.doddle.core.support.Objects.requireNonNull;

/**
 * Provides a facade to the underlying services
 */
public class TaskService {

    private static final Logger       logger = LoggerFactory.getLogger(TaskService.class);
    private final        TaskRegistry registry;
    private final        TaskExecutor executor;

    /**
     * Create a new task service
     *
     * @param registry the task registry
     * @param executor the service to execute the task
     */
    public TaskService(
        @NotNull final TaskRegistry registry,
        @NotNull final TaskExecutor executor) {
        this.registry = requireNonNull(registry, "registry cannot be null");
        this.executor = requireNonNull(executor, "executor cannot be null");
    }

    /**
     * Count all tasks
     *
     * @return a count of tasks
     */
    public int countAll() {
        return registry.countAll();
    }

    /**
     * Execute a task
     *
     * @param details  the task details
     * @param timeout  when the job should timeout in milliseconds
     * @param context  the job context
     * @param callback the task callback
     */
    public void execute(@NotNull final TaskDescriptor details,
                        @NotNull final Long timeout,
                        @NotNull final JobExecutionContext context,
                        @NotNull final Consumer<TaskState> callback) {
        try {
            logger.debug("Executing task: {}", details.getName());
            callback.accept(new ExecutingTaskState());
            executor.execute(details, timeout, context);
            callback.accept(new SuccessfulTaskState());
        } catch (TaskExecutionException exception) {
            callback.accept(new FailedTaskState(exception));
        }
    }

    /**
     * Check if a task exists for a given name
     *
     * @param name the name of the task
     * @return true if exists, otherwise false
     */
    public boolean exists(@NotNull final String name) {
        return getByName(name) != null;
    }

    /**
     * Get all tasks
     *
     * @return a list of tasks
     */
    public List<TaskDescriptor> getAll() {
        return registry.getAll();
    }

    /**
     * Get a task for a given name
     *
     * @param name the name of the task
     * @return the task if found
     */
    public TaskDescriptor getByName(@NotNull final String name) {
        return registry.getForName(name);
    }

    public <X extends Throwable> TaskDescriptor getByNameOrThrow(@NotNull final String name,
                                                                 @NotNull final Supplier<? extends X> exceptionSupplier) throws X {
        final TaskDescriptor descriptor = this.registry.getForName(name);
        if (descriptor == null) {
            throw exceptionSupplier.get();
        }
        return descriptor;
    }

}
