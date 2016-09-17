package com.github.javacommons.encryption;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public final class CommonKeyOperation extends AbstractOperation {

    final EngineSpecParser parser;
    final CommonKeyHodler keyHolder;
    final int times;

    protected CommonKeyOperation(String engineSpec, byte[] secretKey, int times) {
        this.parser = new EngineSpecParser(engineSpec);
        this.keyHolder = new CommonKeyHodler(secretKey);
        this.times = times;
    }

    protected CommonKeyOperation(String engineSpec, String hashType, String password, int times) {
        this.parser = new EngineSpecParser(engineSpec);
        this.keyHolder = new CommonKeyHodler(hashType, password);
        this.times = times;
    }

    @Override
    public byte[] encryptToBytes(byte[] originalSource) {
        try {
            byte[] bytes = originalSource;
            for (int i = 0; i < times; i++) {
                Key key = new SecretKeySpec(keyHolder.getKeyBytes(), this.parser.secretKeySpec);
                Cipher cipher = this.parser.provider == null ? Cipher.getInstance(this.parser.cipherSpec) : Cipher.getInstance(this.parser.cipherSpec, this.parser.provider);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                bytes = cipher.doFinal(bytes);
            }
            return bytes;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public byte[] decryptFromBytes(byte[] encryptedBytes) {
        try {
            byte[] bytes = encryptedBytes;
            for (int i = 0; i < times; i++) {
                Key key = new SecretKeySpec(keyHolder.getKeyBytes(), this.parser.secretKeySpec);
                Cipher cipher = this.parser.provider == null ? Cipher.getInstance(this.parser.cipherSpec) : Cipher.getInstance(this.parser.cipherSpec, this.parser.provider);
                cipher.init(Cipher.DECRYPT_MODE, key);
                bytes = cipher.doFinal(bytes);
            }
            return bytes;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

}
