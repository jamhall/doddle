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
package dev.doddle.core.engine.time;

import dev.doddle.common.support.NotNull;

import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static dev.doddle.core.support.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.*;

public final class Stopwatch {

    private final Clock   clock;
    private       boolean isRunning;
    private       long    elapsedNanos;
    private       long    startTick;

    public Stopwatch(@NotNull final Clock clock) {
        this.clock = requireNonNull(clock, "ticker cannot be null");
    }

    public Stopwatch(@NotNull final Clock clock, boolean start) {
        this.clock = requireNonNull(clock, "ticker cannot be null");
        if (start) {
            this.start();
        }
    }

    public static Stopwatch createStopwatch(@NotNull Clock clock) {
        return new Stopwatch(clock, true);
    }

    private static TimeUnit chooseUnit(long nanos) {
        if (DAYS.convert(nanos, NANOSECONDS) > 0) {
            return DAYS;
        }
        if (HOURS.convert(nanos, NANOSECONDS) > 0) {
            return HOURS;
        }
        if (MINUTES.convert(nanos, NANOSECONDS) > 0) {
            return MINUTES;
        }
        if (SECONDS.convert(nanos, NANOSECONDS) > 0) {
            return SECONDS;
        }
        if (MILLISECONDS.convert(nanos, NANOSECONDS) > 0) {
            return MILLISECONDS;
        }
        if (MICROSECONDS.convert(nanos, NANOSECONDS) > 0) {
            return MICROSECONDS;
        }
        return NANOSECONDS;
    }

    private static String abbreviate(TimeUnit unit) {
        return switch (unit) {
            case NANOSECONDS -> "ns";
            case MICROSECONDS -> "\u03bcs";
            case MILLISECONDS -> "ms";
            case SECONDS -> "s";
            case MINUTES -> "min";
            case HOURS -> "h";
            case DAYS -> "d";
        };
    }

    public long elapsed(TimeUnit desiredUnit) {
        return desiredUnit.convert(elapsedNanos(), NANOSECONDS);
    }

    public Duration elapsed() {
        return Duration.ofNanos(elapsedNanos());
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Stopwatch reset() {
        elapsedNanos = 0;
        isRunning = false;
        return this;
    }

    public Stopwatch start() {
        checkState(!isRunning, "This stopwatch is already running");
        isRunning = true;
        startTick = clock.millis();
        return this;
    }

    public Stopwatch stop() {
        long tick = clock.millis();
        checkState(isRunning, "This stopwatch is already stopped");
        isRunning = false;
        elapsedNanos += tick - startTick;
        return this;
    }

    @Override
    public String toString() {
        final long nanos = elapsedNanos();
        final TimeUnit unit = chooseUnit(nanos);
        double value = (double) nanos / NANOSECONDS.convert(1, unit);
        return format(Locale.ROOT, "%.4g %s", value, abbreviate(unit));
    }

    private void checkState(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    private long elapsedNanos() {
        return isRunning ? clock.millis() - startTick + elapsedNanos : elapsedNanos;
    }
}
