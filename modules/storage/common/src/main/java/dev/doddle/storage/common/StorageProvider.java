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

import dev.doddle.storage.common.domain.*;

import java.io.Reader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StorageProvider {

    /**
     * Identify this storage provider
     * @return the name of this provider
     */
    String getName();

    /**
     * Count all cron jobs
     *
     * @return the count of cron jobs
     */
    Integer countAllCronJobs() throws StorageException;

    /**
     * Count all messages for a given job
     *
     * @param job the job
     * @return the count of messages
     */
    Integer countAllMessagesForJob(Job job) throws StorageException;

    /**
     * Count all queues
     *
     * @return total number of queues
     */
    Integer countAllQueues() throws StorageException;

    Long countJobs(JobFilter filter) throws StorageException;

    Long countJobs() throws StorageException;


    /**
     * Load data into the storage
     *
     * @param reader the data to read and persist into storage
     */
    void create(Reader reader) throws StorageException;

    /**
     * Create a message for a given job
     *
     * @param job     the job
     * @param message the message to create
     * @return the created message
     */
    JobMessage createMessageForJob(Job job, JobMessage message) throws StorageException;

    /**
     * Delete all cron jobs
     */
    void deleteAllCronJobs() throws StorageException;

    void deleteAllJobs() throws StorageException;

    void deleteAllJobs(LocalDateTime date) throws StorageException;

    /**
     * Delete all messages for a given job
     *
     * @param job the job
     */
    void deleteAllMessagesForJob(Job job) throws StorageException;

    /**
     * Delete all queues
     */
    void deleteAllQueues() throws StorageException;

    /**
     * Delete a cron job
     *
     * @param job the cron job to delete
     */
    void deleteCronJob(CronJob job) throws StorageException;

    /**
     * Delete a job
     *
     * @param job the job to delete
     */
    void deleteJob(Job job) throws StorageException;

    /**
     * Delete a queue
     *
     * @param queue the queue to delete
     */
    void deleteQueue(Queue queue) throws StorageException;

    /**
     * Enqueue jobs ready to be processed
     *
     * @return a collection of jobs enqueued
     */
    List<Job> enqueueJobs() throws StorageException;

    /**
     * Get all cron jobs
     *
     * @return a collection of cron jobs
     */
    List<CronJob> getAllCronJobs() throws StorageException;

    /**
     * Get all cron jobs
     *
     * @param pageable paginate the results
     * @return a collection of cron jobs
     */
    List<CronJob> getAllCronJobs(Pageable pageable) throws StorageException;

    /**
     * Get all messages for a given job
     *
     * @param job the job
     * @return a collection of messages
     */
    List<JobMessage> getAllMessagesForJob(Job job) throws StorageException;

    /**
     * Get all messages for a job
     *
     * @param job      the job
     * @param filter   filter the results
     * @param pageable paginate the results
     * @return a collection of messages
     */
    List<JobMessage> getAllMessagesForJob(Job job, JobMessageFilter filter, Pageable pageable) throws StorageException;

    /**
     * Get all queues
     *
     * @return a list of queues
     */
    List<Queue> getAllQueues();

    List<String> getAllTasks() throws StorageException;

    /**
     * Get cron job by a given identifier
     *
     * @param id the identifier
     * @return the cron job if found
     */
    Optional<CronJob> getCronJobById(String id) throws StorageException;

    /**
     * Get cron job by a given name
     *
     * @param name the name
     * @return the cron job if found
     */
    Optional<CronJob> getCronJobByName(String name) throws StorageException;

    /**
     * Get a job by its id
     *
     * @param id the job id
     * @return the job if found
     */
    Optional<Job> getJobById(String id) throws StorageException;

    /**
     * Get a job by its identifier
     *
     * @param identifier the job identifier
     * @return the job if found
     */
    Optional<Job> getJobByIdentifier(String identifier) throws StorageException;

    /**
     * Get jobs
     *
     * @param pageable paginate the results
     * @return a collection of jobs
     */
    List<Job> getJobs(Pageable pageable) throws StorageException;

    /**
     * Get jobs
     *
     * @param filter   the filter to filter the results
     * @param pageable paginate the results
     * @return a collection of jobs
     */
    List<Job> getJobs(JobFilter filter, Pageable pageable) throws StorageException;

    /**
     * Get jobs
     *
     * @return a collection of jobs
     */
    List<Job> getJobs() throws StorageException;

    /**
     * Get a queue for a given id
     *
     * @param name the id of the queue
     * @return the queue
     */
    Optional<Queue> getQueueById(String name);

    /**
     * Get a queue for a given name
     *
     * @param name the name of the queue
     * @return the queue
     */
    Optional<Queue> getQueueByName(String name);

    /**
     * Load data into the storage
     *
     * @param reader the data to read and persist into storage
     */
    void load(Reader reader) throws StorageException;

    /**
     * Pick a job that is ready to be processed
     *
     * @return a job to be processed
     */
    Optional<Job> pickJob() throws StorageException;

    /*
     * Reset the storage adapter
     */
    void reset(Reader reader) throws StorageException;

    /**
     * Save a cron job
     *
     * @param job the cron job to save
     * @return the saved cron job
     */
    CronJob saveCronJob(CronJob job) throws StorageException;

    /**
     * Save a job
     *
     * @param job the job to save
     * @return the saved job
     */
    Job saveJob(Job job) throws StorageException;

    /**
     * Save a queue
     *
     * @param queue the queue to save
     * @return the saved queue
     */
    Queue saveQueue(Queue queue);

    JobStatistic getJobStatistics() throws StorageException;


}

