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
package dev.doddle.storage.common.interceptors;

import dev.doddle.storage.common.domain.Queue;
import dev.doddle.storage.common.support.KeyGenerator;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

public class QueueInterceptor implements Interceptor<Queue> {

    private final KeyGenerator keyGenerator;

    /**
     * Create a new queue interceptor
     *
     * @param keyGenerator the generator to use for generating a unique identifier
     */
    public QueueInterceptor(final KeyGenerator keyGenerator) {
        this.keyGenerator = requireNonNull(keyGenerator, "keyGenerator cannot be null");
    }

    /**
     * Modify the queue queue object
     *
     * @param queue the queue to modify
     * @return the modified queue object
     */
    @Override
    public Queue apply(final Queue queue) {
        if (isNull(requireNonNull(queue, "queue cannot be null").getId())) {
            final LocalDateTime now = LocalDateTime.now();
            final String id = keyGenerator.createKey();
            queue.setId(id);
        }
        return queue;
    }
}
