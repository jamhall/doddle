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
package dev.doddle.storage.sql;

import dev.doddle.storage.common.StorageProvider;
import dev.doddle.storage.common.builders.*;
import dev.doddle.storage.common.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.InjectionContextProvider;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static dev.doddle.storage.common.domain.JobCategory.STANDARD;
import static dev.doddle.storage.common.domain.JobState.*;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(InjectionContextProvider.class)
class StorageProviderTest {

    @TestTemplate
    @DisplayName("it should count all cron jobs")
    void it_should_count_all_cron_jobs(StorageProvider adapter) {
        assertEquals(3, adapter.countAllCronJobs());
    }

    @TestTemplate
    @DisplayName("it should count all messages for a job")
    void it_should_count_all_messages_for_a_job(StorageProvider adapter) {
        final Job job = adapter.getJobById("1f4c7f90-7cc6-11ea-bc55-0242ac130003").orElseThrow();
        final Integer count = adapter.countAllMessagesForJob(job);
        assertEquals(4, count);
    }

    @TestTemplate
    @DisplayName("it should count all queues")
    void it_should_count_all_queues(StorageProvider adapter) {
        assertEquals(3, adapter.countAllQueues());
    }

    @TestTemplate
    @DisplayName("it should count jobs for a given filter")
    void it_should_count_jobs_for_a_given_filter(StorageProvider adapter) {
        final JobFilter filter = new JobFilter();
        filter.setStates(singletonList(SCHEDULED));
        filter.setTags(singletonList("mailer"));
        assertEquals(2, adapter.countJobs(filter));
    }

    @TestTemplate
    @DisplayName("it should get jobs for a given filter")
    void it_should_get_jobs_for_a_given_filter(StorageProvider adapter) {
        final JobFilter filter = new JobFilter();
        filter.setStates(singletonList(SCHEDULED));
        filter.setTags(singletonList("mailer"));
        assertEquals(2, adapter.getJobs(filter, new Pageable()).size());
    }

    @TestTemplate
    @DisplayName("it should create a new cron job")
    void it_should_create_a_new_cron_job(StorageProvider adapter) {
        final Queue queue = adapter.getQueueByName("high").orElseThrow();
        final LocalDateTime now = now();
        final CronJobBuilder builder = CronJobBuilder.newBuilder();
        final CronJob job = builder
            .name("cron1")
            .handler("test.handler")
            .expression("* * * * *")
            .nextRunAt(now)
            .maxRetries(50)
            .queue(queue)
            .timeout(5000)
            .build();
        final CronJob createdJob = adapter.saveCronJob(job);
        assertNotNull(createdJob.getId());
        assertEquals("cron1", createdJob.getName());
        assertEquals("test.handler", createdJob.getHandler());
        assertEquals("* * * * *", createdJob.getExpression());
        assertEquals(5000, createdJob.getTimeout());
        assertEquals(50, createdJob.getMaxRetries());
        assertEquals(queue.getName(), createdJob.getQueue().getName());
    }

    @TestTemplate
    @DisplayName("it should create a new job")
    void it_should_create_a_new_job(StorageProvider adapter) {
        final LocalDateTime now = now();
        final Queue queue = adapter.getQueueByName("high").orElseThrow();
        final JobBuilder builder = JobBuilder.newBuilder();
        final Job job =
            builder
                .queue(queue)
                .name("email.registration")
                .category(STANDARD)
                .handler("sendEmail")
                .timeout(45000)
                .data("{ \"message\": \"Hello world\" }")
                .state(SCHEDULED)
                .scheduledAt(now)
                .completedAt(now)
                .discardedAt(now)
                .failedAt(now)
                .maxRetries(10)
                .retries(2)
                .tags(asList("mailer", "important"))
                .build();
        adapter.saveJob(job);
        adapter.getJobById(job.getId()).ifPresentOrElse(createdJob -> {

            // not great....probably a better way to do this....
            assertEquals(now.truncatedTo(ChronoUnit.MILLIS), createdJob.getScheduledAt().truncatedTo(ChronoUnit.MILLIS));
            assertEquals(now.truncatedTo(ChronoUnit.MILLIS), createdJob.getCompletedAt().truncatedTo(ChronoUnit.MILLIS));
            assertEquals(now.truncatedTo(ChronoUnit.MILLIS), createdJob.getDiscardedAt().truncatedTo(ChronoUnit.MILLIS));
            assertEquals(now.truncatedTo(ChronoUnit.MILLIS), createdJob.getFailedAt().truncatedTo(ChronoUnit.MILLIS));

            assertEquals(45000, createdJob.getTimeout());
            assertEquals(10, createdJob.getMaxRetries());
            assertEquals(2, createdJob.getRetries());
            assertEquals(SCHEDULED, createdJob.getState());
            assertEquals(queue, createdJob.getQueue());
            assertEquals("sendEmail", createdJob.getHandler());
            assertEquals("{ \"message\": \"Hello world\" }", createdJob.getData());
            assertEquals(STANDARD, createdJob.getCategory());
        }, () -> {
            throw new IllegalArgumentException("Job not found");
        });
    }

