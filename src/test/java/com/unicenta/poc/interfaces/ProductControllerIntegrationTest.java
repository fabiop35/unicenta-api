package com.unicenta.poc.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

import com.unicenta.poc.domain.Category;
import com.unicenta.poc.domain.CategoryRepository;
import com.unicenta.poc.domain.Product;
import com.unicenta.poc.domain.ProductRepository;
import com.unicenta.poc.domain.TaxCategory;
import com.unicenta.poc.domain.TaxCategoryRepository;
import com.unicenta.poc.interfaces.dto.ProductDto;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TaxCategoryRepository taxCategoryRepository;

    private Category savedCategory;
    private TaxCategory savedTaxCategory;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        taxCategoryRepository.deleteAll();
        categoryRepository.deleteAll();

        savedCategory = categoryRepository.save(new Category("Lacteos"));
        savedTaxCategory = taxCategoryRepository.save(new TaxCategory("IVA 5%"));
    }

    @Test
    void updateProduct_WhenExists_ShouldReturn200() throws Exception {
        Product existingProduct = new Product("REF-01", "CODE-01", "Leche", 5000, 4000, savedCategory.getId(), savedTaxCategory.getId(), "Leche 1L");
        productRepository.save(existingProduct);

        ProductDto updateDto = new ProductDto();
        updateDto.setName("Leche Deslactosada");
        updateDto.setReference("REF-02");
        updateDto.setCode("CODE-02");
        updateDto.setPricebuy(4200.0);
        updateDto.setPricesell(5500.0);
        updateDto.setDisplay("Leche Deslactosada 1L");
        updateDto.setCategoryId(savedCategory.getId());
        updateDto.setTaxcatId(savedTaxCategory.getId());

        mockMvc.perform(put("/api/v1/products/" + existingProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Leche Deslactosada")))
                .andExpect(jsonPath("$.pricesell", is(5500.0)));
    }
}
