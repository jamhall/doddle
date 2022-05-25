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
package dev.doddle.core.support.tasks.valid;

import dev.doddle.core.engine.JobExecutionContext;
import dev.doddle.core.engine.progress.JobProgress;
import dev.doddle.core.engine.task.Task;
import dev.doddle.core.exceptions.TaskExecutionException;

public class DefaultTasks {

    private final CounterService counterService;

    public DefaultTasks(CounterService counterService) {
        this.counterService = counterService;
    }

    @Task
    public void catchRuntime() {

        throw new RuntimeException("invalid");
    }

    @Task(description = "Clears the cache for a server defined in the job environment")
    public void clearCache(JobExecutionContext context) {
        final String server = (String) context.environment("server");
        if (server == null) {
            throw new TaskExecutionException("Could not find server variable");
        }
    }

    @Task(name = "decrement")
    public void decrement(JobExecutionContext context) {
        counterService.decrementAndGet();
    }

    @Task(name = "failing")
    public void failing() {
        throw new TaskExecutionException("I failed for some reason");
    }

    @Task(name = "increment")
    public void increment(JobExecutionContext context) {
        if (counterService.getCurrentValue() == 5) {
            throw new TaskExecutionException("Invalid value");
        }
        counterService.incrementAndGet();
    }

    @Task(name = "sayHello",
        description = "This task will say hello"
    )
    public void processHelloTask(JobExecutionContext context) {
    }

    @Task(name = "processOrders")
    public void processOrders(JobExecutionContext context) {
    }

    @Task(name = "sendEmail")
    public void sendEmail(JobExecutionContext context) {

    }

    @Task(name = "sendNewsletter")
    public void sendNewsletter(JobExecutionContext context) {
        final JobProgress progress = context.progress(100);
        progress.advance();
    }

    @Task
    public void taskWithNoName(JobExecutionContext context) {
    }

    @Task(name = "timeout")
    public void timeout(JobExecutionContext context) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }


}
