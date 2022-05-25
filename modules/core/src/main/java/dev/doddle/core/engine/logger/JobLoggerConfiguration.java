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
package dev.doddle.core.engine.logger;

import dev.doddle.core.exceptions.DoddleValidationException;

import static dev.doddle.core.support.Objects.requireNonNull;

public class JobLoggerConfiguration {

    private JobLoggerLevel level;
    private Integer        maxLines;

    public JobLoggerConfiguration() {

    }

    public JobLoggerConfiguration(JobLoggerLevel level, Integer maxLines) {
        level(level);
        maxLines(maxLines);
    }

    public JobLoggerConfiguration level(JobLoggerLevel level) {
        this.level = requireNonNull(level, "level cannot be null");
        return this;
    }

    public JobLoggerLevel level() {
        return this.level;
    }

    public JobLoggerConfiguration maxLines(Integer maxSize) {
        if (requireNonNull(maxSize, "maxLines cannot be null") <= 0) {
            throw new DoddleValidationException("maxLines must be greater than zero");
        }
        this.maxLines = maxSize;
        return this;
    }

    public Integer maxLines() {
        return this.maxLines;
    }

}
