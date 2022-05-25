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
package dev.doddle.core.engine.time;

import dev.doddle.core.exceptions.DoddleValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static dev.doddle.core.engine.time.Interval.createInterval;
import static java.util.concurrent.TimeUnit.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IntervalParserTest {

    @DisplayName("it should only accept a minimum unit")
    @Test
    void it_should_only_accept_a_minimum_unit() {
        final IntervalParser parser1 = new IntervalParser();
        final DoddleValidationException exception1 = assertThrows(DoddleValidationException.class, () -> parser1.parse("200ms", "s"));
        assertEquals("Minimum time unit is s", exception1.getMessage());
        assertEquals(createInterval(30, DAYS), parser1.parse("30d", "s"));
        final IntervalParser parser2 = new IntervalParser();
        assertEquals(createInterval(30, MILLISECONDS), parser2.parse("30ms"));
    }

    @DisplayName("it should successfully parse periods")
    @Test
    void it_should_successfully_parse_periods() {
        final IntervalParser parser = new IntervalParser();
        assertEquals(createInterval(30, DAYS), parser.parse("30d"));
        assertEquals(createInterval(30, HOURS), parser.parse("30h"));
        assertEquals(createInterval(30, MINUTES), parser.parse("30m"));
        assertEquals(createInterval(30, SECONDS), parser.parse("30s"));
        assertEquals(createInterval(30, MILLISECONDS), parser.parse("30ms"));
    }

    @DisplayName("it should throw an exception because invalid period")
    @Test
    void it_should_throw_an_exception_because_invalid_period() {
        final IntervalParser parser = new IntervalParser();
        final DoddleValidationException exception1 = assertThrows(DoddleValidationException.class, () -> parser.parse(null));
        final DoddleValidationException exception2 = assertThrows(DoddleValidationException.class, () -> parser.parse("-1m"));
        final DoddleValidationException exception3 = assertThrows(DoddleValidationException.class, () -> parser.parse("1g"));
        assertEquals("input cannot be null", exception1.getMessage());
        assertEquals("Could not parse given interval", exception2.getMessage());
        assertEquals("Could not parse given interval", exception3.getMessage());
    }
}
