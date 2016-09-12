package com.github.javacommons.encryption;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CommonKeyEngineCore extends EngineCoreInterface {

    final EngineSpecParser parser;
    final byte[] secretKey;
    final int times;

    protected CommonKeyEngineCore(String engineSpec, byte[] secretKey, int times) {
        this.parser = new EngineSpecParser(engineSpec);
        this.secretKey = secretKey;
        this.times = times;
    }

    @Override
    public byte[] encryptToBytes(byte[] originalSource) {
        try {
            byte[] bytes = originalSource;
            for (int i = 0; i < times; i++) {
                Key key = new SecretKeySpec(secretKey, this.parser.secretKeySpec);
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
                Key key = new SecretKeySpec(secretKey, this.parser.secretKeySpec);
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
