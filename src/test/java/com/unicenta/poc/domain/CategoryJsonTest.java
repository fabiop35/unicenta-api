package com.unicenta.poc.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;

@JsonTest(excludeAutoConfiguration = {
    DataSourceAutoConfiguration.class,
    JdbcTemplateAutoConfiguration.class,
    JdbcRepositoriesAutoConfiguration.class
})
class CategoryJsonTest {

    @Autowired
    private JacksonTester<Category> json;

    @Test
    void testSerialize() throws Exception {
        Category category = new Category("Bebidas");
        category.setId("cat-123");
        var jsonContent = json.write(category);

        assertThat(jsonContent).extractingJsonPathStringValue("@.id")
                .isEqualTo(category.getId());

        assertThat(jsonContent).hasJsonPathStringValue("@.id");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":\"cat-123\",\"name\":\"Bebidas\",\"value\":\"some-specific-uuid\"}";
        Category expectedCategory = new Category("Bebidas");
        expectedCategory.setId("cat-123");

        Category actual = json.parseObject(content);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("value", "isNewProduct")
                .isEqualTo(expectedCategory);
    }
}
