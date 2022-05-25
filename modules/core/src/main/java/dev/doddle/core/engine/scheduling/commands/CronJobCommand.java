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
package dev.doddle.core.engine.scheduling.commands;

import com.cronutils.model.Cron;
import dev.doddle.common.support.NotNull;
import dev.doddle.core.engine.circuitbreaker.CircuitBreaker;
import dev.doddle.core.engine.circuitbreaker.CircuitBreakerResultCallback;
import dev.doddle.core.engine.task.TaskDescriptor;
import dev.doddle.core.engine.telemetry.events.CronJobScheduledEvent;
import dev.doddle.core.engine.time.Clock;
import dev.doddle.core.services.CronService;
import dev.doddle.core.services.TaskService;
import dev.doddle.core.services.TelemetryService;
import dev.doddle.storage.common.Storage;
import dev.doddle.storage.common.builders.JobBuilder;
import dev.doddle.storage.common.domain.CronJob;
import dev.doddle.storage.common.domain.Job;
import dev.doddle.storage.common.domain.JobCategory;
import dev.doddle.storage.common.domain.JobState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static dev.doddle.core.support.Objects.requireNonNull;

public class CronJobCommand implements SchedulerCommand {

    private static final Logger           logger = LoggerFactory.getLogger(CronJobCommand.class);
    private final        Storage          storage;
    private final        Clock            clock;
    private final        CronService      cronService;
    private final        TaskService      taskService;
    private final        TelemetryService telemetryService;
    private final        CircuitBreaker   circuitBreaker;

    /**
     * Create a new job picker
     */
    public CronJobCommand(@NotNull final Storage storage,
                          @NotNull final Clock clock,
                          @NotNull final CronService cronService,
                          @NotNull final TaskService taskService,
                          @NotNull final TelemetryService telemetryService,
                          @NotNull final CircuitBreaker circuitBreaker) {
        this.storage = requireNonNull(storage, "storage cannot be null");
        this.clock = requireNonNull(clock, "clock cannot be null");
        this.cronService = requireNonNull(cronService, "cronService cannot be null");
        this.taskService = requireNonNull(taskService, "taskService cannot be null");
        this.telemetryService = requireNonNull(telemetryService, "telemetryService cannot be null");
        this.circuitBreaker = requireNonNull(circuitBreaker, "circuitBreaker cannot be null");
    }

    @Override
    public void execute() {
        logger.debug("Executing cron job command");

        final Supplier<List<CronJob>> supplier = this.storage::getAllCronJobs;

        circuitBreaker.apply(supplier, new CircuitBreakerResultCallback<>() {
            @Override
            public void onError(Throwable throwable) {
                logger.error("Error executing cron job command {}", throwable.getMessage());
            }

            @Override
            public void onSuccess(final List<CronJob> jobs) {
                logger.debug("Found {} cron jobs", jobs.size());
                jobs.stream()
                    .filter(job -> !job.isEnabled())
                    .filter(job -> !job.getQueue().isLocked())
                    .filter(job -> job.getNextRunAt() == null || clock.isAfter(job.getNextRunAt()))
                    .forEach(job -> schedule(job));
            }
        });

    }

    /**
     * Enqueue as a normal job
     *
     * @param cron the cron job to enqueue ready to be processed
     */
    private void enqueue(@NotNull CronJob cron) {
        final TaskDescriptor task = taskService.getByName(cron.getHandler());
        final JobBuilder builder = JobBuilder.newBuilder();
        builder.queue(cron.getQueue());
        builder.handler(task.getName());
        builder.maxRetries(cron.getMaxRetries());
        // FIXME
//            builder.tags(task.getTags());
        builder.timeout(cron.getTimeout());
        builder.retries(0);
        builder.scheduledAt(cron.getNextRunAt());
        builder.state(JobState.SCHEDULED);
        builder.category(JobCategory.SCHEDULED);
        final Job job = builder.build();
        storage.saveJob(job);
    }

    /**
     * Save the cron job
     *
     * @param job the cron job to save
     */
    private void persist(CronJob job) {
        storage.saveCronJob(job);
        telemetryService.dispatch(new CronJobScheduledEvent(job));
    }

    /**
     * Schedule a cron job
     *
     * @param job the cron job to schedule
     */
    private void schedule(CronJob job) {
        // @TODO check if a cron already exists
        final String id = job.getId();
        final String expression = job.getExpression();
        final Cron cron = cronService.getForExpression(expression);
        final LocalDateTime nextExecutionTime = cronService.nextExecutionTime(cron);
        logger.debug("Scheduled cron job with id: {} to be next executed at: {}", id, nextExecutionTime);
        job.setNextRunAt(nextExecutionTime);
        // save the cron job...
        persist(job);
        // enqueue as a standard job...
        enqueue(job);
    }
}


