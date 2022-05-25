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
package dev.doddle.core.support;

import dev.doddle.common.support.NotNull;
import dev.doddle.core.engine.time.Interval;
import dev.doddle.core.engine.time.IntervalParser;
import dev.doddle.core.exceptions.DoddleValidationException;

import java.util.function.Function;

import static dev.doddle.core.engine.time.IntervalParser.createIntervalParser;
import static dev.doddle.core.support.Objects.requireNonNull;

public class ScheduleWizard {

    private final IntervalParser intervalParser;
    private       String         name;
    private       String         description;
    private       String         queue;
    private       String         handler;
    private       String         expression;
    private       Long           timeout;
    private       boolean        enabled = true;

    private Integer maxRetries;


    public ScheduleWizard(
        final IntervalParser parser,
        final long defaultTimeout,
        final int defaultMaxRetries) {
        this.intervalParser = requireNonNull(parser, "parser cannot be null");
        this.timeout = defaultTimeout;
        this.maxRetries = defaultMaxRetries;
    }


    public ScheduleWizard(final long defaultTimeout, final int defaultMaxRetries) {
        this(createIntervalParser(), defaultTimeout, defaultMaxRetries);
    }

    public ScheduleDefinition build() {
        return new ScheduleDefinition(
            requireNonNull(name, "name cannot be null"),
            description,
            requireNonNull(queue, "queue cannot be null"),
            requireNonNull(handler, "handler cannot be null"),
            requireNonNull(expression, "expression cannot be null"),
            enabled,
            timeout,
            maxRetries
        );
    }

    public ScheduleWizard description(@NotNull final String description) {
        this.description = description;
        return this;
    }

    public ScheduleWizard enabled(final boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public ScheduleWizard expression(@NotNull final String expression) {
        this.expression = requireNonNull(expression, "expression cannot be null");
        return this;
    }

    public ScheduleWizard expression(@NotNull Function<ScheduleBuilder, ScheduleBuilder> input) {
        requireNonNull(input, "input cannot be null");
        final ScheduleBuilder builder = new ScheduleBuilder();
        this.expression = input.apply(builder).build();
        return this;
    }

    public ScheduleWizard handler(@NotNull final String handler) {
        this.handler = requireNonNull(handler, "handler cannot be null");
        return this;
    }

    public ScheduleWizard name(@NotNull final String name) {
        this.name = requireNonNull(name, "name cannot be null");
        return this;
    }

    public ScheduleWizard queue(@NotNull final String queue) {
        this.queue = requireNonNull(queue, "queue cannot be null");
        return this;
    }

    public ScheduleWizard timeout(@NotNull final String timeout) {
        final Interval parsed = intervalParser.parse(requireNonNull(timeout, "timeout cannot be null"), "s");
        this.timeout = parsed.toMillis();
        return this;
    }

    public ScheduleWizard maxRetries(int maxRetries) {
        if (maxRetries < 0) {
            throw new DoddleValidationException("max retries cannot be negative");
        }
        this.maxRetries = maxRetries;
        return this;
    }

}
