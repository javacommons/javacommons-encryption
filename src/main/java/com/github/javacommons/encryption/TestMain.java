package com.github.javacommons.encryption;

import org.apache.commons.lang.ArrayUtils;

public class TestMain {

    public static void randomTest() {
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
            chain1.addEngine("bc::aes", sha128, 10);
            chain1.addEngine("BC::Blowfish", sha128, 10);
            String base64 = chain1.encryptToBase64(randomBytes);
            System.out.println(base64);
            byte[] result0 = chain0.decryptFromBase64(base64);
            System.out.println("" + result0);
            if(result0 != null) throw new IllegalStateException();
            byte[] result1 = chain1.decryptFromBase64(base64);
            System.out.println(CryptoUtils.md5Hex(result1));
            ////System.out.println(result.equals(randomBytes));
            System.out.println(ArrayUtils.isEquals(result1, randomBytes));
            if (!ArrayUtils.isEquals(result1, randomBytes)) {
                throw new IllegalStateException();
            }
        }
    }

    public static void main(String[] args) {
        byte[] key = "abc".getBytes();
        byte[] sha256 = CryptoUtils.sha256(key);
        //System.out.println(sha256.length);
        sha256 = CryptoUtils.randomAsciiBytes(16);
        //CryptoEngine en = new CryptoEngine("AES", sha256);
        //CryptoEngine en = new CryptoEngine("AESFast", sha256, 10);
        //CryptoEngine en = new CryptoEngine("Blowfish", sha256, 1000);
        //byte[] data = "1234567890123456".getBytes();
        CommonKeyAlgorithm en = new CommonKeyAlgorithm();
        //en.addCryptoEngine("Blowfish", sha256, 10);
        en.addEngine("AES", sha256, 1);
        byte[] data = "abcテスト".getBytes();
        data = ("|" + CryptoUtils.randomAsciiString(1024)).getBytes();
        byte[] enc = en.encryptToBytes(data);
        //String enc2 = en.encryptToBase64(data);
        //System.out.println("enc2=" + enc2);
        //System.out.println(enc.length);
        byte[] dec = en.decryptFromBytes(enc);
        System.out.println(new String(dec));

        Double x = 1.23456789;
        String x64 = en.objectToBase64(x);
        System.out.println("x64=" + x64);
        Double y = en.objectFromBase64(x64, Double.class);
        System.out.println(y);
        System.out.println(x == y);
        ////System.out.println(RandomStringUtils.randomAscii(16));
        randomTest();
    }

}
