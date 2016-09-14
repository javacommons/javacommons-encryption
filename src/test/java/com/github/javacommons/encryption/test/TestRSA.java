package com.github.javacommons.encryption.test;

import com.github.javacommons.encryption.CryptoUtils;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestRSA {

    @Test
    public void test1() throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeySpecException {
        //
        KeyPairGenerator generator;
        generator = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        generator.initialize(1024, random);
        KeyPair keyPair = generator.generateKeyPair();
        //
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        
        PrivateKey privateKey2 = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKey.getEncoded()));
        PublicKey publicKey2 = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKey.getEncoded()));

        System.out.printf("privateKey(%d)=%s\n", privateKey.getEncoded().length, CryptoUtils.hex(privateKey.getEncoded()));
        System.out.printf("privateKey2(%d)=%s\n", privateKey2.getEncoded().length, CryptoUtils.hex(privateKey2.getEncoded()));

        System.out.printf("publicKey(%d)=%s\n", publicKey.getEncoded().length, CryptoUtils.hex(publicKey.getEncoded()));
        System.out.printf("publicKey2(%d)=%s\n", publicKey2.getEncoded().length, CryptoUtils.hex(publicKey2.getEncoded()));

        byte[] data = "abc漢字".getBytes();
        System.out.printf("data=%s\n", new String(data));

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey2);
        byte[] encoded = cipher.doFinal(data);

        System.out.printf("encoded(%d)=%s\n", encoded.length, CryptoUtils.hex(encoded));

        //Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey2);
        byte[] data2 = cipher.doFinal(encoded);

        System.out.printf("data2(%d)=%s\n", data2.length, CryptoUtils.hex(data2));
        System.out.printf("data2=%s\n", new String(data2));
        
        assertTrue(true);
    }

}
