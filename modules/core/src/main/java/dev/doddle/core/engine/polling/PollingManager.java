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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static dev.doddle.core.support.Objects.requireNonNull;

public class PollingManager {

    private static final Logger               logger = LoggerFactory.getLogger(PollingManager.class);
    private final        PollingExecutionPool pool;

    /**
     * Create a new instance of the polling manager
     *
     * @param pool the polling execution pool
     */
    public PollingManager(@NotNull final PollingExecutionPool pool) {
        this.pool = requireNonNull(pool, "pool cannot be null");
    }

    /**
     * Is the processing currently paused?
     *
     * @return true if paused, otherwise false
     */
    public boolean isPaused() {
        return this.pool.isPaused();
    }

    /**
     * Has the poller started?
     *
     * @return if it has started or not
     */
    public boolean isStarted() {
        return true;
    }

    /**
     * Pause the poller
     */
    public void pause() {
        logger.info("Pausing doddle poller");
        this.pool.pause();
    }

    /**
     * Resume the poller
     */
    public void resume() {
        logger.info("Resuming doddle poller");
        this.pool.resume();
    }

    /**
     * Shutdown the poller with a given timeout
     *
     * @param timeout the time to wait
     * @return if shutdown was successful or not
     */
    public boolean shutdown(@NotNull Duration timeout) {
        logger.info("Shutting down doddle poller with a timeout of {}", timeout);
        return this.pool.shutdown(timeout);
    }

    /**
     * Immediately shutdown the poller
     */
    public void shutdownNow() {
        this.pool.shutdownNow();
    }

    /**
     * Start the poller
     */
    public void start() {
        logger.info("Starting doddle poller with {} threads", this.pool.getConcurrency());
        this.pool.start();
    }

}
