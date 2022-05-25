package dev.doddle.core.engine.crypto;

public class EncryptionData {
    private final byte[] iv;
    private final byte[] salt;
    private final char[] password;
    private final byte[] data;

    public EncryptionData(final byte[] iv,
                          final byte[] salt,
                          final char[] password,
                          final byte[] data) {
        this.iv = iv;
        this.salt = salt;
        this.password = password;
        this.data = data;
    }

    public byte[] getIv() {
        return iv;
    }

    public byte[] getSalt() {
        return salt;
    }

    public char[] getPassword() {
        return password;
    }

    public byte[] getData() {
        return data;
    }
}
