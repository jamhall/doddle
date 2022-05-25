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

class ScheduleWizardTest {

    @DisplayName("it should create a definition from the wizard")
    @Test
    void it_should_create_a_definition_from_the_wizard() {

        final ScheduleWizard wizard = new ScheduleWizard(2000, 10) {{
            name("cache.clear")
                .queue("default")
                .handler("cachebuster")
                .description("Clears the cache every hour")
                .expression(ScheduleBuilder::hourly)
                .enabled(false)
                .timeout("1m");
        }};
        final ScheduleDefinition definition = wizard.build();
        assertEquals("cache.clear", definition.getName());
        assertEquals("default", definition.getQueue());
        assertEquals("cachebuster", definition.getHandler());
        assertEquals("Clears the cache every hour", definition.getDescription());
        assertEquals("0 * * * *", definition.getExpression());
        assertEquals(60000, definition.getTimeout());
        assertFalse(definition.isEnabled());
    }


}