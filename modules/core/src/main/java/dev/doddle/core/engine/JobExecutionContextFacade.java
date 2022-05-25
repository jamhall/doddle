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
import dev.doddle.core.engine.logger.JobLoggerConfiguration;
import dev.doddle.core.engine.progress.JobProgress;
import dev.doddle.core.exceptions.TaskExecutionException;
import dev.doddle.storage.common.domain.Job;

import static dev.doddle.core.support.Objects.requireNonNull;

/**
 * Provides a facade to encapsulate the job execution context logic
 */
public class JobExecutionContextFacade {

    private final Job                         job;
    private final JobExecutionContextEventBus bus;
    private final JobEnvironment              environment;
    private final JobLoggerConfiguration      loggerConfiguration;
    private final JobDataMapper               mapper;
    private       JobLogger                   logger;
    private       JobProgress                 progress;

    public JobExecutionContextFacade(final Job job,
                                     final JobExecutionContextEventBus bus,
                                     final JobDataMapper mapper,
                                     final JobEnvironment environment,
                                     final JobLoggerConfiguration loggerConfiguration) {

        this.job = requireNonNull(job, "job cannot be null");
        this.bus = requireNonNull(bus, "bus cannot be null");
        this.mapper = requireNonNull(mapper, "mapper cannot be null");
        this.environment = requireNonNull(environment, "environment cannot be null");
        this.loggerConfiguration = requireNonNull(loggerConfiguration, "loggerConfiguration cannot be null");
    }

    public JobEnvironment getEnvironment() {
        return environment;
    }

    public Job getJob() {
        return job;
    }

    public JobLogger getLogger() {
        if (logger == null) {
            this.logger = new JobLogger(loggerConfiguration.level(), (event) -> this.bus.emit(job, event));
        }
        return logger;
    }

    public JobDataMapper getMapper() {
        return mapper;
    }

    public JobProgress getProgress(int maxValue) {
        if (maxValue <= 0) {
            throw new TaskExecutionException("Max value must be greater than zero");
        }
        if (this.progress == null) {
            this.progress = new JobProgress(maxValue, (event) -> this.bus.emit(job, event));
        }
        return progress;
    }

    public JobProgress getProgress() {
        return progress;
    }
}
