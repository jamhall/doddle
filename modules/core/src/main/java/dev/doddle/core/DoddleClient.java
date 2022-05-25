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
package dev.doddle.core;

import dev.doddle.common.support.NotNull;
import dev.doddle.core.engine.polling.PollingManager;
import dev.doddle.core.engine.scheduling.SchedulingManager;
import dev.doddle.core.engine.task.TaskDescriptor;
import dev.doddle.core.services.CronJobService;
import dev.doddle.core.services.JobService;
import dev.doddle.core.services.QueueService;
import dev.doddle.core.services.TaskService;
import dev.doddle.core.support.EnqueueInWizard;
import dev.doddle.core.support.EnqueueWizard;
import dev.doddle.core.support.ScheduleWizard;
import dev.doddle.storage.common.domain.CronJob;
import dev.doddle.storage.common.domain.Job;

import java.util.List;
import java.util.function.Function;

import static dev.doddle.core.support.Objects.requireNonNull;

public class DoddleClient {

    private final CronJobService    cronJobService;
    private final QueueService      queueService;
    private final TaskService       taskService;
    private final PollingManager    polling;
    private final JobService        jobService;
    private final SchedulingManager scheduling;

    /**
     * Create a new doddle client
     */
    public DoddleClient(@NotNull final JobService jobService,
                        @NotNull final CronJobService cronJobService,
                        @NotNull final QueueService queueService,
                        @NotNull final TaskService taskService,
                        @NotNull final PollingManager polling,
                        @NotNull final SchedulingManager scheduling) {
        this.jobService = requireNonNull(jobService, "jobService cannot be null");
        this.cronJobService = requireNonNull(cronJobService, "cronJobService cannot be null");
        this.queueService = requireNonNull(queueService, "queueService cannot be null");
        this.taskService = requireNonNull(taskService, "taskService cannot be null");
        this.polling = requireNonNull(polling, "polling cannot be null");
        this.scheduling = requireNonNull(scheduling, "scheduling cannot be null");
    }

    public CronJobService crons() {
        return this.cronJobService;
    }

    public Job enqueue(@NotNull final Function<EnqueueWizard, EnqueueWizard> wizard) {
        return this.jobService.enqueue(wizard);
    }

    public Job enqueueIn(@NotNull final Function<EnqueueInWizard, EnqueueInWizard> wizard) {
        return this.jobService.enqueueIn(wizard);
    }

    public JobService jobs() {
        return this.jobService;
    }

    /**
     * Get the polling manager
     *
     * @return the polling manager
     */
    public PollingManager polling() {
        return this.polling;
    }

    public QueueService queues() {
        return this.queueService;
    }

    public CronJob schedule(@NotNull final Function<ScheduleWizard, ScheduleWizard> wizard) {
        return this.cronJobService.schedule(
            requireNonNull(wizard, "input cannot be null")
        );
    }

    public CronJob schedule(@NotNull final ScheduleWizard wizard) {
        return this.cronJobService.schedule(
            requireNonNull(wizard, "wizard cannot be null")
        );
    }
    /**
     * Get the scheduling manager
     *
     * @return the scheduling manager
     */
    public SchedulingManager scheduling() {
        return this.scheduling;
    }

    /**
     * Get a list of all tasks (handlers)
     *
     * @return a list of tasks
     */
    public List<TaskDescriptor> tasks() {
        return this.taskService.getAll();
    }

}
