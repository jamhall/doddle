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

import dev.doddle.core.engine.JobEnvironment;
import dev.doddle.core.engine.JobExecutionContext;
import dev.doddle.core.engine.retry.Retryer;
import dev.doddle.core.engine.retry.RetryerRegistry;
import dev.doddle.core.engine.retry.strategies.LinearRetryStrategy;
import dev.doddle.core.engine.task.*;
import dev.doddle.core.exceptions.TaskExecutionException;
import dev.doddle.core.support.tasks.valid.CounterService;
import dev.doddle.core.support.tasks.valid.DefaultTasks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static dev.doddle.core.engine.task.TaskOptions.createDefaultTaskOptions;
import static org.junit.jupiter.api.Assertions.*;

class TaskExecutorTest {

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
    private static       TaskExecutor           taskExecutor;
    private static       TaskService            taskService;

    @BeforeAll
    public static void init() {

        final List<Retryer> retryerStrategies = new ArrayList<>() {{
            add(new Retryer("linear", new LinearRetryStrategy()));
        }};
        final RetryerRegistry retryRegistry = new RetryerRegistry(retryerStrategies, "linear");
        final TaskDescriptorFactory taskDescriptorFactory = new TaskDescriptorFactory(retryRegistry, createDefaultTaskOptions());
        final TaskParser parser = new TaskParser(taskDescriptorFactory);
        final List<TaskDescriptor> tasks = parser.parse("dev.doddle.common.support.tasks.valid");
        final TaskRegistry registry = new TaskRegistry(tasks);
        taskExecutor = new TaskExecutor(resolver);

        taskService = new TaskService(registry, taskExecutor);
    }

    @Test
    @DisplayName("it should fail to execute a task because a runtime exception was thrown")
    void it_should_fail_to_execute_a_task_because_a_runtime_exception_was_thrown() {
        final TaskExecutionException exception = assertThrows(TaskExecutionException.class, () -> {
            TaskDescriptor descriptor = taskService.getByName("catchRuntime");
            taskExecutor.execute(descriptor, 1000L, createExecutionContext());
        });
        assertTrue(exception.getMessage().contains("invalid"));
    }

    @Test
    @DisplayName("it should fail to execute a task because it could not resolve the dependencies")
    void it_should_fail_to_execute_a_task_because_it_could_not_find_the_dependencies() {
        final TaskExecutor taskExecutor = new TaskExecutor(new TaskDependencyResolver() {
            @Override
            public <T> T resolve(Class<T> type) {
                return null;
            }
        });

        final TaskExecutionException exception = assertThrows(TaskExecutionException.class, () -> {
            TaskDescriptor incrementTask = TaskExecutorTest.taskService.getByName("increment");

            taskExecutor.execute(incrementTask, 1000L, createExecutionContext());
        });
        assertEquals("Could not resolve the class for the task increment. Is it being injected correctly?", exception.getMessage());
    }

    @Test
    @DisplayName("it should fail to execute a task because task throws an exception")
    void it_should_fail_to_execute_a_task_because_of_a_condition() {
        counterService.setValue(5);
        final TaskExecutionException exception = assertThrows(TaskExecutionException.class, () -> {
            TaskDescriptor descriptor = taskService.getByName("failing");
            taskExecutor.execute(descriptor, 1000L, createExecutionContext());
        });
        assertEquals("I failed for some reason", exception.getMessage());
    }

    @Test
    @DisplayName("it should fail to execute a task because of a timeout")
    void it_should_fail_to_execute_a_task_because_of_a_timeout() {
        TaskDescriptor timeoutTask = taskService.getByName("timeout");
        TaskExecutionException thrown = assertThrows(TaskExecutionException.class, () -> {
            taskExecutor.execute(timeoutTask, 1000L, createExecutionContext());
        });
        assertTrue(thrown.getMessage().contains("The task exceeded its timeout"));
    }

    @Test
    @DisplayName("it should fail to execute a task because of environment variable not existing")
    void it_should_fail_to_execute_a_task_because_of_environment_variable_not_existing() {
        final TaskExecutionException exception = assertThrows(TaskExecutionException.class, () -> {
            TaskDescriptor descriptor = taskService.getByName("clearCache");
            taskExecutor.execute(descriptor, 1000L, createExecutionContext());
        });
        assertTrue(exception.getMessage().contains("Could not find server variable"));
    }

    @Test
    @DisplayName("it should fail to execute a task because of invalid data casting")
    void it_should_fail_to_execute_a_task_because_of_invalid_data_casting() {
        counterService.setValue(5);
        final TaskExecutionException exception = assertThrows(TaskExecutionException.class, () -> {
            TaskDescriptor descriptor = taskService.getByName("processOrders");
            taskExecutor.execute(descriptor, 100L, createExecutionContext("\"NaN\""));
        });
        assertTrue(exception.getMessage().contains("Could not deserialise data into the given object: Integer"));
    }

    @Test
    @DisplayName("it should successfully execute a task")
    void it_should_successfully_execute_a_task() {
        TaskDescriptor incrementTask = taskService.getByName("increment");
        TaskDescriptor decrementTask = taskService.getByName("decrement");
        counterService.setValue(0);
        taskExecutor.execute(incrementTask, 1000L, createExecutionContext());
        assertEquals(1, counterService.getCurrentValue());
        taskExecutor.execute(decrementTask, 1000L, createExecutionContext());
        assertEquals(0, counterService.getCurrentValue());
    }

    @Test
    @DisplayName("it should successfully execute a task that uses an environment variable")
    void it_should_successfully_execute_a_task_that_uses_an_environment_variable() {
        JobEnvironment environment = new JobEnvironment();
        environment.put("server", "example.com");
        TaskDescriptor descriptor = taskService.getByName("clearCache");
        JobExecutionContext context = createExecutionContext(environment);
        taskExecutor.execute(descriptor, 1000L, context);
    }

    @Test
    @DisplayName("it should successfully execute a task that uses data")
    void it_should_successfully_execute_a_task_that_uses_data() {
        TaskDescriptor descriptor = taskService.getByName("sendEmail");
        taskExecutor.execute(descriptor, 1000L, createExecutionContext("\"hello@example.com\""));
    }

    @Test
    @DisplayName("it should successfully execute a task that uses the logger")
    void it_should_successfully_execute_a_task_that_uses_the_logger() {
        TaskDescriptor descriptor = taskService.getByName("sendNewsletter");
        JobExecutionContext context = createExecutionContext("\"hello@example.com\"");
        taskExecutor.execute(descriptor, 1000L, context);
    }

    private JobExecutionContext createExecutionContext() {
        return createExecutionContext(null, new JobEnvironment());
    }

    private JobExecutionContext createExecutionContext(String data) {
        return createExecutionContext(data, new JobEnvironment());
    }

    private JobExecutionContext createExecutionContext(JobEnvironment environment) {
        return createExecutionContext(null, environment);
    }


    // @FIXME
    private JobExecutionContext createExecutionContext(String data, JobEnvironment environment) {
//        final Storage storage = mock(Storage.class);
//
//        final Job job = new Job();
//        job.setId("123");
//        job.setData(data);
//        final JobDataMapper mapper = new JobDataMapper(new JacksonMapperAdapter());
//        final JobExecutionContextEventBus eventBus = new JobExecutionContextEventBus(storage);
//        final JobLoggerConfiguration loggerConfiguration = new JobLoggerConfiguration(DEBUG, 100);
//        final JobExecutionContextFacade facade = new JobExecutionContextFacade(job, eventBus, mapper, environment, loggerConfiguration);
//        return new JobExecutionContext(facade);
        return null;
    }
}
