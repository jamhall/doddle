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
package dev.doddle.core.engine.circuitbreaker.state;


import dev.doddle.core.engine.circuitbreaker.CircuitBreaker;

import java.util.concurrent.atomic.AtomicInteger;

import static dev.doddle.core.support.Objects.requireNonNull;

public class ClosedState implements CircuitBreakerState {

    private final AtomicInteger  failed;
    private final CircuitBreaker cb;

    /**
     * Create a new closed state
     *
     * @param cb the circuit breaker
     */
    public ClosedState(final CircuitBreaker cb) {
        this.cb = requireNonNull(cb, "circuitBreaker cannot be null");
        this.failed = new AtomicInteger(0);
    }

    @Override
    public String getLabel() {
        return "CLOSED";
    }

    @Override
    public void handleFailed() {
        final Integer maxFailureCount = cb.getMaxFailureCount();
        if (failed.incrementAndGet() >= maxFailureCount) {
            cb.transitionTo(this, new OpenState(cb));
        }
    }

    @Override
    public void handleSuccess() {
        failed.set(0);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

}
