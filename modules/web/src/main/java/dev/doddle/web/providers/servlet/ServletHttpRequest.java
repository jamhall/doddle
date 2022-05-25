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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.doddle.common.support.NotNull;
import dev.doddle.web.exceptions.HttpException;
import dev.doddle.web.http.HttpQueryParameter;
import dev.doddle.web.http.HttpRequest;
import dev.doddle.web.http.HttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public class ServletHttpRequest implements HttpRequest {

    private final HttpServletRequest  request;
    private final HttpResponse        response;
    private final ObjectMapper        mapper;
    private       Map<String, String> parameters;

    /**
     * Create a new http request
     */
    public ServletHttpRequest(
        final ObjectMapper mapper,
        final HttpServletRequest request,
        final HttpServletResponse response) {
        this.mapper = requireNonNull(mapper, "mapper cannot be null");
        this.request = requireNonNull(request, "request cannot be null");
        this.response = new ServletHttpResponse(
            mapper,
            requireNonNull(response, "response cannot be null")
        );
    }

    @Override
    public String body() {
        try {
            return this.request.getReader().lines().collect(joining());
        } catch (IOException exception) {
            throw new HttpException("Could not get body", exception);
        }
    }

    @Override
    public <T> T body(@NotNull final Class<T> object) {
        try {
            final String body = this.body();
            requireNonNull(object, "object cannot be null");
            return this.mapper.readValue(body, object);
        } catch (JsonProcessingException exception) {
            throw new HttpException(format("Could not deserialise data into the given object: %s", object.getSimpleName()), exception);
        }
    }

    /**
     * Get the http method
     *
     * @return the http method
     */
    @Override
    public String method() {
        return this.request.getMethod();
    }

    /**
     * Get a route parameter
     *
     * @param name the parameter name
     * @return the route parameter value
     */
    @Override
    public String parameter(final String name) {
        return this.parameters.get(name);
    }

    /**
     * Get a route parameter
     *
     * @param name         the parameter name
     * @param defaultValue the default parameter value
     * @return the route parameter value
     */
    @Override
    public String parameter(final String name, final String defaultValue) {
        return this.parameters.getOrDefault(name, defaultValue);
    }

    /**
     * Get the route parameters
     *
     * @return the router parameters
     */
    @Override
    public Map<String, String> parameters() {
        return this.parameters;
    }

    @Override
    public void parameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Get a query parameter
     *
     * @param name the parameter name
     * @return the parameter value
     */
    @Override
    public HttpQueryParameter query(final String name) {
        return new HttpQueryParameter(this.request.getParameter(name));
    }

    @Override
    public HttpResponse response() {
        return this.response;
    }

    /**
     * Get the incoming request uri
     *
     * @return the uri
     */
    @Override
    public String uri() {
        return this.request.getPathInfo();
    }

}
