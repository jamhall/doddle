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
package dev.doddle.storage.common.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class JobProgress {

    private Integer currentValue;
    private Integer maxValue;

    /**
     * Create a new instance
     */
    public JobProgress() {

    }

    /**
     * Create a new instance
     *
     * @param currentValue the current value
     * @param maxValue     the maximum value
     */
    public JobProgress(final Integer currentValue, final Integer maxValue) {
        this.currentValue = requireNonNull(currentValue, "currentValue cannot be null");
        this.maxValue = requireNonNull(maxValue, "maxValue cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        JobProgress progress = (JobProgress) o;

        return new EqualsBuilder().append(currentValue, progress.currentValue).append(maxValue, progress.maxValue).isEquals();
    }

    public Integer getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Integer currentValue) {
        this.currentValue = currentValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Calculate the current percentage
     *
     * @return the calculated percentage
     */
    public int getPercentage() {
        return (int) (this.currentValue * 100.0f / this.maxValue);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(currentValue).append(maxValue).toHashCode();
    }

    @Override
    public String toString() {
        return format("%d/%d (%d%%)", this.currentValue, this.maxValue, this.getPercentage());
    }
}
