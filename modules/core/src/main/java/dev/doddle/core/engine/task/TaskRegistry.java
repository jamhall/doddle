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

import dev.doddle.common.support.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static dev.doddle.core.support.Objects.requireNonNull;

public class TaskRegistry {

    private static final Logger               logger = LoggerFactory.getLogger(TaskRegistry.class);
    private final        List<TaskDescriptor> tasks;

    /**
     * Create a new task registry that holds a reference to the parsed tasks
     *
     * @param tasks the task descriptors
     */
    public TaskRegistry(@NotNull final List<TaskDescriptor> tasks) {
        this.tasks = requireNonNull(tasks, "tasks cannot be null");
        logger.debug("Found {} tasks", this.tasks.size());
    }

    /**
     * Get the number of registered tasks
     *
     * @return the number of tasks
     */
    public int countAll() {
        return tasks.size();
    }

    /**
     * Get all the registered tasks
     *
     * @return a list of tasks
     */
    public List<TaskDescriptor> getAll() {
        return tasks;
    }

    /**
     * Get a task for a given name
     *
     * @param name the name of the task
     * @return the task if found otherwise return null
     */
    public TaskDescriptor getForName(@NotNull final String name) {
        return tasks.stream()
            .filter(task -> task.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

}
