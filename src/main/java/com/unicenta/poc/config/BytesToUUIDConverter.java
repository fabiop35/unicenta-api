package com.unicenta.poc.config;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter // Marks this as a converter for reading from the database
public class BytesToUUIDConverter implements Converter<byte[], UUID> {

    @Override
    public UUID convert(byte[] source) {
        if (source == null || source.length != 16) {
            // Handle null or incorrect length bytes gracefully
            return null; // Or throw an IllegalArgumentException, depending on your error handling
        }
        // Converts the 16-byte array back to a UUID.
        ByteBuffer bb = ByteBuffer.wrap(source);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }
}
