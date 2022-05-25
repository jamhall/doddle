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
package dev.doddle.core.engine.time;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IntervalTest {

    @Test
    @DisplayName("it should test the interval")
    void it_should_test_the_interval() {
        assertEquals(1, new Interval(1, MILLISECONDS).toMillis());
        assertEquals(1000, new Interval(1, SECONDS).toMillis());
        assertEquals(60000, new Interval(1, MINUTES).toMillis());
        assertEquals(60000 * 60, new Interval(1, HOURS).toMillis());
        assertEquals(60000 * 60 * 24, new Interval(1, DAYS).toMillis());
    }
}