    @TestTemplate
    @DisplayName("it should create a new message for a job")
    void it_should_create_a_new_message_for_a_job(StorageProvider adapter) {
        final Job job = adapter.getJobById("1f4c7f90-7cc6-11ea-bc55-0242ac130003").orElseThrow();
        final JobMessageBuilder builder = JobMessageBuilder.newBuilder();
        final JobMessage jobMessage = builder.level("INFO")
            .message("Hello world!")
            .errorMessage("An exception was thrown")
            .errorClass("com.example.Processor")
            .errorStackTrace("A super long stack trace...")
            .build();
        final JobMessage createdJobMessage = adapter.createMessageForJob(job, jobMessage);
        assertNotNull(createdJobMessage.getId());
    }

    @TestTemplate
    @DisplayName("it should create a queue")
    void it_should_create_a_queue(StorageProvider adapter) {
        final LocalDateTime now = now();
        final QueueBuilder builder = QueueBuilder.newBuilder();
        final Queue queue = builder
            .name("critical")
            .priority(0.1f)
            .lockedAt(now)
            .build();
        final Queue createdQueue = adapter.saveQueue(queue);
        assertNotNull(createdQueue.getId());
        assertEquals("critical", createdQueue.getName());
        assertEquals(0.1f, createdQueue.getPriority());
        assertNotNull(createdQueue.getLockedAt());
    }

    @TestTemplate
    @DisplayName("it should delete a cron job")
    void it_should_delete_a_cron_job(StorageProvider adapter) {
        assertEquals(3, adapter.countAllCronJobs());
        final CronJob job = adapter.getCronJobById("4d075677-174a-4485-ab85-d02ea94bb4dd").orElseThrow();
        adapter.deleteCronJob(job);
        assertEquals(2, adapter.countAllCronJobs());

    }

    @TestTemplate
    @DisplayName("it should delete a job")
    void it_should_delete_a_job(StorageProvider adapter) {
        final String id = "1f4c7f90-7cc6-11ea-bc55-0242ac130003";
        adapter.deleteJob(adapter.getJobById(id).get());
        assertFalse(adapter.getJobById(id).isPresent());
    }

    @TestTemplate
    @DisplayName("it should delete all jobs")
    void it_should_delete_all_jobs(StorageProvider adapter) {
        assertEquals(4, adapter.countJobs());

        adapter.deleteAllJobs();

        assertEquals(3, adapter.countJobs());
    }

    @TestTemplate
    @DisplayName("it should delete all jobs older than a given period")
    void it_should_delete_all_jobs_older_than_a_given_period(StorageProvider adapter) {
        assertEquals(4, adapter.countJobs());

        final LocalDateTime date = LocalDateTime.now().minusMinutes(2);
        adapter.deleteAllJobs(date);

        assertEquals(4, adapter.countJobs());

        assertEquals(4, adapter.countJobs());

        final LocalDateTime newDate = LocalDateTime.now().minusSeconds(30);
        adapter.deleteAllJobs(newDate);

        assertEquals(3, adapter.countJobs());
    }

