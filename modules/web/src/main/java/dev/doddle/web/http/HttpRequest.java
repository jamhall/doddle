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
package dev.doddle.web.http;

import dev.doddle.common.support.NotNull;

import java.util.Map;

public interface HttpRequest {

    /**
     * Get the request body
     *
     * @return the body
     */
    String body();

    /**
     * Convert the body  to a given object type
     *
     * @param object the object
     * @param <T>
     * @return the converted body
     */
    <T> T body(@NotNull final Class<T> object);

    /**
     * Get the http method
     *
     * @return the http method
     */
    String method();

    /**
     * Get a route parameter
     *
     * @param name the parameter name
     * @return the route parameter value
     */
    String parameter(final String name);

    /**
     * Get a route parameter
     *
     * @param name         the parameter name
     * @param defaultValue the default parameter value
     * @return the route parameter value
     */
    String parameter(final String name, final String defaultValue);

    void parameters(Map<String, String> parameters);

    /**
     * Get the route parameters
     *
     * @return the router parameters
     */
    Map<String, String> parameters();

    /**
     * Get a query parameter
     *
     * @param name the parameter name
     * @return the parameter value
     */
    HttpQueryParameter query(final String name);

    /**
     * Get the response
     *
     * @return the response
     */
    HttpResponse response();

    /**
     * Get the incoming request uri
     *
     * @return the uri
     */
    String uri();
}
