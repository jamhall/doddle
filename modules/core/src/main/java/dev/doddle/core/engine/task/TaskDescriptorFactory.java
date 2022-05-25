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

import dev.doddle.common.support.NotNull;
import dev.doddle.core.engine.JobExecutionContext;
import dev.doddle.core.engine.retry.RetryerRegistry;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static dev.doddle.common.support.StringUtils.isEmptyOrElseGet;
import static dev.doddle.core.support.Objects.requireNonNull;

public class TaskDescriptorFactory {

    private final TaskOptions     defaultTaskOptions;
    private final RetryerRegistry retryRegistry;

    public TaskDescriptorFactory(@NotNull final RetryerRegistry retryRegistry,
                                 @NotNull final TaskOptions defaultTaskOptions) {
        this.retryRegistry = requireNonNull(retryRegistry, "retryStrategyRegistry cannot be null");
        this.defaultTaskOptions = requireNonNull(defaultTaskOptions, "defaultTaskOptions cannot be null");
    }

    /**
     * Create a task descriptor for a given task
     *
     * @param method the task method
     * @param task   the task details
     * @return the task descriptor
     */
    public TaskDescriptor createTaskDescriptor(final Method method, final Task task) {
        final Parameter[] parameters = method.getParameters();
        final TaskDescriptor information = new TaskDescriptor();
        information.setName(isEmptyOrElseGet(task.name(), method.getName()))
            .setDescription(isEmptyOrElseGet(task.description(), null))
            .setMethod(method)
            .setEnclosingClass(method.getDeclaringClass());

        if (parameters.length > 0) {
            information.setExecutionContextRequested(parameters[0].getType() == JobExecutionContext.class);
        } else {
            information.setExecutionContextRequested(false);
        }

        final String defaultRetryStrategy = defaultTaskOptions.retryStrategy();
        information.setRetryer(retryRegistry.getForName(defaultRetryStrategy));

        return information;
    }

}
