package com.github.javacommons.encryption.test;

import com.github.javacommons.encryption.CommonKeyAlgorithm;
import com.github.javacommons.encryption.CryptoUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import static org.junit.Assert.*;

public class BasicTest {

    @Test
    public void testRandom1() {
        for (int i = 9990; i < 10000; i++) {
            //String randomString = CryptoUtils.randomAsciiString(i);
            //byte[] randomBytes = randomString.getBytes();
            byte[] randomBytes = CryptoUtils.randomBinaryBytes(i);
            System.out.println(CryptoUtils.md5Hex(randomBytes));
            byte[] key = CryptoUtils.randomAsciiBytes(i);
            byte[] sha256 = CryptoUtils.sha256(key);
            byte[] sha128 = CryptoUtils.randomAsciiBytes(16);
            CommonKeyAlgorithm chain0 = new CommonKeyAlgorithm();
            CommonKeyAlgorithm chain1 = new CommonKeyAlgorithm();
            //chain1.addCryptoEngine("Blowfish", sha256, 10);
            //chain1.addCryptoEngine("Rijndael", sha256, 10);
            //chain1.addCryptoEngine("jdk::aes", sha128, 10);
            chain1.addOperation("bc::aes", sha128, 10);
            chain1.addOperation("BC::Blowfish", sha128, 10);
            chain1.addOperation("aes", "md5", "あいうえお", 10);
            chain1.addOperation("aes", "128bit", "かきくけこ", 10);
            chain1.addOperation("aes", "hex", "12345678901234567890123456789012", 10);
            chain1.addOperation("bc::Twofish", "128bit", "かきくけこ", 10);
            //chain1.addOperation("aes", "160bit", "かきくけこ", 10);
            //chain1.addOperation("bc::aes", "256bit", "かきくけこ", 10);
            //chain1.addOperation("bc::blowfish", "256bit", "かきくけこ", 10);
            //chain1.addOperation("bc::Rijndael", "256bit", "かきくけこ", 10);
            //chain1.addOperation("bc::Rijndael", "160bit", "かきくけこ", 10);
            //chain1.addOperation("bc::AES/CBC/PKCS5Padding", "256bit", "かきくけこ", 10);
            //chain1.addOperation("bc::Twofish", "256bit", "かきくけこ", 10);
            //chain1.addOperation("GNU-CRYPTO::aes", "256bit", "かきくけこ", 10);

            String base64 = chain1.encryptToBase64(randomBytes);
            System.out.println(base64);
            /*
            byte[] result0 = chain0.decryptFromBase64(base64);
            System.out.println("" + result0);
            if (result0 != null) {
                throw new IllegalStateException();
            }*/
            byte[] result1 = (byte[])chain1.decryptFromBase64(base64);
            System.out.println(CryptoUtils.md5Hex(result1));
            ////System.out.println(result.equals(randomBytes));
            System.out.println(ArrayUtils.isEquals(result1, randomBytes));
            if (!ArrayUtils.isEquals(result1, randomBytes)) {
                throw new IllegalStateException();
            }
        }
     }

    @Test
    public void testDecryptRijndael() {
        //throw new IllegalStateException();
        //assertTrue(false);
    }
}
