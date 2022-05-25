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
package dev.doddle.storage.common;

import dev.doddle.common.support.NotNull;
import dev.doddle.storage.common.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.util.Objects.requireNonNull;

/**
 * Provides a facade to the storage provider
 */
public class Storage {

    private final StorageProvider provider;

    public Storage(final StorageProvider provider) {
        this.provider = requireNonNull(provider, "provider cannot be null");
    }

    public Integer countAllCronJobs() throws StorageException {
        return this.provider.countAllCronJobs();
    }

    public Integer countAllMessagesForJob(@NotNull Job job) throws StorageException {
        return this.provider.countAllMessagesForJob(
            requireNonNull(job, "job cannot be null")
        );
    }

    public Integer countAllQueues() {
        return this.provider.countAllQueues();
    }

    public Long countJobs(@NotNull final JobFilter filter) throws StorageException {
        return this.provider.countJobs(
            requireNonNull(filter, "filter cannot be null")
        );
    }

    public JobMessage createMessageForJob(@NotNull Job job, @NotNull JobMessage message) throws StorageException {
        return this.provider.createMessageForJob(
            requireNonNull(job, "job cannot be null"),
            requireNonNull(message, "message cannot be null")
        );
    }

    public void deleteAllCronJobs() throws StorageException {
        this.provider.deleteAllCronJobs();
    }

    public void deleteAllJobs() throws StorageException {
        this.provider.deleteAllJobs();
    }


    public void deleteAllJobs(@NotNull final LocalDateTime date) throws StorageException {
        this.provider.deleteAllJobs(date);
    }

    public void deleteAllMessagesForJob(@NotNull final Job job) throws StorageException {
        this.provider.deleteAllMessagesForJob(
            requireNonNull(job, "job cannot be null")
        );
    }

    public void deleteAllQueues() {
        this.provider.deleteAllQueues();
    }

    public void deleteCronJob(@NotNull final CronJob job) throws StorageException {
        this.provider.deleteCronJob(
            requireNonNull(job, "job cannot be null")
        );
    }

    public void deleteJob(@NotNull final Job job) throws StorageException {
        this.provider.deleteJob(
            requireNonNull(job, "job cannot be null")
        );
    }

    public void deleteQueue(@NotNull final Queue queue) throws StorageException {
        this.provider.deleteQueue(
            requireNonNull(queue, "queue cannot be null")
        );
    }

    public List<Job> enqueueJobs() throws StorageException {
        return this.provider.enqueueJobs();
    }

    public List<CronJob> getAllCronJobs() throws StorageException {
        return this.provider.getAllCronJobs();
    }

    public List<CronJob> getAllCronJobs(@NotNull final Pageable pageable) throws StorageException {
        return this.provider.getAllCronJobs(
            requireNonNull(pageable, "pageable cannot be null")
        );
    }

    public List<JobMessage> getAllMessagesForJob(@NotNull Job job) throws StorageException {
        return this.provider.getAllMessagesForJob(
            requireNonNull(job, "job cannot be null")
        );
    }

    public List<JobMessage> getAllMessagesForJob(@NotNull Job job, @NotNull JobMessageFilter filter, @NotNull Pageable pageable) throws StorageException {
        return this.provider.getAllMessagesForJob(
            requireNonNull(job, "job cannot be null"),
            requireNonNull(filter, "filter cannot be null"), requireNonNull(pageable, "pageable cannot be null")

        );
    }

    public List<Queue> getAllQueues() {
        return this.provider.getAllQueues();
    }

    public List<String> getAllTasks() throws StorageException {
        return this.provider.getAllTasks();
    }

    public Optional<CronJob> getCronJobById(@NotNull final String id) throws StorageException {
        return this.provider.getCronJobById(
            requireNonNull(id, "id cannot be null")
        );
    }

    public Optional<CronJob> getCronJobByName(@NotNull final String name) throws StorageException {
        return this.provider.getCronJobByName(
            requireNonNull(name, "name cannot be null")
        );
    }

    public Optional<Job> getJobById(@NotNull final String id) throws StorageException {
        return this.provider.getJobById(
            requireNonNull(id, "id cannot be null")
        );
    }

    public Optional<Job> getJobByIdentifier(@NotNull final String identifier) throws StorageException {
        return this.provider.getJobByIdentifier(
            requireNonNull(identifier, "identifier cannot be null")
        );
    }


    public JobStatistic getJobStatistics() throws StorageException {
        return this.provider.getJobStatistics();
    }

    public List<Job> getJobs(final @NotNull Pageable pageable) throws StorageException {
        return this.provider.getJobs(
            requireNonNull(pageable, "pageable cannot be null")
        );
    }

    public List<Job> getJobs(final @NotNull JobFilter filter,
                             final @NotNull Pageable pageable) throws StorageException {
        return this.provider.getJobs(
            requireNonNull(filter, "filter cannot be null"),
            requireNonNull(pageable, "pageable cannot be null")
        );
    }

    public List<Job> getJobs() throws StorageException {
        return this.provider.getJobs();
    }

    public Optional<Queue> getQueueById(@NotNull final String id) throws StorageException {
        return this.provider.getQueueById(requireNonNull(id, "id cannot be null"));
    }

    public Optional<Queue> getQueueByName(@NotNull final String name) throws StorageException {
        return this.provider.getQueueByName(requireNonNull(name, "name cannot be null"));
    }

    public Optional<Job> pickJob() throws StorageException {
        return this.provider.pickJob();
    }

    public CronJob saveCronJob(@NotNull final CronJob job) throws StorageException {
        return this.provider.saveCronJob(
            requireNonNull(job, "job cannot be null")
        );
    }

    public Job saveJob(final Job job) throws StorageException {
        try {
            return this.provider.saveJob(
                requireNonNull(job, "job cannot be null")
            );
        } catch (StorageException exception) {
            return null;
        }
    }

    public Queue saveQueue(@NotNull final Queue queue) throws StorageException {
        return this.provider.saveQueue(requireNonNull(queue, "queue cannot be null"));
    }

}