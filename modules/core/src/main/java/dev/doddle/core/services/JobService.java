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
import dev.doddle.core.engine.JobData;
import dev.doddle.core.engine.JobDataMapper;
import dev.doddle.core.engine.JobDescriptor;
import dev.doddle.core.engine.JobEncryption;
import dev.doddle.core.engine.crypto.EncryptionCredential;
import dev.doddle.core.engine.crypto.EncryptionService;
import dev.doddle.core.engine.task.TaskDescriptor;
import dev.doddle.core.engine.task.TaskOptions;
import dev.doddle.core.engine.telemetry.events.JobCreatedEvent;
import dev.doddle.core.engine.time.Interval;
import dev.doddle.core.engine.time.IntervalParser;
import dev.doddle.core.exceptions.DoddleException;
import dev.doddle.core.exceptions.DoddleValidationException;
import dev.doddle.core.exceptions.MapperException;
import dev.doddle.core.support.*;
import dev.doddle.storage.common.Storage;
import dev.doddle.storage.common.StorageException;
import dev.doddle.storage.common.builders.JobBuilder;
import dev.doddle.storage.common.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static dev.doddle.core.engine.time.IntervalParser.createIntervalParser;
import static dev.doddle.core.support.Objects.requireNonNull;
import static dev.doddle.core.support.Objects.requireNonNullElse;
import static dev.doddle.storage.common.domain.JobCategory.STANDARD;
import static dev.doddle.storage.common.domain.JobState.*;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;

public class JobService {

    private static final Logger            logger = LoggerFactory.getLogger(JobService.class);
    private final        TaskService       taskService;
    private final        JobDataMapper     mapper;
    private final        Storage           storage;
    private final        JobMessageService messageService;
    private final        TelemetryService  telemetryService;
    private final        TaskOptions       defaultTaskOptions;
    private final        EncryptionService encryptionService;

    /**
     * Create a new job service
     *
     * @param taskService        the task service
     * @param storage            the storage
     * @param mapper             the job data mapper
     * @param telemetryService   the telemetry service
     * @param defaultTaskOptions the default task options
     */
    public JobService(@NotNull final TaskService taskService,
                      @NotNull final Storage storage,
                      @NotNull final JobDataMapper mapper,
                      @NotNull final TelemetryService telemetryService,
                      @NotNull final EncryptionService encryptionService,
                      @NotNull final TaskOptions defaultTaskOptions) {
        this.taskService = requireNonNull(taskService, "taskService cannot be null");
        this.storage = requireNonNull(storage, "storage cannot be null");
        this.mapper = requireNonNull(mapper, "mapper cannot be null");
        this.encryptionService = requireNonNull(encryptionService, "encryptionService cannot be null");
        this.defaultTaskOptions = requireNonNull(defaultTaskOptions, "defaultTaskOptions cannot be null");
        this.telemetryService = requireNonNull(telemetryService, "telemetryService cannot be null");
        this.messageService = new JobMessageService(storage);
    }

    /**
     * Get all jobs
     *
     * @param offset the offset
     * @param limit  the limit
     * @return a collection of jobs
     */
    public List<Job> all(final int offset, final int limit) {
        if (limit < 0 || offset < 0) {
            throw new DoddleValidationException("offset and limit must be positive");
        }
        return all(new Pageable(offset, limit));
    }

