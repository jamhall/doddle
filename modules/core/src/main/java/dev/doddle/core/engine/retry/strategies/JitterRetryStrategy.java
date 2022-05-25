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
package dev.doddle.core.engine.retry.strategies;

import dev.doddle.core.engine.retry.RetryStrategy;
import dev.doddle.core.engine.time.Interval;

import static java.lang.Math.random;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Delay by a base amount plus some jitter
 * e.g. 1→32, 2→61, 3→135
 */
public class JitterRetryStrategy implements RetryStrategy {

    @Override
    public Interval apply(final Integer retries) {
        final int max = 30;
        final int min = 1;
        final int range = max - min + 1;
        final int rand = (int) (random() * range) + min;
        final int seconds = (int) ((retries * 4) + 15 + ((long) rand * (retries + 1)));
        return new Interval(seconds, SECONDS);
    }
}
