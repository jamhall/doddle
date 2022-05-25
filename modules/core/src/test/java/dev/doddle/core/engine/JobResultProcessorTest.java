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

import dev.doddle.core.engine.retry.strategies.LinearRetryStrategy;
import dev.doddle.core.exceptions.DoddleValidationException;
import dev.doddle.storage.common.Storage;
import dev.doddle.storage.common.domain.Job;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static dev.doddle.storage.common.domain.JobState.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class JobResultProcessorTest {

    @DisplayName("it should mark a job as completed")
    @Test
    void it_should_mark_a_job_as_completed() {
        final Storage storage = mock(Storage.class);
        final JobResultProcessor processor = new JobResultProcessor(new JobRetryer(), storage);
        final Job job = new Job();
        job.setRetries(5);
        job.setMaxRetries(10);
        processor.handleSuccessful(job);
        assertEquals(COMPLETED, job.getState());
    }

    @DisplayName("it should mark a job as failed because it has exhausted it's maximum attempts")
    @Test
    void it_should_mark_a_job_as_failed_because_it_has_exhausted_its_maximum_attempts() {
        final Storage storage = mock(Storage.class);
        final JobResultProcessor processor = new JobResultProcessor(new JobRetryer(), storage);
        final Job job = new Job();
        job.setRetries(10);
        job.setMaxRetries(10);
        processor.handleFailed(job, new DoddleValidationException("Hello world"), new LinearRetryStrategy());
        assertEquals(FAILED, job.getState());
    }

    @DisplayName("it should mark a job as retryable because it has not exhausted it's maximum attempts")
    @Test
    void it_should_mark_a_job_as_retryable_because_it_has_not_exhausted_its_maximum_attempts() {
        final Storage storage = mock(Storage.class);
        final JobResultProcessor processor = new JobResultProcessor(new JobRetryer(), storage);
        final Job job = new Job();
        job.setRetries(5);
        job.setMaxRetries(10);
        processor.handleFailed(job, new DoddleValidationException("Hello world"), new LinearRetryStrategy());
        assertEquals(RETRYABLE, job.getState());
        assertNotNull(job.getScheduledAt());
        assertEquals(6, job.getRetries());
    }
}
