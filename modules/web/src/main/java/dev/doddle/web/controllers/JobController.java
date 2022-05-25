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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.doddle.core.DoddleClient;
import dev.doddle.core.exceptions.DoddleException;
import dev.doddle.storage.common.domain.Job;
import dev.doddle.storage.common.domain.JobState;
import dev.doddle.storage.common.domain.JobStatistic;
import dev.doddle.storage.common.domain.Pageable;
import dev.doddle.web.dtos.*;
import dev.doddle.web.exceptions.BadRequestException;
import dev.doddle.web.exceptions.NotFoundException;
import dev.doddle.web.http.HttpQueryParameter;
import dev.doddle.web.http.HttpRequest;
import dev.doddle.web.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static dev.doddle.web.http.HttpStatus.CREATED;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class JobController {

    private static final Logger       logger = LoggerFactory.getLogger(JobController.class);
    private final        DoddleClient client;

    /**
     * Create a new job controller
     *
     * @param client the doddle client
     */
    public JobController(final DoddleClient client) {
        this.client = requireNonNull(client, "client cannot be null");
    }

    /**
     * Get all jobs
     *
     * @param request the http request
     */
    public void all(final HttpRequest request) {

        final HttpResponse response = request.response();
        final Long offset = request.query("offset").asLong(0L);
        final Long limit = request.query("limit").asLong(25L);
        final String name = request.query("name").asString();
        final String identifier = request.query("identifier").asString();
        final List<JobState> states = request.query("states").asList().stream()
            .map(JobState::fromName)
            .collect(toList());
        final List<String> queues = request.query("queues").asList();
        final List<JobDto> jobs = client.jobs().search(filter -> {
                filter.states(states)
                    .queues(queues)
                    .name(name)
                    .identifier(identifier);
                return filter;
            }, new Pageable(offset, limit)
        ).stream().map(job -> {
            try {
                final ObjectMapper mapper = response.mapper();
                final JobDataDto data = mapper.readValue(job.getData(), JobDataDto.class);
                return new JobDto(job, data);
            } catch (JsonProcessingException e) {
                throw new DoddleException("Unable to decode data");
            }
        }).collect(toList());
        final Long count = client.jobs().count((filter) -> {
            filter.states(states)
                .queues(queues);
            return filter;
        });
        response.json(new JobListDto(count, limit, offset, jobs));
    }

    /**
     * Cancel a job
     *
     * @param request the http request
     */
    public void cancel(final HttpRequest request) {
        final HttpResponse response = request.response();
        final String id = request.parameter(":id");
        client.jobs().id(id).ifPresentOrElse(job -> {
            client.jobs().cancel(job);
            response.status(CREATED).json("Discarded job");
        }, () -> {
            throw new NotFoundException(format("Job not found for id: %s", id));
        });
    }

    /**
     * Count jobs
     *
     * @param request the http request
     */
    public void count(final HttpRequest request) {
        final HttpResponse response = request.response();
        final List<JobState> states = request.query("states").asList()
            .stream()
            .map(JobState::valueOf)
            .collect(toList());
        final List<String> queues = request.query("queues").asList();
        final String name = request.query("name").asString();
        final String identifier = request.query("identifier").asString();
        final Long count = client.jobs().count((filter) -> {
            filter.states(states);
            filter.queues(queues);
            filter.name(name);
            filter.identifier(identifier);
            return filter;
        });
        final ObjectNode node = response.mapper().createObjectNode();
        node.put("count", count);
        response.json(node);
    }

    /**
     * Create a new job
     *
     * @param request the http request
     */
    public void create(final HttpRequest request) {
        try {
            final HttpResponse response = request.response();
            final JobCreateDto dto = request.body(JobCreateDto.class);
            final Job job = client.enqueueIn(wizard -> {
                wizard.period(dto.getPeriod())
                    .queue(dto.getQueue())
                    .task(dto.getHandler());
                return wizard;
            });
            final JobDataDto data = response.mapper().readValue(job.getData(), JobDataDto.class);
            response.status(CREATED).json(new JobDto(job, data));
        } catch (DoddleException | JsonProcessingException exception) {
            throw new BadRequestException(exception.getMessage());
        }
    }

    /**
     * Delete a job
     *
     * @param request the http request
     */
    public void delete(final HttpRequest request) {
        final HttpResponse response = request.response();
        final String id = request.parameter(":id");
        client.jobs().id(id).ifPresentOrElse(job -> {
            client.jobs().delete(id);
            response.status(CREATED).json("Deleted job");
        }, () -> {
            throw new NotFoundException(format("Job not found for id: %s ", id));
        });
    }

    /**
     * Delete all jobs
     *
     * @param request the http request
     */
    public void deleteAll(final HttpRequest request) {
        final HttpResponse response = request.response();
        final HttpQueryParameter period = request.query("period");
        client.jobs().deleteAll(period.asString("0m"));
        if (period.isNull()) {
            response.json("Deleted all jobs");
        } else {
            response.json(format("Deleted jobs older than %s", period.asString()));
        }
    }

    /**
     * Discard a job
     *
     * @param request the http request
     */
    public void discard(final HttpRequest request) {
        final HttpResponse response = request.response();
        final String id = request.parameter(":id");
        client.jobs().id(id).ifPresentOrElse(job -> {
            client.jobs().discard(job);
            response.status(CREATED).json("Discarded job");
        }, () -> {
            throw new NotFoundException(format("Job not found for id: %s", id));
        });
    }

    /**
     * Get a job
     *
     * @param request the http request
     */
    public void get(final HttpRequest request) {
        try {
            final HttpResponse response = request.response();
            final String id = request.parameter(":id");
            final Job job = client.jobs().id(id).orElse(null);
            if (job == null) {
                throw new NotFoundException(format("Job not found for id: %s", id));
            }
            final JobDataDto data = response.mapper().readValue(job.getData(), JobDataDto.class);
            response.json(new JobDto(job, data));
        } catch (DoddleException | JsonProcessingException exception) {
            throw new BadRequestException(exception.getMessage());
        }
    }

    /**
     * Get all messages for a job
     *
     * @param request the http request
     */
    public void messages(final HttpRequest request) {
        final HttpResponse response = request.response();
        final String id = request.parameter(":id");
        final Integer offset = request.query("offset").asInt(0);
        final Integer limit = request.query("limit").asInt(25);
        final List<String> levels = request.query("levels").asList();
        final String message = request.query("message").asString();
        client.jobs().id(id).ifPresentOrElse(job -> {
            final List<JobMessageDto> messages = client.jobs()
                .messages()
                .all(job, (filter) -> {
                    filter.setLevels(levels);
                    filter.setMessage(message);
                    return filter;
                }, new Pageable(offset, limit))
                .stream()
                .map(JobMessageDto::new)
                .collect(toList());
            response.json(messages);
        }, () -> {
            throw new NotFoundException(format("Job not found for id: %s", id));
        });
    }

    /**
     * Schedule a job to be retried
     *
     * @param request the http request
     */
    public void retry(final HttpRequest request) {
        final HttpResponse response = request.response();
        final String id = request.parameter(":id");
        client.jobs().id(id).ifPresentOrElse(job -> {
            final HttpQueryParameter period = request.query("period");
            client.jobs().retry(id, period.asString("0s"));
            if (period.isNull()) {
                response.status(CREATED).json("Job will be retried immediately");
            } else {
                response.status(CREATED).json(format("Job will be retried in %s", period));
            }
        }, () -> {
            throw new NotFoundException(format("Job not found for id: %s", id));
        });
    }

    public void statistics(final HttpRequest request) {
        final HttpResponse response = request.response();
        final JobStatistic statistics = client.jobs().statistics();
        response.json(statistics);
    }


}
