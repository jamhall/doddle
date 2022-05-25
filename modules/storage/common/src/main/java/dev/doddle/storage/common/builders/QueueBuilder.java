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
package dev.doddle.storage.common.builders;

import dev.doddle.storage.common.domain.Queue;

import java.time.LocalDateTime;

public final class QueueBuilder {
    private String        id;
    private String        name;
    private Float         priority;
    private LocalDateTime lockedAt;

    private QueueBuilder() {
    }

    public static QueueBuilder newBuilder() {
        return new QueueBuilder();
    }

    public Queue build() {
        Queue queue = new Queue();
        queue.setId(id);
        queue.setName(name);
        queue.setPriority(priority);
        queue.setLockedAt(lockedAt);
        return queue;
    }

    public QueueBuilder id(String id) {
        this.id = id;
        return this;
    }

    public QueueBuilder lockedAt(LocalDateTime lockedAt) {
        this.lockedAt = lockedAt;
        return this;
    }

    public QueueBuilder name(String name) {
        this.name = name;
        return this;
    }

    public QueueBuilder priority(Float priority) {
        this.priority = priority;
        return this;
    }
}
