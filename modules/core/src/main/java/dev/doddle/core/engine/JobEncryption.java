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
package dev.doddle.core.engine;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.doddle.common.support.NotNull;

import java.security.SecureRandom;

import static dev.doddle.core.support.Objects.requireNonNull;
import static java.util.Arrays.copyOf;

public class JobEncryption {
    @JsonProperty(value = "salt", required = true)
    @NotNull
    private byte[] salt;

    @JsonProperty(value = "iv", required = true)
    @NotNull
    private byte[] iv;

    @JsonProperty(value = "version", required = true)
    @NotNull
    private String version;


    public JobEncryption() {

    }

    public JobEncryption(final byte[] iv, final byte[] salt, final String version) {
        this.iv = requireNonNull(iv, "iv cannot be null");
        this.salt = requireNonNull(salt, "salt cannot be null");
        this.version = requireNonNull(version, "version cannot be null");
    }

    public JobEncryption(@NotNull final String version) {
        this.iv = createRandomBytes(16);
        this.salt = createRandomBytes(16);
        this.version = version;
    }

    private byte[] createRandomBytes(final int count) {
        final SecureRandom random = new SecureRandom();
        final byte[] bytes = new byte[count];
        random.nextBytes(bytes);
        return bytes;
    }

    @NotNull
    public String getVersion() {
        return version;
    }

    public void setVersion(@NotNull String version) {
        this.version = version;
    }


    public byte[] getIv() {
        return copyOf(iv, iv.length);
    }

    public void setIv(final byte[] iv) {
        this.iv = copyOf(iv, iv.length);
    }

    public byte[] getSalt() {
        return copyOf(this.salt, this.salt.length);
    }

    public void setSalt(final byte[] salt) {
        this.salt = copyOf(salt, salt.length);
    }

}


