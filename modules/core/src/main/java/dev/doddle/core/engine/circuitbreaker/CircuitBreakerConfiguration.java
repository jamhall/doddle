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
package dev.doddle.core.engine.circuitbreaker;

import dev.doddle.common.support.NotNull;
import dev.doddle.core.engine.time.Clock;
import dev.doddle.core.engine.time.Interval;
import dev.doddle.core.engine.time.IntervalParser;
import dev.doddle.core.engine.time.ticker.TickerStrategy;
import dev.doddle.core.exceptions.DoddleValidationException;

import static dev.doddle.core.support.Objects.requireNonNull;

public class CircuitBreakerConfiguration {

    private Integer  maxSuccessCount;
    private Integer  maxFailureCount;
    private Interval retryTimeout;
    private Clock    clock;

    public CircuitBreakerConfiguration() {

    }

    /**
     * Create a new circuit breaker configuration
     *
     * @param maxSuccessCount the maximum number of successes before switching to the closed state
     * @param maxFailureCount the maximum number of failures before switching to the open state
     * @param retryTimeout    the breaker will transition to the half-open state if the timeout is exceeded
     * @param clock           the ticker to use for providing milliseconds
     */
    public CircuitBreakerConfiguration(Integer maxSuccessCount,
                                       Integer maxFailureCount,
                                       Interval retryTimeout,
                                       Clock clock) {
        this.maxSuccessCount = maxSuccessCount;
        this.maxFailureCount = maxFailureCount;
        this.retryTimeout = retryTimeout;
        this.clock = clock;
    }

    /**
     * Get the max failure count
     *
     * @return the max failure count
     */
    public Integer maxFailureCount() {
        return maxFailureCount;
    }

    /**
     * Set the max failure count
     *
     * @param maxFailureCount the max failure count
     * @return this
     */
    public CircuitBreakerConfiguration maxFailureCount(Integer maxFailureCount) {
        if (requireNonNull(maxFailureCount, "maxFailureCount cannot be null") <= 0) {
            throw new DoddleValidationException("maxFailureCount must be greater than zero");
        }
        this.maxFailureCount = maxFailureCount;
        return this;
    }

    /**
     * Get the max success count
     *
     * @return the success count
     */
    public Integer maxSuccessCount() {
        return maxSuccessCount;
    }

    /**
     * Set the max success count
     *
     * @param maxSuccessCount the success count
     * @return this
     */
    public CircuitBreakerConfiguration maxSuccessCount(Integer maxSuccessCount) {
        if (requireNonNull(maxSuccessCount, "maxSuccessCount cannot be null") <= 0) {
            throw new DoddleValidationException("maxSuccessCount must be greater than zero");
        }
        this.maxSuccessCount = maxSuccessCount;
        return this;
    }

    /**
     * Get the retry timeout
     *
     * @return the retry timeout
     */
    public Interval retryTimeout() {
        return retryTimeout;
    }

    /**
     * Set the retry timeout
     *
     * @param retryTimeout the retry timeout
     * @return this
     */
    public CircuitBreakerConfiguration retryTimeout(@NotNull String retryTimeout) {
        final IntervalParser parser = new IntervalParser();
        this.retryTimeout = parser.parse(requireNonNull(retryTimeout, "retryTimeout cannot be null"));
        return this;
    }

    /**
     * Get the ticker
     *
     * @return the ticker
     */
    public Clock ticker() {
        return clock;
    }

    /**
     * Set the ticker
     *
     * @param tickerStrategy the ticker strategy
     * @return this
     */
    public CircuitBreakerConfiguration ticker(@NotNull TickerStrategy tickerStrategy) {
        this.clock = new Clock(requireNonNull(tickerStrategy, "tickerStrategy cannot be null"));
        return this;
    }

}
