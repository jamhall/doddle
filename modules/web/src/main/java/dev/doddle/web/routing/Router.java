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
import dev.doddle.web.dtos.ErrorMessageDto;
import dev.doddle.web.exceptions.BadRequestException;
import dev.doddle.web.exceptions.NotFoundException;
import dev.doddle.web.http.HttpMethod;
import dev.doddle.web.http.HttpRequest;
import dev.doddle.web.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static dev.doddle.web.http.HttpMethod.*;
import static dev.doddle.web.http.HttpStatus.*;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

public class Router {

    private final static Logger       logger = LoggerFactory.getLogger(Router.class);
    private final        List<Route>  routes;
    private final        RouteHandler notFoundHandler;
    private final        RouteHandler invalidMethodHandler;
    private final        RouteHandler assetHandler;
    private final        String       prefix;

    /**
     * Create a new http router
     *
     * @param prefix route prefix
     */
    public Router(@NotNull final String prefix) {
        this.prefix = requireNonNull(prefix, "prefix cannot be null");
        this.routes = new ArrayList<>();
        this.notFoundHandler = (request) -> {
            final HttpResponse response = request.response();
            response.status(NOT_FOUND);
            response.header("Content-Type", "text/plain; charset=UTF-8");
            response.send("Not found");
        };
        this.invalidMethodHandler = (request) -> {
            final HttpResponse response = request.response();
            response.status(INTERNAL_SERVER_ERROR);
            response.header("Content-Type", "text/plain; charset=UTF-8");
            response.send("Unsupported method");
        };
        this.assetHandler = (request) -> {
            final HttpResponse response = request.response();
            response.send(request.uri().substring(1));
        };
    }

    /**
     * Add a delete url with a handler
     *
     * @param path    the path to match
     * @param handler the handler to process the request
     * @return this
     */
    public Router delete(final String path, final RouteHandler handler) {
        add(DELETE, path, handler);
        return this;
    }

    /**
     * Add a get url with a handler
     *
     * @param path    the path to match
     * @param handler the handler to process the request
     * @return this
     */
    public Router get(final String path, final RouteHandler handler) {
        add(GET, path, handler);
        return this;
    }

    /**
     * Handle the incoming request
     *
     * @param request the incoming the request
     */
    public void handle(final HttpRequest request) {
        try {
            final String uri = requireNonNullElse(request.uri(), "");
            final HttpMethod method = HttpMethod.get(request.method());
            if (method == UNSUPPORTED) {
                invalidMethodHandler.handle(request);
            } else {
                if (uri.startsWith("/assets")) {
                    assetHandler.handle(request);
                } else {
                    final RouteMatch route = route(method, uri);
                    if (route == null) {
                        notFoundHandler.handle(request);
                    } else {
                        request.parameters(route.parameters());
                        route.handler().handle(request);
                    }
                }
            }
        } catch (Exception exception) {
            logger.error("Error handling route", exception);
            handleError(request, exception);
        }
    }

    /**
     * Add a post url with a handler
     *
     * @param path    the path to match
     * @param handler the handler to process the request
     * @return this
     */
    public Router post(final String path, final RouteHandler handler) {
        add(POST, path, handler);
        return this;
    }

    /**
     * Add a put url with a handler
     *
     * @param path    the path to match
     * @param handler the handler to process the request
     * @return this
     */
    public Router put(final String path, final RouteHandler handler) {
        add(PUT, path, handler);
        return this;
    }

    /**
     * Add a new route
     *
     * @param method  the http method
     * @param path    the path
     * @param handler the handler
     */
    private void add(final HttpMethod method, final String path, final RouteHandler handler) {
        final String normalisedPath = format("%s%s", this.prefix, path);
        final Route route = new Route(method, normalisedPath, handler);
        if (this.routes.contains(route)) {
            throw new IllegalArgumentException("Route for the given path and method has already been registered");
        }
        this.routes.add(route);
    }

    /**
     * Handle any exceptions thrown during the execution of a route
     *
     * @param request   the http request
     * @param throwable the thrown exception
     */
    private void handleError(final HttpRequest request, final Throwable throwable) {
        final HttpResponse response = request.response();
        if (throwable instanceof NotFoundException) {
            response.status(NOT_FOUND);
            response.json(
                new ErrorMessageDto(NOT_FOUND, throwable.getMessage())
            );
        } else if (throwable instanceof BadRequestException) {
            response.status(BAD_REQUEST);
            response.json(
                new ErrorMessageDto(BAD_REQUEST, throwable.getMessage())
            );
        } else {
            response.status(INTERNAL_SERVER_ERROR);
            logger.debug(throwable.getMessage(), throwable);
            response.json(
                new ErrorMessageDto(INTERNAL_SERVER_ERROR, throwable.getMessage())
            );
        }
    }

    private RouteMatch route(final HttpMethod method, final String uri) {
        for (final Route route : this.routes) {
            if (route.matches(method, uri)) {
                return new RouteMatch(
                    route.path(),
                    uri,
                    route.handler()
                );
            }
        }
        return null;
    }

}
