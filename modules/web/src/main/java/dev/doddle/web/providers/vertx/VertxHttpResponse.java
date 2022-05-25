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
import dev.doddle.web.http.HttpResponse;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

import static dev.doddle.web.http.HttpStatus.NOT_FOUND;
import static java.util.Objects.requireNonNull;

public class VertxHttpResponse implements HttpResponse {

    private static final Logger             logger = LoggerFactory.getLogger(VertxHttpResponse.class);
    private final        HttpServerResponse response;
    private final        ObjectMapper       mapper;

    public VertxHttpResponse(final ObjectMapper mapper, final HttpServerResponse response) {
        this.mapper = requireNonNull(mapper, "mapper cannot be null");
        this.response = requireNonNull(response, "response cannot be null");
    }

    @Override
    public HttpResponse header(String name, String value) {
        this.response.putHeader(
            requireNonNull(name, "name cannot be null"),
            requireNonNull(value, "value cannot be null"));
        return this;
    }

    @Override
    public HttpResponse json(final Object data) {
        try {
            final String json = mapper.writeValueAsString(data);
            header("Content-Type", "application/json");
            send(json);
        } catch (JsonProcessingException exception) {
            logger.error("Error converting to json", exception);
            response.setStatusCode(500);
            send("Error converting to json");
        }
        return this;
    }

    @Override
    public ObjectMapper mapper() {
        return this.mapper;
    }

    @Override
    public void notFound(String message) {
        this.status(NOT_FOUND);
        this.header("Content-Type", "text/plain; charset=UTF-8");
        this.send(message);
    }

    @Override
    public HttpResponse send(String data) {
        this.response.end(data);
        return this;
    }

    @Override
    public HttpResponse sendFile(final String file) {
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL url = classLoader.getResource(file);
        if (url == null) {
            this.notFound("File not found");
        } else {
            this.response
                .putHeader(HttpHeaders.TRANSFER_ENCODING, "chunked")
                .sendFile(file);
        }
        return this;
    }

    @Override
    public HttpResponse status(int code) {
        this.response.setStatusCode(code);
        return this;
    }

}
