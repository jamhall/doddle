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

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;

public class RouteMatch {

    private final String       matchUri;
    private final String       requestUri;
    private final RouteHandler handler;

    public RouteMatch(final String matchUri,
                      final String requestUri,
                      final RouteHandler handler) {
        this.matchUri = requireNonNull(matchUri, "matchUri cannot be null");
        this.requestUri = requireNonNull(requestUri, "requestUri cannot be null");
        this.handler = requireNonNull(handler, "handler cannot be null");
    }

    public RouteHandler handler() {
        return handler;
    }

    public Map<String, String> parameters() {
        final List<String> segments = asList(requestUri.split("/"));
        final List<String> matchSegments = asList(matchUri.split("/"));
        final Map<String, String> params = new HashMap<>();
        for (int i = 0; (i < segments.size()) && (i < matchSegments.size()); i++) {
            final String matchedSegment = matchSegments.get(i);
            if (matchedSegment.startsWith(":")) {
                final String decoded = URLDecoder.decode(segments.get(i), UTF_8);
                params.put(matchedSegment.toLowerCase(), decoded);
            }
        }
        return unmodifiableMap(params);
    }

}
