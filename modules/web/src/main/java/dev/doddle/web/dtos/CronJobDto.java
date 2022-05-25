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
package dev.doddle.web.dtos;

import dev.doddle.storage.common.domain.CronJob;
import dev.doddle.storage.common.domain.Queue;

import java.time.LocalDateTime;

public class CronJobDto {

    private final String id;

    private final String name;

    private final String description;

    private final String expression;

    private final String handler;

    private final LocalDateTime createdAt;

    private final boolean enabled;

    private final Long timeout;

    private final Queue queue;

    public CronJobDto(final CronJob job) {
        this.id = job.getId();
        this.name = job.getName();
        this.description = job.getDescription();
        this.expression = job.getExpression();
        this.handler = job.getHandler();
        this.createdAt = job.getCreatedAt();
        this.enabled = job.isEnabled();
        this.timeout = job.getTimeout();
        this.queue = job.getQueue();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getDescription() {
        return description;
    }

    public String getExpression() {
        return expression;
    }

    public String getHandler() {
        return handler;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Queue getQueue() {
        return queue;
    }

    public Long getTimeout() {
        return timeout;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