    @TestTemplate
    @DisplayName("it should delete all cron jobs")
    void it_should_delete_all_cron_jobs(StorageProvider adapter) {
        assertEquals(3, adapter.countAllCronJobs());
        adapter.deleteAllCronJobs();
        assertEquals(0, adapter.countAllCronJobs());
    }

    @TestTemplate
    @DisplayName("it should delete all messages for a job")
    void it_should_delete_all_messages_for_a_job(StorageProvider adapter) {
        final Job job = adapter.getJobById("1f4c7f90-7cc6-11ea-bc55-0242ac130003").orElseThrow();
        assertEquals(4, adapter.countAllMessagesForJob(job));
        adapter.deleteAllMessagesForJob(job);
        assertEquals(0, adapter.countAllMessagesForJob(job));
    }

    @TestTemplate
    @DisplayName("it should delete all queues")
    void it_should_delete_all_queues(StorageProvider adapter) {
        assertEquals(3, adapter.countAllQueues());
        adapter.deleteAllQueues();
        assertEquals(0, adapter.countAllQueues());
    }

    @TestTemplate
    @DisplayName("it should enqueue jobs that are ready to be processed")
    void it_should_enqueue_jobs_that_are_ready_to_be_processed(StorageProvider adapter) {
        assertEquals(1, adapter.enqueueJobs().size());
        assertEquals(0, adapter.enqueueJobs().size());
    }

    @TestTemplate
    @DisplayName("it should fail to find a job for a given id")
    void it_should_fail_to_get_a_job_for_a_given_id(StorageProvider adapter) {
        assertFalse(adapter.getJobById("1f4c7f90-6dfc-11ea-bc55-0242ac130003").isPresent());
    }

    @TestTemplate
    @DisplayName("it should get a cron job for a given id")
    void it_should_get_a_cron_job_for_a_given_id(StorageProvider adapter) {
        final CronJob job = adapter.getCronJobById("4d075677-174a-4485-ab85-d02ea94bb4dd").orElseThrow();
        assertEquals("4d075677-174a-4485-ab85-d02ea94bb4dd", job.getId());
        assertTrue(job.isEnabled());
        assertEquals("* * * * *", job.getExpression());
        assertEquals("mailer.send", job.getHandler());
    }

    @TestTemplate
    @DisplayName("it should get a cron job for a given name")
    void it_should_get_a_cron_job_for_a_given_name(StorageProvider adapter) {
        final CronJob job = adapter.getCronJobByName("job1").orElseThrow();
        assertEquals("4d075677-174a-4485-ab85-d02ea94bb4dd", job.getId());
        assertTrue(job.isEnabled());
        assertEquals("job1", job.getName());
        assertEquals("job 1 description", job.getDescription());
    }

    @TestTemplate
    @DisplayName("it should get a job for a given id")
    void it_should_get_a_job_for_a_given_id(StorageProvider adapter) {
        Job job = adapter.getJobById("1f4c7f90-7cc6-11ea-bc55-0242ac130003").orElseThrow();
        assertEquals(new JobProgress(5, 100), job.getProgress());
    }

    @TestTemplate
    @DisplayName("it should get a job for a given identifier")
    void it_should_get_a_job_for_a_given_identifier(StorageProvider adapter) {
        Job job = adapter.getJobByIdentifier("email.123").orElse(null);
        assertNotNull(job);
        assertEquals("1f4c7f90-7cc6-11ea-bc55-0242ac130003", job.getId());
    }

    @TestTemplate
    @DisplayName("it should get a queue for a given id")
    void it_should_get_a_queue_for_a_given_id(StorageProvider adapter) {
        final Queue queue = adapter.getQueueById("b9c5133a-b62b-11eb-8529-0242ac130003").orElseThrow();
        assertEquals("b9c5133a-b62b-11eb-8529-0242ac130003", queue.getId());
        assertEquals("low", queue.getName());
        assertEquals(1.0f, queue.getPriority());
        assertTrue(queue.isLocked());
    }

