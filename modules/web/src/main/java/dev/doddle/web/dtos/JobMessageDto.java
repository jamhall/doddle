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
package dev.doddle.web.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.doddle.storage.common.domain.JobMessage;

import java.time.LocalDateTime;

public class JobMessageDto {

    private final String        id;
    private final String        level;
    private final String        message;
    private final LocalDateTime createdAt;
    private       Error         error = null;

    public JobMessageDto(final JobMessage message) {
        this.id = message.getId();
        this.message = message.getMessage();
        this.level = message.getLevel();
        this.createdAt = message.getCreatedAt();
        if (message.getErrorMessage() != null || message.getErrorClass() != null | message.getErrorStackTrace() != null) {
            this.error = new Error(message.getErrorClass(), message.getErrorMessage(), message.getErrorStackTrace());
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Error getError() {
        return error;
    }

    public String getId() {
        return id;
    }

    public String getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    private static class Error {
        private final String message;
        private final String stackTrace;
        @JsonProperty("class")
        private       String clazz;

        public Error(String clazz, String message, String stackTrace) {
            this.clazz = clazz;
            this.message = message;
            this.stackTrace = stackTrace;
        }

        public String getClazz() {
            return clazz;
        }

        public String getMessage() {
            return message;
        }

        public String getStackTrace() {
            return stackTrace;
        }


    }
}
