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

import dev.doddle.common.support.NotNull;
import dev.doddle.core.engine.telemetry.TelemetryEvent;
import dev.doddle.core.engine.telemetry.TelemetrySubscriber;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static dev.doddle.core.support.Objects.requireNonNull;

public class TelemetryService {

    private final List<TelemetrySubscriber> subscribers;

    public TelemetryService() {
        this.subscribers = new ArrayList<>();
    }

    public TelemetryService(@NotNull final List<TelemetrySubscriber> subscribers) {
        this.subscribers = requireNonNull(subscribers, "subscribers cannot be null");
    }

    public TelemetryService(@NotNull final TelemetrySubscriber... subscribers) {
        this(asList(subscribers));
    }

    public void dispatch(final TelemetryEvent event) {
        if (this.subscribers.size() > 0) {
            this.subscribers.stream()
                .filter(subscriber -> subscriber.supports(event.name()))
                .forEach(subscriber -> subscriber.handle(event));
        }
    }

    public List<TelemetrySubscriber> getSubscribers() {
        return subscribers;
    }

    public void register(@NotNull final TelemetrySubscriber subscriber) {
        this.subscribers.add(requireNonNull(subscriber, "subscriber cannot be null"));
    }

}
