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
package dev.doddle.core.engine.progress;

import static java.lang.String.format;

public class JobProgressEvent {

    private final int maxValue;
    private final int currentValue;
    private final int percentage;

    /**
     * Create a new job progress event
     *
     * @param maxValue     the maximum value
     * @param currentValue the current value
     * @param percentage   the current percentage complete
     */
    public JobProgressEvent(final int maxValue, final int currentValue, final int percentage) {
        this.maxValue = maxValue;
        this.currentValue = currentValue;
        this.percentage = percentage;
    }

    /**
     * Get the current value
     *
     * @return the current value
     */
    public int getCurrentValue() {
        return currentValue;
    }

    /**
     * Get the max value
     *
     * @return the max value
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * Get the current percentage
     *
     * @return the current percentage
     */
    public int getPercentage() {
        return percentage;
    }

    @Override
    public String toString() {
        return format("%d/%d (%d%%)", this.currentValue, this.maxValue, this.percentage);
    }

}
