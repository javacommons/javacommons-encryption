package com.github.javacommons.encryption;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.undercouch.bson4jackson.BsonFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.ArrayUtils;

/**
 * Combination of multiple crypto engines.
 */
public class CommonKeyAlgorithm {

    final static int PAD_SIZE = 32;
    final List<AbstractOperation> engineList = new ArrayList<>();

    /**
     * Combination of multiple crypto engines.
     */
    public CommonKeyAlgorithm() {
    }

    /**
     * Add a crypto engine to the sequence.
     *
     * @param engineSpec
     * @param secretKey
     * @param times
     */
    public void addOperation(String engineSpec, byte[] secretKey, int times) {
        AbstractOperation engine = new CommonKeyOperation(engineSpec, secretKey, times);
        engineList.add(engine);
    }

    public void addOperation(String engineSpec, String hashSpec, String password, int times) {
        AbstractOperation operation = new CommonKeyOperation(engineSpec, hashSpec, password, times);
        engineList.add(operation);
    }

    /**
     * Returns encrypted byte array data of originalSource. データを秘密鍵で暗号化してバイト列で返す
     */
    public byte[] encryptToBytes(byte[] originalSource) {
        byte[] bytes = _appendPadding(originalSource);
        for (int i = 0; i < engineList.size(); i++) {
            bytes = engineList.get(i).encryptToBytes(bytes);
        }
        return bytes;
    }

    /**
     * Returns encrypted byte array data of an Object. オブジェクトを秘密鍵で暗号化してバイト列で返す
     */
    public byte[] objectToBytes(Object o) {
        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            mapper.writeValue(baos, o);
        } catch (JsonProcessingException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
        byte[] originalSource = baos.toByteArray();
        return encryptToBytes(originalSource);
    }
    /*
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
    */

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
        for (int i = engineList.size() - 1; i >= 0; i--) {
            bytes = engineList.get(i).decryptFromBytes(bytes);
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
        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            return mapper.readValue(bais, valueType);
        } catch (IOException ex) {
            return null;
        }
    }
    /*
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
    }*/

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

    private byte[] _appendPadding(byte[] bytes) { return bytes; }
    private byte[] _removePadding(byte[] bytes) { return bytes; }
    /*
    private final byte[] md5Head = "--MD5=".getBytes();
    private final byte[] sha1Head = "--SHA1=".getBytes();
    private final byte[] tail = "--".getBytes();

    private byte[] _appendPadding(byte[] bytes) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // PAD
            byte[] pad = CryptoUtils.randomAsciiBytes(PAD_SIZE);
            CryptoUtils.assertBoolean(pad.length == PAD_SIZE);
            baos.write(pad);
            // MD5
            baos.write(md5Head);
            String md5 = CryptoUtils.md5Hex(bytes).toLowerCase();
            byte[] md5Bytes = md5.getBytes();
            CryptoUtils.assertBoolean(md5Bytes.length == 32);
            baos.write(md5Bytes);
            // SHA1
            baos.write(sha1Head);
            String sha1 = CryptoUtils.sha1Hex(bytes).toLowerCase();
            byte[] sha1Bytes = sha1.getBytes();
            CryptoUtils.assertBoolean(sha1Bytes.length == 40);
            baos.write(sha1Bytes);
            // TAIL
            baos.write(tail);
            // Content
            baos.write(bytes);
            //
            //byte[] result = CryptoUtils.appendByteArrays(pad, head, md5Bytes, tail, bytes);
            byte[] result = baos.toByteArray();
            return result;
        } catch (IOException ex) {
            //Logger.getLogger(CommonKeyAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    private byte[] _removePadding(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        // PAD
        bais.skip(PAD_SIZE);
        // MD5
        bais.skip(md5Head.length);
        byte[] md5HexBytes = new byte[32];
        if (bais.read(md5HexBytes, 0, md5HexBytes.length) != md5HexBytes.length) {
            return null;
        }
        ////String md5Hex = new String(md5HexBytes);
        // SHA1
        bais.skip(sha1Head.length);
        byte[] sha1HexBytes = new byte[40];
        if (bais.read(sha1HexBytes, 0, sha1HexBytes.length) != sha1HexBytes.length) {
            return null;
        }
        ////String sha1Hex = new String(sha1HexBytes);
        // TAIL
        bais.skip(tail.length);
        // Check Digests
        byte[] rest = new byte[bais.available()];
        if (bais.read(rest, 0, rest.length) != rest.length) {
            return null;
        }
        byte[] restMd5HexBytes = CryptoUtils.md5Hex(rest).getBytes();
        if (!ArrayUtils.isEquals(restMd5HexBytes, md5HexBytes)) {
            return null;
        }
        byte[] restSha1HexBytes = CryptoUtils.sha1Hex(rest).getBytes();
        if (!ArrayUtils.isEquals(restSha1HexBytes, sha1HexBytes)) {
            return null;
        }
        return rest;
    }
    */

}
