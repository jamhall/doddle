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

import dev.doddle.common.support.NotNull;
import dev.doddle.common.support.Nullable;

import static dev.doddle.core.engine.logger.JobLoggerLevel.*;
import static dev.doddle.core.support.Objects.requireNonNull;

public class JobLogger {

    private final JobLoggerConsumer consumer;
    private final JobLoggerLevel    level;

    public JobLogger(
        final JobLoggerLevel level,
        final JobLoggerConsumer consumer) {
        this.level = requireNonNull(level, "level cannot be null");
        this.consumer = requireNonNull(consumer, "consumer cannot be null");
    }

    public void debug(@NotNull final String message, @Nullable final Object... args) {
        apply(DEBUG, message, args);
    }

    public void error(@NotNull final String message, @Nullable final Object... args) {
        apply(ERROR, message, args);
    }

    public void info(@NotNull final String message, @Nullable final Object... args) {
        apply(INFO, message, args);
    }

    private void apply(@NotNull JobLoggerLevel level,
                       @NotNull String message,
                       @Nullable Object... arguments) {
        if (level.isLoggable(this.level)) {
            final JobLoggerMessageFormatter formatter = new JobLoggerMessageFormatter();
            if (arguments == null) {
                this.consumer.accept(formatter.format(level.getLabel(), message));
            } else {
                if (arguments.length > 0) {
                    this.consumer.accept(formatter.format(level.getLabel(), message, arguments));
                } else {
                    this.consumer.accept(formatter.format(level.getLabel(), message));
                }
            }
        }
    }

}
