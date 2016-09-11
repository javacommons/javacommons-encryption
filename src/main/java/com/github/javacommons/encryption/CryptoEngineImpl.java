package com.github.javacommons.encryption;

public abstract class CryptoEngineImpl {

    /**
     * Returns encrypted byte array data of originalSource.
     * データを秘密鍵で暗号化してバイト列で返す
     */
    public abstract byte[] encryptToBytes(byte[] originalSource);
    
    /**
     * Returns decrypted byte array data of encryptedBytes.
     * 暗号化データを元のデータに復元する
     */
    public abstract byte[] decryptFromBytes(byte[] encryptedBytes);

}
