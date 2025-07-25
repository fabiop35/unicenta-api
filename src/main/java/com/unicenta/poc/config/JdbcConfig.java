package com.unicenta.poc.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;

@Configuration
public class JdbcConfig extends AbstractJdbcConfiguration {

    @Override
    public List<?> userConverters() {
        // Register your custom converters here
        return Arrays.asList(
                new UUIDToBytesConverter(),
                new BytesToUUIDConverter()
        );
    }

}
