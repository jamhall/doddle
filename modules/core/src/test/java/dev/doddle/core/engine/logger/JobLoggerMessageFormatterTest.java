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

import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobLoggerMessageFormatterTest {

    @DisplayName("it should format a message that contains a throwable")
    @Test
    void it_should_format_a_message_that_contains_throwable() {
        JobLoggerMessageFormatter formatter = new JobLoggerMessageFormatter();
        JobLoggerMessageEvent formatted = formatter.format("INFO", "Message: {}", new Message("hello"), new DoddleValidationException("Oops"));
        assertEquals("Message: Message[content='hello']", formatted.getMessage());
        assertTrue(formatted.getThrowable() instanceof DoddleValidationException);
    }

    @DisplayName("it should format a string")
    @Test
    void it_should_format_a_string() {
        JobLoggerMessageFormatter formatter = new JobLoggerMessageFormatter();
        JobLoggerMessageEvent formatted = formatter.format("INFO", "Hello {}", "world");
        assertEquals("Hello world", formatted.getMessage());
    }

    @DisplayName("it should format an array of booleans")
    @Test
    void it_should_format_an_array_of_booleans() {
        JobLoggerMessageFormatter formatter = new JobLoggerMessageFormatter();
        JobLoggerMessageEvent formatted = formatter.format("INFO", "Values: {}", new boolean[]{true, false, true});
        assertEquals("Values: [true, false, true]", formatted.getMessage());
    }

    @DisplayName("it should format an array of bytes")
    @Test
    void it_should_format_an_array_of_bytes() {
        JobLoggerMessageFormatter formatter = new JobLoggerMessageFormatter();
        JobLoggerMessageEvent formatted = formatter.format("INFO", "Values: {}", new byte[]{69, 121, 101, 45, 62});
        assertEquals("Values: [69, 121, 101, 45, 62]", formatted.getMessage());
    }

    @DisplayName("it should format an array of chars")
    @Test
    void it_should_format_an_array_of_chars() {
        JobLoggerMessageFormatter formatter = new JobLoggerMessageFormatter();
        JobLoggerMessageEvent formatted = formatter.format("INFO", "Values: {}", new char[]{'h', 'e', 'l', 'l', 'o'});
        assertEquals("Values: [h, e, l, l, o]", formatted.getMessage());
    }

    @DisplayName("it should format an array of integers")
    @Test
    void it_should_format_an_array_of_integers() {
        JobLoggerMessageFormatter formatter = new JobLoggerMessageFormatter();
        JobLoggerMessageEvent formatted = formatter.format("INFO", "Values: {}", new int[]{2, 3, 5, 7, 11});
        assertEquals("Values: [2, 3, 5, 7, 11]", formatted.getMessage());
    }

    @DisplayName("it should format an array of shorts")
    @Test
    void it_should_format_an_array_of_shorts() {
        JobLoggerMessageFormatter formatter = new JobLoggerMessageFormatter();
        JobLoggerMessageEvent formatted = formatter.format("INFO", "Values: {}", new short[]{2, 3, 5, 7, 11});
        assertEquals("Values: [2, 3, 5, 7, 11]", formatted.getMessage());
    }

    @DisplayName("it should format an integer")
    @Test
    void it_should_format_an_integer() {
        JobLoggerMessageFormatter formatter = new JobLoggerMessageFormatter();
        JobLoggerMessageEvent formatted = formatter.format("INFO", "2 + 2 = {}", 4);
        assertEquals("2 + 2 = 4", formatted.getMessage());
    }

    @DisplayName("it should format an object")
    @Test
    void it_should_format_an_object() {
        JobLoggerMessageFormatter formatter = new JobLoggerMessageFormatter();
        JobLoggerMessageEvent formatted = formatter.format("INFO", "Message: {}", new Message("hello"));
        assertEquals("Message: Message[content='hello']", formatted.getMessage());
    }

    @DisplayName("it should format multiple parameters")
    @Test
    void it_should_format_multiple_parameters() {
        JobLoggerMessageFormatter formatter = new JobLoggerMessageFormatter();
        JobLoggerMessageEvent formatted = formatter.format("INFO", "The {} is {}m tall and started construction in {}", "London Shard", "310", 2009);
        assertEquals("The London Shard is 310m tall and started construction in 2009", formatted.getMessage());
        assertEquals(3, formatted.getArguments().length);
    }

    private static class Message {
        private final String content;

        public Message(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Message.class.getSimpleName() + "[", "]")
                .add("content='" + content + "'")
                .toString();
        }
    }

}
