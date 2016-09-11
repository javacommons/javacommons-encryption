package com.github.javacommons.encryption;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.util.Arrays;

/**
 * Combination of multiple crypto engines.
 */
public class CryptoSequence {

    final static int PAD_SIZE = 32;
    final List<CryptoEngine> seq = new ArrayList<>();

    /**
     * Combination of multiple crypto engines.
     */
    public CryptoSequence() {
    }

    /**
     * Add a crypto engine to the sequence.
     * @param algorithm
     * @param secretKey
     * @param times 
     */
    public void addCryptoEngine(String algorithm, byte[] secretKey, int times) {
        CryptoEngine engine = new CryptoEngine(algorithm, secretKey, times);
        seq.add(engine);
    }

    /**
     * Returns encrypted byte array data of originalSource.
     * データを秘密鍵で暗号化してバイト列で返す
     */
    public byte[] encryptToBytes(byte[] originalSource) {
        if (seq.isEmpty()) {
            return originalSource;
        }
        byte[] pad = CryptoUtils.randomBytes(PAD_SIZE);
        ByteBuffer byteBuf = ByteBuffer.allocate(pad.length + originalSource.length);
        byteBuf.put(pad);
        byteBuf.put(originalSource);
        byte[] bytes = byteBuf.array();
        for (int i = 0; i < seq.size(); i++) {
            bytes = seq.get(i).encryptToBytes(bytes);
        }
        return bytes;
    }

    /**
     * Returns encrypted byte array data of an Object.
     * オブジェクトを秘密鍵で暗号化してバイト列で返す
     */
    public byte[] objectToBytes(Object o) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String json;
        try {
            json = mapper.writeValueAsString(o);
        } catch (JsonProcessingException ex) {
            return new byte[0];
        }
        byte[] originalSource = json.getBytes();
        return encryptToBytes(originalSource);
    }

    /**
     * Returns encrypted byte array data of originalSource (as Base64 String).
     * データを秘密鍵で暗号化してBase64した文字列で返す
     */
    public String encryptToBase64(byte[] originalSource) {
        byte[] encryptBytes = encryptToBytes(originalSource);
        byte[] encryptBytesBase64 = Base64.encodeBase64(encryptBytes, false);
        return new String(encryptBytesBase64);
    }

    /**
     * Returns encrypted Base64 string of an Object.
     * オブジェクトを秘密鍵で暗号化してBase64した文字列で返す
     */
    public String objectToBase64(Object o) {
        byte[] encryptBytes = objectToBytes(o);
        byte[] encryptBytesBase64 = Base64.encodeBase64(encryptBytes, false);
        return new String(encryptBytesBase64);
    }

    /**
     * Returns decrypted byte array data of encryptedBytes.
     * 暗号化データを元のデータに復元する
     */
    public byte[] decryptFromBytes(byte[] encryptedBytes) {
        if (seq.isEmpty()) {
            return encryptedBytes;
        }
        byte[] bytes = encryptedBytes;
        for (int i = seq.size() - 1; i >= 0; i--) {
            bytes = seq.get(i).encryptToBytes(bytes);
        }
        if (bytes.length < PAD_SIZE) {
            return null;
        }
        return Arrays.copyOfRange(bytes, PAD_SIZE, bytes.length);
    }

    /**
     * Restore an Object from encrypted byte array.
     * 暗号化データを元のオブジェクトに復元する
     */
    public <T extends Object> T objectFromBytes(byte[] encryptedBytes, Class<T> valueType) {
        byte[] bytes = decryptFromBytes(encryptedBytes);
        if (bytes == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(bytes, valueType);
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Returns decrypted byte array data of encryptedBase64String.
     * Base64された暗号化データを元のデータに復元する
     */
    public byte[] decryptFromBase64(String encryptedBase64String) {
        byte[] encryptBytes = Base64.decodeBase64(encryptedBase64String);
        return decryptFromBytes(encryptBytes);
    }

    /**
     * Restore an Object from encrypted Base64 string.
     * Base64された暗号化データを元のデータに復元する
     */
    public <T extends Object> T objectFromBase64(String encryptedBase64String, Class<T> valueType) {
        byte[] encryptBytes = Base64.decodeBase64(encryptedBase64String);
        return objectFromBytes(encryptBytes, valueType);
    }

}
