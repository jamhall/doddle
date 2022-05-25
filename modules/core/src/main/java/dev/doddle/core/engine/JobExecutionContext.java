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
package dev.doddle.core.engine;

import dev.doddle.core.engine.logger.JobLogger;
import dev.doddle.core.engine.progress.JobProgress;
import dev.doddle.core.exceptions.DoddleValidationException;
import dev.doddle.core.exceptions.MapperException;
import dev.doddle.core.exceptions.TaskExecutionException;
import dev.doddle.core.support.Argument;
import dev.doddle.storage.common.domain.Job;
import dev.doddle.storage.common.domain.JobState;
import dev.doddle.storage.common.domain.Queue;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static dev.doddle.core.support.Objects.requireNonNull;

public class JobExecutionContext {

    private final ImmutableJob              job;
    private final JobExecutionContextFacade facade;
    private final List<Argument> arguments;

    public JobExecutionContext(
        final JobExecutionContextFacade facade
    ) {
        this.facade = facade;
        this.job = toImmutableJob();
        this.arguments = arguments();
    }


    private List<Argument> arguments() {
        try {
            final JobDataMapper mapper = facade.getMapper();
            final JobData data = mapper.convertToObject(job.getData());
            return data.getArguments();
        } catch (MapperException exception) {
            throw new TaskExecutionException(exception);
        }
    }

    public JobArgument argument(final String name) {
        if (name == null) {
            throw new DoddleValidationException("argument name cannot be null");
        }
        return this.arguments
            .stream()
            .filter(argument -> argument.getName().equals(name))
            .findAny()
            .map(JobArgument::new)
            .orElse(new JobArgument());
    }

    /**
     * Get an environment value
     *
     * @param key the key to look up
     * @return the value
     */
    public Object environment(final String key) {
        final JobEnvironment environment = facade.getEnvironment();
        return environment.getByKey(requireNonNull(key, "key cannot be null"));
    }

    public JobEnvironment environment() {
        return facade.getEnvironment();
    }

    /**
     * Sugar for getting the job identifier
     *
     * @return the job identifier
     */
    public String id() {
        return this.job.getId();
    }

    /**
     * Get the immutable job details
     *
     * @return the job
     */
    public ImmutableJob job() {
        return this.job;
    }

    /**
     * Get the logger
     *
     * @return the job logger
     */
    public JobLogger logger() {
        return this.facade.getLogger();
    }

    /**
     * Create a new progress
     *
     * @param maxValue the max value
     * @return the job progress
     */
    public JobProgress progress(int maxValue) {
        return this.facade.getProgress(maxValue);
    }

    /**
     * Get the job progress
     *
     * @return the job progress
     */
    public JobProgress progress() {
        final JobProgress progress = facade.getProgress();
        if (progress == null) {
            throw new TaskExecutionException("Progress needs to be initialised with a max value");
        } else {
            return progress;
        }
    }

    private ImmutableJob toImmutableJob() {
        final Job job = facade.getJob();
        return new ImmutableJob(
            job.getId(),
            job.getName(),
            job.getIdentifier(),
            job.getQueue(),
            job.getHandler(),
            job.getData(),
            job.getState(),
            job.getCreatedAt(),
            job.getCompletedAt(),
            job.getScheduledAt(),
            job.getDiscardedAt(),
            job.getExecutingAt(),
            job.getFailedAt(),
            job.getMaxRetries(),
            job.getRetries(),
            job.getTags(),
            job.getTimeout()
        );
    }

    /**
     * the job object should not be modified during execution otherwise we're gonna have a bad time...
     * therefore this class ensures it is immutable and thread safe....
     */
    public static final class ImmutableJob {

        private final String        id;
        private final Queue         queue;
        private final String        handler;
        private final String        data;
        private final JobState      state;
        private final LocalDateTime createdAt;
        private final LocalDateTime completedAt;
        private final LocalDateTime scheduledAt;
        private final LocalDateTime discardedAt;
        private final LocalDateTime executingAt;
        private final LocalDateTime failedAt;
        private final Integer       maxRetries;
        private final Integer       retries;
        private final List<String>  tags;
        private final Long          timeout;
        private final String        name;
        private final String        identifier;

        private ImmutableJob(String id,
                             String name,
                             String identifier,
                             Queue queue,
                             String handler,
                             String data,
                             JobState state,
                             LocalDateTime createdAt,
                             LocalDateTime completedAt,
                             LocalDateTime scheduledAt,
                             LocalDateTime discardedAt,
                             LocalDateTime executingAt,
                             LocalDateTime failedAt,
                             Integer maxRetries,
                             Integer retries,
                             List<String> tags,
                             Long timeout) {
            this.id = id;
            this.name = name;
            this.identifier = identifier;
            this.queue = queue;
            this.handler = handler;
            this.data = data;
            this.state = state;
            this.createdAt = createdAt;
            this.completedAt = completedAt;
            this.scheduledAt = scheduledAt;
            this.discardedAt = discardedAt;
            this.executingAt = executingAt;
            this.failedAt = failedAt;
            this.maxRetries = maxRetries;
            this.retries = retries;
            this.tags = tags;
            this.timeout = timeout;
        }

        public LocalDateTime getCompletedAt() {
            return completedAt;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public String getData() {
            return data;
        }

        public LocalDateTime getDiscardedAt() {
            return discardedAt;
        }

        public LocalDateTime getExecutingAt() {
            return executingAt;
        }

        public LocalDateTime getFailedAt() {
            return failedAt;
        }

        public String getHandler() {
            return handler;
        }

        public String getId() {
            return id;
        }

        public String getIdentifier() {
            return identifier;
        }

        public Integer getMaxRetries() {
            return maxRetries;
        }

        public String getName() {
            return name;
        }

        public Queue getQueue() {
            return queue;
        }

        public Integer getRetries() {
            return retries;
        }

        public LocalDateTime getScheduledAt() {
            return scheduledAt;
        }

        public JobState getState() {
            return state;
        }

        public List<String> getTags() {
            return tags;
        }

        public Long getTimeout() {
            return timeout;
        }

        public boolean isHandler(String handler) {
            return this.handler.equals(handler);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("identifier", identifier)
                .append("queue", queue)
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
                .toString();
        }
    }
}
