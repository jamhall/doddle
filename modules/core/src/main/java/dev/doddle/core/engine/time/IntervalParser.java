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
import dev.doddle.core.exceptions.DoddleValidationException;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static dev.doddle.core.support.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.*;

public class IntervalParser {

    private static final Set<IntervalTimeUnit> units;

    static {
        units = Set.of(
            new IntervalTimeUnit("ms", MILLISECONDS, 5000),
            new IntervalTimeUnit("s", SECONDS, 4000),
            new IntervalTimeUnit("m", MINUTES, 3000),
            new IntervalTimeUnit("h", HOURS, 2000),
            new IntervalTimeUnit("d", DAYS, 1000)
        );
    }

    public IntervalParser() {

    }

    public static IntervalParser createIntervalParser() {
        return new IntervalParser();
    }

    /**
     * Parser the input into an interval
     *
     * @param input the input i.e 1s, 1d, 20m etc.
     * @return the parsed interval
     */
    public Interval parse(@NotNull final String input) {
        return parse(input, "ms");
    }

    public Interval parse(@NotNull final String input,
                          @NotNull final String minimum) {
        final IntervalTimeUnit minimumUnit = this.getUnitForLabel(requireNonNull(minimum, "minimum unit cannot be null"));
        if (minimumUnit == null) {
            throw new DoddleValidationException("Cannot find minimum unit provided");
        }
        final Pattern pattern = Pattern.compile("^(?<value>\\d+)(?<unit>d|h|m|s|ms)$");
        final Matcher matcher = pattern.matcher(requireNonNull(input, "input cannot be null"));
        if (matcher.matches()) {
            final int value = parseInt(matcher.group("value"));

            if (value < 0) {
                throw new DoddleValidationException("Interval cannot be negative");
            }

            final IntervalTimeUnit unit = this.getUnitForLabel(matcher.group("unit"));
            if (unit == null) {
                throw new DoddleValidationException("Invalid time unit given");
            }
            if (unit.getLevel() > minimumUnit.getLevel()) {
                throw new DoddleValidationException(format("Minimum time unit is %s", minimumUnit.getLabel()));

            }
            return new Interval(value, unit.getUnit());
        }
        throw new DoddleValidationException("Could not parse given interval");
    }

    private IntervalTimeUnit getUnitForLabel(@NotNull final String label) {
        for (final IntervalTimeUnit unit : units) {
            if (unit.isLabel(label)) {
                return unit;
            }
        }
        return null;
    }

}
