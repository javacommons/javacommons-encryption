package com.github.javacommons.encryption;

/**
 * Crypto Engine encapsulation specified by algorithm, secret key, and iteration
 * times.
 */
public class Algorithm {

    final AlgorithmEngine engine;

    /**
     * Crypto Engine encapsulation specified by algorithm, secret key, and
     * iteration times.
     */
    protected Algorithm(String algorithmSpec, byte[] secretKey, int times) {
        if (times <= 0) {
            throw new IllegalStateException("Times must be greater than zero: " + times);
        }
        this.engine = new CommonKeyAlgorithmEngine(algorithmSpec, secretKey, times);
    }

    /**
     * Returns encrypted byte array data of originalSource. 
     * データを秘密鍵で暗号化してバイト列で返す
     */
    public byte[] encryptToBytes(byte[] originalSource) {
        return this.engine.encryptToBytes(originalSource);
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
        return this.engine.decryptFromBytes(encryptedBytes);
    }

    /**
     * Returns decrypted byte array data of encryptedBase64String.
     * Base64された暗号化データを元のデータに復元する
     */
    public byte[] decryptFromBase64(String encryptedBase64String) {
        byte[] encryptBytes = CryptoUtils.base64Decode(encryptedBase64String);
        return decryptFromBytes(encryptBytes);
    }

}
