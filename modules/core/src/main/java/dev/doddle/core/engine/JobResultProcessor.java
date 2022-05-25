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

import dev.doddle.common.support.NotNull;
import dev.doddle.common.support.Nullable;
import dev.doddle.core.engine.retry.RetryStrategy;
import dev.doddle.storage.common.Storage;
import dev.doddle.storage.common.domain.Job;
import dev.doddle.storage.common.domain.JobError;

import java.time.LocalDateTime;

import static dev.doddle.common.support.StringUtils.getStackTraceAsString;
import static dev.doddle.storage.common.domain.JobState.*;
import static java.time.LocalDateTime.now;
import static dev.doddle.core.support.Objects.requireNonNull;

public class JobResultProcessor {

    private final JobRetryer retryer;
    private final Storage    storage;

    public JobResultProcessor(@NotNull final JobRetryer retryer,
                              @NotNull final Storage storage) {
        this.retryer = requireNonNull(retryer, "retryer cannot be null");
        this.storage = requireNonNull(storage, "storage cannot be null");
    }

    /**
     * Handle if the execution of the task failed for the given job
     *
     * @param job           the given job
     * @param throwable     the throwable (if thrown)
     * @param retryStrategy the retry strategy associated to the task
     */
    public void handleFailed(@NotNull final Job job,
                             @Nullable final Throwable throwable,
                             @NotNull final RetryStrategy retryStrategy) {
        final int retries = job.getRetries();
        final int maxRetries = job.getMaxRetries();
        if (retryer.isRetryable(retries, maxRetries)) {
            final LocalDateTime nextRetryAt = retryer.getNextRetryAt(retries, retryStrategy);
            job.setScheduledAt(nextRetryAt);
            job.setFailedAt(now());
            job.setState(RETRYABLE);
            job.setRetries(retries + 1);
            job.setExecutingAt(null);
        } else {
            // job can't be retried
            job.setFailedAt(now());
            job.setDiscardedAt(now());
            job.setExecutingAt(null);
            job.setState(FAILED);
        }
        job.setError(new JobError(throwable));
        this.storage.saveJob(job);
    }


    /**
     * Handle if the execution of the task was successful for the given job
     *
     * @param job the given job
     */
    public void handleSuccessful(@NotNull final Job job) {
        job.setCompletedAt(now());
        job.setState(COMPLETED);
        job.setFailedAt(null);
        job.setDiscardedAt(null);
        job.setError(null);
        this.storage.saveJob(job);
    }
}
