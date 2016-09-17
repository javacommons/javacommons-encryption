package com.github.javacommons.encryption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.crypto.Crypto;
import org.apache.commons.crypto.cipher.CryptoCipher;
import org.apache.commons.crypto.cipher.CryptoCipherFactory;
import org.apache.commons.crypto.cipher.CryptoCipherFactory.CipherProvider;
import org.apache.commons.crypto.stream.CryptoInputStream;
import org.apache.commons.crypto.stream.CryptoOutputStream;
import org.apache.commons.crypto.utils.ReflectionUtils;
import org.junit.Test;
import static org.junit.Assert.*;

class MyCryptoOutputStream extends CryptoOutputStream {

    public MyCryptoOutputStream(OutputStream out, CryptoCipher cipher, int bufferSize, Key key, AlgorithmParameterSpec params) throws IOException {
        super(out, cipher, bufferSize, key, params);
    }

}

class MyCryptoInputStream extends CryptoInputStream {

    public MyCryptoInputStream(InputStream in, CryptoCipher cipher, int bufferSize, Key key, AlgorithmParameterSpec params) throws IOException {
        super(in, cipher, bufferSize, key, params);
    }

}

public class Test05_OpenSSL {

    //@Test
    public void test1() throws IOException, GeneralSecurityException {
        //final SecretKeySpec key = new SecretKeySpec(getUTF8Bytes("1234567890123456"), "AES");
        //final IvParameterSpec iv = new IvParameterSpec(getUTF8Bytes("1234567890123456"));
        final SecretKeySpec key = new SecretKeySpec(getUTF8Bytes("12345678901234567890123456789012"), "AES");
        final IvParameterSpec iv = new IvParameterSpec(getUTF8Bytes("12345678901234567890123456789012"));
        Properties properties = new Properties();
        final String transform = "AES/CBC/PKCS5Padding";
        /*
        transformations = new String[] {
                "AES/CBC/NoPadding",
                "AES/CBC/PKCS5Padding",
                "AES/CTR/NoPadding"};
         */

        System.out.println(Crypto.isNativeCodeLoaded());

        CryptoCipher cipher = CryptoCipherFactory.getCryptoCipher(transform);

        /*
        Properties ps = new Properties();
        ps.setProperty(CryptoCipherFactory.CLASSES_KEY, CipherProvider.OPENSSL.getClassName());
        //ps.setProperty(...); // if required by the implementation
        cipher = CryptoCipherFactory.getCryptoCipher(transform, ps);
         */
        Properties props = new Properties();
        try {
            //cipher = (CryptoCipher) ReflectionUtils.newInstance(ReflectionUtils.getClassByName("org.apache.commons.crypto.cipher.OpenSslCipher"), props, transform);
            cipher = (CryptoCipher) ReflectionUtils.newInstance(ReflectionUtils.getClassByName("org.apache.commons.crypto.jna.OpenSslJnaCipher"), props, transform);
            
        } catch (Exception ex) {
            Logger.getLogger(Test05_OpenSSL.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        System.out.println(cipher.getClass().getName());

        String input = "hello world!";
        //Encryption with CryptoOutputStream.

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        CryptoOutputStream cos0 = new MyCryptoOutputStream(outputStream, cipher, 8192,
                new SecretKeySpec(getUTF8Bytes("12345678901234567890123456789012"), "AES"),
                new IvParameterSpec(getUTF8Bytes("12345678901234567890123456789012")));

        try (CryptoOutputStream cos = new CryptoOutputStream(transform, properties, outputStream, key, iv)) {
            cos.write(getUTF8Bytes(input));
            cos.flush();
        }

        // The encrypted data:
        System.out.println("Encrypted: " + Arrays.toString(outputStream.toByteArray()));

        // Decryption with CryptoInputStream.
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        CryptoInputStream cis0 = new MyCryptoInputStream(
                inputStream,
                cipher,
                8192,
                new SecretKeySpec(getUTF8Bytes("12345678901234567890123456789012"), "AES"),
                new IvParameterSpec(getUTF8Bytes("12345678901234567890123456789012")));

        try (CryptoInputStream cis = new CryptoInputStream(transform, properties, inputStream, key, iv)) {
            byte[] decryptedData = new byte[1024];
            int decryptedLen = 0;
            int i;
            while ((i = cis.read(decryptedData, decryptedLen, decryptedData.length - decryptedLen)) > -1) {
                decryptedLen += i;
            }
            System.out.println("Decrypted: " + new String(decryptedData, 0, decryptedLen, StandardCharsets.UTF_8));
        }

        assertTrue(true);
    }

    private static byte[] getUTF8Bytes(String input) {
        return input.getBytes(StandardCharsets.UTF_8);
    }
}
