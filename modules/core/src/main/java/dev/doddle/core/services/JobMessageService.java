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
import dev.doddle.storage.common.Storage;
import dev.doddle.storage.common.StorageException;
import dev.doddle.storage.common.domain.Job;
import dev.doddle.storage.common.domain.JobMessage;
import dev.doddle.storage.common.domain.JobMessageFilter;
import dev.doddle.storage.common.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static dev.doddle.core.support.Objects.requireNonNull;

public class JobMessageService {

    private final Storage storage;

    public JobMessageService(@NotNull final Storage storage) {
        this.storage = storage;
    }

    public List<JobMessage> all(@NotNull final Job job) {
        try {
            return storage.getAllMessagesForJob(
                requireNonNull(job, "job cannot be null")
            );
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    public List<JobMessage> all(@NotNull final Job job, @NotNull final Supplier<JobMessageFilter> filter,
                                @NotNull final Pageable pageable) {
        try {
            return this.storage.getAllMessagesForJob(
                requireNonNull(job, "job cannot be be null"),
                requireNonNull(filter, "filter cannot be null").get(),
                requireNonNull(pageable, "pageable cannot be null")
            );
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }

    public List<JobMessage> all(@NotNull final Job job,
                                @NotNull final Function<JobMessageFilter, JobMessageFilter> filter,
                                @NotNull final Pageable pageable) {
        try {
            return this.storage.getAllMessagesForJob(
                requireNonNull(job, "job cannot be be null"),
                filter.apply(new JobMessageFilter()),
                requireNonNull(pageable, "pageable cannot be null")
            );
        } catch (StorageException exception) {
            throw new DoddleException(exception);
        }
    }


}
