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

import dev.doddle.web.exceptions.HttpException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public class HttpQueryParameter {

    private final String value;

    public HttpQueryParameter(final String value) {
        if (value == null || value.trim().isEmpty()) {
            this.value = null;
        } else {
            this.value = value;
        }
    }

    public Integer asInt() {
        return asInt(null);
    }

    public Integer asInt(final Integer defaultValue) {
        try {
            if (this.value == null) {
                return defaultValue;
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new HttpException(format("Invalid number supplied: %s", this.value));
        }
    }

    public List<String> asList() {
        if (this.value == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(this.value.split("\\s*,\\s*"));
    }

    public Long asLong(final Long defaultValue) {
        try {
            if (this.value == null) {
                return defaultValue;
            }
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new HttpException(format("Invalid number supplied: %s", this.value));
        }
    }

    public Long asLong() {
        return asLong(null);
    }

    public String asString() {
        return asString(null);
    }

    public String asString(final String defaultValue) {
        if (this.value == null) {
            return defaultValue;
        }
        return this.value;
    }

    public boolean isNull() {
        return this.value == null;
    }
}