    @TestTemplate
    @DisplayName("it should get a queue for a given name")
    void it_should_get_a_queue_for_a_given_name(StorageProvider adapter) {
        final Queue queue = adapter.getQueueByName("high").orElseThrow();
        assertEquals("bee2f9aa-b62a-11eb-8529-0242ac130003", queue.getId());
        assertEquals("high", queue.getName());
        assertEquals(0.0f, queue.getPriority());
        assertNull(queue.getLockedAt());
    }

    @TestTemplate
    @DisplayName("it should get all cron jobs")
    void it_should_get_all_cron_jobs(StorageProvider adapter) {
        assertEquals(3, adapter.getAllCronJobs().size());
    }

    @TestTemplate
    @DisplayName("it should get all filtered messages for a job")
    void it_should_get_all_filtered_messages_for_a_job(StorageProvider adapter) {
        final Job job = adapter.getJobById("1f4c7f90-7cc6-11ea-bc55-0242ac130003").orElseThrow();
        final JobMessageFilter filter = new JobMessageFilter(singletonList("INFO"), "another log");
        final List<JobMessage> messages = adapter.getAllMessagesForJob(job, filter, new Pageable(0, 25));
        assertEquals(1, messages.size());
    }

    @TestTemplate
    @DisplayName("it should get all jobs")
    void it_should_get_all_jobs(StorageProvider adapter) {
        final List<Job> jobs = adapter.getJobs();
        assertEquals(4, jobs.size());
    }

    @TestTemplate
    @DisplayName("it should get all jobs for a given name")
    void it_should_get_all_jobs_for_a_given_name(StorageProvider adapter) {
        final JobFilter filter = new JobFilter() {{
            setName("email.forgot-password");
        }};
        assertEquals(1, adapter.getJobs(filter, new Pageable()).size());
    }

    @TestTemplate
    @DisplayName("it should count al jobs for a given name")
    void it_should_count_all_jobs_for_a_given_name(StorageProvider adapter) {
        final JobFilter filter = new JobFilter() {{
            setName("email.forgot-password");
        }};
        assertEquals(1, adapter.countJobs(filter));
    }

    @TestTemplate
    @DisplayName("it should get all messages for a job")
    void it_should_get_all_messages_for_a_job(StorageProvider adapter) {
        final Job job = adapter.getJobById("1f4c7f90-7cc6-11ea-bc55-0242ac130003").orElseThrow();
        final List<JobMessage> messages = adapter.getAllMessagesForJob(job);
        assertEquals(4, messages.size());
    }

    @TestTemplate
    @DisplayName("it should get all paginated jobs")
    void it_should_get_all_paginated_jobs(StorageProvider adapter) {
        assertEquals(2, adapter.getJobs(new Pageable(0, 2)).size());
        assertEquals(1, adapter.getJobs(new Pageable(3, 10)).size());
    }

    @TestTemplate
    @DisplayName("it should get all queues")
    void it_should_get_all_queues(StorageProvider adapter) {
        assertEquals(3, adapter.getAllQueues().size());
    }

    @TestTemplate
    @DisplayName("it should get jobs for a list of queues")
    void it_should_get_jobs_for_a_list_of_queues(StorageProvider adapter) {
        final JobFilter filter1 = new JobFilter();
        filter1.setQueues(singletonList("high"));
        final List<Job> jobs = adapter.getJobs(filter1, new Pageable(0, 10));
        assertEquals(3, jobs.size());
    }

    @TestTemplate
    @DisplayName("it should get jobs for a list of tags")
    void it_should_get_jobs_for_a_list_of_tags(StorageProvider adapter) {
        final JobFilter filter1 = new JobFilter();
        filter1.setTags(singletonList("mailer"));
        final List<Job> jobs = adapter.getJobs(filter1, new Pageable(0, 10));
        assertEquals(4, jobs.size());
        final JobFilter filter2 = new JobFilter();
        filter2.setTags(asList("mailer", "groups"));
        final List<Job> jobs2 = adapter.getJobs(filter2, new Pageable(0, 10));
        assertEquals(2, jobs2.size());
    }

    @TestTemplate
    @DisplayName("it should get paginated cron jobs")
    void it_should_get_paginated_cron_jobs(StorageProvider adapter) {
        assertEquals(2, adapter.getAllCronJobs(new Pageable(1, 3)).size());
    }

