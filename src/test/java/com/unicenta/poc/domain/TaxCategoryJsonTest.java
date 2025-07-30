package com.unicenta.poc.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

@JsonTest
class TaxCategoryJsonTest {

    @Autowired
    private JacksonTester<TaxCategory> json;

    @Test
    void testSerialize() throws Exception {
        TaxCategory taxCategory = new TaxCategory("IVA 19%");
        taxCategory.setId("taxcat-123");

        assertThat(json.write(taxCategory)).extractingJsonPathStringValue("@.id").isEqualTo("taxcat-123");
        assertThat(json.write(taxCategory)).extractingJsonPathStringValue("@.name").isEqualTo("IVA 19%");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":\"taxcat-123\",\"name\":\"IVA 19%\"}";
        TaxCategory expected = new TaxCategory("IVA 19%");
        expected.setId("taxcat-123");

        // Deserializing the JSON
        TaxCategory actual = json.parseObject(content);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("value", "isNewProduct") // Ignore the fields with random or transient values
                .isEqualTo(expected);

    }
}
