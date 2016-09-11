package com.github.javacommons.encryption;

/**
 * Crypto Engine encapsulation specified by algorithm, secret key, and iteration
 * times.
 */
public class CryptoEngine {

    final CryptoEngineImpl impl;

    /**
     * Crypto Engine encapsulation specified by algorithm, secret key, and
     * iteration times.
     */
    public CryptoEngine(String algorithm, byte[] secretKey, int times) {
        if (times <= 0) {
            throw new IllegalStateException("Times must be greater than zero: " + times);
        }
        this.impl = getEngineImpl(algorithm, secretKey, times);
        if (this.impl == null) {
            throw new IllegalStateException("Algorithm not found: " + algorithm);
        }
    }

    /**
     * Returns encrypted byte array data of originalSource. 
     * データを秘密鍵で暗号化してバイト列で返す
     */
    public byte[] encryptToBytes(byte[] originalSource) {
        return this.impl.encryptToBytes(originalSource);
    }

    /**
     * Returns encrypted byte array data of originalSource (as Base64 String).
     * データを秘密鍵で暗号化してBase64した文字列で返す
     */
    public String encryptToBase64(byte[] originalSource) {
        byte[] encryptBytes = encryptToBytes(originalSource);
        return CryptoUtils.base64Encode(encryptBytes);
    }

    /**
     * Returns decrypted byte array data of encryptedBytes. 
     * 暗号化データを元のデータに復元する
     */
    public byte[] decryptFromBytes(byte[] encryptedBytes) {
        return this.impl.decryptFromBytes(encryptedBytes);
    }

    /**
     * Returns decrypted byte array data of encryptedBase64String.
     * Base64された暗号化データを元のデータに復元する
     */
    public byte[] decryptFromBase64(String encryptedBase64String) {
        byte[] encryptBytes = CryptoUtils.base64Decode(encryptedBase64String);
        return decryptFromBytes(encryptBytes);
    }

    private CryptoEngineImpl getEngineImpl(String algorithm, byte[] secretKey, int times) {
        CryptoEngineImpl impl;
        impl = CryptoEngineImplBC.findAlgorithm(algorithm, secretKey, times);
        if(impl != null) return impl;
        impl = CryptoEngineImplJDK.findAlgorithm(algorithm, secretKey, times);
        return impl;
    }
}