    @TestTemplate
    @DisplayName("it should pick a job for processing")
    void it_should_pick_job_for_processing(StorageProvider adapter) {
        final Job job = adapter.pickJob().orElseThrow();
        assertEquals("18cf1f10-63b4-4eec-a1c5-cdcaba624b22", job.getId());
        assertEquals(EXECUTING, job.getState());
        assertTrue(adapter.pickJob().isEmpty());
    }

    @TestTemplate
    @DisplayName("it should update a cron job")
    void it_should_update_a_cron_job(StorageProvider adapter) {
        final CronJob job = adapter.getCronJobById("4d075677-174a-4485-ab85-d02ea94bb4dd").orElseThrow();
        job.setEnabled(false);
        final CronJob updatedJob = adapter.saveCronJob(job);
        assertEquals("4d075677-174a-4485-ab85-d02ea94bb4dd", updatedJob.getId());
        assertFalse(updatedJob.isEnabled());
    }

    @TestTemplate
    @DisplayName("it should update a job")
    void it_should_update_a_job(StorageProvider adapter) {
        final String id = "1f4c7f90-7cc6-11ea-bc55-0242ac130003";
        final Job job = adapter.getJobById(id).orElseThrow(IllegalArgumentException::new);

        final LocalDateTime now = now();
        final JobProgress progress = new JobProgress(5, 100);
        job.setScheduledAt(now);
        job.setMaxRetries(200);
        job.setRetries(10);
        job.setState(COMPLETED);
        job.setData("{ \"a\": \"b\"}");
        job.setProgress(progress);
        job.setName("order.process");
        job.setIdentifier("order.999");
        adapter.saveJob(job);

        adapter.getJobById(id).ifPresentOrElse(updatedJob -> {
            assertEquals(now.truncatedTo(ChronoUnit.SECONDS), updatedJob.getScheduledAt().truncatedTo(ChronoUnit.SECONDS));
            assertEquals(200, updatedJob.getMaxRetries());
            assertEquals(10, updatedJob.getRetries());
            assertEquals(COMPLETED, updatedJob.getState());
            assertEquals("{ \"a\": \"b\"}", updatedJob.getData());
            assertEquals(progress, updatedJob.getProgress());
            assertEquals("order.process", job.getName());
            assertEquals("order.999", job.getIdentifier());
        }, () -> {
            throw new IllegalArgumentException("Job not found");
        });
    }

    @TestTemplate
    @DisplayName("it should update a job with an error")
    void it_should_update_a_job_with_an_error(StorageProvider adapter) {
        final String id = "1f4c7f90-7cc6-11ea-bc55-0242ac130003";
        final Job job = adapter.getJobById(id).orElseThrow(IllegalArgumentException::new);
        final JobErrorBuilder builder = JobErrorBuilder.newBuilder();
        final JobError error = builder.message("Hello world!")
            .throwable("java.lang.IllegalArgumentException")
            .stackTrace("A super long stack trace...")
            .build();

        job.setError(error);
        adapter.saveJob(job);

        adapter.getJobById(id).ifPresentOrElse(updatedJob -> {
            assertNotNull(updatedJob.getError());
            assertEquals("Hello world!", updatedJob.getError().getMessage());
            assertEquals("java.lang.IllegalArgumentException", updatedJob.getError().getThrowable());
            assertEquals("A super long stack trace...", updatedJob.getError().getStackTrace());
        }, () -> {
            throw new IllegalArgumentException("Job not found");
        });
    }

    @TestTemplate
    @DisplayName("it should update a queue")
    void it_should_update_a_queue(StorageProvider adapter) {
        final String id = "bee2f9aa-b62a-11eb-8529-0242ac130003";
        final Queue queue = adapter.getQueueById(id).orElseThrow(IllegalArgumentException::new);

        queue.setName("higher");
        adapter.saveQueue(queue);

        adapter.getQueueById(id).ifPresentOrElse(updatedQueue -> {
            assertEquals("higher", updatedQueue.getName());
        }, () -> {
            throw new IllegalArgumentException("Queue not found");
        });
    }


}
