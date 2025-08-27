package com.unicenta.poc.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ProductJsonTest {

    @Autowired
    private JacksonTester<Product> json;

    @Test
    void testSerialize() throws Exception {
        Product product = new Product("REF001", "CODE001", "Test Product", 100.0, 80.0, "cat-01", "tax-cat-01", "Test Product Display", "dee29ece-5b13-4f71-bc9e-845dbccddea9");
        product.setId("prod-01");

        assertThat(json.write(product)).extractingJsonPathStringValue("@.id").isEqualTo("prod-01");
        assertThat(json.write(product)).extractingJsonPathStringValue("@.name").isEqualTo("Test Product");
        assertThat(json.write(product)).extractingJsonPathNumberValue("@.pricesell").isEqualTo(100.0);
        assertThat(json.write(product)).extractingJsonPathStringValue("@.categoryId").isEqualTo("cat-01");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":\"prod-01\",\"reference\":\"REF001\",\"code\":\"CODE001\",\"name\":\"Test Product\",\"pricesell\":100.0,\"pricebuy\":80.0,\"categoryId\":\"cat-01\",\"taxcatId\":\"tax-cat-01\",\"display\":\"Test Product Display\"}";

        Product expected = new Product("REF001", "CODE001", "Test Product", 100.0, 80.0, "cat-01", "tax-cat-01", "Test Product Display", "dee29ece-5b13-4f71-bc9e-845dbccddea9");
        expected.setId("prod-01");

        Product actual = json.parseObject(content);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("value", "categoryIdNotKey", "memodate", "now")
                .isEqualTo(expected);

    }
}
