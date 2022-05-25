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
package dev.doddle.core.engine.polling.loop;

import dev.doddle.common.support.NotNull;
import dev.doddle.core.engine.time.Clock;
import dev.doddle.core.engine.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static dev.doddle.core.support.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class DefaultLoopStrategy implements LoopStrategy {
    private static final Logger logger = LoggerFactory.getLogger(DefaultLoopStrategy.class);

    private final Lock          lock      = new ReentrantLock();
    private final AtomicBoolean paused    = new AtomicBoolean();
    private final Condition     condition = lock.newCondition();
    private final Clock         clock;

    public DefaultLoopStrategy(final Clock clock) {
        this.clock = requireNonNull(clock, "ticker cannot be null");
    }

    @Override
    public void doRun(final Runnable runnable) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                lock.lock();
                while (paused.get()) {
                    condition.await();
                }
                runnable.run();
            } catch (InterruptedException ignored) {
                logger.error("sleep interrupted: threadName={}", Thread.currentThread().getName());
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Wait to process the next loop
     *
     * @param interval the time to wait
     */
    @Override
    public void doWait(@NotNull Interval interval) {
        try {
            lock.lock();
            long plannedWakeupTime = clock.millis() + interval.toMillis();
            long timeToSleep = plannedWakeupTime - clock.millis();
            while (timeToSleep > 1L) {

                if (!paused.get()) {
                    logger.debug("Waiting: {} milliseconds", timeToSleep);
                    this.condition.await(timeToSleep, MILLISECONDS);
                }

                timeToSleep = plannedWakeupTime - clock.millis();
            }
        } catch (InterruptedException ignored) {
            final Thread currentThread = Thread.currentThread();
            logger.error("sleep interrupted: threadName={}", currentThread.getName());
            currentThread.interrupt();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isPaused() {
        return this.paused.get();
    }

    @Override
    public void pause() {
        try {
            lock.lock();
            paused.set(true);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void resume() {
        try {
            lock.lock();
            paused.set(false);
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

}
