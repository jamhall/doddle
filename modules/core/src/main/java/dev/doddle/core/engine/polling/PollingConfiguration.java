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
import dev.doddle.core.engine.time.Interval;
import dev.doddle.core.engine.time.IntervalParser;
import dev.doddle.core.exceptions.DoddleValidationException;

import static dev.doddle.core.engine.time.IntervalParser.createIntervalParser;
import static dev.doddle.core.support.Objects.requireNonNull;

public class PollingConfiguration {

    private Integer              concurrency;
    private ThreadNamingStrategy threadNaming;
    private Interval             interval;

    /**
     * How many threads to use to poll for new jobs that are ready to be processed
     *
     * @param concurrency the number of threads
     * @return this
     */
    public PollingConfiguration concurrency(@NotNull final int concurrency) {
        if (concurrency < 0) {
            throw new DoddleValidationException("Concurrency cannot be less than zero");
        }
        this.concurrency = concurrency;
        return this;
    }

    /**
     * Get the number of threads to be used for polling
     *
     * @return the number of threads
     */
    public Integer concurrency() {
        return concurrency;
    }

    /**
     * How often the system should poll for jobs ready to be processed
     *
     * @param period the interval period
     */
    public PollingConfiguration interval(@NotNull String period) {
        final IntervalParser parser = createIntervalParser();
        return interval(parser.parse(period));
    }

    /**
     * How often the system should poll for jobs ready to be processed
     *
     * @param interval the polling interval
     * @return this
     */
    public PollingConfiguration interval(@NotNull Interval interval) {
        this.interval = requireNonNull(interval, "interval cannot be null");
        return this;
    }

    /**
     * Get the interval for how often to poll for new jobs
     *
     * @return the polling interval
     */
    public Interval interval() {
        return interval;
    }

    /**
     * Get the naming strategy to be used for naming the worker threads
     *
     * @return the naming strategy
     */
    public ThreadNamingStrategy threadNaming() {
        return threadNaming;
    }

    /**
     * The naming strategy to use for naming the worker threads
     *
     * @param threadNamingStrategy the strategy to use for naming the threads
     * @return this
     */
    public PollingConfiguration threadNaming(@NotNull final ThreadNamingStrategy threadNamingStrategy) {
        this.threadNaming = requireNonNull(threadNamingStrategy, "threadNamingStrategy cannot be null");
        return this;
    }
}
