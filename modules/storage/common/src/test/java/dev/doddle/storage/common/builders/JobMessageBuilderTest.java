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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JobMessageBuilderTest {

    @Test
    @DisplayName("it should test building a job message")
    void it_should_test_building_a_job_message() {
        final LocalDateTime now = now();
        final JobMessageBuilder builder = JobMessageBuilder.newBuilder();
        final JobMessage jobMessage = builder.id("123-123-123")
            .level("INFO")
            .message("Hello world!")
            .errorMessage("An exception was thrown")
            .errorClass("com.example.Processor")
            .errorStackTrace("A super long stack trace...")
            .createdAt(now)
            .build();
        assertEquals("123-123-123", jobMessage.getId());
        assertEquals("INFO", jobMessage.getLevel());
        assertEquals("Hello world!", jobMessage.getMessage());
        assertEquals("An exception was thrown", jobMessage.getErrorMessage());
        assertEquals("com.example.Processor", jobMessage.getErrorClass());
        assertEquals("A super long stack trace...", jobMessage.getErrorStackTrace());
        assertEquals(now, jobMessage.getCreatedAt());
    }

}
