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
import dev.doddle.core.engine.circuitbreaker.state.CircuitBreakerState;
import dev.doddle.core.engine.circuitbreaker.state.ClosedState;
import dev.doddle.core.engine.time.Clock;
import dev.doddle.core.engine.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static dev.doddle.core.support.Objects.requireNonNull;


public class CircuitBreaker {

    private static final Logger                               logger = LoggerFactory.getLogger(CircuitBreaker.class);
    /**
     * Thread safe reference to the state
     */
    private final        AtomicReference<CircuitBreakerState> state;
    private final        Integer                              maxSuccessCount;
    private final        Integer                              maxFailureCount;
    private final        Interval                             retryTimeout;
    private final        Clock                                clock;

    /**
     * Create a new circuit breaker
     *
     * @param maxSuccessCount the maximum number of successes before switching to the closed state
     * @param maxFailureCount the maximum number of failures before switching to the open state
     * @param retryTimeout    the breaker will transition to the half-open state if the timeout is exceeded
     * @param clock           the clock to use for providing milliseconds and date calculations
     */
    public CircuitBreaker(
        @NotNull final Integer maxSuccessCount,
        @NotNull final Integer maxFailureCount,
        @NotNull final Interval retryTimeout,
        @NotNull final Clock clock) {
        this.maxSuccessCount = requireNonNull(maxSuccessCount, "maxSuccessCount cannot be null");
        this.maxFailureCount = requireNonNull(maxFailureCount, "maxFailureCount cannot be null");
        this.retryTimeout = requireNonNull(retryTimeout, "retryTimeout cannot be null");
        this.clock = requireNonNull(clock, "clock cannot be null");
        this.state = new AtomicReference<>(
            new ClosedState(this)
        );
        logger.debug("Initialising initial state to: {}", state.get().getLabel());
    }

    /**
     * Process the supplier
     *
     * @param supplier the supplier to process
     * @param callback the callback
     */
    public <T> void apply(final Supplier<T> supplier, final CircuitBreakerResultCallback<T> callback) {
        final CircuitBreakerState state = this.state.get();
        try {
            final T result = supplier.get();
            state.handleSuccess();
            callback.onSuccess(result);
        } catch (Exception exception) {
            state.handleFailed();
            callback.onError(exception);
        }
    }

    /**
     * Get the max failure count
     *
     * @return the max failure count
     */
    public Integer getMaxFailureCount() {
        return maxFailureCount;
    }

    /**
     * Get the max success count
     *
     * @return the max success count
     */
    public Integer getMaxSuccessCount() {
        return maxSuccessCount;
    }

    /**
     * Get the retry timeout
     *
     * @return the retry timeout
     */
    public Interval getRetryTimeout() {
        return retryTimeout;
    }

    /**
     * Get the current state
     *
     * @return the current state
     */
    public AtomicReference<CircuitBreakerState> getState() {
        return state;
    }

    /**
     * Get the ticker
     *
     * @return the ticker
     */
    public Clock getTicker() {
        return clock;
    }

    /**
     * Check if the circuit breaker is allowing requests
     *
     * @return true if half open or closed
     */
    public boolean isAvailable() {
        return state.get().isAvailable();
    }

    /**
     * Check if the current state is equal to the state given
     *
     * @param stateClass the state to check
     * @return true if it matches the current state, otherwise false
     */
    public boolean isState(final Class<? extends CircuitBreakerState> stateClass) {
        return this.state.get().getClass() == stateClass;
    }

    /**
     * Transition from the current state to a new state
     * The state will only be updated if it has changed
     *
     * @param current the current state
     * @param to      the state to transition to
     */
    public void transitionTo(final CircuitBreakerState current, final CircuitBreakerState to) {
        logger.debug("Transitioning from state: {} to state: {}", current.getLabel(), to.getLabel());
        state.compareAndSet(current, to);
    }

    /**
     * Transition the current state to a new state
     *
     * @param to the state to transition to
     */
    public void transitionTo(final CircuitBreakerState to) {
        logger.debug("Transitioning state to: {}", to.getLabel());
        state.set(to);
    }
}
