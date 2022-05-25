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

import dev.doddle.core.engine.circuitbreaker.CircuitBreaker;
import dev.doddle.core.engine.telemetry.TelemetryEvent;
import dev.doddle.core.engine.telemetry.TelemetrySubscriber;
import dev.doddle.core.engine.telemetry.events.JobSelectedEvent;
import dev.doddle.core.engine.time.Clock;
import dev.doddle.core.engine.time.ticker.strategies.FakeTickerStrategy;
import dev.doddle.core.services.TelemetryService;
import dev.doddle.storage.common.Storage;
import dev.doddle.storage.common.domain.Job;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class JobPickerTest {

    @DisplayName("it should not find a job to pick")
    @Test
    void it_should_not_find_a_job_to_pick() {
        final Storage storage = mock(Storage.class);
        final CircuitBreaker circuitBreaker = mock(CircuitBreaker.class);
        final TelemetryService telemetryService = mock(TelemetryService.class);
        when(storage.pickJob()).thenAnswer((answer) -> Optional.empty());
        final Clock clock = new Clock(new FakeTickerStrategy());
        new JobPicker(storage, clock, telemetryService, circuitBreaker);
        verify(telemetryService, never()).dispatch(null);
    }

    @DisplayName("it should pick a job")
    @Test
    void it_should_pick_a_job() {
        final Storage storage = mock(Storage.class);
        final CircuitBreaker circuitBreaker = mock(CircuitBreaker.class);
        final Clock clock = new Clock(new FakeTickerStrategy());
        when(storage.pickJob()).thenAnswer((answer) -> {
            final Job job = new Job();
            job.setId("123");
            return Optional.of(job);
        });
        JobPicker jobPicker = new JobPicker(storage, clock, new TelemetryService(singletonList(new TelemetrySubscriber() {
            @Override
            public void handle(TelemetryEvent event) {
                final JobSelectedEvent parsed = (JobSelectedEvent) event;
                assertEquals("123", parsed.getJob().getId());
                assertEquals(1, parsed.getElapsed());
            }

            @Override
            public boolean supports(final String name) {
                return true;
            }

        })), circuitBreaker);
        jobPicker.pick(job -> {
            assertTrue(job.isPresent());
        });
    }
}
