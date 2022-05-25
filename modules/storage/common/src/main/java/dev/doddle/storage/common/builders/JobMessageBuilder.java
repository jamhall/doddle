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

import dev.doddle.storage.common.domain.JobMessage;

import java.time.LocalDateTime;

public final class JobMessageBuilder {

    private String        id;
    private String        message;
    private String        level;
    private String        errorMessage;
    private String        errorClass;
    private String        errorStackTrace;
    private LocalDateTime createdAt;

    private JobMessageBuilder() {
    }

    public static JobMessageBuilder newBuilder() {
        return new JobMessageBuilder();
    }

    public JobMessage build() {
        final JobMessage jobMessage = new JobMessage();
        jobMessage.setId(id);
        jobMessage.setMessage(message);
        jobMessage.setLevel(level);
        jobMessage.setErrorMessage(errorMessage);
        jobMessage.setErrorClass(errorClass);
        jobMessage.setErrorStackTrace(errorStackTrace);
        jobMessage.setCreatedAt(createdAt);
        return jobMessage;
    }

    public JobMessageBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public JobMessageBuilder errorClass(String errorClass) {
        this.errorClass = errorClass;
        return this;
    }

    public JobMessageBuilder errorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public JobMessageBuilder errorStackTrace(String errorStackTrace) {
        this.errorStackTrace = errorStackTrace;
        return this;
    }

    public JobMessageBuilder id(String id) {
        this.id = id;
        return this;
    }

    public JobMessageBuilder level(String level) {
        this.level = level;
        return this;
    }

    public JobMessageBuilder message(String message) {
        this.message = message;
        return this;
    }

}
