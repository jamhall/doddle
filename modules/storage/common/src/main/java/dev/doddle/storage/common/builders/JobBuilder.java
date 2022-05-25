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

import dev.doddle.storage.common.domain.*;

import java.time.LocalDateTime;
import java.util.List;

public final class JobBuilder {

    private String        id;
    private String        name;
    private String        identifier;
    private Queue         queue;
    private String        handler;
    private String        data;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime discardedAt;
    private LocalDateTime executingAt;
    private LocalDateTime failedAt;
    private Integer       maxRetries;
    private Integer       retries = 0;
    private JobState      state;
    private List<String>  tags;
    private Long          timeout;
    private JobProgress   progress;
    private JobCategory   category;

    private JobBuilder() {
    }

    public static JobBuilder newBuilder() {
        return new JobBuilder();
    }

    public Job build() {
        Job job = new Job();
        job.setId(id);
        job.setName(name);
        job.setIdentifier(identifier);
        job.setQueue(queue);
        job.setHandler(handler);
        job.setData(data);
        job.setCreatedAt(createdAt);
        job.setCompletedAt(completedAt);
        job.setDiscardedAt(discardedAt);
        job.setExecutingAt(executingAt);
        job.setFailedAt(failedAt);
        job.setScheduledAt(scheduledAt);
        job.setMaxRetries(maxRetries);
        job.setRetries(retries);
        job.setState(state);
        job.setTimeout(timeout);
        job.setTags(tags);
        job.setProgress(progress);
        job.setCategory(category);
        return job;
    }

    public JobBuilder category(JobCategory category) {
        this.category = category;
        return this;
    }

    public JobBuilder completedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
        return this;
    }

    public JobBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public JobBuilder data(String data) {
        this.data = data;
        return this;
    }

    public JobBuilder discardedAt(LocalDateTime discardedAt) {
        this.discardedAt = discardedAt;
        return this;
    }

    public JobBuilder executingAt(LocalDateTime executingAt) {
        this.executingAt = executingAt;
        return this;
    }

    public JobBuilder failedAt(LocalDateTime failedAt) {
        this.failedAt = failedAt;
        return this;
    }

    public JobBuilder handler(String handler) {
        this.handler = handler;
        return this;
    }

    public JobBuilder id(String id) {
        this.id = id;
        return this;
    }

    public JobBuilder identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public JobBuilder maxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    public JobBuilder name(String name) {
        this.name = name;
        return this;
    }

    public JobProgress progress(JobProgress progress) {
        this.progress = progress;
        return progress;
    }

    public JobBuilder queue(Queue queue) {
        this.queue = queue;
        return this;
    }

    public JobBuilder retries(Integer retries) {
        this.retries = retries;
        return this;
    }

    public JobBuilder scheduledAt(LocalDateTime enqueuedAt) {
        this.scheduledAt = enqueuedAt;
        return this;
    }

    public JobBuilder state(JobState state) {
        this.state = state;
        return this;
    }

    public JobBuilder tags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public JobBuilder timeout(long timeout) {
        this.timeout = timeout;
        return this;
    }
}
