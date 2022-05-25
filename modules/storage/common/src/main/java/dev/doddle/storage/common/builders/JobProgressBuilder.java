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
package dev.doddle.storage.common.builders;

import dev.doddle.storage.common.domain.JobProgress;

public final class JobProgressBuilder {

    private Integer currentValue;
    private Integer maxValue;

    private JobProgressBuilder() {
    }

    public static JobProgressBuilder newBuilder() {
        return new JobProgressBuilder();
    }

    public JobProgress build() {
        JobProgress jobProgress = new JobProgress();
        jobProgress.setCurrentValue(currentValue);
        jobProgress.setMaxValue(maxValue);
        return jobProgress;
    }

    public JobProgressBuilder currentValue(Integer currentValue) {
        this.currentValue = currentValue;
        return this;
    }

    public JobProgressBuilder maxValue(Integer maxValue) {
        this.maxValue = maxValue;
        return this;
    }
}