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
import dev.doddle.storage.common.domain.Queue;
import dev.doddle.web.dtos.QueueCreateDto;
import dev.doddle.web.dtos.QueueDto;
import dev.doddle.web.dtos.QueueListDto;
import dev.doddle.web.dtos.QueueUpdateDto;
import dev.doddle.web.exceptions.BadRequestException;
import dev.doddle.web.exceptions.NotFoundException;
import dev.doddle.web.http.HttpRequest;
import dev.doddle.web.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

import static dev.doddle.web.http.HttpStatus.CREATED;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class QueueController {

    private static final Logger       logger = LoggerFactory.getLogger(QueueController.class);
    private final        DoddleClient client;

    /**
     * Create a new queue controller
     *
     * @param client the doddle client
     */
    public QueueController(final DoddleClient client) {
        this.client = requireNonNull(client, "client cannot be null");
    }

    /**
     * Get all queues
     *
     * @param request the http request
     */
    public void all(final HttpRequest request) {
        logger.debug("Fetching all queues");
        final HttpResponse response = request.response();
        final List<QueueDto> queues = client.queues().all()
            .stream()
            .map(QueueDto::new)
            .collect(toList());
        response.json(new QueueListDto(queues));
    }

    /**
     * Count all queues
     *
     * @param request the http request
     */
    public void count(final HttpRequest request) {
        logger.debug("Counting all queues");
        final HttpResponse response = request.response();
        final int count = client.queues().count();
        final ObjectNode node = response.mapper().createObjectNode();
        node.put("count", count);
        response.json(node);
    }

    /**
     * Create a queue
     *
     * @param request the http request
     */
    public void create(final HttpRequest request) {
        try {
            logger.debug("Creating a new queue");
            final LocalDateTime now = now();
            final HttpResponse response = request.response();
            final QueueCreateDto dto = request.body(QueueCreateDto.class);
            final Queue queue = client.queues().create(wizard -> {
                return wizard.name(dto.getName())
                    .priority(dto.getPriority());
            });
            response.status(CREATED).json(new QueueDto(queue));
        } catch (DoddleException exception) {
            throw new BadRequestException(exception.getMessage());
        }
    }

    /**
     * Delete a queue
     *
     * @param request the http request
     */
    public void delete(final HttpRequest request) {
        logger.debug("Deleting queue");
        final HttpResponse response = request.response();
        final String id = request.parameter(":id");
        client.queues().id(id).ifPresentOrElse(queue -> {
            client.queues().delete(id);
            response.json("Deleted queue");
        }, () -> {
            throw new NotFoundException(format("Queue not found for id: %s ", id));
        });
    }

    /**
     * Delete all queues
     *
     * @param request the http request
     */
    public void deleteAll(final HttpRequest request) {
        logger.debug("Deleting all queues");
        final HttpResponse response = request.response();
        final Integer total = client.queues().count();
        client.queues().deleteAll();
        response.json(format("Deleted %d queues", total));
    }

    /**
     * Lock a queue
     *
     * @param request the http request
     */
    public void lock(final HttpRequest request) {
        logger.debug("Locking queue");
        final HttpResponse response = request.response();
        final String id = request.parameter(":id");
        client.queues().id(id).ifPresentOrElse(ignore -> {
            client.queues().lock(id);
            client.queues().id(id).ifPresent(queue -> response.json(new QueueDto(queue)));
        }, () -> {
            throw new NotFoundException(format("Queue not found for id: %s", id));
        });
    }

    /**
     * Lock all queues
     *
     * @param request the http request
     */
    public void lockAll(final HttpRequest request) {
        logger.debug("Locking all queues");
        client.queues().lockAll();
        this.all(request);
    }

    /**
     * Unlock a queue
     *
     * @param request the http request
     */
    public void unlock(final HttpRequest request) {
        logger.debug("Unlocking queue");
        final HttpResponse response = request.response();
        final String id = request.parameter(":id");
        client.queues().id(id).ifPresentOrElse(ignore -> {
            client.queues().unlock(id);
            client.queues().id(id).ifPresent(queue -> response.json(new QueueDto(queue)));
        }, () -> {
            throw new NotFoundException(format("Queue not found for id: %s ", id));
        });
    }

    /**
     * Unlock all queues
     *
     * @param request the http request
     */
    public void unlockAll(final HttpRequest request) {
        logger.debug("Unlocking all queues");
        client.queues().unlockAll();
        this.all(request);
    }

    /**
     * Update a queue
     *
     * @param request the http request
     */
    public void update(final HttpRequest request) {
        try {
            logger.debug("Updating queue");
            final HttpResponse response = request.response();
            final String id = request.parameter(":id");
            final QueueUpdateDto dto = request.body(QueueUpdateDto.class);
            this.client.queues().id(id).ifPresentOrElse(ignore -> {
                client.queues().update(id, wizard -> {
                    return wizard.name(dto.getName())
                        .priority(dto.getPriority());
                });
                this.client.queues().id(id).ifPresent(queue -> response.json(new QueueDto(queue)));
            }, () -> {
                throw new NotFoundException(format("Cron job not found for id: %s ", id));
            });
        } catch (DoddleException exception) {
            throw new BadRequestException(exception.getMessage());
        }
    }

}
