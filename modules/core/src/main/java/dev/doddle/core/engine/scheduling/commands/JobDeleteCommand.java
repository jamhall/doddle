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
package dev.doddle.core.engine.scheduling.commands;

import dev.doddle.common.support.NotNull;
import dev.doddle.core.services.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.doddle.core.support.Objects.requireNonNull;

public class JobDeleteCommand implements SchedulerCommand {

    private final Logger     logger = LoggerFactory.getLogger(JobDeleteCommand.class);
    private final JobService jobService;
    private final String     period;

    /**
     * Create a new job delete command
     *
     * @param jobService the job service
     * @param period     jobs to delete that are older than this given period
     */
    public JobDeleteCommand(@NotNull final JobService jobService,
                            @NotNull final String period) {
        this.jobService = requireNonNull(jobService, "jobService cannot be null");
        this.period = period;
    }

    @Override
    public void execute() {
        if (!period.isEmpty()) {
            logger.debug("Deleting jobs older than {}", period);
            jobService.deleteAll(period);
        }
    }
}
