package com.github.javacommons.encryption;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public final class CommonKeyHodler {

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

