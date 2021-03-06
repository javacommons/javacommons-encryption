package com.github.javacommons.encryption;

import com.google.common.base.Preconditions;
import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class EngineSpecParser {

    public final String providerName;
    public final Provider provider;
    public final String cipherSpec;
    //public final Cipher cipher;
    public final String secretKeySpec;

    protected EngineSpecParser(String algorithmSpec) {
        Preconditions.checkNotNull(algorithmSpec, "[algorithmSpec]");
        if (algorithmSpec.contains("::")) {
            String[] array = algorithmSpec.split("::");
            if (array.length != 2) {
                throw new IllegalStateException("Too many ::");
            }
            this.providerName = array[0];
            algorithmSpec = array[1];
            this.provider = _getProvider(this.providerName);
        } else {
            providerName = null;
            provider = null;
        }
        if (algorithmSpec.contains("|")) {
            String[] array = algorithmSpec.split("|");
            if (array.length != 2) {
                throw new IllegalStateException("Too many |");
            }
            this.cipherSpec = array[0];
            this.secretKeySpec = array[1];
        } else {
            this.cipherSpec = algorithmSpec;
            this.secretKeySpec = algorithmSpec;
        }
        /*
        try {
            this.cipher = (this.provider == null ? Cipher.getInstance(cipherSpec) : Cipher.getInstance(cipherSpec, provider));
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }*/
    }

    private static Provider _getProvider(String providerName) {
        if ("BC".equalsIgnoreCase(providerName)) {
            return new BouncyCastleProvider();
        //} else if ("GNU-CRYPTO".equalsIgnoreCase(providerName)) {
        //    return new GnuCrypto();
        } else {
            Provider p = Security.getProvider(providerName);
            if (p == null) {
                throw new IllegalStateException("Provider not found: " + providerName);
            }
            return p;
        }
    }
}
