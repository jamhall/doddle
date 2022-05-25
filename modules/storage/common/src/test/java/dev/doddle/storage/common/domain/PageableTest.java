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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PageableTest {

    @DisplayName("it should create a new instance")
    @Test
    void it_should_create_a_new_instance() {
        final Pageable pageable = new Pageable(25, 50);
        assertEquals(25, pageable.getOffset());
        assertEquals(50, pageable.getLimit());
    }

    @DisplayName("it should fail to create a new instance")
    @Test
    void it_should_fail_to_create_a_new_instance() {
        final IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> new Pageable(-50, 25));
        assertTrue(exception1.getMessage().contains("offset cannot be less than zero"));
        final IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> new Pageable(50, -25));
        assertTrue(exception2.getMessage().contains("limit cannot be less than zero"));
    }
}
