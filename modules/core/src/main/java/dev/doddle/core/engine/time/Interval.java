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

import dev.doddle.core.exceptions.DoddleValidationException;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static dev.doddle.core.support.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.*;

public class Interval {

    private final long     period;
    private final TimeUnit unit;

    /**
     * Create a new interval
     *
     * @param period the interval period
     * @param unit   the time unit to use (defaults to milliseconds)
     */
    public Interval(long period, TimeUnit unit) {
        if (period < 0) {
            throw new DoddleValidationException("Period must be greater than zero");
        }
        this.period = period;
        this.unit = requireNonNull(unit, "Time unit cannot be null");
        if (asList(NANOSECONDS, MICROSECONDS).contains(unit)) {
            throw new DoddleValidationException("Lowest time unit is milliseconds");
        }
    }

    /**
     * Create a new interval
     *
     * @param period the interval period
     */
    public Interval(long period) {
        this(period, MILLISECONDS);
    }

    public static Interval createInterval(long period, TimeUnit unit) {
        return new Interval(period, unit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Interval interval = (Interval) o;

        if (period != interval.period) {
            return false;
        }
        return unit == interval.unit;
    }

    /**
     * Get the period
     *
     * @return the period
     */
    public long getPeriod() {
        return period;
    }

    /**
     * Get the unit
     *
     * @return the unit
     */
    public TimeUnit getUnit() {
        return unit;
    }

    @Override
    public int hashCode() {
        int result = (int) (period ^ (period >>> 32));
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        return result;
    }

    /**
     * Convert to a date
     *
     * @return the date
     */
    public LocalDateTime toDate() {
        final LocalDateTime now = LocalDateTime.now();
        final long millis = this.toMillis();
        final long nanos = MILLISECONDS.toNanos(millis);
        return now.plusNanos(nanos);
    }

    /**
     * Convert to milliseconds
     *
     * @return the period in milliseconds
     */
    public long toMillis() {
        return unit.toMillis(period);
    }

    public long toMinutes() {
        return unit.toMinutes(period);
    }


    @Override
    public String toString() {
        return format(Locale.ROOT, "%d%s", period, abbreviate(unit));
    }

    private String abbreviate(TimeUnit unit) {
        return switch (unit) {
            case MILLISECONDS -> "ms";
            case SECONDS -> "s";
            case MINUTES -> "m";
            case HOURS -> "h";
            case DAYS -> "d";
            default -> throw new IllegalStateException(format("Unexpected value: %s", unit));
        };
    }
}
