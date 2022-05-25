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

import dev.doddle.common.support.Nullable;
import dev.doddle.core.exceptions.TaskExecutionException;

import static java.lang.String.format;
import static dev.doddle.core.support.Objects.requireNonNull;

public class JobProgress {

    private final int                 maxValue;
    private final JobProgressConsumer consumer;
    private       int                 currentValue = 0;

    /**
     * Create a new job progress
     *
     * @param maxValue the maximum value
     * @param consumer the event consumer
     */
    public JobProgress(final int maxValue, @Nullable final JobProgressConsumer consumer) {
        if (maxValue <= 0) {
            throw new TaskExecutionException("progress needs to be initialised with a value greater than zero");
        }
        this.maxValue = maxValue;
        this.consumer = requireNonNull(consumer, "consumer cannot be null");
    }

    /**
     * Create a new job progress
     *
     * @param maxValue the maximum value
     */
    public JobProgress(final int maxValue) {
        this(maxValue, new JobProgressNoopConsumer());
    }

    /**
     * Advance the progress by a given amount
     *
     * @param amount the given amount
     * @throws TaskExecutionException if the given amount added to the current value exceeds the maximum value
     */
    public void advance(final int amount) {
        final int newValue = this.currentValue + amount;
        if ((newValue) > maxValue) {
            throw new TaskExecutionException("advancing progress by amount that exceeds the maximum value");
        }
        this.set(newValue);
    }

    /**
     * Advance the progress by one
     */
    public void advance() {
        this.set(this.currentValue + 1);
    }

    /**
     * Get the current value
     *
     * @return the current value
     */
    public int currentValue() {
        return this.currentValue;
    }

    /**
     * Get the maximum value
     *
     * @return the maximum value
     */
    public int maxValue() {
        return this.maxValue;
    }

    /**
     * Calculate the current percentage
     *
     * @return the calculate percentage
     */
    public int percentage() {
        return (int) (this.currentValue * 100.0f / this.maxValue);
    }

    /**
     * Reset the progress to zero
     */
    public void reset() {
        this.set(0);
    }

    /**
     * Set the progress to a given value
     *
     * @param value the given value
     * @throws TaskExecutionException if the value is not greater or equal to zero
     * @throws TaskExecutionException if the value is greater than the maximum value
     */
    public void set(final int value) {
        if (value < 0) {
            throw new TaskExecutionException("value must be greater than or equal to zero");
        } else if (value > this.maxValue) {
            throw new TaskExecutionException("value cannot be greater than the max value");
        }
        this.currentValue = value;
        this.consumer.accept(new JobProgressEvent(maxValue, currentValue, percentage()));
    }

    @Override
    public String toString() {
        return format("%d/%d (%d%%)", currentValue, this.maxValue, this.percentage());
    }
}
