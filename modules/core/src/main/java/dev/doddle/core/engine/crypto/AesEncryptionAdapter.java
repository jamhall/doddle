package dev.doddle.core.engine.crypto;

import dev.doddle.core.exceptions.DoddleException;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public class AesEncryptionAdapter implements EncryptionAdapter {

    @Override
    public byte[] encrypt(final EncryptionData data) throws DoddleException {
        try {
            final Cipher cipher = getEncryptCipher(data);
            return cipher.doFinal(data.getData());
        } catch (IllegalBlockSizeException | BadPaddingException exception) {
            throw new DoddleException(exception);
        }
    }

    @Override
    public byte[] decrypt(final EncryptionData data) throws DoddleException {
        try {
            final Cipher cipher = getDecryptCipher(data);
            return cipher.doFinal(data.getData());
        } catch (IllegalBlockSizeException | BadPaddingException exception) {
            throw new DoddleException(exception);
        }
    }

    private SecretKeySpec createSecretKeySpec(final EncryptionData data) throws DoddleException {
        try {
            final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            final char[] password = data.getPassword();
            final byte[] salt = data.getSalt();
            final PBEKeySpec passwordBasedEncryptionKeySpec = new PBEKeySpec(password, salt, 65556, 256);
            final SecretKey secretKey = factory.generateSecret(passwordBasedEncryptionKeySpec);
            return new SecretKeySpec(secretKey.getEncoded(), "AES");
        } catch (Exception exception) {
            throw new DoddleException(exception);
        }
    }

    private Cipher getDecryptCipher(final EncryptionData data) throws DoddleException {
        return getCipher(data, DECRYPT_MODE);
    }

    private Cipher getEncryptCipher(final EncryptionData data) throws DoddleException {
        return getCipher(data, ENCRYPT_MODE);
    }

    private Cipher getCipher(final EncryptionData data, final int encryptMode) {
        try {
            final SecretKeySpec secretKeySpec = createSecretKeySpec(data);
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(encryptMode, secretKeySpec, new IvParameterSpec(data.getIv()));
            return cipher;
        } catch (Exception exception) {
            throw new DoddleException(exception);
        }
    }
}
