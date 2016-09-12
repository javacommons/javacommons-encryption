package com.github.javacommons.encryption;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

class CommonKeyHodler {

    final byte[] bytes;
    final String hashType;
    final String password;

    protected CommonKeyHodler(byte[] key) {
        this.bytes = key;
        this.hashType = null;
        this.password = null;
    }

    protected CommonKeyHodler(String hashType, String password) {
        if (hashType == null) {
            throw new IllegalStateException("Hash type is null.");
        }
        if (password == null) {
            throw new IllegalStateException("Password is null.");
        }
        this.bytes = null;
        this.hashType = hashType;
        this.password = password;
    }

    public byte[] getKeyBytes() {
        if (this.bytes != null) {
            return this.bytes;
        }
        /*
        if (this.hashType == null) {
            throw new IllegalStateException("Hash type is null.");
        }
        if (this.password == null) {
            throw new IllegalStateException("Password is null.");
        }*/
        switch (this.hashType.toLowerCase()) {
            case "hex": {
                try {
                    return Hex.decodeHex(this.password.toCharArray());
                } catch (DecoderException ex) {
                    throw new IllegalStateException(ex);
                }
            }
            case "base64":
                return Base64.decodeBase64(this.password);
            case "md5":
            case "128bit":
                return CryptoUtils.md5(this.password.getBytes());
            case "sha1":
            case "160bit":
                return CryptoUtils.sha1(this.password.getBytes());
            case "sha256":
            case "256bit":
                return CryptoUtils.sha256(this.password.getBytes());
            default:
                throw new IllegalStateException("Illegal hash type: " + this.hashType);
        }
    }

}

public class CommonKeyOperation extends AbstractOperation {

    final EngineSpecParser parser;
    //final byte[] secretKey;
    final CommonKeyHodler keyHolder;
    final int times;

    protected CommonKeyOperation(String engineSpec, byte[] secretKey, int times) {
        this.parser = new EngineSpecParser(engineSpec);
        //this.secretKey = secretKey;
        this.keyHolder = new CommonKeyHodler(secretKey);
        this.times = times;
    }

    protected CommonKeyOperation(String engineSpec, String hashType, String password, int times) {
        this.parser = new EngineSpecParser(engineSpec);
        //this.secretKey = secretKey;
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
