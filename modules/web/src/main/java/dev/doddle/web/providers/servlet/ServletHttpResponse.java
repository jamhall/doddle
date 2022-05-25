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
import dev.doddle.web.http.HttpResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static dev.doddle.web.http.HttpStatus.NOT_FOUND;
import static java.util.Objects.requireNonNull;
public class ServletHttpResponse implements HttpResponse {

    private static final Logger              logger = LoggerFactory.getLogger(ServletHttpResponse.class);
    private final        HttpServletResponse response;
    private final        ObjectMapper        mapper;

    public ServletHttpResponse(final ObjectMapper mapper,
                               final HttpServletResponse response) {
        this.mapper = requireNonNull(mapper, "mapper cannot be null");
        this.response = requireNonNull(response, "response cannot be null");
    }

    public Path getPathFromResource(String fileName) {
        try {
            final ClassLoader classLoader = this.getClass().getClassLoader();
            final URL resource = classLoader.getResource(fileName);
            if (resource == null) {
                return null;
            }
            return Paths.get(resource.toURI());
        } catch (URISyntaxException exception) {
            return null;
        }
    }

    @Override
    public HttpResponse header(final String name, final String value) {
        this.response.setHeader(
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
            response.setStatus(500);
            send("Error converting to json");
        }
        return this;
    }

    @Override
    public ObjectMapper mapper() {
        return this.mapper;
    }

    @Override
    public void notFound(final String message) {
        this.status(NOT_FOUND);
        this.header("Content-Type", "text/plain; charset=UTF-8");
        this.send(message);
    }

    @Override
    public HttpResponse send(final String data) {
        try {
            this.response.getWriter().write(data);
            this.response.getWriter().flush();
            this.response.getWriter().close();
        } catch (IOException exception) {
            logger.error("Error writing a response", exception);
        }
        return this;
    }

    public HttpResponse send(final Path path) {
        try (
            final OutputStream out = this.response.getOutputStream();
            final InputStream is = Files.newInputStream(path)) {
            final String mimeType = Files.probeContentType(path);
            header("Content-Type", mimeType);
            final byte[] buffer = new byte[1048];
            int numBytesRead;
            while ((numBytesRead = is.read(buffer)) > 0) {
                out.write(buffer, 0, numBytesRead);
            }
        } catch (IOException exception) {
            logger.error("Error writing a response", exception);
        }
        return this;
    }

    @Override
    public HttpResponse sendFile(final String file) {
        final Path path = this.getPathFromResource(file);
        if (path == null) {
            this.notFound("Index not found");
        } else {
            this.send(path);
        }
        return this;
    }

    @Override
    public HttpResponse status(final int code) {
        this.response.setStatus(code);
        return this;
    }

}
