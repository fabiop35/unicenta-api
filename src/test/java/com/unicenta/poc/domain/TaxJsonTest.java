package com.unicenta.poc.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

@JsonTest
class TaxJsonTest {

    @Autowired
    private JacksonTester<Tax> json;

    @Test
    void testSerialize() throws Exception {
        Tax tax = new Tax("IVA General", "taxcat-123", 0.19);
        tax.setId("tax-01");

        assertThat(json.write(tax)).extractingJsonPathStringValue("@.id").isEqualTo("tax-01");
        assertThat(json.write(tax)).extractingJsonPathStringValue("@.name").isEqualTo("IVA General");
        assertThat(json.write(tax)).extractingJsonPathStringValue("@.categoryId").isEqualTo("taxcat-123");
        assertThat(json.write(tax)).extractingJsonPathNumberValue("@.rate").isEqualTo(0.19);
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":\"tax-01\",\"name\":\"IVA General\",\"categoryId\":\"taxcat-123\",\"rate\":0.19}";
        Tax expected = new Tax("IVA General", "taxcat-123", 0.19);
        expected.setId("tax-01");

        Tax actual = json.parseObject(content);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("value", "isNewProduct")
                .isEqualTo(expected);
    }
}
