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

import dev.doddle.core.engine.retry.Retryer;
import dev.doddle.core.engine.retry.RetryerRegistry;
import dev.doddle.core.engine.retry.strategies.LinearRetryStrategy;
import dev.doddle.core.engine.task.*;
import dev.doddle.core.support.tasks.valid.CounterService;
import dev.doddle.core.support.tasks.valid.DefaultTasks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

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
    private static       TaskService            service;

    @BeforeAll
    public static void init() {
        final TaskOptions options = new TaskOptions();
        options.maxRetries(20);
        options.retryStrategy("linear");
        final List<Retryer> retryerStrategies = new ArrayList<>() {{
            add(new Retryer("linear", new LinearRetryStrategy()));
        }};

        final RetryerRegistry retryRegistry = new RetryerRegistry(retryerStrategies, "linear");
        final TaskDescriptorFactory taskDescriptorFactory = new TaskDescriptorFactory(retryRegistry, options);

        final TaskParser parser = new TaskParser(taskDescriptorFactory);
        final List<TaskDescriptor> descriptors = parser.parse("dev.doddle.common.support.tasks.valid");
        final TaskRegistry registry = new TaskRegistry(descriptors);
        final TaskExecutor executor = new TaskExecutor(resolver);
        service = new TaskService(registry, executor);
    }

    @DisplayName("it should find all tasks")
    @Test
    void it_should_find_all_tasks() {
        assertEquals(12, service.countAll());
    }

    @DisplayName("it should successfully find a task with complete options supplied")
    @Test
    void it_should_successfully_find_a_task_with_complete_options_supplied() {
        final TaskDescriptor task = service.getByName("sayHello");
        assertEquals("This task will say hello", task.getDescription());
        assertEquals("sayHello", task.getName());
    }

    @DisplayName("it should successfully find a task with default options applied")
    @Test
    void it_should_successfully_find_a_task_with_default_options_applied() {
        final TaskDescriptor task = service.getByName("taskWithNoName");
        assertEquals("taskWithNoName", task.getName());
        assertEquals(LinearRetryStrategy.class, task.getRetryer().getClass());
    }

    @DisplayName("it should successfully find a task with no name")
    @Test
    void it_should_successfully_find_a_task_with_no_name() {
        assertNotNull(service.getByName("taskWithNoName"));
    }

}
