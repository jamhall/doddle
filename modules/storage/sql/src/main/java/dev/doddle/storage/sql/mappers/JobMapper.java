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
import dev.doddle.storage.common.domain.JobFilter;
import dev.doddle.storage.common.domain.JobStatistic;
import dev.doddle.storage.common.domain.Pageable;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JobMapper {

    /**
     * Count all jobs for a given filter
     *
     * @param filter the filter to use
     * @return a count of all jobs matching the filter
     */
    Long countAll(@Param("filter") JobFilter filter);

    /**
     * Create a job
     *
     * @param job the job to create
     */
    void create(@Param("job") Job job);

    /**
     * Delete a job
     *
     * @param job the job to delete
     */
    void delete(@Param("job") Job job);

    /**
     * Delete all jobs
     */
    void deleteAll(@Param("date") LocalDateTime date);

    /**
     * Enqueue all jobs that ready to be processed
     *
     * @return the jobs that were enqueued
     */
    List<Job> enqueue();

    /**
     * Get all jobs
     *
     * @return a list of jobs
     */
    List<Job> getAll(@Param("filter") JobFilter filter, @Param("pageable") Pageable pageable);

    JobStatistic getStatistics();

    List<String> getAllTasks();

    /**
     * Get a job by its id
     *
     * @param id the job id
     * @return the job if found
     */
    Optional<Job> getById(String id);

    /**
     * Get a job by its identifier
     *
     * @param identifier the job identifier
     * @return the job if found
     */
    Optional<Job> getByIdentifier(String identifier);

    /**
     * Pick a job
     *
     * @return a job if found
     */
    Optional<Job> pick();

    /**
     * Update a job
     *
     * @param job the job to update
     */
    void update(@Param("job") Job job);

}
