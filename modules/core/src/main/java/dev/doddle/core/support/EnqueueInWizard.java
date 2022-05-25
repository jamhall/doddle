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

import java.util.ArrayList;
import java.util.List;

import static dev.doddle.core.engine.time.IntervalParser.createIntervalParser;
import static dev.doddle.core.support.Objects.requireNonNull;
import static dev.doddle.core.support.Objects.requireNonNullElse;

public class EnqueueInWizard extends AbstractEnqueueWizard {


    private Interval period;


    public EnqueueInWizard(final IntervalParser parser,
                           final long defaultTimeout,
                           final int defaultMaxRetries) {
        super(parser, defaultTimeout, defaultMaxRetries);
    }

    public EnqueueInWizard(final long defaultTimeout, final int defaultMaxRetries) {
        this(createIntervalParser(), defaultTimeout, defaultMaxRetries);
    }

    /**
     * Build a new enqueue definition
     *
     * @return the built definition
     */
    public EnqueueDefinition build() {
        return new EnqueueDefinition(
            requireNonNull(period, "period cannot be null"),
            name,
            identifier,
            requireNonNull(queue, "queue cannot be null"),
            requireNonNull(handler, "handler cannot be null"),
            requireNonNull(tags, "tags cannot be null"),
            requireNonNull(timeout, "timeout cannot be null"),
            requireNonNull(maxRetries),
            requireNonNullElse(arguments, new ArrayList<>())
        );
    }

    public EnqueueInWizard period(@NotNull final String period) {
        final IntervalParser parser = new IntervalParser();
        this.period = parser.parse(requireNonNull(period, "period cannot be null"), "s");
        return this;
    }

    @Override
    public EnqueueInWizard task(@NotNull String handler) {
        super.task(handler);
        return this;
    }

    @Override
    public EnqueueInWizard identifier(@NotNull String identifier) {
        super.identifier(identifier);
        return this;
    }

    @Override
    public EnqueueInWizard maxRetries(int maxRetries) {
        super.maxRetries(maxRetries);
        return this;
    }

    @Override
    public EnqueueInWizard name(@NotNull String name) {
        super.name(name);
        return this;
    }

    @Override
    public EnqueueInWizard queue(@NotNull String queue) {
        super.queue(queue);
        return this;
    }

    @Override
    public EnqueueInWizard tags(@NotNull String... tags) {
        super.tags(tags);
        return this;
    }

    @Override
    public EnqueueInWizard tags(@NotNull List<String> tags) {
        super.tags(tags);
        return this;
    }

    @Override
    public EnqueueInWizard timeout(@NotNull String timeout) {
        super.timeout(timeout);
        return this;
    }

    @Override
    public EnqueueInWizard arguments(@NotNull Argument... arguments) {
        super.arguments(arguments);
        return this;
    }
}
