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
package dev.doddle.storage.sql.mappers;

import dev.doddle.storage.common.domain.Job;
import dev.doddle.storage.common.domain.JobMessage;
import dev.doddle.storage.common.domain.JobMessageFilter;
import dev.doddle.storage.common.domain.Pageable;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JobMessageMapper {

    /**
     * Count all messages for a given job
     *
     * @param job the job
     * @return the number of messages
     */
    Integer countAll(@Param("job") Job job);

    /**
     * Create a message
     *
     * @param job     the job
     * @param message the message
     */
    void create(@Param("job") Job job, @Param("message") JobMessage message);

    void deleteAll(@Param("job") Job job);

    /**
     * Get all messages a for a given job
     *
     * @param job      the job
     * @param filter   the filter
     * @param pageable paginate the results
     * @return a list of messages
     */
    List<JobMessage> getAll(@Param("job") Job job, @Param("filter") JobMessageFilter filter, @Param("pageable") Pageable pageable);

    /**
     * Get all messages for a given job
     *
     * @param job the job
     * @return a list of messages
     */
    List<JobMessage> getAll(@Param("job") Job job);
}
