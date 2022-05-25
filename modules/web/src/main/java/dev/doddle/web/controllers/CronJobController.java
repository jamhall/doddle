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
import dev.doddle.core.exceptions.DoddleException;
import dev.doddle.storage.common.domain.CronJob;
import dev.doddle.web.dtos.CronJobCreateDto;
import dev.doddle.web.dtos.CronJobDto;
import dev.doddle.web.dtos.CronJobListDto;
import dev.doddle.web.dtos.CronJobUpdateDto;
import dev.doddle.web.exceptions.BadRequestException;
import dev.doddle.web.exceptions.NotFoundException;
import dev.doddle.web.http.HttpRequest;
import dev.doddle.web.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static dev.doddle.web.http.HttpStatus.CREATED;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class CronJobController {

    private static final Logger       logger = LoggerFactory.getLogger(CronJobController.class);
    private final        DoddleClient client;

    /**
     * Create a new cron job controller
     *
     * @param client the doddle client
     */
    public CronJobController(final DoddleClient client) {
        this.client = requireNonNull(client, "client cannot be null");
    }

    /**
     * Fetch all cron jobs
     *
     * @param request the http request
     */
    public void all(final HttpRequest request) {
        final HttpResponse response = request.response();
        final List<CronJobDto> jobs = client.crons()
            .all()
            .stream()
            .map(CronJobDto::new)
            .collect(toList());
        response.json(new CronJobListDto(jobs));
    }

    /**
     * Count the number of cron jobs
     *
     * @param request the http request
     */
    public void count(final HttpRequest request) {
        final HttpResponse response = request.response();
        final Integer count = client.crons().count();
        final ObjectNode node = response.mapper().createObjectNode();
        node.put("count", count);
        response.json(count);
    }

    /**
     * Create a cron job
     *
     * @param request the http request
     */
    public void create(final HttpRequest request) {
        try {
            final HttpResponse response = request.response();
            final CronJobCreateDto dto = request.body(CronJobCreateDto.class);
            final CronJob job = client.schedule(wizard -> {
                wizard.name(dto.getName())
                    .description(dto.getDescription())
                    .expression(dto.getExpression())
                    .enabled(dto.isEnabled())
                    .handler(dto.getHandler())
                    .queue(dto.getQueue())
                    .timeout(dto.getTimeout());
                return wizard;
            });
            response.status(CREATED).json(new CronJobDto(job));
        } catch (DoddleException exception) {
            throw new BadRequestException(exception.getMessage());
        }
    }

    /**
     * Delete a cron job
     *
     * @param request the http request
     */
    public void delete(final HttpRequest request) {
        final HttpResponse response = request.response();
        final String id = request.parameter(":id");
        client.crons().id(id).ifPresentOrElse(job -> {
            client.crons().delete(id);
            response.json("Deleted cron job");
        }, () -> {
            throw new NotFoundException(format("Cron job not found for id: %s ", id));
        });
    }

    /**
     * Delete all jobs
     *
     * @param request the http request
     */
    public void deleteAll(final HttpRequest request) {
        final HttpResponse response = request.response();
        final Integer total = client.crons().count();
        client.crons().deleteAll();
        response.json(format("Deleted %d crons", total));
    }

    /**
     * Update a cron job
     *
     * @param request the http request
     */
    public void update(final HttpRequest request) {
        try {
            final HttpResponse response = request.response();
            final String id = request.parameter(":id");
            final CronJobUpdateDto dto = request.body(CronJobUpdateDto.class);
            this.client.crons().id(id).ifPresentOrElse(job -> {
                client.crons().update(id, wizard -> {
                    wizard.name(dto.getName())
                        .description(dto.getDescription())
                        .expression(dto.getExpression())
                        .enabled(dto.isEnabled())
                        .handler(dto.getHandler())
                        .queue(dto.getQueue())
                        .timeout(dto.getTimeout());
                    return wizard;
                });
                this.client.crons().id(id).ifPresent(cron -> response.json(new CronJobDto(cron)));
            }, () -> {
                throw new NotFoundException(format("Cron job not found for id: %s ", id));
            });
        } catch (DoddleException exception) {
            throw new BadRequestException(exception.getMessage());
        }
    }
}
