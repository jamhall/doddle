package dev.doddle.core.engine.crypto;

public interface EncryptionAdapter {
    byte[] encrypt(final EncryptionData data);

    byte[] decrypt(final EncryptionData data);
}
