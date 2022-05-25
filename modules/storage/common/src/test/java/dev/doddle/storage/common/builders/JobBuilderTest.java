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

import dev.doddle.storage.common.domain.Job;
import dev.doddle.storage.common.domain.Queue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static dev.doddle.storage.common.domain.JobState.COMPLETED;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JobBuilderTest {

    @Test
    @DisplayName("it should test building a job")
    void it_should_test_building_a_job() {
        final LocalDateTime now = LocalDateTime.now();
        final Queue queue = new Queue("default", 1.0f);
        final JobBuilder builder = JobBuilder.newBuilder();
        builder.id("123-123-123-123")
            .timeout(5000)
            .createdAt(now)
            .discardedAt(now)
            .failedAt(now)
            .scheduledAt(now)
            .completedAt(now)
            .handler("newsletter")
            .data("hello")
            .maxRetries(10)
            .retries(5)
            .queue(queue)
            .state(COMPLETED)
            .tags(asList("hello", "world"));
        final Job job = builder.build();
        assertEquals("123-123-123-123", job.getId());
        assertEquals(5000, job.getTimeout());
        assertEquals(now, job.getCreatedAt());
        assertEquals(now, job.getDiscardedAt());
        assertEquals(now, job.getScheduledAt());
        assertEquals(now, job.getFailedAt());
        assertEquals(now, job.getCompletedAt());
        assertEquals("newsletter", job.getHandler());
        assertEquals("hello", job.getData());
        assertEquals(10, job.getMaxRetries());
        assertEquals(5, job.getRetries());
        assertEquals(queue, job.getQueue());
        assertEquals(COMPLETED, job.getState());
        assertEquals(asList("hello", "world"), job.getTags());
    }
}
