package com.unicenta.poc.application;

import com.unicenta.poc.application.services.LookupService;
import com.unicenta.poc.domain.Category;
import com.unicenta.poc.domain.CategoryRepository;
import com.unicenta.poc.domain.Product;
import com.unicenta.poc.domain.ProductRepository;
import com.unicenta.poc.domain.Tax;
import com.unicenta.poc.domain.TaxCategory;
import com.unicenta.poc.domain.TaxCategoryRepository;
import com.unicenta.poc.domain.TaxRepository;
import com.unicenta.poc.interfaces.dto.ProductResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ProductServiceIntegrationTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TaxRepository taxRepository;

    @Autowired
    private TaxCategoryRepository taxCategoryRepository;
    
    @Autowired
    private LookupService lookupService;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        // Bootstrap test data
        Category cat = new Category("CAT1", "Test Cat");
        TaxCategory taxCat = new TaxCategory("TAXCAT1", "Test TaxCat");
        Tax tax = new Tax("Test Tax", taxCat.getId(), 0.1);

        categoryRepository.save(cat);
        taxRepository.save(tax);

        productRepository.save(new Product("REF001", "7701", "Test Product", 100, 50, cat.getId(), taxCat.getId(), "TP"));

        this.productService = new ProductService(productRepository, categoryRepository, taxCategoryRepository, taxRepository, lookupService);
    }

    @Test
    void integrationTest_GetAllProducts_ReturnsCorrectData() {
        Page<ProductResponseDto> result = productService.getAllProducts(PageRequest.of(0, 10));
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getTaxRate()).isEqualTo(0.1);
    }
}
