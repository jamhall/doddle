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
import dev.doddle.core.engine.JobRunner;
import dev.doddle.core.engine.time.Interval;

import static dev.doddle.core.support.Objects.requireNonNull;

public class PollingLoop {

    private final LoopStrategy loopStrategy;
    private final Interval     interval;

    /**
     * Create a new job loop
     *
     * @param loopStrategy the loop strategy to use
     * @param interval     How often to poll for new jobs
     */
    public PollingLoop(@NotNull final LoopStrategy loopStrategy,
                       @NotNull final Interval interval) {
        this.loopStrategy = requireNonNull(loopStrategy, "loopStrategy cannot be null");
        this.interval = requireNonNull(interval, "interval cannot be null");
    }

    /**
     * Is the loop paused?
     *
     * @return if the loop is paused or not
     */
    public boolean isPaused() {
        return this.loopStrategy.isPaused();
    }

    /**
     * Pause the processing cycle
     */
    public void pause() {
        this.loopStrategy.pause();
    }

    /**
     * Resume the processing cycle
     */
    public void resume() {
        this.loopStrategy.resume();
    }

    /**
     * Start the loop
     *
     * @param runner the job runner that will execute the job
     */
    public void start(@NotNull JobRunner runner) {
        this.loopStrategy.doRun(() -> {
            runner.execute(status -> {
                switch (status) {
                    case SKIPPED -> loopStrategy.doWait(interval);
                    case PROCESSED -> loopStrategy.doWait(interval);
                }
            });
        });
    }

    /**
     * Resume the processing cycle
     */
    public void unpause() {
        this.resume();
    }

}
