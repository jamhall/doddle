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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RouteMatchTest {

    @DisplayName("it should match a route with multiple parameters")
    @Test
    void it_should_match_a_route_with_multiple_parameters() {
        final RouteHandler handler = (request) -> {

        };
        final RouteMatch match = new RouteMatch("/jobs/:id/logs/:level", "/jobs/123/logs/info", handler);
        final Map<String, String> parameters = match.parameters();
        assertEquals(2, parameters.size());
        assertEquals("123", parameters.get(":id"));
        assertEquals("info", parameters.get(":level"));
    }

    @DisplayName("it should match a route with no parameters")
    @Test
    void it_should_match_a_route_with_no_parameter() {
        final RouteHandler handler = (request) -> {

        };
        final RouteMatch match = new RouteMatch("/jobs", "/jobs", handler);
        final Map<String, String> parameters = match.parameters();
        assertEquals(0, parameters.size());
    }

    @DisplayName("it should match a route with one parameter")
    @Test
    void it_should_match_a_route_with_one_parameter() {
        final RouteHandler handler = (request) -> {

        };
        final RouteMatch match = new RouteMatch("/jobs/:id", "/jobs/123", handler);
        final Map<String, String> parameters = match.parameters();
        assertEquals(1, parameters.size());
        assertEquals("123", parameters.get(":id"));
    }
}
