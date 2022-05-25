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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static dev.doddle.core.engine.logger.JobLoggerLevel.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobLoggerLevelTest {

    @Test
    @DisplayName("it should log info messages")
    void it_should_log_info_messages() {
        assertTrue(INFO.isLoggable(DEBUG));
    }

    @Test
    @DisplayName("it should not log any messages")
    void it_should_not_log_any_messages() {
        assertFalse(DEBUG.isLoggable(OFF));
        assertFalse(INFO.isLoggable(OFF));
        assertFalse(ERROR.isLoggable(OFF));
    }

    @Test
    @DisplayName("it should not log debug messages")
    void it_should_not_log_debug_messages() {
        assertFalse(DEBUG.isLoggable(INFO));
    }

    @Test
    @DisplayName("it should not log trace messages")
    void it_should_not_log_trace_messages() {
        assertFalse(TRACE.isLoggable(DEBUG));
    }

    @Test
    @DisplayName("it should only log error messages")
    void it_should_only_log_error_messages() {
        assertTrue(ERROR.isLoggable(ERROR));
        assertFalse(INFO.isLoggable(ERROR));
        assertFalse(DEBUG.isLoggable(ERROR));
        assertFalse(TRACE.isLoggable(ERROR));
    }
}
