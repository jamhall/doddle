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

import dev.doddle.core.engine.time.ticker.strategies.FakeTickerStrategy;
import dev.doddle.core.exceptions.DoddleValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static dev.doddle.core.engine.time.Interval.createInterval;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

class CircuitBreakerConfigurationTest {

    @DisplayName("it should successfully build a circuit breaker configuration")
    @Test
    void it_should_successfully_build_a_logger_configuration() {
        final CircuitBreakerConfiguration configuration = new CircuitBreakerConfiguration();
        configuration.maxFailureCount(10);
        configuration.maxSuccessCount(15);
        configuration.retryTimeout("30s");
        configuration.ticker(new FakeTickerStrategy());
        assertEquals(10, configuration.maxFailureCount());
        assertEquals(15, configuration.maxSuccessCount());
        assertEquals(createInterval(30, SECONDS), configuration.retryTimeout());
        assertNotNull(configuration.ticker());
    }

    @DisplayName("it should throw an exception because invalid max failure count")
    @Test
    void it_should_throw_an_exception_because_invalid_max_failure_count() {
        final CircuitBreakerConfiguration configuration = new CircuitBreakerConfiguration();
        final NullPointerException exception1 = assertThrows(NullPointerException.class, () -> configuration.maxFailureCount(null));
        final DoddleValidationException exception2 = assertThrows(DoddleValidationException.class, () -> configuration.maxFailureCount(-1));
        assertEquals("maxFailureCount cannot be null", exception1.getMessage());
        assertEquals("maxFailureCount must be greater than zero", exception2.getMessage());
    }

    @DisplayName("it should throw an exception because invalid max success count")
    @Test
    void it_should_throw_an_exception_because_invalid_max_success_count() {
        final CircuitBreakerConfiguration configuration = new CircuitBreakerConfiguration();
        final NullPointerException exception1 = assertThrows(NullPointerException.class, () -> configuration.maxSuccessCount(null));
        final DoddleValidationException exception2 = assertThrows(DoddleValidationException.class, () -> configuration.maxSuccessCount(-1));
        assertEquals("maxSuccessCount cannot be null", exception1.getMessage());
        assertEquals("maxSuccessCount must be greater than zero", exception2.getMessage());
    }

    @DisplayName("it should throw an exception because invalid retry timeout")
    @Test
    void it_should_throw_an_exception_because_invalid_retry_timeout() {
        final CircuitBreakerConfiguration configuration = new CircuitBreakerConfiguration();
        final NullPointerException exception1 = assertThrows(NullPointerException.class, () -> configuration.retryTimeout(null));
        final DoddleValidationException exception2 = assertThrows(DoddleValidationException.class, () -> configuration.retryTimeout("-1m"));
        final DoddleValidationException exception3 = assertThrows(DoddleValidationException.class, () -> configuration.retryTimeout("0m"));
        assertEquals("retryTimeout cannot be null", exception1.getMessage());
        assertEquals("Could not parse given interval", exception2.getMessage());
        assertEquals("Interval must be greater than zero", exception3.getMessage());
    }
}
