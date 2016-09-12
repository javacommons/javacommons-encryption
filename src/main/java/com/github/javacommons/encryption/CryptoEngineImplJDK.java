package com.github.javacommons.encryption;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class CryptoEngineImplJDK extends CryptoEngineImpl {

    final AlgorithmSpecParser parser;
    //final Provider provider;
    //final String cipherSpec;
    //final String secretKeySpec;
    final byte[] secretKey;
    final int times;

    protected CryptoEngineImplJDK(String algorithmSpec, byte[] secretKey, int times) {
        this.parser = new AlgorithmSpecParser(algorithmSpec);
        //this.provider = provider;
        //this.cipherSpec = cipherSpec;
        //this.secretKeySpec = secretKeySpec;
        this.secretKey = secretKey;
        this.times = times;
    }

    /*
    public static CryptoEngineImpl findAlgorithm(String algorithmSpec, byte[] secretKey, int times) {

        
        if ("JDK::AES".equalsIgnoreCase(algorithm)) {
            return new CryptoEngineImplJDK(null, "AES", "AES", secretKey, times);
        } else if ("BC::AES".equalsIgnoreCase(algorithm)) {
            return new CryptoEngineImplJDK(new BouncyCastleProvider(), "AES", "AES", secretKey, times);
        } else if ("BC::Blowfish".equalsIgnoreCase(algorithm)) {
            return new CryptoEngineImplJDK(new BouncyCastleProvider(), "Blowfish", "Blowfish", secretKey, times);
        } else {
            return null;
        }
    }*/

    @Override
    public byte[] encryptToBytes(byte[] originalSource) {
        try {
            byte[] bytes = originalSource;
            for (int i = 0; i < times; i++) {
                Key key = new SecretKeySpec(secretKey, this.parser.secretKeySpec);
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
                Key key = new SecretKeySpec(secretKey, this.parser.secretKeySpec);
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
