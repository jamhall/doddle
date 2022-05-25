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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static dev.doddle.core.engine.logger.JobLoggerLevel.TRACE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JobLoggerConfigurationTest {

    @DisplayName("it should successfully build a logger configuration")
    @Test
    void it_should_successfully_build_a_logger_configuration() {
        final JobLoggerConfiguration configuration = new JobLoggerConfiguration();
        configuration.level(TRACE);
        configuration.maxLines(200);
        assertEquals(TRACE, configuration.level());
        assertEquals(200, configuration.maxLines());
    }

    @DisplayName("it should throw an exception because invalid level")
    @Test
    void it_should_throw_an_exception_because_invalid_level() {
        final JobLoggerConfiguration configuration = new JobLoggerConfiguration();
        final NullPointerException exception1 = assertThrows(NullPointerException.class, () -> configuration.level(null));
        assertEquals("level cannot be null", exception1.getMessage());
    }

    @DisplayName("it should throw an exception because invalid max lines")
    @Test
    void it_should_throw_an_exception_because_invalid_max_lines() {
        final JobLoggerConfiguration configuration = new JobLoggerConfiguration();
        final NullPointerException exception1 = assertThrows(NullPointerException.class, () -> configuration.maxLines(null));
        final DoddleValidationException exception2 = assertThrows(DoddleValidationException.class, () -> configuration.maxLines(-1));
        assertEquals("maxLines cannot be null", exception1.getMessage());
        assertEquals("maxLines must be greater than zero", exception2.getMessage());
    }

}
