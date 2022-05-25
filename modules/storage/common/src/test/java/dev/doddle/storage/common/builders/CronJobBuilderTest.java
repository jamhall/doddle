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

import dev.doddle.storage.common.domain.CronJob;
import dev.doddle.storage.common.domain.Queue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CronJobBuilderTest {

    @DisplayName("it should build a cron job")
    @Test
    void it_should__build_a_cron_job() {
        final Queue queue = new Queue("default", 1.0f);
        final LocalDateTime now = now();
        final CronJobBuilder builder = CronJobBuilder.newBuilder();
        final CronJob schedule = builder.id("123-123-123")
            .createdAt(now)
            .nextRunAt(now)
            .enabled(true)
            .handler("my.handler")
            .expression("*/5 * * * *")
            .timeout(5000)
            .queue(queue)
            .build();
        assertEquals("123-123-123", schedule.getId());
        assertEquals("*/5 * * * *", schedule.getExpression());
        assertEquals("my.handler", schedule.getHandler());
        assertEquals(now, schedule.getCreatedAt());
        assertEquals(now, schedule.getNextRunAt());
        assertTrue(schedule.isEnabled());
        assertEquals(5000, schedule.getTimeout());
        assertEquals(queue, schedule.getQueue());
    }
}
