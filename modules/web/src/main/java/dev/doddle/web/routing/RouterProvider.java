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
package dev.doddle.web.routing;

import dev.doddle.common.support.NotNull;
import dev.doddle.web.controllers.CronJobController;
import dev.doddle.web.controllers.JobController;
import dev.doddle.web.controllers.QueueController;
import dev.doddle.web.controllers.TaskController;
import dev.doddle.web.http.HttpResponse;

import static java.util.Objects.requireNonNull;

public class RouterProvider {

    private final String            prefix;
    private final JobController     jobController;
    private final CronJobController cronJobController;
    private final TaskController    taskController;
    private final QueueController   queueController;

    /**
     * Create a new router provider
     *
     * @param prefix            if the routes require a prefix
     * @param jobController     the job controller
     * @param cronJobController the cron job controller
     * @param taskController    the task controller
     * @param queueController   the queue controller
     */
    public RouterProvider(@NotNull final String prefix,
                          @NotNull final JobController jobController,
                          @NotNull final CronJobController cronJobController,
                          @NotNull final TaskController taskController,
                          @NotNull final QueueController queueController) {
        this.prefix = requireNonNull(prefix, "prefix cannot be null");
        this.jobController = requireNonNull(jobController, "jobController cannot be null");
        this.cronJobController = requireNonNull(cronJobController, "cronJobController cannot be null");
        this.taskController = requireNonNull(taskController, "taskController cannot be null");
        this.queueController = queueController;
    }

    public RouterProvider(@NotNull final JobController jobController,
                          @NotNull final CronJobController cronJobController,
                          @NotNull final TaskController taskController,
                          @NotNull final QueueController queueController) {
        this("", jobController, cronJobController, taskController, queueController);
    }

    /**
     * Configure the router with valid routes
     *
     * @return the configured router
     */
    public Router apply() {
        final Router router = new Router(prefix);
        router.get("", (request) -> {
            final HttpResponse response = request.response();
            response.sendFile("index.html");
        });
        // job operations
        router.get("/api/jobs", jobController::all);
        router.post("/api/jobs", jobController::create);
        router.get("/api/jobs/_count", jobController::count);
        router.get("/api/jobs/_stats", jobController::statistics);
        router.delete("/api/jobs", jobController::deleteAll);
        router.get("/api/jobs/:id", jobController::get);
        router.delete("/api/jobs/:id", jobController::delete);
        router.get("/api/jobs/:id/messages", jobController::messages);
        router.put("/api/jobs/:id/_discard", jobController::discard);
        router.put("/api/jobs/:id/_cancel", jobController::cancel);
        router.put("/api/jobs/:id/_retry", jobController::retry);
        // cron operations
        router.get("/api/crons", cronJobController::all);
        router.get("/api/crons/_count", cronJobController::count);
        router.delete("/api/crons/_delete", cronJobController::deleteAll);
        router.delete("/api/crons/:id", cronJobController::delete);
        router.put("/api/crons/:id", cronJobController::update);
        router.post("/api/crons", cronJobController::create);
        // task operations
        router.get("/api/tasks", taskController::all);
        router.get("/api/tasks/_count", taskController::count);
        // queue operations
        router.get("/api/queues", queueController::all);
        router.post("/api/queues", queueController::create);
        router.put("/api/queues/_lock", queueController::lockAll);
        router.put("/api/queues/_unlock", queueController::unlockAll);
        router.delete("/api/queues", queueController::deleteAll);
        router.delete("/api/queues/:id", queueController::delete);
        router.put("/api/queues/:id", queueController::update);
        router.put("/api/queues/:id/_unlock", queueController::unlock);
        router.put("/api/queues/:id/_lock", queueController::lock);
        router.get("/api/queues/_count", queueController::count);

        return router;
    }
}
