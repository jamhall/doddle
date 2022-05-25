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

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static dev.doddle.common.support.StringUtils.getStackTraceAsString;

public class Job {

    private String id;

    private String name;

    private String identifier;

    private Queue queue;

    private String handler;

    private String data;

    private JobState state;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime discardedAt;
    private LocalDateTime executingAt;
    private LocalDateTime failedAt;

    private JobProgress progress;

    private Integer maxRetries;

    private Integer retries;

    private Long timeout;

    private List<String> tags;

    private JobCategory category;

    private JobError error;

    public JobCategory getCategory() {
        return category;
    }

    public void setCategory(JobCategory category) {
        this.category = category;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public LocalDateTime getDiscardedAt() {
        return discardedAt;
    }

    public void setDiscardedAt(LocalDateTime discardedAt) {
        this.discardedAt = discardedAt;
    }

    public JobError getError() {
        return error;
    }

    public void setError(JobError error) {
        this.error = error;
    }

    public LocalDateTime getExecutingAt() {
        return executingAt;
    }

    public void setExecutingAt(LocalDateTime executingAt) {
        this.executingAt = executingAt;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(LocalDateTime failedAt) {
        this.failedAt = failedAt;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public JobProgress getProgress() {
        return progress;
    }

    public void setProgress(JobProgress progress) {
        this.progress = progress;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public JobState getState() {
        return state;
    }

    public void setState(JobState state) {
        this.state = state;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Transient
    public boolean isCompleted() {
        return completedAt != null;
    }

    public boolean isMaxRetries(final Integer retries) {
        return maxRetries.equals(retries);
    }


    /**
     * Is the job retryable?
     * A job can only be retried if it has not exhausted its maximum number of retries
     *
     * @return true if retryable otherwise false
     */
    @Transient
    public boolean isRetryable() {
        if (retries.equals(maxRetries)) {
            return false;
        }
        return retries <= maxRetries;
    }

    public boolean isState(JobState state) {
        return this.state.equals(state);
    }

    /**
     * Check if this job is stuck.
     * It is stuck if the state is executing_at has exceeded the timeout
     *
     * @return if the job is stuck
     */
    public boolean isStuck() {
        if (this.executingAt == null) {
            return false;
        }
        final LocalDateTime now = LocalDateTime.now();
        final long timeout = this.timeout + Math.round((this.timeout * 0.2));
        long delta = ChronoUnit.MILLIS.between(this.executingAt, now);
        return delta > timeout;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("name", name)
            .append("identifier", identifier)
            .append("queue", queue)
            .append("category", category)
            .append("handler", handler)
            .append("data", data)
            .append("createdAt", createdAt)
            .append("completedAt", completedAt)
            .append("scheduledAt", scheduledAt)
            .append("discardedAt", discardedAt)
            .append("executingAt", executingAt)
            .append("failedAt", failedAt)
            .append("maxRetries", maxRetries)
            .append("retries", retries)
            .append("state", state)
            .append("tags", tags)
            .append("timeout", timeout)
            .append("error", error)
            .toString();
    }


}
