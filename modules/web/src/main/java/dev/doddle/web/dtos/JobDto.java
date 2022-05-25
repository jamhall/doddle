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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import dev.doddle.storage.common.domain.Job;
import dev.doddle.storage.common.domain.JobProgress;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;

@JsonPropertyOrder({"id", "name", "identifier", "queue", "task", "state", "data", "maxRetries", "retries", "tags", "timeout", "createdAt", "completedAt", "scheduledAt", "discardedAt", "executingAt", "failedAt", "error"})
public class JobDto {

    private String               id;
    private String               name;
    private String               identifier;
    private QueueDto             queue;
    private TaskDto              task;
    private String               category;
    private List<JobArgumentDto> data;
    private String               state;
    private LocalDateTime        createdAt;
    private LocalDateTime        completedAt;
    private LocalDateTime        scheduledAt;
    private LocalDateTime        discardedAt;
    private LocalDateTime        executingAt;
    private LocalDateTime        failedAt;
    private Integer              maxRetries;
    private Integer              retries;
    private Long                 timeout;
    private JobErrorDto          error;
    // not sure whether to keep tags or not...
    private List<String>         tags;
    private ProgressDto          progress;

    public JobDto() {

    }

    public JobDto(final Job job, final JobDataDto data) {
        this.id = job.getId();
        this.name = job.getName();
        this.identifier = job.getIdentifier();
        this.data = data.getArguments();
        this.state = job.getState().getName();
        this.createdAt = job.getCreatedAt();
        this.completedAt = job.getCompletedAt();
        this.scheduledAt = job.getScheduledAt();
        this.discardedAt = job.getDiscardedAt();
        this.executingAt = job.getExecutingAt();
        this.failedAt = job.getFailedAt();
        this.maxRetries = job.getMaxRetries();
        this.retries = job.getRetries();
        this.tags = job.getTags();
        this.timeout = job.getTimeout();
        this.category = job.getCategory().getName();
        if (job.getError() != null) {
            this.error = new JobErrorDto(job.getError());
        }
        if (job.getProgress() != null) {
            final JobProgress progress = job.getProgress();
            this.progress = new ProgressDto(progress.getCurrentValue(), progress.getMaxValue());
        }
        this.queue = new QueueDto(job.getQueue());
        this.task = new TaskDto(job.getHandler(), "A description");
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
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

    public List<JobArgumentDto> getData() {
        return data;
    }

    public void setData(List<JobArgumentDto> data) {
        this.data = data;
    }

    public LocalDateTime getDiscardedAt() {
        return discardedAt;
    }

    public void setDiscardedAt(LocalDateTime discardedAt) {
        this.discardedAt = discardedAt;
    }

    public JobErrorDto getError() {
        return error;
    }

    public void setError(JobErrorDto error) {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public ProgressDto getProgress() {
        return progress;
    }

    public QueueDto getQueue() {
        return queue;
    }

    public void setQueue(QueueDto queue) {
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public TaskDto getTask() {
        return task;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("queue", queue)
            .append("task", task)
            .append("data", data)
            .append("createdAt", createdAt)
            .append("completedAt", completedAt)
            .append("scheduledAt", scheduledAt)
            .append("discardedAt", discardedAt)
            .append("failedAt", failedAt)
            .append("maxRetries", maxRetries)
            .append("retries", retries)
            .append("state", state)
            .append("tags", tags)
            .append("timeout", timeout)
            .append("error", error)
            .append("progress", progress)
            .toString();
    }

    public static class TaskDto {

        private final String name;
        private final String description;

        public TaskDto(final String name, final String description) {
            this.name = name;
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public String getName() {
            return name;
        }

    }

    public static class ProgressDto {
        private final Integer currentValue;
        private final Integer maxValue;

        public ProgressDto(Integer currentValue, Integer maxValue) {
            this.currentValue = currentValue;
            this.maxValue = maxValue;
        }

        public Integer getCurrentValue() {
            return currentValue;
        }


        public Integer getMaxValue() {
            return maxValue;
        }


        /**
         * Calculate the current percentage
         *
         * @return the calculated percentage
         */
        public int getPercentage() {
            return (int) (this.currentValue * 100.0f / this.maxValue);
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(currentValue).append(maxValue).toHashCode();
        }

        @Override
        public String toString() {
            return format("%d/%d (%d%%)", this.currentValue, this.maxValue, this.getPercentage());
        }
    }
}
