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

import java.util.ArrayList;
import java.util.List;

import static dev.doddle.core.engine.time.IntervalParser.createIntervalParser;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static dev.doddle.core.support.Objects.requireNonNull;
import static dev.doddle.core.support.Objects.requireNonNullElse;

public abstract class AbstractEnqueueWizard {

    protected final IntervalParser intervalParser;
    protected       String         queue;
    protected       String         name;
    protected       String         identifier;
    protected       String         handler;
    protected       List<String>   tags;
    protected       Long           timeout;
    protected       Integer        maxRetries;
    protected       List<Argument> arguments;

    public AbstractEnqueueWizard(final IntervalParser parser,
                                 final long defaultTimeout,
                                 final int defaultMaxRetries) {
        this.intervalParser = requireNonNull(parser, "parser cannot be null");
        this.tags = new ArrayList<>();
        this.arguments = new ArrayList<>();
        this.timeout = defaultTimeout;
        this.maxRetries = defaultMaxRetries;
    }


    public AbstractEnqueueWizard(final long defaultTimeout, final int defaultMaxRetries) {
        this(createIntervalParser(), defaultTimeout, defaultMaxRetries);
    }

    /**
     * Build a new enqueue definition
     *
     * @return the built definition
     */
    public EnqueueDefinition build() {
        final Interval period = intervalParser.parse("0s");
        return new EnqueueDefinition(
            period,
            name,
            identifier,
            requireNonNull(queue, "queue cannot be null"),
            requireNonNull(handler, "handler cannot be null"),
            requireNonNull(tags, "tags cannot be null"),
            requireNonNull(timeout, "timeout cannot be null"),
            requireNonNull(maxRetries, "maxRetries cannot be null"),
            requireNonNullElse(arguments, new ArrayList<>())
        );
    }


    public AbstractEnqueueWizard task(@NotNull final String handler) {
        this.handler = requireNonNull(handler, "task cannot be null");
        return this;
    }

    public AbstractEnqueueWizard identifier(@NotNull final String identifier) {
        this.identifier = requireNonNull(identifier, "identifier cannot be null");
        return this;
    }

    public AbstractEnqueueWizard maxRetries(int maxRetries) {
        if (maxRetries < 0) {
            throw new DoddleValidationException("max retries cannot be negative");
        }
        this.maxRetries = maxRetries;
        return this;
    }

    public AbstractEnqueueWizard name(@NotNull final String name) {
        this.name = requireNonNull(name, "name cannot be null");
        return this;
    }

    public AbstractEnqueueWizard queue(@NotNull final String queue) {
        this.queue = requireNonNull(queue, "queue cannot be null");
        return this;
    }

    public AbstractEnqueueWizard tags(@NotNull final String... tags) {
        this.tags.addAll(asList(tags));
        return this;
    }

    public AbstractEnqueueWizard tags(@NotNull final List<String> tags) {
        this.tags.addAll(tags);
        return this;
    }

    public AbstractEnqueueWizard timeout(@NotNull final String timeout) {
        final Interval parsed = intervalParser.parse(requireNonNull(timeout, "timeout cannot be null"), "s");
        this.timeout = parsed.toMillis();
        return this;
    }

    public AbstractEnqueueWizard arguments(@NotNull final Argument... arguments) {
        for (final Argument argument : arguments) {
            if (this.arguments.contains(argument)) {
                throw new DoddleValidationException(format("Argument %s has already been provided", argument.getName()));
            }
            this.arguments.add(argument);
        }
        return this;
    }

}
