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

import dev.doddle.web.http.HttpMethod;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

class Route {

    private final HttpMethod   method;
    private final String       path;
    private final RouteHandler handler;

    /**
     * Create a new route
     *
     * @param method  the http method for the route
     * @param path    the path for the route
     * @param handler the handler for the route
     */
    public Route(final HttpMethod method, final String path, final RouteHandler handler) {
        this.method = requireNonNull(method, "method cannot be null");
        this.handler = requireNonNull(handler, "handler cannot be null");
        this.path = requireNonNull(path, "path cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Route route = (Route) o;
        return method == route.method && path.equals(route.path);
    }

    /**
     * Set the handler
     *
     * @return the handler
     */
    public RouteHandler handler() {
        return handler;
    }

    @Override
    public int hashCode() {
        return hash(method, path);
    }

    /**
     * Check if a given request matches this route
     *
     * @param method the requests method
     * @param uri    the requests uri
     * @return true if matches, otherwise false
     */
    public boolean matches(final HttpMethod method, final String uri) {
        if (this.method != method) {
            return false;
        }
        if (!path.endsWith("*") &&
            ((uri.endsWith("/") && !path.endsWith("/"))
                || (path.endsWith("/") && !uri.endsWith("/"))
            )) {
            // One and not both ends with slash
            return false;
        }
        if (this.path.equals(uri)) {
            // Paths are the same
            return true;
        }
        final List<String> segments = asList(path.split("/"));
        final List<String> matchSegments = asList(uri.split("/"));

        if (segments.size() == matchSegments.size()) {
            for (int i = 0; i < segments.size(); i++) {
                final String pathSegment = segments.get(i);
                final String matchSegment = matchSegments.get(i);

                if ((i == segments.size() - 1)
                    && (pathSegment.equals("*")
                    && path.endsWith("*"))) {
                    return true;
                }

                if ((!pathSegment.startsWith(":")) &&
                    !pathSegment.equals(matchSegment) &&
                    !pathSegment.equals("*")) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Get the http method
     *
     * @return the http method
     */
    public HttpMethod method() {
        return method;
    }

    /**
     * Get the path
     *
     * @return the path
     */
    public String path() {
        return path;
    }
}