    /**
     * Get all jobs
     *
     * @param pageable paginate the results
     * @return a collection of jobs
     */
    public List<Job> all(@NotNull final Pageable pageable) {
        try {
            return storage.getJobs(pageable);
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    /**
     * Cancel a job
     * The job won't be cancelled if it is currently executing
     *
     * @param job the job to be cancelled
     */
    public void cancel(@NotNull final Job job) {
        try {
            final LocalDateTime now = now();
            if (asList(AVAILABLE, SCHEDULED, RETRYABLE).contains(job.getState())) {
                job.setDiscardedAt(now());
                job.setState(DISCARDED);
                storage.saveJob(job);
            } else {
                throw new DoddleException("Cannot cancel job as the state does not match available, scheduled or retryable");
            }
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    /**
     * Cancel a job for a given id
     *
     * @param id the job identifier
     */
    public void cancel(@NotNull final String id) {
        id(id).ifPresentOrElse(this::cancel, () -> {
            throw new DoddleException(format("Job not found for id: %s", id));
        });
    }

    /**
     * Count jobs
     *
     * @param wizard the search filter wizard
     * @return a count of jobs
     */
    public Long count(@NotNull final Function<FilterWizard, FilterWizard> wizard) {
        final JobFilter filter = wizard.apply(new FilterWizard()).build();
        return this.count(filter);
    }

    public Long count(@NotNull JobFilter filter) {
        try {
            return storage.countJobs(requireNonNull(filter, "filter cannot be null"));
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    /**
     * Count jobs
     *
     * @return a count of jobs
     */
    public Long count() {
        try {
            return storage.countJobs(new JobFilter());
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    /**
     * Delete a job for a given id
     * The given job will not be deleted if it is currently being executed...
     *
     * @param id the job identifier
     */
    public void delete(@NotNull final String id) {
        try {
            final Job job = this.id(id).orElseThrow(DoddleException::new);
            if (job.isState(EXECUTING)) {
                throw new DoddleException("Cannot delete a job that is currently executing");
            }
            storage.deleteJob(job);
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    /**
     * Delete all jobs
     */
    public void deleteAll() {
        deleteAll("0ms");
    }

    /**
     * Delete all jobs older than a given period
     */
    public void deleteAll(@NotNull final String period) {
        try {
            final IntervalParser parser = createIntervalParser();
            final long minutes = parser.parse(period, "m").toMinutes();
            final LocalDateTime date = now().minusMinutes(minutes);
            storage.deleteAllJobs(date);
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    /**
     * Discard a job for a given id
     *
     * @param id the job identifier
     */
    public void discard(@NotNull final String id) {
        this.id(id).ifPresentOrElse(this::discard, () -> {
            throw new DoddleException(format("Job not found for id: %s", id));
        });
    }

    /**
     * Discard a job
     *
     * @param job the job to the discard
     */
    public void discard(@NotNull final Job job) {
        try {
            final LocalDateTime now = now();
            job.setDiscardedAt(now);
            job.setState(DISCARDED);
            storage.saveJob(job);
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    public Job enqueue(@NotNull final Function<EnqueueWizard, EnqueueWizard> input) {
        final IntervalParser parser = createIntervalParser();
        final EnqueueWizard wizard = new EnqueueWizard(parser, defaultTaskOptions.timeout(), defaultTaskOptions.maxRetries());
        final EnqueueDefinition definition = input.apply(wizard).build();
        return enqueueIn(definition);
    }

    public Job enqueueIn(@NotNull final Function<EnqueueInWizard, EnqueueInWizard> input) {
        final IntervalParser parser = createIntervalParser();
        final EnqueueInWizard wizard = new EnqueueInWizard(parser, defaultTaskOptions.timeout(), defaultTaskOptions.maxRetries());
        final EnqueueDefinition definition = input.apply(wizard).build();
        return enqueueIn(definition);
    }

    /**
     * Get a job for a given id
     *
     * @param id the job identifier
     * @return the job if found
     */
    public Optional<Job> id(@NotNull final String id) {
        try {
            return storage.getJobById(requireNonNull(id, "id cannot be null"));
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    /**
     * Get a job for a given identifier
     *
     * @param identifier the job identifier
     * @return the job if found
     */
    public Optional<Job> identifier(@NotNull final String identifier) {
        try {
            return storage.getJobByIdentifier(requireNonNull(identifier, "identifier cannot be null"));
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    public JobMessageService messages() {
        return this.messageService;
    }

    /**
     * Schedule a job to be immediately retried
     *
     * @param id the job identifier
     */
    public void retry(@NotNull final String id) {
        final LocalDateTime now = now();
        retry(id, now);
    }

    /**
     * Retry a job for a given id
     *
     * @param id     the job id
     * @param period how long to snooze the job for
     */
    public void retry(@NotNull final String id, @NotNull final String period) {
        final IntervalParser parser = createIntervalParser();
        final LocalDateTime parsed = parser.parse(period).toDate();
        retry(id, parsed);
    }

    /**
     * Search for jobs
     *
     * @param filter the search filter
     * @return a collection of jobs
     */
    public List<Job> search(@NotNull final Function<FilterWizard, FilterWizard> filter) {
        return search(filter, new Pageable());
    }

    public List<Job> search(@NotNull final Function<FilterWizard, FilterWizard> wizard, Pageable pageable) {
        final JobFilter filter = wizard.apply(new FilterWizard()).build();
        return search(filter, pageable);
    }

    /**
     * Search for jobs
     *
     * @param filter the search filter
     * @return a collection of jobs
     */
    public List<Job> search(@NotNull JobFilter filter, @NotNull Pageable pageable) {
        try {
            return storage.getJobs(requireNonNull(filter), requireNonNull(pageable));
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    public List<Job> search(@NotNull JobFilter filter) {
        return search(filter, new Pageable());

    }

    /**
     * Get job statistics (i.e. number complete, failed, scheduled etc.)
     *
     * @return the statistics
     */
    public JobStatistic statistics() {
        try {
            return storage.getJobStatistics();
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    public List<String> tasks() {
        return storage.getAllTasks();
    }

    private Job createJob(@NotNull final JobDescriptor descriptor) {
        final JobBuilder builder = JobBuilder.newBuilder();
        builder.queue(descriptor.getQueue());
        builder.handler(descriptor.getHandler());
        builder.maxRetries(descriptor.getMaxRetries());
        builder.retries(0);
        builder.tags(descriptor.getTags());
        builder.timeout(descriptor.getTimeout());
        builder.data(descriptor.getData());
        builder.scheduledAt(descriptor.getScheduledAt());
        builder.name(descriptor.getName());
        builder.identifier(descriptor.getIdentifier());
        builder.state(SCHEDULED);
        builder.category(STANDARD);
        return builder.build();
    }

    private JobData createJobData(final List<Argument> arguments) {

        if (arguments.stream().anyMatch(Argument::isEncrypted)) {
            if (!encryptionService.isEnabled()) {
                throw new DoddleValidationException("Unable to encrypt an argument because encryption is not enabled");
            }
        }

        if (encryptionService.isEnabled()) {
            final EncryptionCredential credential = encryptionService.getCurrentVersion();
            final JobEncryption encryption = new JobEncryption(credential.getVersion());
            return new JobData(encryption, arguments);
        }

        return new JobData(null, arguments);
    }

    private Job enqueueIn(@NotNull final JobDescriptor descriptor) {
        try {
            final Job job = this.createJob(descriptor);
            logger.debug("Enqueuing job  {} and scheduled for {}", job.getId(), job.getScheduledAt());
            final Job savedJob = this.storage.saveJob(job);
            this.telemetryService.dispatch(new JobCreatedEvent(savedJob));
            return savedJob;
        } catch (DoddleValidationException | StorageException | MapperException exception) {
            throw new DoddleException(exception);
        }
    }

    private Job enqueueIn(@NotNull final EnqueueDefinition definition) {
        final Queue queue = requireNonNull(storage.getQueueByName(definition.getQueue()).orElse(null), format("Queue not found for the given name: %s", definition.getQueue()));
        final TaskDescriptor task = requireNonNull(taskService.getByName(definition.getHandler()), format("Task not found for the given handler: %s", definition.getHandler()));
        final JobData data = createJobData(definition.getArguments());
        final String json = mapper.convertToJson(data);

        final IntervalParser parser = createIntervalParser();
        final Interval period = requireNonNullElse(definition.getPeriod(), parser.parse("0s"));
        final JobDescriptor descriptor = new JobDescriptor(queue, definition.getIdentifier(), definition.getName(), task.getName(), json, period.toDate(), definition.getTags(), definition.getTimeout(), definition.getMaxRetries());
        return enqueueIn(descriptor);
    }

    private void retry(@NotNull final String id, @NotNull final LocalDateTime time) {
        try {
            logger.debug("Retrying job: {}", id);
            final Job job = id(id).orElseThrow(DoddleException::new);

            if (asList(EXECUTING, COMPLETED).contains(job.getState())) {
                throw new DoddleException("A job that is executing or has completed successfully cannot be retried");
            }

            if (job.getMaxRetries().equals(job.getRetries())) {
                logger.debug("Job has exceeded maximum retries so will increment max retries by one");
                final Integer maxRetries = job.getMaxRetries() + 1;
                job.setMaxRetries(maxRetries);
            }
            final Integer retries = job.getRetries() + 1;
            job.setScheduledAt(time);
            job.setRetries(retries);
            job.setState(RETRYABLE);
            job.setCompletedAt(null);
            job.setDiscardedAt(null);
            job.setFailedAt(null);
            storage.saveJob(job);

        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

}
