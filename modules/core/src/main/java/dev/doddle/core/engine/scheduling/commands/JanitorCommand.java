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
package dev.doddle.core.engine.scheduling.commands;

import dev.doddle.common.support.NotNull;
import dev.doddle.storage.common.Storage;
import dev.doddle.storage.common.domain.Job;
import dev.doddle.storage.common.domain.JobFilter;
import dev.doddle.storage.common.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static dev.doddle.storage.common.domain.JobState.EXECUTING;
import static dev.doddle.storage.common.domain.JobState.RETRYABLE;
import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;
import static dev.doddle.core.support.Objects.requireNonNull;

public class JanitorCommand implements SchedulerCommand {

    private final Logger  logger = LoggerFactory.getLogger(JanitorCommand.class);
    private final Storage storage;

    public JanitorCommand(@NotNull final Storage storage) {
        this.storage = requireNonNull(storage, "storage cannot be null");
    }

    @Override
    public void execute() {
        // let's check for jobs that got stuck...
        logger.debug("Executing job janitor command");
        final JobFilter filter = new JobFilter(singletonList(EXECUTING));
        final Pageable pageable = new Pageable(0, 1000);
        final List<Job> jobs = storage.getJobs(filter, pageable);
        logger.debug("Found {} that are executing", jobs.size());
        for (final Job job : jobs) {
            if (job.isStuck()) {
                logger.info("Job {} is stuck", job.getId());
                // schedule for execution immediately
                if (job.isMaxRetries(job.getRetries())) {
                    logger.debug("Job has exceeded maximum retries so will increment max retries by one");
                    final Integer maxRetries = job.getMaxRetries() + 1;
                    job.setMaxRetries(maxRetries);
                }
                final Integer retries = job.getRetries() + 1;
                job.setScheduledAt(now());
                job.setRetries(retries);
                job.setState(RETRYABLE);
                job.setExecutingAt(null);
                job.setCompletedAt(null);
                job.setDiscardedAt(null);
                job.setFailedAt(null);

                this.storage.saveJob(job);
            }
        }
    }
}
