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
import dev.doddle.core.engine.threadnaming.ThreadNamingStrategy;
import dev.doddle.core.engine.threadnaming.strategies.DefaultThreadNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static dev.doddle.core.support.Objects.requireNonNull;

public class PollingThreadFactory implements ThreadFactory {

    private static final Logger                   logger = LoggerFactory.getLogger(PollingThreadFactory.class);
    private final        ThreadFactory            threadFactory;
    private final        UncaughtExceptionHandler exceptionHandler;
    private final        ThreadNamingStrategy     threadNamingStrategy;

    /**
     * Create a new polling thread factory
     */
    public PollingThreadFactory() {
        this(new DefaultThreadNamingStrategy(), new PollingThreadUncaughtExceptionHandler());
    }

    /**
     * Create a new polling thread factory
     *
     * @param threadNamingStrategy the strategy to use for naming the threads that are created
     */
    public PollingThreadFactory(@NotNull final ThreadNamingStrategy threadNamingStrategy) {
        this(threadNamingStrategy, new PollingThreadUncaughtExceptionHandler());
    }

    /**
     * Create a new job thread factory
     *
     * @param threadNamingStrategy     the strategy to use for naming the threads that are created
     * @param uncaughtExceptionHandler the uncaught exception handler
     */
    public PollingThreadFactory(@NotNull final ThreadNamingStrategy threadNamingStrategy,
                                @NotNull final UncaughtExceptionHandler uncaughtExceptionHandler) {
        logger.debug("Creating new job thread factory");
        this.threadNamingStrategy = requireNonNull(threadNamingStrategy, "threadNamingStrategy cannot be null");
        this.exceptionHandler = requireNonNull(uncaughtExceptionHandler, "uncaughtExceptionHandler cannot be null");
        this.threadFactory = Executors.defaultThreadFactory();
    }

    /**
     * Create a new thread
     *
     * @param runnable the runnable to execute
     * @return the new thread
     */
    @Override
    public Thread newThread(@NotNull final Runnable runnable) {
        requireNonNull(runnable, "runnable cannot be null");
        return createThread(runnable);
    }

    /**
     * Create a new thread
     *
     * @param runnable the runnable to execute
     * @return the new thread
     */
    private Thread createThread(@NotNull final Runnable runnable) {
        final String name = threadNamingStrategy.createName();
        final Thread thread = threadFactory.newThread(runnable);
        thread.setName(name);
        thread.setUncaughtExceptionHandler(exceptionHandler);
        thread.setDaemon(false);
        logger.debug("Created new thread: threadName={}", name);
        return thread;
    }

}
