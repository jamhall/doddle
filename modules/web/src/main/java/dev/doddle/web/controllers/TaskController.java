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
package dev.doddle.web.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.doddle.core.DoddleClient;
import dev.doddle.web.dtos.TaskListDto;
import dev.doddle.web.http.HttpRequest;
import dev.doddle.web.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class TaskController {

    private static final Logger       logger = LoggerFactory.getLogger(TaskController.class);
    private final        DoddleClient client;

    public TaskController(final DoddleClient client) {
        this.client = requireNonNull(client, "client cannot be null");
    }

    /**
     * Get all tasks
     *
     * @param request the http request
     */
    public void all(final HttpRequest request) {
        final HttpResponse response = request.response();
        final List<String> tasks = client.jobs().tasks();
        response.json(new TaskListDto(tasks));
    }

    /**
     * Count all tasks
     *
     * @param request the http request
     */
    public void count(final HttpRequest request) {
        final HttpResponse response = request.response();
        final int count = client.jobs().tasks().size();
        final ObjectNode node = response.mapper().createObjectNode();
        node.put("count", count);
        response.json(node);
    }

}
