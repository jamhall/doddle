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
import dev.doddle.core.engine.circuitbreaker.CircuitBreaker;
import dev.doddle.core.engine.circuitbreaker.CircuitBreakerResultCallback;
import dev.doddle.core.engine.telemetry.events.JobEnqueuedEvent;
import dev.doddle.core.services.TelemetryService;
import dev.doddle.storage.common.Storage;
import dev.doddle.storage.common.domain.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Supplier;

import static dev.doddle.core.support.Objects.requireNonNull;

public class JobEnqueueCommand implements SchedulerCommand {

    private static final Logger           logger = LoggerFactory.getLogger(JobEnqueueCommand.class);
    private final        Storage          storage;
    private final        TelemetryService telemetryService;
    private final        CircuitBreaker   circuitBreaker;

    /**
     * Create a new job picker
     */
    public JobEnqueueCommand(@NotNull final Storage storage,
                             @NotNull final TelemetryService telemetryService,
                             @NotNull final CircuitBreaker circuitBreaker) {
        this.storage = requireNonNull(storage, "storage cannot be null");
        this.telemetryService = requireNonNull(telemetryService, "telemetryService cannot be null");
        this.circuitBreaker = requireNonNull(circuitBreaker, "circuitBreaker cannot be null");
    }

    @Override
    public void execute() {
        logger.debug("Executing job enqueue command");

        final Supplier<List<Job>> supplier = this.storage::enqueueJobs;

        circuitBreaker.apply(supplier, new CircuitBreakerResultCallback<>() {
            @Override
            public void onError(Throwable throwable) {
                logger.error("Error executing job enqueue command {}", throwable.getMessage());
            }

            @Override
            public void onSuccess(List<Job> jobs) {
                logger.debug("Enqueued {} jobs", jobs.size());
                // dispatch an event for each job
                jobs.forEach(job -> {
                    telemetryService.dispatch(new JobEnqueuedEvent(job));
                });
            }
        });

    }


}


