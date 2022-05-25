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
package dev.doddle.core.support;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ScheduleDefinitionTest {

    @DisplayName("it should create a new schedule definition")
    @Test
    void it_should_create_a_new_schedule_definition() {

        final ScheduleDefinition definition = new ScheduleDefinition(
            "cache.clear",
            "Clears the cache every hour",
            "default",
            "cachebuster",
            "* * * * *",
            false,
            60000,
            10
        );

        assertEquals("cache.clear", definition.getName());
        assertEquals("Clears the cache every hour", definition.getDescription());
        assertEquals("default", definition.getQueue());
        assertEquals("cachebuster", definition.getHandler());
        assertEquals("* * * * *", definition.getExpression());
        assertEquals(60000, definition.getTimeout());
        assertEquals(10, definition.getMaxRetries());
        assertFalse(definition.isEnabled());

    }

}