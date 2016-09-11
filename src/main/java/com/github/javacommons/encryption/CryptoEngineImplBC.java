package com.github.javacommons.encryption;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.engines.CAST5Engine;
import org.bouncycastle.crypto.engines.CAST6Engine;
import org.bouncycastle.crypto.engines.CamelliaEngine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.GOST28147Engine;
import org.bouncycastle.crypto.engines.NoekeonEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.engines.RC564Engine;
import org.bouncycastle.crypto.engines.RC6Engine;
import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.crypto.engines.SEEDEngine;
import org.bouncycastle.crypto.engines.SerpentEngine;
import org.bouncycastle.crypto.engines.SkipjackEngine;
import org.bouncycastle.crypto.engines.TEAEngine;
import org.bouncycastle.crypto.engines.TwofishEngine;
import org.bouncycastle.crypto.engines.XTEAEngine;
import org.bouncycastle.crypto.params.KeyParameter;

public class CryptoEngineImplBC extends CryptoEngineImpl {

    BlockCipher cipher = null;
    byte[] secretKey;
    int times;
    
    private CryptoEngineImplBC(BlockCipher cipher, byte[] secretKey, int times)
    {
        this.cipher = cipher;
        this.secretKey = secretKey;
        this.times = times;
    }
    
    public static CryptoEngineImpl findAlgorithm(String algorithm, byte[] secretKey, int times)
    {
        BlockCipher cipher;
        if ("Rijndael".equalsIgnoreCase(algorithm)) {
            cipher = new RijndaelEngine();
        } else if ("AES".equalsIgnoreCase(algorithm)) {
            cipher = new AESFastEngine();
        } else if ("Blowfish".equalsIgnoreCase(algorithm)) {
            cipher = new BlowfishEngine();
        } else if ("Camellia".equalsIgnoreCase(algorithm)) {
            cipher = new CamelliaEngine();
        } else if ("CAST5".equalsIgnoreCase(algorithm)) {
            cipher = new CAST5Engine();
        } else if ("CAST6".equalsIgnoreCase(algorithm)) {
            cipher = new CAST6Engine();
        } else if ("DES".equalsIgnoreCase(algorithm)) {
            cipher = new DESEngine();
        } else if ("DESede".equalsIgnoreCase(algorithm) || "DES3".equalsIgnoreCase(algorithm)) {
            cipher = new DESedeEngine();
        } else if ("GOST".equalsIgnoreCase(algorithm) || "GOST28147".equals(algorithm)) {
            cipher = new GOST28147Engine();
        } else if ("Noekeon".equalsIgnoreCase(algorithm)) {
            cipher = new NoekeonEngine();
        } else if ("RC2".equalsIgnoreCase(algorithm)) {
            cipher = new RC2Engine();
        } else if ("RC5".equalsIgnoreCase(algorithm)) {
            cipher = new RC564Engine();
        } else if ("RC6".equalsIgnoreCase(algorithm)) {
            cipher = new RC6Engine();
        } else if ("SEED".equalsIgnoreCase(algorithm)) {
            cipher = new SEEDEngine();
        } else if ("Serpent".equalsIgnoreCase(algorithm)) {
            cipher = new SerpentEngine();
        } else if ("Skipjack".equalsIgnoreCase(algorithm)) {
            cipher = new SkipjackEngine();
        } else if ("TEA".equalsIgnoreCase(algorithm)) {
            cipher = new TEAEngine();
        } else if ("Twofish".equalsIgnoreCase(algorithm)) {
            cipher = new TwofishEngine();
        } else if ("XTEA".equalsIgnoreCase(algorithm)) {
            cipher = new XTEAEngine();
        } else {
            return null;
        }
        return new CryptoEngineImplBC(cipher, secretKey, times);
    }
    
    @Override
    public byte[] encryptToBytes(byte[] originalSource) {
        byte[] buffer = new byte[originalSource.length];
        System.arraycopy(originalSource, 0, buffer, 0, originalSource.length);
        cipher.init(true, new KeyParameter(secretKey));
        //System.out.println(CryptoUtils.md5Hex(buffer));
        for (int i = 0; i < times; ++i) {
            cipher.processBlock(buffer, 0, buffer, 0);
            //System.out.println(cipher.getAlgorithmName() + ":" + CryptoUtils.md5Hex(buffer));
        }
        return buffer;
    }

    @Override
    public byte[] decryptFromBytes(byte[] encryptedBytes) {
        byte[] buffer = new byte[encryptedBytes.length];
        System.arraycopy(encryptedBytes, 0, buffer, 0, encryptedBytes.length);
        cipher.init(false, new KeyParameter(secretKey));
        for (int i = 0; i < times; ++i) {
            cipher.processBlock(buffer, 0, buffer, 0);
        }
        return buffer;
    }
    
}
