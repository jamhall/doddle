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
package dev.doddle.core.engine.crypto;

import dev.doddle.common.support.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static dev.doddle.core.support.Objects.requireNonNull;

public class EncryptionCredential {

    private final String version;

    private final char[] password;

    private final boolean current;

    public EncryptionCredential(@NotNull final String version,
                                @NotNull final String password,
                                boolean current) {
        this.version = requireNonNull(version, "version cannot be null");
        this.password = requireNonNull(password, "password cannot be null").toCharArray();
        this.current = current;
    }

    public String getVersion() {
        return version;
    }

    public boolean isCurrent() {
        return current;
    }

    public char[] getPassword() {
        return password;
    }

    public boolean isVersion(final String version) {
        return version.equals(this.version);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EncryptionCredential that = (EncryptionCredential) o;

        return new EqualsBuilder().append(version, that.version).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(version).toHashCode();
    }
}
