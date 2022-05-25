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
package dev.doddle.core.engine.polling;

import dev.doddle.common.support.NotNull;
import dev.doddle.core.engine.JobRunner;
import dev.doddle.core.engine.polling.loop.PollingLoop;
import dev.doddle.core.engine.threadnaming.ThreadNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.currentThread;
import static dev.doddle.core.support.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.IntStream.range;

public class PollingExecutionPool {

    private static final Logger               logger  = LoggerFactory.getLogger(PollingExecutionPool.class);
    private final        ThreadNamingStrategy threadNamingStrategy;
    private final        ExecutorService      executor;
    private final        JobRunner            runner;
    private final        Integer              concurrency;
    private final        PollingLoop          loop;
    private final        AtomicBoolean        started = new AtomicBoolean(false);

    public PollingExecutionPool(final ThreadNamingStrategy threadNamingStrategy,
                                final Integer concurrency,
                                final PollingLoop loop,
                                final JobRunner runner) {
        this.threadNamingStrategy = requireNonNull(threadNamingStrategy, "threadNamingStrategy cannot be null");
        this.concurrency = requireNonNull(concurrency, "concurrency cannot be null");
        this.loop = requireNonNull(loop, "loop cannot be null");
        this.runner = requireNonNull(runner, "runner cannot be null");
        this.executor = createDefaultExecutionPool();
    }

    /**
     * Create the default executor pool
     *
     * @return the executor service
     */
    public ExecutorService createDefaultExecutionPool() {
        return new ThreadPoolExecutor(concurrency, concurrency,
            0L, MILLISECONDS,
            new ArrayBlockingQueue<>(concurrency),
            new PollingThreadFactory(threadNamingStrategy));
    }

    /**
     * Get the number of threads assigned to this execution pool
     *
     * @return the number of threads
     */
    public Integer getConcurrency() {
        return concurrency;
    }

    /**
     * Is the executor paused?
     *
     * @return true if paused, otherwise false
     */
    public boolean isPaused() {
        return loop.isPaused();
    }

    /**
     * Is the executor terminated?
     *
     * @return if it is terminated
     */
    public boolean isTerminated() {
        return executor.isTerminated();
    }

    /**
     * Pause the executor
     */
    public void pause() {
        loop.pause();
    }

    /**
     * Resume the executor
     */
    public void resume() {
        loop.resume();
    }

    /**
     * Shutdown the executor
     */
    public void shutdown() {
        executor.shutdownNow();
    }

    /**
     * Shutdown the executor
     *
     * @param timeout the time to wait until shutting it down
     * @return true if shutdown was successful, otherwise false
     */
    public boolean shutdown(@NotNull Duration timeout) {
        try {
            requireNonNull(timeout, "Timeout cannot be null");
            return executor.awaitTermination(timeout.getSeconds(), SECONDS);
        } catch (InterruptedException exception) {
            logger.error("Caught interruption exception when awaiting termination of the job executor: {}", exception.getMessage());
            currentThread().interrupt();
            return false;
        }
    }

    /**
     * Shutdown the executor
     */
    public void shutdownNow() {
        executor.shutdownNow();
    }

    /**
     * Start the executor
     */
    public void start() {
        if (!started.get()) {
            logger.debug("Starting job loop");
            range(0, concurrency).forEachOrdered(n -> {
                executor.execute(() -> loop.start(runner));
            });
            started.set(true);
        }
        loop.resume();
    }
}
