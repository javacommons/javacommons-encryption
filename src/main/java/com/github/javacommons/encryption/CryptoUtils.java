package com.github.javacommons.encryption;

import java.security.SecureRandom;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

public class CryptoUtils {

    public static String hex(byte[] bytes)
    {
        return Hex.encodeHexString(bytes);
    }

    public static byte[] md5(byte[] bytes) {
        return DigestUtils.md5(bytes);
    }

    public static String md5Hex(byte[] bytes) {
        return DigestUtils.md5Hex(bytes);
    }

    public static byte[] sha256(byte[] bytes) {
        return DigestUtils.sha256(bytes);
    }

    public static String sha256Hex(byte[] bytes) {
        // 16進数文字列でMD5値を取得する
        return DigestUtils.sha256Hex(bytes);
    }
    
    public static byte[] randomBytes(int count)
    {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[count];
        random.nextBytes(bytes);
        return bytes;
    }

    public static byte[] randomAsciiBytes(int count) {
        return RandomStringUtils.randomAscii(count).getBytes();
    }

    public static String randomAsciiString(int count) {
        return RandomStringUtils.randomAscii(count);
    }

}
