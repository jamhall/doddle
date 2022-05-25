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
package dev.doddle.core.engine;

import dev.doddle.common.support.NotNull;
import dev.doddle.core.engine.middleware.MiddlewarePipeline;
import dev.doddle.core.engine.task.TaskDescriptor;
import dev.doddle.core.engine.task.states.ExecutingTaskState;
import dev.doddle.core.engine.task.states.FailedTaskState;
import dev.doddle.core.engine.task.states.SuccessfulTaskState;
import dev.doddle.core.engine.task.states.TaskState;
import dev.doddle.core.engine.telemetry.events.JobCompletedEvent;
import dev.doddle.core.engine.telemetry.events.JobExceptionEvent;
import dev.doddle.core.engine.telemetry.events.JobExecutingEvent;
import dev.doddle.core.engine.telemetry.events.JobFailedEvent;
import dev.doddle.core.exceptions.TaskExecutionException;
import dev.doddle.core.services.TaskService;
import dev.doddle.core.services.TelemetryService;
import dev.doddle.storage.common.Storage;
import dev.doddle.storage.common.domain.Job;

import java.util.function.Consumer;

import static dev.doddle.core.support.Objects.requireNonNull;
import static java.lang.String.format;

public class JobProcessor {

    private final TelemetryService           telemetryService;
    // TODO Remove - not needed
    private final Storage                    storage;
    private final TaskService                taskService;
    private final JobResultProcessor         resultProcessor;
    private final JobExecutionContextFactory executionContextFactory;
    private       MiddlewarePipeline         middleware;

    /**
     * Create a new job processor
     *
     * @param taskService             the task service
     * @param resultProcessor         the result processor
     * @param executionContextFactory the execution context factory
     * @param telemetryService        the telemetry service
     * @param storage                 the storage service
     */
    public JobProcessor(
        final TaskService taskService,
        final JobResultProcessor resultProcessor,
        final JobExecutionContextFactory executionContextFactory,
        final TelemetryService telemetryService,
        final MiddlewarePipeline middleware,
        final Storage storage) {
        this.taskService = requireNonNull(taskService, "taskService cannot be null");
        this.resultProcessor = requireNonNull(resultProcessor, "resultProcessor cannot be null");
        this.executionContextFactory = requireNonNull(executionContextFactory, "executionContextFactory cannot be null");
        this.telemetryService = requireNonNull(telemetryService, "telemetryService cannot be null");
        this.middleware = requireNonNull(middleware, "middleware cannot be null");
        this.storage = requireNonNull(storage, "storage cannot be null");
    }

    /**
     * Process the job
     *
     * @param job the job to process
     */
    public void process(@NotNull final Job job) {
        final String handler = job.getHandler();
        final Long timeout = job.getTimeout();
        final TaskDescriptor task = this.taskService.getByName(handler);
        if (task == null) {
            this.telemetryService.dispatch(new JobExceptionEvent(job, new TaskExecutionException(
                format("Could not find task for given name: %s", handler))
            ));
        } else {
            final JobExecutionContext context = executionContextFactory.create(job);
            final Consumer<TaskState> callback = createCallback(job, task);
            middleware.beforeExecution(context);
            taskService.execute(task, timeout, context, callback);
        }
    }

    private Consumer<TaskState> createCallback(@NotNull final Job job,
                                               @NotNull final TaskDescriptor task) {
        return (state) -> {
            if (state instanceof ExecutingTaskState) {
                this.telemetryService.dispatch(new JobExecutingEvent(job));
            } else if (state instanceof SuccessfulTaskState) {
                resultProcessor.handleSuccessful(job);
                telemetryService.dispatch(new JobCompletedEvent(job));
            } else if (state instanceof FailedTaskState) {
                resultProcessor.handleFailed(job, ((FailedTaskState) state).getException(), task.getRetryer().getStrategy());
                telemetryService.dispatch(new JobFailedEvent(job));
                telemetryService.dispatch(new JobExceptionEvent(job, ((FailedTaskState) state).getException()));
            }
        };
    }

}
