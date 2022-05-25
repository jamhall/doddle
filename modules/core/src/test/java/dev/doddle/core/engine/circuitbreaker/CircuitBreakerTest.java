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

import dev.doddle.core.engine.circuitbreaker.state.ClosedState;
import dev.doddle.core.engine.circuitbreaker.state.OpenState;
import dev.doddle.core.engine.time.Clock;
import dev.doddle.core.engine.time.Interval;
import dev.doddle.core.engine.time.ticker.strategies.FakeTickerStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class CircuitBreakerTest {

    private CircuitBreaker cb;

    @DisplayName("it should start in the closed state by default")
    @Test
    public void it_should_start_in_the_closed_state_by_default() {
        assertTrue(cb.isState(ClosedState.class));
    }

    @DisplayName("it should successfully process a supplier")
    @Test
    public void it_should_successfully_process_a_supplier() {
        final Supplier<Boolean> supplier = () -> true;
        cb.apply(supplier, new CircuitBreakerResultCallback<>() {
            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onSuccess(Boolean result) {
                assertTrue(result);
            }
        });
        assertTrue(cb.isState(ClosedState.class));
    }

    @DisplayName("it should transition to open state and then half open state")
    @Test
    public void it_should_transition_to_open_state_and_then_half_open_state() {
        final Supplier<Boolean> supplier = () -> {
            throw new RuntimeException("Error!");
        };
        final CircuitBreakerResultCallback<Boolean> callback = new CircuitBreakerResultCallback<>() {
        };
        cb.apply(supplier, callback);
        cb.apply(supplier, callback);
        cb.apply(supplier, callback);
        assertTrue(cb.isState(OpenState.class));
    }

    @BeforeEach
    public void setUp() {
        final Clock clock = new Clock(new FakeTickerStrategy());
        this.cb = new CircuitBreaker(2, 3, new Interval(1, SECONDS), clock);
    }

}
