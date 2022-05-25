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

import dev.doddle.core.engine.time.Interval;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class EnqueueDefinition {

    private final String       queue;
    private final String       name;
    private final String       identifier;
    private final String       handler;
    private final List<String> tags;
    private final Interval     period;
    private final Long         timeout;
    private final Integer      maxRetries;

    private final List<Argument> arguments;

    public EnqueueDefinition(final Interval period,
                             final String name,
                             final String identifier,
                             final String queue,
                             final String handler,
                             final List<String> tags,
                             final Long timeout,
                             final Integer maxRetries,
                             final List<Argument> arguments) {
        this.period = period;
        this.name = name;
        this.identifier = identifier;
        this.queue = queue;
        this.handler = handler;
        this.tags = tags;
        this.timeout = timeout;
        this.maxRetries = maxRetries;
        this.arguments = arguments;
    }

    public String getHandler() {
        return handler;
    }

    public Interval getPeriod() {
        return period;
    }

    public String getQueue() {
        return queue;
    }

    public List<String> getTags() {
        return tags;
    }

    public Long getTimeout() {
        return timeout;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<Argument> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("name", name)
            .append("identifier", identifier)
            .append("period", period)
            .append("queue", queue)
            .append("handler", handler)
            .append("tags", tags)
            .append("timeout", timeout)
            .append("maxRetries", maxRetries)
            .toString();
    }
}
