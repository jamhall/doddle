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

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

class JobFilterTest {

    @DisplayName("it should create a job filter")
    @Test
    void it_should_create_a_job_filter() {
        final JobFilter filter = new JobFilter();
        filter.setTags(asList("apple", "oranges"));
        filter.setStates(singletonList(JobState.COMPLETED));
        assertEquals(asList("apple", "oranges"), filter.getTags());
        assertEquals(JobState.COMPLETED, filter.getStates().get(0));
    }

    @DisplayName("it should fail to create a job filter")
    @Test
    void it_should_fail_to_create_a_job_filter() {
        final JobFilter filter = new JobFilter();
        final NullPointerException exception1 = assertThrows(NullPointerException.class, () -> new JobFilter(null, asList("apple", "oranges")));
        assertTrue(exception1.getMessage().contains("state cannot be null"));
        final NullPointerException exception2 = assertThrows(NullPointerException.class, () -> new JobFilter(null));
        assertTrue(exception2.getMessage().contains("state cannot be null"));
        final NullPointerException exception3 = assertThrows(NullPointerException.class, () -> new JobFilter(singletonList(JobState.COMPLETED), null));
        assertTrue(exception3.getMessage().contains("tags cannot be null"));
        final NullPointerException exception4 = assertThrows(NullPointerException.class, () -> filter.setStates(null));
        assertTrue(exception4.getMessage().contains("state cannot be null"));
        final NullPointerException exception5 = assertThrows(NullPointerException.class, () -> filter.setTags(null));
        assertTrue(exception5.getMessage().contains("tags cannot be null"));
    }
}
