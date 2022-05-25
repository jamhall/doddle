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

import dev.doddle.storage.common.domain.CronJob;
import dev.doddle.storage.common.domain.Pageable;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

public interface CronJobMapper {

    /**
     * Count all cron jobs
     *
     * @return a count of all cron jobs matching the filter
     */
    Integer countAll();

    /**
     * Create a cron job
     *
     * @param job the cron job to create
     */
    void create(@Param("job") CronJob job);

    /**
     * Delete a cron job
     *
     * @param job the cron job to delete
     */
    void delete(@Param("job") CronJob job);

    /**
     * Delete all cron jobs
     */
    void deleteAll();

    /**
     * Get all cron jobs
     *
     * @return a list of cron jobs
     */
    List<CronJob> getAll(@Param("pageable") Pageable pageable);

    List<CronJob> getAll();

    /**
     * Get a cron job by its id
     *
     * @param id the job id
     * @return the job if found
     */
    Optional<CronJob> getById(String id);

    /**
     * Get a cron job by its name
     *
     * @param name the job name
     * @return the job if found
     */
    Optional<CronJob> getByName(String name);

    /**
     * Schedule  cron jobs
     */
    void schedule();

    /**
     * Update a cron job
     *
     * @param job the cron job to update
     */
    void update(@Param("job") CronJob job);
}
