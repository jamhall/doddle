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
package dev.doddle.core.services;

import dev.doddle.core.engine.telemetry.TelemetryEvent;
import dev.doddle.core.engine.telemetry.TelemetrySubscriber;
import dev.doddle.core.engine.telemetry.events.JobFailedEvent;
import dev.doddle.storage.common.domain.Job;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class TelemetryServiceTest {

    @DisplayName("it should test the telemetry service")
    @Test
    void it_should_test_the_telemetry_service() {
        final TelemetrySubscriber subscriber = new TelemetrySubscriber() {
            @Override
            public void handle(TelemetryEvent event) {
                assertEquals("job.failed", event.name());
            }

            public boolean supports(final String name) {
                return true;
            }

        };


        final TelemetryService service = new TelemetryService(singletonList(subscriber));

        assertEquals(1, service.getSubscribers().size());
        service.dispatch(new JobFailedEvent(mock(Job.class)));

    }
}