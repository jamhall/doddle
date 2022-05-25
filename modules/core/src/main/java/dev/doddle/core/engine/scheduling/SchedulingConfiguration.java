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
package dev.doddle.core.engine.scheduling;

import dev.doddle.common.support.NotNull;
import dev.doddle.core.engine.threadnaming.ThreadNamingStrategy;
import dev.doddle.core.engine.threadnaming.strategies.DefaultThreadNamingStrategy;
import dev.doddle.core.engine.time.Interval;
import dev.doddle.core.engine.time.IntervalParser;

import static dev.doddle.core.support.Objects.requireNonNull;

public class SchedulingConfiguration {

    private Interval             interval;
    private Interval             delay;
    private ThreadNamingStrategy threadNamingStrategy;

    public SchedulingConfiguration() {
        this.threadNamingStrategy = new DefaultThreadNamingStrategy("doddle-scheduler-thread-%d");
    }

    public SchedulingConfiguration delay(@NotNull final String delay) {
        final IntervalParser parser = new IntervalParser();
        this.delay = parser.parse(requireNonNull(delay, "delay cannot be null"));
        return this;
    }

    public Interval delay() {
        return this.delay;
    }

    public Interval interval() {
        return interval;
    }

    public SchedulingConfiguration interval(final String period) {
        final IntervalParser parser = new IntervalParser();
        this.interval = parser.parse(requireNonNull(period, "period cannot be null"));
        return this;
    }

    public SchedulingConfiguration threadNaming(@NotNull final ThreadNamingStrategy threadNamingStrategy) {
        this.threadNamingStrategy = requireNonNull(threadNamingStrategy, "threadNamingStrategy cannot be null");
        return this;
    }

    public ThreadNamingStrategy threadNaming() {
        return this.threadNamingStrategy;
    }

}
