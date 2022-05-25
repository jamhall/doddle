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

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ScheduleDefinition {

    private final String  expression;
    private final String  name;
    private final String  description;
    private final String  queue;
    private final String  handler;
    private final Boolean enabled;
    private final Long    timeout;
    private final Integer maxRetries;

    public ScheduleDefinition(final String name,
                              final String description,
                              final String queue,
                              final String handler,
                              final String expression,
                              final boolean enabled,
                              final long timeout,
                              final int maxRetries) {
        this.name = name;
        this.description = description;
        this.queue = queue;
        this.handler = handler;
        this.expression = expression;
        this.enabled = enabled;
        this.timeout = timeout;
        this.maxRetries = maxRetries;
    }

    public String getDescription() {
        return description;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public String getExpression() {
        return expression;
    }

    public String getHandler() {
        return handler;
    }

    public String getName() {
        return name;
    }

    public String getQueue() {
        return queue;
    }

    public Long getTimeout() {
        return timeout;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("name", name)
            .append("description", description)
            .append("queue", queue)
            .append("handler", handler)
            .append("expression", expression)
            .append("enabled", enabled)
            .append("timeout", timeout)
            .append("maxRetries", maxRetries)
            .toString();
    }
}
