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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

import static dev.doddle.core.engine.JobProcessingStatus.PROCESSED;
import static dev.doddle.core.engine.JobProcessingStatus.SKIPPED;
import static dev.doddle.core.support.Objects.requireNonNull;

/**
 * This class manages the running of jobs
 * It will check if a new job exists, and then execute it
 * If it doesn't exist then it will skip ready to be checked again at the next polling interval
 */
public class JobRunner {

    private static final Logger       logger = LoggerFactory.getLogger(JobRunner.class);
    private final        Executor     executor;
    private final        JobPicker    picker;
    private final        JobProcessor processor;

    /**
     * Create a new job runner
     *
     * @param executor  the executor to use
     * @param processor the processor to use
     * @param picker    the picker for picking jobs ready to be executed
     */
    public JobRunner(@NotNull Executor executor,
                     @NotNull JobProcessor processor,
                     @NotNull JobPicker picker) {
        this.executor = requireNonNull(executor, "executor cannot be null");
        this.processor = requireNonNull(processor, "processor cannot be null");
        this.picker = requireNonNull(picker, "picker cannot be null");
    }

    /**
     * Pick a job and then execute it
     */
    public void execute(final JobRunnerCallback callback) {
        picker.pick(job -> {
            if (job.isEmpty()) {
                logger.debug("No job picked. Skipping....");
                callback.apply(SKIPPED);
            } else {
                executor.execute(() -> processor.process(job.get()));
                callback.apply(PROCESSED);
            }
        });

    }

}
