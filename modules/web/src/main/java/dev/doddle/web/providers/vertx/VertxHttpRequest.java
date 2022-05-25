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
package dev.doddle.web.providers.vertx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.doddle.common.support.NotNull;
import dev.doddle.web.exceptions.HttpException;
import dev.doddle.web.http.HttpQueryParameter;
import dev.doddle.web.http.HttpRequest;
import dev.doddle.web.http.HttpResponse;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class VertxHttpRequest implements HttpRequest {

    private final ObjectMapper        mapper;
    private final RoutingContext      context;
    private final HttpResponse        response;
    private       Map<String, String> parameters;

    public VertxHttpRequest(final ObjectMapper mapper,
                            final RoutingContext context) {
        this.mapper = mapper;
        this.context = context;
        this.response = new VertxHttpResponse(mapper, context.response());
    }

    @Override
    public String body() {
        return this.context.getBodyAsString();
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

    @Override
    public String method() {
        return this.context.request().method().toString();
    }

    @Override
    public String parameter(String name) {
        return this.parameters.get(name);
    }

    @Override
    public String parameter(String name, String defaultValue) {
        return this.parameters.getOrDefault(name, defaultValue);
    }

    @Override
    public void parameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public Map<String, String> parameters() {
        return this.parameters;
    }

    @Override
    public HttpQueryParameter query(final String name) {
        final List<String> parameters = context.queryParam(name);
        if (parameters == null || parameters.size() == 0) {
            return new HttpQueryParameter(null);
        }
        final String parameter = parameters.get(0);
        return new HttpQueryParameter(parameter);
    }

    @Override
    public HttpResponse response() {
        return this.response;
    }

    @Override
    public String uri() {
        return this.context.request().path();
    }

}
