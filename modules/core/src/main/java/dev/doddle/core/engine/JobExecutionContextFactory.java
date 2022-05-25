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
import dev.doddle.core.engine.logger.JobLoggerConfiguration;
import dev.doddle.storage.common.domain.Job;

import static dev.doddle.core.support.Objects.requireNonNull;

public class JobExecutionContextFactory {

    private final JobEnvironment              environment;
    private final JobDataMapper               mapper;
    private final JobLoggerConfiguration      loggerConfiguration;
    private final JobExecutionContextEventBus bus;

    public JobExecutionContextFactory(
        final JobExecutionContextEventBus bus,
        final JobEnvironment environment,
        final JobDataMapper mapper,
        final JobLoggerConfiguration loggerConfiguration) {
        this.bus = requireNonNull(bus, "bus cannot be null");
        this.environment = requireNonNull(environment, "environment cannot be null");
        this.mapper = requireNonNull(mapper, "mapper cannot be null");
        this.loggerConfiguration = requireNonNull(loggerConfiguration, "loggerConfiguration factory cannot be null");
    }

    public JobExecutionContext create(@NotNull final Job job) {
        // We need  to clone the environment as this could be modified by the middleware pipeline
        final JobEnvironment environment = this.environment.clone();
        final JobExecutionContextFacade facade = new JobExecutionContextFacade(job, bus, mapper, environment, loggerConfiguration);
        return new JobExecutionContext(facade);
    }
}
