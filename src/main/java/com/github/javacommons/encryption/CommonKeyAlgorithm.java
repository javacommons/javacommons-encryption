package com.github.javacommons.encryption;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.undercouch.bson4jackson.BsonFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;

/**
 * Combination of multiple crypto engines.
 */
public final class CommonKeyAlgorithm {

    //final static int PAD_SIZE = 32;
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
     * Returns encrypted byte array data of originalSource. データを秘密鍵で暗号化してバイト列で返す
     */
    public byte[] encryptToBytes(Encryptable originalSource) {
        byte[] bytes = _appendPadding(originalSource);
        for (int i = 0; i < engineList.size(); i++) {
            bytes = engineList.get(i).encryptToBytes(bytes);
        }
        return bytes;
    }

    /**
     * Returns encrypted byte array data of originalSource (as Base64 String).
     * データを秘密鍵で暗号化してBase64した文字列で返す
     */
    public String encryptToBase64(byte[] originalSource) {
        byte[] encryptBytes = encryptToBytes(originalSource);
        return CryptoUtils.base64Encode(encryptBytes, true);
    }

    /**
     * Returns encrypted byte array data of originalSource (as Base64 String).
     * データを秘密鍵で暗号化してBase64した文字列で返す
     */
    public String encryptToBase64(Encryptable originalSource) {
        byte[] encryptBytes = encryptToBytes(originalSource);
        return CryptoUtils.base64Encode(encryptBytes, true);
    }

    /**
     * Returns decrypted byte array data of encryptedBytes. 暗号化データを元のデータに復元する
     */
    public Object decryptFromBytes(byte[] encryptedBytes) {
        byte[] bytes = encryptedBytes;
        for (int i = engineList.size() - 1; i >= 0; i--) {
            bytes = engineList.get(i).decryptFromBytes(bytes);
        }
        return _removePadding(bytes);
    }

    /**
     * Returns decrypted byte array data of encryptedBase64String.
     * Base64された暗号化データを元のデータに復元する
     */
    public Object decryptFromBase64(String encryptedBase64String) {
        //Base64 base64 = new Base64(true); // (urlSafe)
        //byte[] encryptBytes = base64.decodeBase64(encryptedBase64String);
        byte[] encryptBytes = CryptoUtils.base64Decode(encryptedBase64String);
        return decryptFromBytes(encryptBytes);
    }

    private byte[] _encryptableToBytes(Encryptable e) {
        if (e == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            mapper.writeValue(baos, e);
            return baos.toByteArray();
        } catch (JsonProcessingException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    private Encryptable _encryptableFromBytes(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
        //ObjectMapper mapper = new ObjectMapper();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Encryptable e;
        try {
            return mapper.readValue(bais, Encryptable.class);
        } catch (IOException ex) {
            //ex.printStackTrace();
            return null;
        }
    }

    private byte[] _appendPadding(Object o) {
        if (o == null) {
            return null;
        }
        Encryptable e = null;
        if ("[B".equals(o.getClass().getName())) {
            e = new StoreBytes((byte[]) o);
        } else if (o instanceof Encryptable) {
            byte[] bytes = _encryptableToBytes((Encryptable) o);
            StoreObject store = new StoreObject();
            store.bytes = bytes;
            e = store;
        } else {
            return null;
        }
        return _encryptableToBytes(e);
    }

    private Object _removePadding(byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
        //ObjectMapper mapper = new ObjectMapper();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Encryptable e = _encryptableFromBytes(bytes);
        if (e == null) {
            System.out.println("(0)");
            return null;
        }
        if ("com.github.javacommons.encryption.StoreBytes".equals(e.getClass().getName())) {
            System.out.println("(1)");
            com.github.javacommons.encryption.StoreBytes store = (com.github.javacommons.encryption.StoreBytes) e;
            return store.data();
        }
        if ("com.github.javacommons.encryption.StoreObject".equals(e.getClass().getName())) {
            System.out.println("(2)");
            com.github.javacommons.encryption.StoreObject store = (com.github.javacommons.encryption.StoreObject) e;
            Encryptable o = _encryptableFromBytes(store.bytes);
            return o;
        }
        System.out.println("(3)");
        return null;
    }

}
