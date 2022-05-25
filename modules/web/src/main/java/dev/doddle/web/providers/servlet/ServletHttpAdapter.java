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

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.doddle.web.http.AbstractHttpAdapter;
import dev.doddle.web.http.HttpAdapter;
import dev.doddle.web.http.HttpRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import static java.util.Objects.requireNonNull;

public class ServletHttpAdapter extends AbstractHttpAdapter implements HttpAdapter {

    private final HttpServletRequest  servletRequest;
    private final HttpServletResponse servletResponse;
    private final ObjectMapper        mapper;

    public ServletHttpAdapter(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse) {
        this.servletRequest = requireNonNull(servletRequest, "servletRequest cannot be null");
        this.servletResponse = requireNonNull(servletResponse, "servletResponse cannot be null");
        this.mapper = this.createObjectMapper();
    }


    @Override
    public HttpRequest read() {
        return new ServletHttpRequest(mapper, servletRequest, servletResponse);
    }
}
