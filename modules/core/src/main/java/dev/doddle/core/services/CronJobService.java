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
import dev.doddle.core.engine.ScheduleDescriptor;
import dev.doddle.core.engine.task.TaskDescriptor;
import dev.doddle.core.engine.task.TaskOptions;
import dev.doddle.core.engine.telemetry.events.CronJobCreatedEvent;
import dev.doddle.core.engine.telemetry.events.CronJobUpdatedEvent;
import dev.doddle.core.engine.time.IntervalParser;
import dev.doddle.core.exceptions.DoddleException;
import dev.doddle.core.support.ScheduleDefinition;
import dev.doddle.core.support.ScheduleWizard;
import dev.doddle.storage.common.Storage;
import dev.doddle.storage.common.StorageException;
import dev.doddle.storage.common.builders.CronJobBuilder;
import dev.doddle.storage.common.domain.CronJob;
import dev.doddle.storage.common.domain.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static dev.doddle.core.engine.time.IntervalParser.createIntervalParser;
import static java.lang.String.format;
import static dev.doddle.core.support.Objects.requireNonNull;

public class CronJobService {

    private static final Logger           logger = LoggerFactory.getLogger(CronJobService.class);
    private final        Storage          storage;
    private final        TaskService      taskService;
    private final        CronService      cronService;
    private final        TelemetryService telemetryService;
    private final        TaskOptions      defaultTaskOptions;

    public CronJobService(@NotNull final TaskService taskService,
                          @NotNull final Storage storage,
                          @NotNull final CronService cronService,
                          @NotNull final TelemetryService telemetryService,
                          @NotNull final TaskOptions defaultTaskOptions) {
        this.taskService = requireNonNull(taskService, "taskService cannot be null");
        this.storage = requireNonNull(storage, "storage cannot be null");
        this.cronService = requireNonNull(cronService, "cronService cannot be null");
        this.telemetryService = requireNonNull(telemetryService, "telemetryService cannot be null");
        this.defaultTaskOptions = requireNonNull(defaultTaskOptions, "defaultTaskOptions cannot be null");

    }

    public List<CronJob> all() {
        try {
            return storage.getAllCronJobs();
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    public Integer count() {
        try {
            return storage.countAllCronJobs();
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    /**
     * Delete a cron job
     *
     * @param id the id of the cron job
     */
    public void delete(@NotNull final String id) {
        try {
            final CronJob job = storage.getCronJobById(requireNonNull(id, "id cannot be null")).orElseThrow(DoddleException::new);
            storage.deleteCronJob(job);
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    /**
     * Delete all cron jobs
     */
    public void deleteAll() {
        try {
            storage.deleteAllCronJobs();
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }


    public Optional<CronJob> id(@NotNull final String id) {
        try {
            return storage.getCronJobById(requireNonNull(id, "id cannot be null"));
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    public Optional<CronJob> name(@NotNull final String name) {
        try {
            return storage.getCronJobByName(requireNonNull(name, "name cannot be null"));
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    public CronJob schedule(@NotNull final Function<ScheduleWizard, ScheduleWizard> input) {
        final IntervalParser parser = createIntervalParser();
        final ScheduleWizard wizard = new ScheduleWizard(parser, defaultTaskOptions.timeout(), defaultTaskOptions.maxRetries());
        return schedule(input.apply(wizard));
    }

    public CronJob schedule(@NotNull final ScheduleWizard wizard) {
        final ScheduleDefinition definition = wizard.build();
        if (name(definition.getName()).isPresent()) {
            throw new DoddleException("A cron job with this name already exists");
        }
        final Queue queue = requireNonNull(storage.getQueueByName(definition.getQueue()).orElse(null), format("Queue not found for the given name: %s", definition.getQueue()));
        final TaskDescriptor task = requireNonNull(taskService.getByName(definition.getHandler()), format("Task not found for the given handler: %s", definition.getHandler()));
        final ScheduleDescriptor descriptor = new ScheduleDescriptor(definition.getName(), definition.getDescription(), definition.getExpression(), queue, task.getName(), null, definition.getTimeout(), definition.getMaxRetries(), definition.isEnabled());
        return schedule(descriptor);
    }

    /**
     * Update a job
     *
     * @param id    the id of the cron job
     * @param input the wizard to update the cron job
     */
    public void update(@NotNull final String id, final @NotNull Function<ScheduleWizard, ScheduleWizard> input) {
        this.id(id).ifPresentOrElse(job -> {
            try {
                final IntervalParser parser = createIntervalParser();
                final ScheduleWizard wizard = new ScheduleWizard(parser, defaultTaskOptions.timeout(), defaultTaskOptions.maxRetries());
                final ScheduleDefinition definition = input.apply(wizard).build();
                final Queue queue = requireNonNull(storage.getQueueByName(definition.getQueue()).orElse(null), format("Queue not found for the given name: %s", definition.getQueue()));
                final TaskDescriptor task = requireNonNull(taskService.getByName(definition.getHandler()), format("Task not found for the given handler: %s", definition.getHandler()));
                job.setName(definition.getName());
                job.setExpression(definition.getExpression());
                job.setDescription(definition.getDescription());
                job.setEnabled(definition.isEnabled());
                job.setQueue(queue);
                //  FIXME ADD RETRIES
                job.setHandler(task.getName());
                final CronJob createdCronJob = storage.saveCronJob(job);
                telemetryService.dispatch(new CronJobUpdatedEvent(createdCronJob));
            } catch (StorageException exception) {
                throw new DoddleException(exception);
            }
        }, () -> {
            throw new DoddleException(format("Cron job not found for id: %s", id));
        });

    }

    public void update(@NotNull final CronJob job) {
        try {
            final CronJob cron = storage.getCronJobById(requireNonNull(job.getId(), "id cannot be null")).orElseThrow(DoddleException::new);
            storage.saveCronJob(cron);
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    public void disable(@NotNull final String id) {
        enabled(id, false);
    }

    private void enabled(@NotNull final String id, final boolean enabled) {
        final CronJob job = id(id).orElse(null);
        if (job == null) {
            throw new DoddleException(format("Cron job not found for the given id: %s", id));
        }
        enabled(job, enabled);
    }

    private void enabled(@NotNull final CronJob job, final boolean enabled) {
        job.setEnabled(enabled);
        final CronJob updatedCronJob = storage.saveCronJob(job);
        telemetryService.dispatch(new CronJobUpdatedEvent(updatedCronJob));
    }

    public void enable(@NotNull final String id) {
        enabled(id, true);
    }

    public void enableAll() {
        this.all().forEach(job -> enabled(job, true));
    }

    public void disableAll() {
        this.all().forEach(job -> enabled(job, false));
    }

    private CronJob buildCron(@NotNull final ScheduleDescriptor descriptor) {
        final CronJobBuilder builder = CronJobBuilder.newBuilder();
        builder.name(descriptor.getName());
        builder.description(descriptor.getDescription());
        builder.enabled(descriptor.isEnabled());
        builder.expression(descriptor.getExpression());
        builder.timeout(descriptor.getTimeout());
        builder.handler(descriptor.getHandler());
        builder.maxRetries(descriptor.getMaxRetries());
        builder.queue(descriptor.getQueue());
        return builder.build();
    }

    private CronJob schedule(@NotNull final ScheduleDescriptor descriptor) {
        try {
            logger.info("Creating cron job for task {}", descriptor.getHandler());
            final CronJob job = this.buildCron(descriptor);
            final CronJob cronJob = this.storage.saveCronJob(job);
            telemetryService.dispatch(new CronJobCreatedEvent(cronJob));
            return cronJob;
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }
}
