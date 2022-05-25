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
package dev.doddle.core.engine.task;

import dev.doddle.core.engine.retry.Retryer;
import dev.doddle.core.engine.retry.RetryerRegistry;
import dev.doddle.core.engine.retry.strategies.LinearRetryStrategy;
import dev.doddle.core.exceptions.TaskParserException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static dev.doddle.core.engine.task.TaskOptions.createDefaultTaskOptions;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

class TaskRegistryTest {

    private static final List<Retryer> retries = new ArrayList<>() {{
        add(new Retryer("linear", new LinearRetryStrategy()));
    }};

    private static TaskRegistry registry;

    @BeforeAll
    public static void init() {
        final RetryerRegistry retryRegistry = new RetryerRegistry(retries, "linear");
        final TaskDescriptorFactory taskDescriptorFactory = new TaskDescriptorFactory(retryRegistry, createDefaultTaskOptions());
        final TaskParser parser = new TaskParser(taskDescriptorFactory);
        final List<TaskDescriptor> descriptors = parser.parse("dev.doddle.core.support.tasks.valid");
        registry = new TaskRegistry(descriptors);
    }

    @DisplayName("it should find all tasks")
    @Test
    void it_should_find_all_tasks() {
        assertEquals(12, registry.countAll());
    }

    @DisplayName("it should get all tasks")
    @Test
    void it_should_get_all_tasks() {
        assertEquals(12, registry.getAll().size());
    }

    @DisplayName("it should not find a task that doesn't exist")
    @Test
    void it_should_not_find_a_task_that_doesnt_exist() {
        assertNull(registry.getForName("tomatoes"));
    }

    @DisplayName("it should successfully find a task with complete options supplied")
    @Test
    void it_should_successfully_find_a_task_with_complete_options_supplied() {
        final TaskDescriptor task = registry.getForName("sayHello");
        assertEquals("This task will say hello", task.getDescription());
        assertEquals("sayHello", task.getName());
        assertEquals(LinearRetryStrategy.class, task.getRetryer().getClass());
    }

    @DisplayName("it should successfully find a task with default options applied")
    @Test
    void it_should_successfully_find_a_task_with_default_options_applied() {
        final TaskDescriptor task = registry.getForName("taskWithNoName");
        assertEquals("taskWithNoName", task.getName());
        assertEquals(LinearRetryStrategy.class, task.getRetryer().getClass());
    }

    @DisplayName("it should successfully find a task with no name")
    @Test
    void it_should_successfully_find_a_task_with_no_name() {
        assertNotNull(registry.getForName("taskWithNoName"));
    }

    @DisplayName("it should throw an exception because of a duplicate task name")
    @Test
    void it_should_throw_an_exception_because_of_a_duplicate_task_name() {
        final TaskParserException exception = assertThrows(TaskParserException.class, () -> {
            final RetryerRegistry retryRegistry = new RetryerRegistry(retries, "linear");
            final TaskDescriptorFactory taskDescriptorFactory = new TaskDescriptorFactory(retryRegistry, createDefaultTaskOptions());
            final TaskParser parser = new TaskParser(taskDescriptorFactory);
            final List<TaskDescriptor> descriptors = parser.parse("dev.doddle.core.support.tasks.invalid");
            new TaskRegistry(descriptors);
        });
        assertEquals("A duplicate task has been found for: invalidTask. Task names must be unique", exception.getMessage());
    }

}
