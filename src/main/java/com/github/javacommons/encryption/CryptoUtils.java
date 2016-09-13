package com.github.javacommons.encryption;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

public class CryptoUtils {

    public static void assertBoolean(boolean b) {
        if (!b) {
            throw new IllegalStateException("Assertion failed.");
        }
    }

    public static byte[] appendByteArrays(byte[]... args) {
        int len = 0;
        for (byte[] arg : args) {
            len += arg.length;
        }
        ByteBuffer byteBuf = ByteBuffer.allocate(len);
        for (byte[] arg : args) {
            byteBuf.put(arg);
        }
        return byteBuf.array();
    }

    public static String base64Encode(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        Base64 base64 = new Base64(-1, "\n".getBytes(), true); // no newline & urlSafe
        String base64String = base64.encodeAsString(bytes);
        return base64String;
        /*
        try {
            base64String = "--URLENCODED_BASE64--" + base64String;
            return new URLCodec("UTF-8").encode(base64String);
        } catch (EncoderException ex) {
            return null;
        }*/
    }

    public static byte[] base64Decode(String base64String) {
        if (base64String == null) {
            return null;
        }
        return Base64.decodeBase64(base64String);
        /*
        try {
            String header = "--URLENCODED_BASE64--";
            if (base64String.startsWith(header)) {
                base64String = base64String.substring(header.length());
                base64String = new URLCodec("UTF-8").decode(base64String);
            }
            return Base64.decodeBase64(base64String);
        } catch (DecoderException ex) {
            return null;
        }*/
    }

    public static String hex(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    public static byte[] md5(byte[] bytes) {
        return DigestUtils.md5(bytes);
    }

    public static String md5Hex(byte[] bytes) {
        return DigestUtils.md5Hex(bytes);
    }

    public static byte[] sha1(byte[] bytes) {
        return DigestUtils.sha1(bytes);
    }

    public static String sha1Hex(byte[] bytes) {
        return DigestUtils.sha1Hex(bytes);
    }

    public static byte[] sha256(byte[] bytes) {
        return DigestUtils.sha256(bytes);
    }

    public static String sha256Hex(byte[] bytes) {
        return DigestUtils.sha256Hex(bytes);
    }

    public static byte[] randomBinaryBytes(int count) {
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

    public static String randomJapaneseString(int count) {
        String result = "";
        SecureRandom sr = new SecureRandom();
        for (int i = 0; i < count; i++) {
            int index = sr.nextInt(UnicodeRange.JAPANESE_RANGE.length);
            result += UnicodeRange.JAPANESE_RANGE[index];
        }
        return result;
    }

}
