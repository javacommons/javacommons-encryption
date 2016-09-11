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

    final Provider provider;
    final String cipherSpec;
    final String secretKeySpec;
    final byte[] secretKey;
    final int times;

    private CryptoEngineImplJDK(Provider provider, String cipherSpec, String secretKeySpec, byte[] secretKey, int times) {
        this.provider = provider;
        this.cipherSpec = cipherSpec;
        this.secretKeySpec = secretKeySpec;
        this.secretKey = secretKey;
        this.times = times;
    }

    public static CryptoEngineImpl findAlgorithm(String algorithm, byte[] secretKey, int times) {
        if ("JDK::AES".equalsIgnoreCase(algorithm)) {
            return new CryptoEngineImplJDK(null, "AES", "AES", secretKey, times);
        } else if ("BC::AES".equalsIgnoreCase(algorithm)) {
            return new CryptoEngineImplJDK(new BouncyCastleProvider(), "AES", "AES", secretKey, times);
        } else {
            return null;
        }
    }

    @Override
    public byte[] encryptToBytes(byte[] originalSource) {
        try {
            byte[] bytes = originalSource;
            for (int i = 0; i < times; i++) {
                Key key = new SecretKeySpec(secretKey, secretKeySpec);
                Cipher cipher = provider == null ? Cipher.getInstance(cipherSpec) : Cipher.getInstance(cipherSpec, provider);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                bytes = cipher.doFinal(bytes);
            }
            return bytes;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CryptoEngineImplJDK.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(CryptoEngineImplJDK.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(CryptoEngineImplJDK.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(CryptoEngineImplJDK.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(CryptoEngineImplJDK.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public byte[] decryptFromBytes(byte[] encryptedBytes) {
        try {
            byte[] bytes = encryptedBytes;
            for (int i = 0; i < times; i++) {
                Key key = new SecretKeySpec(secretKey, secretKeySpec);
                Cipher cipher = provider == null ? Cipher.getInstance(cipherSpec) : Cipher.getInstance(cipherSpec, provider);
                cipher.init(Cipher.DECRYPT_MODE, key);
                bytes = cipher.doFinal(bytes);
            }
            return bytes;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CryptoEngineImplJDK.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(CryptoEngineImplJDK.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(CryptoEngineImplJDK.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(CryptoEngineImplJDK.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(CryptoEngineImplJDK.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
