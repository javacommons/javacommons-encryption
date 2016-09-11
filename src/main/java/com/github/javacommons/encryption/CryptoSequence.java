package com.github.javacommons.encryption;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
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
        ////if (seq.isEmpty()) return originalSource;
        byte[] bytes = _appendPadding(originalSource);
        for (int i = 0; i < seq.size(); i++) {
            bytes = seq.get(i).encryptToBytes(bytes);
            ////ArrayUtils.reverse(bytes);
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
        //Base64 base64 = new Base64(true); // (urlSafe)
        //byte[] encryptBytesBase64 = base64.encodeBase64(encryptBytes, false);
        return CryptoUtils.base64Encode(encryptBytes);
    }

    /**
     * Returns encrypted Base64 string of an Object.
     * オブジェクトを秘密鍵で暗号化してBase64した文字列で返す
     */
    public String objectToBase64(Object o) {
        byte[] encryptBytes = objectToBytes(o);
        //Base64 base64 = new Base64(true); // (urlSafe)
        //byte[] encryptBytesBase64 = base64.encodeBase64(encryptBytes, false);
        return CryptoUtils.base64Encode(encryptBytes);
    }

    /**
     * Returns decrypted byte array data of encryptedBytes. 暗号化データを元のデータに復元する
     */
    public byte[] decryptFromBytes(byte[] encryptedBytes) {
        ////if (seq.isEmpty()) return encryptedBytes;
        byte[] bytes = encryptedBytes;
        for (int i = seq.size() - 1; i >= 0; i--) {
            ////ArrayUtils.reverse(bytes);
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
        ////byte[] head = "--HEAD--".getBytes();
        String md5 = CryptoUtils.md5Hex(bytes);
        byte[] md5Bytes = md5.getBytes();
        ////System.out.println(md5);
        ////System.out.println(md5.length());
        ////System.out.println(head.length);
        ////System.out.println(new String(head));
        //byte[] pad = CryptoUtils.randomBinaryBytes(PAD_SIZE);
        byte[] pad = CryptoUtils.randomAsciiBytes(PAD_SIZE);
        if (pad.length != PAD_SIZE) {
            throw new IllegalStateException();
        }
        ByteBuffer byteBuf = ByteBuffer.allocate(pad.length + head.length + md5Bytes.length + tail.length + bytes.length);
        byteBuf.put(pad);
        byteBuf.put(head);
        byteBuf.put(md5Bytes);
        byteBuf.put(tail);
        byteBuf.put(bytes);
        byte[] result = byteBuf.array();
        for (int i = 0; i < head.length; i++) {
            ////byte[] temp = new byte[1];
            ////temp[0] = result[PAD_SIZE + i];
            ////System.out.printf("(1)i=%d (%s)\n", i, new String(temp));
            if (result[PAD_SIZE + i] != head[i]) {
                return null;
            }
        }
        return result;
    }

    private byte[] _removePadding(byte[] bytes) {
        int removeSize = head.length + tail.length + PAD_SIZE + 32; // 32: md5Hex string
        if (bytes.length < removeSize) {
            return null;
        }
        //String s = new String(bytes);
        //System.out.printf("s=%s\n", s);
        for (int i = 0; i < head.length; i++) {
            ////byte[] temp = new byte[1];
            ////temp[0] = bytes[PAD_SIZE + i];
            ////System.out.printf("(2)i=%d (%s)\n", i, new String(temp));
            if (bytes[PAD_SIZE + i] != head[i]) {
                return null;
            }
        }
        byte[] md5Bytes = Arrays.copyOfRange(bytes, PAD_SIZE + head.length, PAD_SIZE + head.length + 32);
        String md5A = new String(md5Bytes);
        byte[] result = Arrays.copyOfRange(bytes, removeSize, bytes.length);
        String md5B = CryptoUtils.md5Hex(result);
        if (!md5A.equals(md5B)) {
            return null;
        }
        return result;
    }

}
