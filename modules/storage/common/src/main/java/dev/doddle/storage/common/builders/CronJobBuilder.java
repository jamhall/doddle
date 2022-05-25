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

import java.time.LocalDateTime;

public final class CronJobBuilder {

    private String        id;
    private String        name;
    private String        description;
    private String        expression;
    private LocalDateTime nextRunAt;
    private String        handler;
    private LocalDateTime createdAt;
    private boolean       enabled;
    private Long          timeout;
    private Queue         queue;

    private int maxRetries;

    private CronJobBuilder() {
    }

    public static CronJobBuilder newBuilder() {
        return new CronJobBuilder();
    }

    public CronJob build() {
        final CronJob job = new CronJob();
        job.setId(id);
        job.setName(name);
        job.setDescription(description);
        job.setExpression(expression);
        job.setNextRunAt(nextRunAt);
        job.setHandler(handler);
        job.setCreatedAt(createdAt);
        job.setEnabled(enabled);
        job.setTimeout(timeout);
        job.setMaxRetries(maxRetries);
        job.setQueue(queue);
        return job;
    }

    public CronJobBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public CronJobBuilder description(String description) {
        this.description = description;
        return this;
    }

    public CronJobBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public CronJobBuilder expression(String expression) {
        this.expression = expression;
        return this;
    }

    public CronJobBuilder handler(String handler) {
        this.handler = handler;
        return this;
    }

    public CronJobBuilder id(String id) {
        this.id = id;
        return this;
    }

    public CronJobBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CronJobBuilder nextRunAt(LocalDateTime nextRunAt) {
        this.nextRunAt = nextRunAt;
        return this;
    }

    public CronJobBuilder queue(Queue queue) {
        this.queue = queue;
        return this;
    }

    public CronJobBuilder timeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public CronJobBuilder maxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }
}
