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
import dev.doddle.core.engine.logger.JobLoggerMessageEvent;
import dev.doddle.core.engine.progress.JobProgressEvent;
import dev.doddle.storage.common.Storage;
import dev.doddle.storage.common.StorageException;
import dev.doddle.storage.common.builders.JobMessageBuilder;
import dev.doddle.storage.common.builders.JobProgressBuilder;
import dev.doddle.storage.common.domain.Job;
import dev.doddle.storage.common.domain.JobMessage;
import dev.doddle.storage.common.domain.JobProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.doddle.core.support.Objects.requireNonNull;

/**
 * Centralise events from the execution context
 */
public class JobExecutionContextEventBus {

    private static final Logger  logger = LoggerFactory.getLogger(JobExecutionContextEventBus.class);
    private final        Storage storage;

    /**
     * Create a new job execution context event bus
     *
     * @param storage the storage
     */
    public JobExecutionContextEventBus(final Storage storage) {
        this.storage = requireNonNull(storage, "storage cannot be null");
    }

    /**
     * Process the job progress event
     *
     * @param job   the job
     * @param event the job progress event
     */
    public void emit(@NotNull final Job job, @NotNull final JobProgressEvent event) {
        try {
            logger.debug("Processing job ({}) progress event: {}", job, event);
            final JobProgress progress = this.toJobProgress(event);
            job.setProgress(progress);
            this.storage.saveJob(job);
        } catch (StorageException exception) {
            logger.error("Error persisting job progress event to storage: {}", exception.getMessage());
        }
    }

    /**
     * Process the job logger message event
     *
     * @param job   the job
     * @param event the message event
     */
    public void emit(@NotNull final Job job, @NotNull final JobLoggerMessageEvent event) {
        try {
            logger.debug("Processing job ({}) logger event: {}", job, event);
            final JobMessage message = this.toJobMessage(event);
            this.storage.createMessageForJob(job, message);
        } catch (StorageException exception) {
            logger.error("Error persisting job message event to storage: {}", exception.getMessage());
        }
    }

    private JobMessage toJobMessage(@NotNull JobLoggerMessageEvent event) {
        final JobMessageBuilder builder = JobMessageBuilder.newBuilder();
        builder.message(event.getMessage());
        builder.level(event.getLevel());
        return builder.build();
    }

    private JobProgress toJobProgress(@NotNull final JobProgressEvent event) {
        final JobProgressBuilder builder = JobProgressBuilder.newBuilder();
        builder.currentValue(event.getCurrentValue());
        builder.maxValue(event.getMaxValue());
        return builder.build();
    }
}
