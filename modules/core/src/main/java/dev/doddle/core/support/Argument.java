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
package dev.doddle.core.support;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static dev.doddle.core.support.Objects.requireNonNull;

public class Argument {

    private final String name;
    private final String value;

    private final boolean encrypted;

    @JsonCreator
    protected Argument(@JsonProperty("name") final String name,
                       @JsonProperty("value") final String value,
                       @JsonProperty("encrypted") final boolean encrypted) {
        this.name = requireNonNull(name, "name cannot be null");
        this.value = requireNonNull(value, "value cannot be null");
        this.encrypted = encrypted;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public static Argument argument(final String name, final String value) {
        return argument(name, value, false);
    }

    public static Argument argument(final String name, final String value, final boolean encrypted) {
        return new Argument(name, value, encrypted);
    }

    public static Argument argument(final String name, final boolean value) {
        final String converted = String.valueOf(value);
        return argument(name, converted);
    }

    public static Argument argument(final String name, final int value) {
        final String converted = String.valueOf(value);
        return argument(name, converted);
    }

    public static Argument argument(final String name, final long value) {
        final String converted = String.valueOf(value);
        return argument(name, converted);
    }

    public static Argument argument(final String name, final double value) {
        final String converted = String.valueOf(value);
        return argument(name, converted);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;

        if (object == null || getClass() != object.getClass()) return false;

        Argument that = (Argument) object;

        return new EqualsBuilder().append(name, that.name).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).toHashCode();
    }
}
