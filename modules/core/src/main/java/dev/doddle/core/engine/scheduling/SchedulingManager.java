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
package dev.doddle.core.engine.scheduling;

import dev.doddle.core.engine.scheduling.commands.SchedulerCommand;
import dev.doddle.core.engine.threadnaming.ThreadNamingStrategy;
import dev.doddle.core.engine.threadnaming.strategies.DefaultThreadNamingStrategy;
import dev.doddle.core.engine.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static dev.doddle.core.support.Objects.requireNonNull;
import static java.util.concurrent.Executors.defaultThreadFactory;
import static java.util.concurrent.Executors.unconfigurableScheduledExecutorService;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class SchedulingManager {
    private final static Logger                   logger = LoggerFactory.getLogger(SchedulingManager.class);
    private final        ScheduledExecutorService executor;
    private final        Interval                 initialInterval;
    private final        Interval                 interval;
    private final        ThreadNamingStrategy     threadNamingStrategy;
    private final        List<SchedulerCommand>   commands;
    private              ScheduledFuture<?>       future;

    public SchedulingManager(final Interval initialInterval,
                             final Interval interval,
                             final ThreadNamingStrategy threadNamingStrategy,
                             final List<SchedulerCommand> commands) {
        this.initialInterval = requireNonNull(initialInterval, "initialInterval cannot be null");
        this.interval = requireNonNull(interval, "interval cannot be null");
        this.threadNamingStrategy = requireNonNull(threadNamingStrategy, "threadNamingStrategy cannot be null");
        this.commands = requireNonNull(commands, "commands cannot be null");
        this.executor = createExecutor();
    }

    public SchedulingManager(final Interval initialInterval,
                             final Interval interval) {
        this(initialInterval, interval, new DefaultThreadNamingStrategy("doddle-scheduling-thread-%d"), new ArrayList<>());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void join() throws InterruptedException {
        this.executor.awaitTermination(Long.MAX_VALUE, MILLISECONDS);
    }

    public void start() {
        synchronized (this) {
            if (this.future == null || this.future.isDone()) {
                this.future = this.schedule();
                logger.info("SchedulingManager started");
            }
        }
    }

    public void stop() {
        synchronized (this) {
            this.future.cancel(false);
            this.executor.shutdownNow();
            logger.info("SchedulingManager stopped");
        }
    }

    private ScheduledExecutorService createExecutor() {
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        // remove the task from scheduler on cancel
        executor.setRemoveOnCancelPolicy(true);
        executor.setThreadFactory(this::createThread);
        return unconfigurableScheduledExecutorService(executor);
    }

    private Thread createThread(Runnable runnable) {
        final Thread thread = defaultThreadFactory().newThread(runnable);
        thread.setDaemon(true);
        thread.setName(createThreadName());
        return thread;
    }

    private String createThreadName() {
        return this.threadNamingStrategy.createName();
    }

    private Runnable execute() {
        return () -> {
            logger.debug("Executing commands");
            for (final SchedulerCommand command : this.commands) {
                command.execute();
            }
        };
    }

    private ScheduledFuture<?> schedule() {
        return this.executor.scheduleAtFixedRate(
            execute(),
            initialInterval.toMillis(),
            interval.toMillis(),
            MILLISECONDS
        );
    }
}
