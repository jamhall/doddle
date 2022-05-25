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
import dev.doddle.core.engine.circuitbreaker.CircuitBreaker;
import dev.doddle.core.engine.circuitbreaker.CircuitBreakerResultCallback;
import dev.doddle.core.engine.telemetry.events. JobSelectedEvent;
import dev.doddle.core.engine.time.Clock;
import dev.doddle.core.engine.time.Stopwatch;
import dev.doddle.core.services.TelemetryService;
import dev.doddle.storage.common.Storage;
import dev.doddle.storage.common.domain.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static dev.doddle.core.engine.time.Stopwatch.createStopwatch;
import static dev.doddle.core.support.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class JobPicker {

    private static final Logger           logger = LoggerFactory.getLogger(JobPicker.class);
    private final        Storage          storage;
    private final        Clock            clock;
    private final        TelemetryService telemetry;
    private final        CircuitBreaker   circuitBreaker;

    /**
     * Create a new job picker
     *
     * @param storage   the storage
     * @param clock     the ticker
     * @param telemetry the telemetry service
     */
    public JobPicker(@NotNull final Storage storage,
                     @NotNull final Clock clock,
                     @NotNull final TelemetryService telemetry,
                     @NotNull final CircuitBreaker circuitBreaker) {
        this.storage = requireNonNull(storage, "storage cannot be null");
        this.clock = requireNonNull(clock, "clock cannot be null");
        this.telemetry = requireNonNull(telemetry, "telemetry cannot be null");
        this.circuitBreaker = requireNonNull(circuitBreaker, "circuitBreaker cannot be null");
    }

    /**
     * Pick a job for processing and notify listener that the job has been picked
     *
     * @param callback the callback
     */
    public void pick(Consumer<Optional<Job>> callback) {
        final Stopwatch stopwatch = createStopwatch(clock);
        final Supplier<Optional<Job>> supplier = storage::pickJob;
        this.circuitBreaker.apply(supplier, new CircuitBreakerResultCallback<>() {
            @Override
            public void onError(Throwable throwable) {
                logger.error(throwable.getMessage());
            }

            @Override
            public void onSuccess(final Optional<Job> job) {
                if (job.isEmpty()) {
                    callback.accept(empty());
                    return;
                }
                job.ifPresent(value -> {
                    long elapsed = stopwatch.elapsed(MILLISECONDS);
                    telemetry.dispatch(new JobSelectedEvent(value, elapsed));
                });
                callback.accept(job);
            }
        });


    }

}
