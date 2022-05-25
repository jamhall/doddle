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
package dev.doddle.core.services;

import dev.doddle.common.support.NotNull;
import dev.doddle.core.exceptions.DoddleException;
import dev.doddle.core.support.QueueDefinition;
import dev.doddle.core.support.QueueWizard;
import dev.doddle.storage.common.Storage;
import dev.doddle.storage.common.StorageException;
import dev.doddle.storage.common.domain.JobFilter;
import dev.doddle.storage.common.domain.Queue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;
import static dev.doddle.core.support.Objects.requireNonNull;

public class QueueService {


    private final Storage storage;

    public QueueService(final Storage storage) {
        this.storage = requireNonNull(storage, "storage cannot be null");
    }

    public List<Queue> all() {
        return this.storage.getAllQueues();
    }

    public Integer count() {
        try {
            return this.storage.countAllQueues();
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    public Queue create(@NotNull final Function<QueueWizard, QueueWizard> input) {
        final QueueDefinition definition = input.apply(new QueueWizard()).build();
        return save(new Queue(definition.getName(), definition.getPriority()));
    }

    public void delete(@NotNull final String id) {
        try {
            final Queue queue = this.storage.getQueueById(
                requireNonNull(id, "id cannot be null")
            ).orElseThrow(DoddleException::new);
            final JobFilter filter = new JobFilter();
            filter.setQueues(singletonList(queue.getName()));
            final long jobs = this.storage.countJobs(filter);
            if (jobs > 0) {
                throw new DoddleException(
                    format("Cannot delete queue because there are %s jobs associated to it", jobs)
                );
            }
            this.storage.deleteQueue(queue);
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    public void deleteAll() {
        this.storage.deleteAllQueues();
    }

    public Optional<Queue> id(@NotNull final String id) {
        try {
            return this.storage.getQueueById(id);
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    /**
     * Lock a queue
     *
     * @param id the id of the queue
     */
    public void lock(@NotNull final String id) {
        this.id(id).ifPresent(this::lock);
    }

    private void lock(@NotNull final Queue queue) {
        if (!requireNonNull(queue).isLocked()) {
            final LocalDateTime now = now();
            queue.setLockedAt(now);
            save(queue);
        }
    }

    public void lockAll() {
        this.all().forEach(this::lock);
    }

    public Optional<Queue> name(@NotNull final String name) {
        try {
            return this.storage.getQueueByName(name);
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    public Queue save(final Queue queue) {
        return this.storage.saveQueue(queue);
    }

    public void unlock(@NotNull final String id) {
        this.id(id).ifPresent(this::unlock);
    }

    public void unlock(@NotNull final Queue queue) {
        if (requireNonNull(queue).getLockedAt() != null) {
            queue.setLockedAt(null);
            save(queue);
        }
    }

    public void unlockAll() {
        this.all().forEach(this::unlock);
    }

    public Queue update(@NotNull final String id,
                        @NotNull final Function<QueueWizard, QueueWizard> input) {
        final QueueDefinition definition = input.apply(new QueueWizard()).build();
        final Queue queue = this.storage.getQueueById(
            requireNonNull(id, "id cannot be null")
        ).orElseThrow(DoddleException::new);
        queue.setName(definition.getName());
        queue.setPriority(definition.getPriority());
        return this.save(queue);
    }

}
