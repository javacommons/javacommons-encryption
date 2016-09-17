package com.github.javacommons.encryption;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.UUID;

final class UuidUtils {

    public static byte[] serializeUuid(UUID uuid)
    {
        Preconditions.checkNotNull(uuid);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeLong(uuid.getMostSignificantBits());
        out.writeLong(uuid.getLeastSignificantBits());
        return out.toByteArray();
    }
    
    public static UUID deserializeUuid(byte[] bytes)
    {
        Preconditions.checkNotNull(bytes);
        Preconditions.checkState(bytes.length == 16);
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        return new UUID(in.readLong(), in.readLong());
    }
    
    public static UUID generateUuidForName(String name)
    {
        Preconditions.checkNotNull(name);
        byte[] bytes = name.getBytes();
        byte[] md5 = CryptoUtils.md5(bytes);
        return deserializeUuid(md5);
    }
    
}
