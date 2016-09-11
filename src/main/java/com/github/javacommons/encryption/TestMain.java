package com.github.javacommons.encryption;

public class TestMain {

    public static void main(String[] args) {
        byte[] key = "abc".getBytes();
        byte[] sha256 = CryptoUtils.sha256(key);
        //System.out.println(sha256.length);
        sha256 = CryptoUtils.randomAsciiBytes(16);
        //CryptoEngine en = new CryptoEngine("AES", sha256);
        //CryptoEngine en = new CryptoEngine("AESFast", sha256, 10);
        //CryptoEngine en = new CryptoEngine("Blowfish", sha256, 1000);
        //byte[] data = "1234567890123456".getBytes();
        CryptoSequence en = new CryptoSequence();
        en.addCryptoEngine("Blowfish", sha256, 10);
        en.addCryptoEngine("AES", sha256, 10);
        byte[] data = "abcテスト".getBytes();
        byte[] enc = en.encryptToBytes(data);
        String enc2 = en.encryptToBase64(data);
        System.out.println(enc2);
        //System.out.println(enc.length);
        byte[] dec = en.decryptFromBytes(enc);
        System.out.println(new String(dec));

        Double x = 1.23456789;
        String x64 = en.objectToBase64(x);
        System.out.println(x64);
        Double y = en.objectFromBase64(x64, Double.class);
        System.out.println(y);
        System.out.println(x == y);
        ////System.out.println(RandomStringUtils.randomAscii(16));
    }

}
