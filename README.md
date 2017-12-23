[![Maven Central](https://img.shields.io/maven-central/v/com.github.javacommons/javacommons-encryption.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.javacommons%22%20AND%20a%3A%22javacommons-encryption%22)

Java package (Maven Central package) that enables a program to enctypt using multiple algorithms/keys (in order to prevent/disable hacking).

```
package com.github.javacommons.encryption;

import com.github.javacommons.encryption.CommonKeyAlgorithm;
import com.github.javacommons.encryption.CryptoUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import static org.junit.Assert.*;

public class Test01_Basic {
    @Test
    public void testRandom1() {
        for (int i = 10; i < 11; i++) {
            byte[] randomBytes = CryptoUtils.randomBinaryBytes(i);
            System.out.println(CryptoUtils.md5Hex(randomBytes));
            byte[] key = CryptoUtils.randomAsciiBytes(i);
            byte[] sha256 = CryptoUtils.sha256(key);
            byte[] sha128 = CryptoUtils.randomAsciiBytes(16);
            CommonKeyAlgorithm chain1 = new CommonKeyAlgorithm();
            chain1.addOperation("bc::aes", sha128, 10);
            chain1.addOperation("BC::Blowfish", sha128, 10);
            chain1.addOperation("aes", "md5", "‚ ‚¢‚¤‚¦‚¨", 10);
            chain1.addOperation("aes", "128bit", "‚©‚«‚­‚¯‚±", 10);
            chain1.addOperation("aes", "hex", "12345678901234567890123456789012", 10);
            chain1.addOperation("bc::Twofish", "128bit", "‚©‚«‚­‚¯‚±", 10);
            //chain1.addOperation("aes", "160bit", "‚©‚«‚­‚¯‚±", 10);
            //chain1.addOperation("bc::aes", "256bit", "‚©‚«‚­‚¯‚±", 10);
            //chain1.addOperation("bc::blowfish", "256bit", "‚©‚«‚­‚¯‚±", 10);
            //chain1.addOperation("bc::Rijndael", "256bit", "‚©‚«‚­‚¯‚±", 10);
            //chain1.addOperation("bc::Rijndael", "160bit", "‚©‚«‚­‚¯‚±", 10);
            //chain1.addOperation("bc::AES/CBC/PKCS5Padding", "256bit", "‚©‚«‚­‚¯‚±", 10);
            //chain1.addOperation("bc::Twofish", "256bit", "‚©‚«‚­‚¯‚±", 10);
            //chain1.addOperation("GNU-CRYPTO::aes", "256bit", "‚©‚«‚­‚¯‚±", 10);
            String base64 = chain1.encryptToBase64(randomBytes);
            System.out.println(base64);
            byte[] result1 = (byte[])chain1.decryptFromBase64(base64);
            System.out.println(CryptoUtils.md5Hex(result1));
            System.out.println(ArrayUtils.isEquals(result1, randomBytes));
            if (!ArrayUtils.isEquals(result1, randomBytes)) {
                throw new IllegalStateException();
            }
        }
     }
}
```
