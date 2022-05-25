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

import dev.doddle.common.support.NotNull;
import dev.doddle.core.engine.retry.RetryStrategy;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static java.time.LocalDateTime.now;

public class JobRetryer {

    /**
     * Calculate the next retry time
     *
     * @param retries  the current number of attempts
     * @param strategy the strategy to use for calculate the next retry time
     * @return the next retry time
     */
    public LocalDateTime getNextRetryAt(@NotNull final int retries,
                                        @NotNull final RetryStrategy strategy) {
        final long delta = TimeUnit.MILLISECONDS.toNanos(strategy.apply(retries).toMillis());
        return now().plusNanos(delta);
    }

    /**
     * Can the job be retried?
     *
     * @param retries    the current number of attempts
     * @param maxRetries the maximum number of attempts allowed
     * @return true if it can be retried, otherwise false
     */
    public boolean isRetryable(@NotNull final int retries, @NotNull final int maxRetries) {
        if (retries == maxRetries) {
            return false;
        }
        return retries < maxRetries;
    }


}
