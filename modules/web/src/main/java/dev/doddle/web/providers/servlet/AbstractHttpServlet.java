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

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class AbstractHttpServlet extends HttpServlet {

    /**
     * Handle a DELETE request
     *
     * @param request  the http request
     * @param response the http response
     * @throws ServletException if the request for the DELETE could not be handled
     * @throws IOException      if an input or output error occurs
     */
    @Override
    protected void doDelete(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.handleRequest(request, response);
    }

    /**
     * Handle a get request
     *
     * @param request  the http request
     * @param response the http response
     * @throws ServletException if the request for the GET could not be handled
     * @throws IOException      if an input or output error occurs
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.handleRequest(request, response);
    }

    /**
     * Handle a head request
     *
     * @param request  the http request
     * @param response the http response
     * @throws ServletException if the request for the HEAD could not be handled
     * @throws IOException      if an input or output error occurs
     */
    @Override
    protected void doHead(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.handleRequest(request, response);
    }

    /**
     * Handle an options request
     *
     * @param request  the http request
     * @param response the http response
     * @throws ServletException if the request for the OPTIONS could not be handled
     * @throws IOException      if an input or output error occurs
     */
    @Override
    protected void doOptions(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.handleRequest(request, response);
    }

    /**
     * Handle a post request
     *
     * @param request  the http request
     * @param response the http response
     * @throws ServletException if the request for the POST could not be handled
     * @throws IOException      if an input or output error occurs
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.handleRequest(request, response);
    }

    /**
     * Handle a put request
     *
     * @param request  the http request
     * @param response the http response
     * @throws ServletException if the request for the PUT could not be handled
     * @throws IOException      if an input or output error occurs
     */
    @Override
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.handleRequest(request, response);
    }

    /**
     * Handle a trace request
     *
     * @param request  the http request
     * @param response the http response
     * @throws ServletException if the request for the TRACE could not be handled
     * @throws IOException      if an input or output error occurs
     */
    @Override
    protected void doTrace(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.handleRequest(request, response);
    }

    /**
     * Handle the incoming request
     *
     * @param request  the http request
     * @param response the http response
     * @throws ServletException if the request could not be handled
     * @throws IOException      if an input or output error occurs
     */
    protected abstract void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException;

}
