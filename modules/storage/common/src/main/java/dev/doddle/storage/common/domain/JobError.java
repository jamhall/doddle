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

import org.apache.commons.lang3.builder.ToStringBuilder;

import static dev.doddle.common.support.StringUtils.getStackTraceAsString;

public class JobError {

    private String message;
    private String throwable;
    private String stackTrace;

    public JobError() {

    }


    public JobError(Throwable throwable) {
        if (throwable != null) {
            this.message = throwable.getMessage();
            if (throwable.getCause() == null) {
                final StackTraceElement[] stackTrace = throwable.getStackTrace();
                this.throwable = throwable.getClass().getName();
                this.stackTrace = getStackTraceAsString(stackTrace, 100);
            } else {
                final StackTraceElement[] stackTrace = throwable.getCause().getStackTrace();
                this.throwable = throwable.getCause().getClass().getName();
                this.stackTrace = getStackTraceAsString(stackTrace, 100);
            }
        }
    }

    public JobError(final String message, final String throwable, final String stackTrace) {
        this.message = message;
        this.throwable = throwable;
        this.stackTrace = stackTrace;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getThrowable() {
        return throwable;
    }

    public void setThrowable(String throwable) {
        this.throwable = throwable;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("message", message)
            .append("throwable", throwable)
            .append("stackTrace", stackTrace)
            .toString();
    }
}
