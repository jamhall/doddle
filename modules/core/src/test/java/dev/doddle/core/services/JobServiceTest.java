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

import dev.doddle.core.engine.task.TaskDependencyResolver;
import dev.doddle.core.exceptions.DoddleException;
import dev.doddle.core.support.tasks.valid.CounterService;
import dev.doddle.core.support.tasks.valid.DefaultTasks;
import dev.doddle.storage.common.domain.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.*;

@Disabled()
class JobServiceTest {
    private static final CounterService         counterService = new CounterService();
    private static final TaskDependencyResolver resolver       = new TaskDependencyResolver() {
        @Override
        public <T> T resolve(Class<T> type) {
            if (type.equals(DefaultTasks.class)) {
                return (T) new DefaultTasks(counterService);
            }
            return null;
        }
    };
    private static       JobService             jobService;

    @BeforeEach
    public void initEach() {
//        final JobBuilder builder = JobBuilder.newJob();
//        final Job defaultJob = builder.id("ac28f0ee-b1d1-4241-bd91-0c4b3a24727a")
//            .queue("default")
//            .handler("sayHello")
//            .state(JobState.SCHEDULED)
//            .data("\"hello\"")
//            .retries(0)
//            .maxRetries(10)
//            .createdAt(now())
//            .scheduledAt(now().minusHours(1))
//            .tags(singletonList("messaging"))
//            .timeout(25000)
//            .build();
////        final Storage storage = new Storage(new MemoryStorageAdapter());
//        final Storage storage = null;
//        storage.createJob(defaultJob);
//
//        final TaskOptions defaultTaskOptions = new TaskOptions();
//        defaultTaskOptions.maxRetries(20);
//        defaultTaskOptions.retryStrategy(LinearRetryStrategy.class);
//
//        final Map<Class<? extends RetryStrategy>, RetryStrategy> retryStrategies = new HashMap<>() {{
//            put(LinearRetryStrategy.class, new LinearRetryStrategy());
//        }};
//        final JobDataMapper mapper = new JobDataMapper(new JacksonMapperAdapter(new ObjectMapper()));
//        final TaskDescriptorFactory = new TaskDescriptorFactory()
//        final TaskRegistry taskRegistry = new TaskRegistry(
//            new TaskParser("dev.doddle.common.support.tasks.valid",
//                retryStrategies,
//                defaultTaskOptions, new TaskMethodValidator()));
//        final TaskExecutor taskExecutor = new TaskExecutor(resolver);
//
//        final TaskService taskService = new TaskService(taskRegistry, taskExecutor);
//        jobService = new JobService(taskService, storage, mapper);
    }

    @DisplayName("it should fail to enqueue a job because of invalid payload type")
    @Test
    void it_should_fail_to_enqueue_a_job_because_of_invalid_payload_type() {
        final DoddleException exception = assertThrows(DoddleException.class, () -> {
            jobService.enqueue(wizard -> {
                return wizard.queue("default")
                    .task("sayHello");
            });
        });
        assertEquals("The task is expecting a payload type of String but the payload type of Integer was given", exception.getMessage());
    }

    @DisplayName("it should fail to find a job")
    @Test
    void it_should_fail_to_find_a_job() {
        final Optional<Job> job = jobService.id("ac2df0ee-a1d1-c241-ed91-0a4b3a24727a");
        assertFalse(job.isPresent());
    }

    @DisplayName("it should successfully enqueue a job")
    @Test
    void it_should_successfully_enqueue_a_job() {
        final Job job = jobService.enqueue(wizard -> {
            return wizard.queue("default")
                .task("sayHello");
        });
        assertNotNull(job.getId());
        assertNotNull(job.getQueue());
        assertNotNull(job.getHandler());
        assertNotNull(job.getState());
        assertNotNull(job.getMaxRetries());
        assertNotNull(job.getRetries());
        assertNotNull(job.getTags());
        assertNotNull(job.getCreatedAt());
        assertNotNull(job.getScheduledAt());
    }

    @DisplayName("it should successfully enqueue a job in the future")
    @Test
    void it_should_successfully_enqueue_a_job_in_the_future() {
        final Job job = jobService.enqueueIn(wizard -> {
            return wizard.period("5m")
                .queue("default");
        });
        assertTrue(job.getScheduledAt().isAfter(now().plusMinutes(3)));
    }

    @DisplayName("it should successfully get a job")
    @Test
    void it_should_successfully_get_a_job() {
        final Optional<Job> job = jobService.id("ac28f0ee-b1d1-4241-bd91-0c4b3a24727a");
        assertTrue(job.isPresent());
    }

    @DisplayName("it should fail to enqueue a job because of missing payload")
    @Test
    void it_should_to_enqueue_a_job_because_of_missing_payload() {
        final DoddleException exception = assertThrows(DoddleException.class, () -> {
            jobService.enqueue(wizard -> {
                return wizard.queue("default")
                    .task("sayHello");
            });
        });
        assertEquals("The task is expecting a payload but the payload given is null", exception.getMessage());
    }

    @DisplayName("it should fail to enqueue a job because the task name was null")
    @Test
    void it_should_to_enqueue_a_job_because_the_task_name_was_null() {
        final NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            jobService.enqueue(wizard -> {
                return wizard.queue("default")
                    .task(null);
            });
        });
        assertEquals("name cannot be null", exception.getMessage());
    }

    @DisplayName("it should fail to enqueue a job because the task was not found")
    @Test
    void it_should_to_enqueue_a_job_because_the_task_was_not_found() {
        final DoddleException exception = assertThrows(DoddleException.class, () -> {
            jobService.enqueue(wizard -> {
                return wizard.queue("default")
                    .task("unknown");
            });
        });

        assertEquals("Task not found for the given name: unknown", exception.getMessage());
    }


}
