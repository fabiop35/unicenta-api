package com.unicenta.poc.config;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter // Marks this as a converter for writing to the database
public class UUIDToBytesConverter implements Converter<UUID, byte[]> {

    @Override
    public byte[] convert(UUID source) {
        if (source == null) {
            return null;
        }
        // Converts UUID to its 16-byte representation.
        // Most significant bits (8 bytes) followed by least significant bits (8 bytes).
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(source.getMostSignificantBits());
        bb.putLong(source.getLeastSignificantBits());
        return bb.array();
    }
}
