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
package dev.doddle.core.engine.retry;

import dev.doddle.core.engine.retry.strategies.ConstantRetryStrategy;
import dev.doddle.core.engine.retry.strategies.LinearRetryStrategy;
import dev.doddle.core.engine.retry.strategies.SquaredRetryStrategy;
import dev.doddle.core.engine.time.Interval;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RetryerStrategyTest {

    @DisplayName("it should calculate the next retry for the constant strategy")
    @Test
    void it_should_calculate_the_next_retry_for_the_constant_strategy() {
        final RetryStrategy strategy = new ConstantRetryStrategy();
        assertEquals(new Interval(10, SECONDS), strategy.apply(4));
        assertEquals(new Interval(10, SECONDS), strategy.apply(10));
    }

    @DisplayName("it should calculate the next retry for the linear strategy")
    @Test
    void it_should_calculate_the_next_retry_for_the_linear_strategy() {
        final RetryStrategy strategy = new LinearRetryStrategy();
        assertEquals(new Interval(4, SECONDS), strategy.apply(4));
        assertEquals(new Interval(10, SECONDS), strategy.apply(10));
    }

    @DisplayName("it should calculate the next retry for the squared strategy")
    @Test
    void it_should_calculate_the_next_retry_for_the_squared_strategy() {
        final RetryStrategy strategy = new SquaredRetryStrategy();
        assertEquals(new Interval(16, SECONDS), strategy.apply(4));
        assertEquals(new Interval(100, SECONDS), strategy.apply(10));
    }

}
