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

import org.apache.commons.lang3.builder.ToStringBuilder;

public class JobLoggerMessageEvent {

    private final String    level;
    private final String    message;
    private final Throwable throwable;
    private final Object[]  arguments;

    public JobLoggerMessageEvent(
        final String level,
        final String message) {
        this(level, message, null, null);
    }

    public JobLoggerMessageEvent(final String level,
                                 final String message,
                                 final Object[] arguments,
                                 final Throwable throwable) {
        this.level = level;
        this.message = message;
        this.arguments = arguments;
        this.throwable = throwable;
    }

    public JobLoggerMessageEvent(final String level, final String message, final Object[] arguments) {
        this(level, message, arguments, null);
    }

    public Object[] getArguments() {
        return arguments;
    }

    public String getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("message", message)
            .append("throwable", throwable)
            .append("arguments", arguments)
            .append("level", level)
            .toString();
    }
}
