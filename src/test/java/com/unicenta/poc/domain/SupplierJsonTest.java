package com.unicenta.poc.domain;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class SupplierJsonTest {

    @Autowired
    private JacksonTester<Supplier> json;

    @Test
    void testSerialize() throws Exception {
        Supplier supplier = new Supplier("S-001", "ACME Corp", 10000);
        supplier.setId("sup-123");
        supplier.setEmail("contact@acme.com");
        supplier.setCurdate(LocalDateTime.parse("2025-07-30T10:00:00"));

        assertThat(json.write(supplier)).hasJsonPathStringValue("@.id");
        assertThat(json.write(supplier)).extractingJsonPathStringValue("@.id").isEqualTo("sup-123");
        assertThat(json.write(supplier)).extractingJsonPathStringValue("@.name").isEqualTo("ACME Corp");
        assertThat(json.write(supplier)).extractingJsonPathNumberValue("@.maxdebt").isEqualTo(10000.0);
        assertThat(json.write(supplier)).extractingJsonPathStringValue("@.curdate").isEqualTo("2025-07-30T10:00:00");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":\"sup-123\",\"searchkey\":\"S-001\",\"name\":\"ACME Corp\",\"maxdebt\":10000.0,\"email\":\"contact@acme.com\"}";

        Supplier parsed = json.parseObject(content);

        assertThat(parsed.getId()).isEqualTo("sup-123");
        assertThat(parsed.getName()).isEqualTo("ACME Corp");
        assertThat(parsed.getEmail()).isEqualTo("contact@acme.com");
    }
}
