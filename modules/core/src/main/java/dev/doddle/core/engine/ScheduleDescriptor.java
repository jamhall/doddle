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
package dev.doddle.core.engine;

import dev.doddle.storage.common.domain.Queue;

public class ScheduleDescriptor {

    private final Queue  queue;
    private final String expression;
    private final String name;
    private final String data;

    private final String  description;
    private final Long    timeout;
    private final Integer maxRetries;
    private final String  handler;

    private final Boolean enabled;

    public ScheduleDescriptor(
        final String name,
        final String description,
        final String expression,
        final Queue queue,
        final String handler,
        final String data,
        final Long timeout,
        final Integer maxRetries,
        final Boolean enabled
    ) {
        this.queue = queue;
        this.description = description;
        this.expression = expression;
        this.name = name;
        this.data = data;
        this.timeout = timeout;
        this.maxRetries = maxRetries;
        this.handler = handler;
        this.enabled = enabled;
    }

    public String getData() {
        return data;
    }

    public String getHandler() {
        return handler;
    }

    public String getExpression() {
        return expression;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public String getName() {
        return name;
    }

    public Queue getQueue() {
        return queue;
    }

    public String getDescription() {
        return description;
    }

    public Long getTimeout() {
        return timeout;
    }

    public Boolean isEnabled() {
        return enabled;
    }
}
