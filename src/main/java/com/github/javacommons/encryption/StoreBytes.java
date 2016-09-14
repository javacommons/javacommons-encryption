package com.github.javacommons.encryption;

public class StoreBytes extends Encryptable {

    public byte[] bytes = null;
    
    private StoreBytes()
    {
    }

    public StoreBytes(byte[] bytes) {
        this.bytes = bytes;
    }
    
    public byte[] data()
    {
        return this.bytes;
    }

}
