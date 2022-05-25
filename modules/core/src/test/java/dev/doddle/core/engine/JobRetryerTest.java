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
package dev.doddle.core.engine;

import dev.doddle.core.engine.retry.strategies.LinearRetryStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobRetryerTest {

    @DisplayName("it should check if retryable")
    @Test
    void it_should_check_if_retryable() {
        final JobRetryer retryer = new JobRetryer();
        assertTrue(retryer.isRetryable(5, 20));
        assertTrue(retryer.isRetryable(19, 20));
        assertFalse(retryer.isRetryable(20, 20));
        assertFalse(retryer.isRetryable(21, 20));
    }

    @DisplayName("it should get the next retry time")
    @Test
    void it_should_get_the_next_retry_time() {
        final JobRetryer retryer = new JobRetryer();
        assertTrue(retryer.getNextRetryAt(10, new LinearRetryStrategy()).isAfter(now()));
    }
}
