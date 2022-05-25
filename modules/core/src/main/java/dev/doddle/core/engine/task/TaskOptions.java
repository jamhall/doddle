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
package dev.doddle.core.engine.task;

import dev.doddle.common.support.NotNull;
import dev.doddle.core.engine.time.Interval;
import dev.doddle.core.engine.time.IntervalParser;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static dev.doddle.core.support.Objects.requireNonNull;

public class TaskOptions {

    private Integer maxRetries;
    private String  retryStrategy;
    private Long    timeout;

    public static TaskOptions createDefaultTaskOptions() {
        final TaskOptions options = new TaskOptions();
        options.maxRetries(20);
        options.retryStrategy("linear");
        options.timeout("1h");
        return options;
    }

    public TaskOptions maxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    public Integer maxRetries() {
        return maxRetries;
    }

    public String retryStrategy() {
        return retryStrategy;
    }

    public TaskOptions retryStrategy(String retryStrategy) {
        this.retryStrategy = retryStrategy;
        return this;
    }

    public Long timeout() {
        return timeout;
    }

    public TaskOptions timeout(@NotNull final String timeout) {
        final IntervalParser parser = new IntervalParser();
        final Interval parsed = parser.parse(requireNonNull(timeout, "timeout cannot be null"), "s");
        this.timeout = parsed.toMillis();
        return this;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("maxRetries", maxRetries)
            .append("retryStrategy", retryStrategy)
            .append("timeout", timeout)
            .toString();
    }
}
