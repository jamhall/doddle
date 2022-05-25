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
import dev.doddle.core.exceptions.DoddleValidationException;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class EncryptionStore {

    public final List<EncryptionCredential> credentials;

    public EncryptionStore() {
        this.credentials = new ArrayList<>();
    }


    public EncryptionCredential getByVersion(final String version) {
        return credentials.stream()
            .filter(credential -> credential.isVersion(version))
            .findAny()
            .orElse(null);
    }

    public EncryptionCredential getCurrent() {
        return credentials.stream()
            .filter(EncryptionCredential::isCurrent)
            .findAny()
            .orElse(null);
    }


    public EncryptionStore put(@NotNull final String version, @NotNull final String password) {
        return put(version, password, false);
    }

    public EncryptionStore put(@NotNull final String version, @NotNull final String password, final boolean current) {
        final EncryptionCredential credential = new EncryptionCredential(version, password, current);
        if (credentials.contains(credential)) {
            throw new DoddleValidationException(format("Version %s already exists in the credentials store", version));
        } else {
            if (this.getCurrent() == null) {
                credentials.add(credential);
            } else {
                throw new DoddleValidationException("There is already a version set to the current one");
            }
        }
        return this;
    }


}
