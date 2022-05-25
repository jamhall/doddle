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
package dev.doddle.web.providers.servlet;

import dev.doddle.common.support.NotNull;
import dev.doddle.core.DoddleClient;
import dev.doddle.web.controllers.CronJobController;
import dev.doddle.web.controllers.JobController;
import dev.doddle.web.controllers.QueueController;
import dev.doddle.web.controllers.TaskController;
import dev.doddle.web.http.HttpAdapter;
import dev.doddle.web.routing.Router;
import dev.doddle.web.routing.RouterProvider;
import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DoddleWebServlet extends AbstractHttpServlet implements Servlet {

    private final Router router;

    /**
     * Create a new doddle web servlet
     */
    public DoddleWebServlet(@NotNull final DoddleClient client) {
        this.router = createRouter(
            new JobController(client),
            new CronJobController(client),
            new TaskController(client),
            new QueueController(client)
        );
    }

    /**
     * Handle the incoming request
     *
     * @param request  the http servlet request
     * @param response the http servlet response
     */
    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) {
        final HttpAdapter adapter = new ServletHttpAdapter(request, response);
        this.router.handle(adapter.read());
    }



    /**
     * Create a new router
     *
     * @param jobController     the job controller for mapping job API requests
     * @param cronJobController the cron job controller for mapping cron job API requests
     * @param taskController    the task controller for mapping task API requests
     * @param queueController   the queue controller for mapping queue API requests
     * @return a new router
     */
    private Router createRouter(final JobController jobController,
                                final CronJobController cronJobController,
                                final TaskController taskController,
                                final QueueController queueController) {
        final RouterProvider provider = new RouterProvider(
            jobController,
            cronJobController,
            taskController,
            queueController);
        return provider.apply();
    }
}
