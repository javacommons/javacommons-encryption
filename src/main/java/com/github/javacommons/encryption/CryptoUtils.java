package com.github.javacommons.encryption;

import java.security.SecureRandom;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.RandomStringUtils;

public class CryptoUtils {

    public static String base64Encode(byte[] bytes)
    {
        Base64 base64 = new Base64(true); // (urlSafe)
        byte[] base64Bytes = base64.encodeBase64(bytes, false);
        String  base64String = new String(base64Bytes);
        base64String = "--BASE64--" + base64String + "--BASE64--";
        try {
            //return URLEncoder.encode(base64String, "UTF-8");
            return new URLCodec("UTF-8").encode(base64String);
        } catch (EncoderException ex) {
            return null;
        }
    }
    
    public static byte[] base64Decode(String base64String)
    {
        Base64 base64 = new Base64(true); // (urlSafe)
        try {
            //String decodedResult = URLDecoder.decode(base64String, "UTF-8");
            String decodedResult = new URLCodec("UTF-8").decode(base64String);
            decodedResult = decodedResult.replace("--BASE64--", "");
            return base64.decodeBase64(decodedResult);
        } catch (DecoderException ex) {
            return null;
        }
    }
    
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
    
    public static byte[] randomBinaryBytes(int count)
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
