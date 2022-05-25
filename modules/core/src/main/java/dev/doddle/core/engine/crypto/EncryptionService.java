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

import dev.doddle.core.engine.JobEncryption;
import dev.doddle.core.exceptions.DoddleException;

import static dev.doddle.core.support.Objects.requireNonNull;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

public class EncryptionService {

    private final EncryptionStore   store;
    private final EncryptionAdapter adapter;
    private final boolean enabled;

    public EncryptionService(
        final boolean enabled,
        final EncryptionStore store,
        final EncryptionAdapter adapter) {
        this.enabled = enabled;
        this.store = requireNonNull(store, "store cannot be null");
        this.adapter = requireNonNull(adapter, "adapter cannot be null");
    }

    public byte[] encrypt(final byte[] bytes, final JobEncryption parameters) throws DoddleException {
        final String version = parameters.getVersion();
        final EncryptionCredential credential = store.getByVersion(version);
        if (credential == null) {
            throw new DoddleException(format("Encryption credential for version %s not found in store", version));
        }
        final EncryptionData data = createEncryptionData(parameters, credential, bytes);
        return adapter.encrypt(data);
    }

    public byte[] decrypt(final byte[] bytes, final JobEncryption parameters) throws DoddleException {
        final String version = parameters.getVersion();
        final EncryptionCredential credential = store.getByVersion(version);
        if (credential == null) {
            throw new DoddleException(format("Encryption credential for version %s not found in store", version));
        }
        final EncryptionData data = createEncryptionData(parameters, credential, bytes);
        return adapter.decrypt(data);
    }

    public byte[] encrypt(final String text, final JobEncryption parameters) throws DoddleException {
        try {
            final byte[] bytes = text.getBytes(UTF_8.name());
            return encrypt(bytes, parameters);
        } catch (Exception exception) {
            throw new DoddleException(exception);
        }
    }

    public EncryptionCredential getCurrentVersion() {
        return this.store.getCurrent();
    }

    private EncryptionData createEncryptionData(final JobEncryption parameters,
                                                final EncryptionCredential credential,
                                                final byte[] bytes) {
        return new EncryptionData(parameters.getIv(), parameters.getSalt(), credential.getPassword(), bytes);
    }

    public boolean isEnabled() {
        return enabled;
    }
}
