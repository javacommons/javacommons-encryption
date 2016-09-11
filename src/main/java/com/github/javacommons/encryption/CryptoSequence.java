package com.github.javacommons.encryption;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;

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
     *
     * @param algorithm
     * @param secretKey
     * @param times
     */
    public void addCryptoEngine(String algorithm, byte[] secretKey, int times) {
        CryptoEngine engine = new CryptoEngine(algorithm, secretKey, times);
        byte[] data = CryptoUtils.randomAsciiBytes(64);
        data = engine.encryptToBytes(data);
        if (data == null) {
            throw new IllegalStateException("Test encryption failed: " + algorithm);
        }
        data = engine.decryptFromBytes(data);
        if (data == null) {
            throw new IllegalStateException("Test decryption failed: " + algorithm);
        }
        seq.add(engine);
    }

    /**
     * Returns encrypted byte array data of originalSource. データを秘密鍵で暗号化してバイト列で返す
     */
    public byte[] encryptToBytes(byte[] originalSource) {
        byte[] bytes = _appendPadding(originalSource);
        for (int i = 0; i < seq.size(); i++) {
            bytes = seq.get(i).encryptToBytes(bytes);
        }
        return bytes;
    }

    /**
     * Returns encrypted byte array data of an Object. オブジェクトを秘密鍵で暗号化してバイト列で返す
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
        return CryptoUtils.base64Encode(encryptBytes);
    }

    /**
     * Returns encrypted Base64 string of an Object.
     * オブジェクトを秘密鍵で暗号化してBase64した文字列で返す
     */
    public String objectToBase64(Object o) {
        byte[] encryptBytes = objectToBytes(o);
        return CryptoUtils.base64Encode(encryptBytes);
    }

    /**
     * Returns decrypted byte array data of encryptedBytes. 暗号化データを元のデータに復元する
     */
    public byte[] decryptFromBytes(byte[] encryptedBytes) {
        byte[] bytes = encryptedBytes;
        for (int i = seq.size() - 1; i >= 0; i--) {
            bytes = seq.get(i).decryptFromBytes(bytes);
        }
        return _removePadding(bytes);
    }

    /**
     * Restore an Object from encrypted byte array. 暗号化データを元のオブジェクトに復元する
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
        //Base64 base64 = new Base64(true); // (urlSafe)
        //byte[] encryptBytes = base64.decodeBase64(encryptedBase64String);
        byte[] encryptBytes = CryptoUtils.base64Decode(encryptedBase64String);
        return decryptFromBytes(encryptBytes);
    }

    /**
     * Restore an Object from encrypted Base64 string.
     * Base64された暗号化データを元のデータに復元する
     */
    public <T extends Object> T objectFromBase64(String encryptedBase64String, Class<T> valueType) {
        //Base64 base64 = new Base64(true); // (urlSafe)
        //byte[] encryptBytes = base64.decodeBase64(encryptedBase64String);
        byte[] encryptBytes = CryptoUtils.base64Decode(encryptedBase64String);
        return objectFromBytes(encryptBytes, valueType);
    }

    private final byte[] head = "--HEAD--MD5=".getBytes();
    private final byte[] tail = "--".getBytes();

    private byte[] _appendPadding(byte[] bytes) {
        String md5 = CryptoUtils.md5Hex(bytes);
        byte[] md5Bytes = md5.getBytes();
        byte[] pad = CryptoUtils.randomAsciiBytes(PAD_SIZE);
        if (pad.length != PAD_SIZE) {
            throw new IllegalStateException();
        }
        byte[] result = CryptoUtils.appendByteArrays(pad, head, md5Bytes, tail, bytes);
        /*for (int i = 0; i < head.length; i++) {
            if (result[PAD_SIZE + i] != head[i]) {
                return null;
            }
        }*/
        return result;
    }

    private byte[] _removePadding(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        bais.skip(PAD_SIZE);
        byte[] headData = new byte[head.length];
        if (bais.read(headData, 0, headData.length) != head.length) {
            return null;
        }
        if (!ArrayUtils.isEquals(headData, head)) {
            return null;
        }
        byte[] md5HexBytes = new byte[32];
        if (bais.read(md5HexBytes, 0, md5HexBytes.length) != 32) {
            return null;
        }
        String md5Hex = new String(md5HexBytes);
        ////System.out.printf("(a)%s\n", md5Hex);
        bais.skip(tail.length);
        byte[] rest = new byte[bais.available()];
        if (bais.read(rest, 0, rest.length) != rest.length) {
            return null;
        }
        String restMd5Hex = CryptoUtils.md5Hex(rest);
        ////System.out.printf("(b)%s\n", restMd5Hex);
        if (restMd5Hex.equals(md5Hex)) {
            return rest;
        }
        return null;
    }

}
