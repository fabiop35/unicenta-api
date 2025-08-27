package com.unicenta.poc.application;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.unicenta.poc.domain.CategoryRepository;
import com.unicenta.poc.domain.Product;
import com.unicenta.poc.domain.ProductRepository;
import com.unicenta.poc.interfaces.dto.ProductDto;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void updateProduct_WhenExists_ShouldUpdateAndReturnProduct() {
        // Arrange
        ProductDto dto = new ProductDto();
        dto.setName("Updated Leche");
        dto.setReference("UPD-REF-01");
        dto.setCode("UPD-CODE-01");
        dto.setPricebuy(4000.0);
        dto.setPricesell(5200.0);
        dto.setCategoryId("cat-123");
        dto.setTaxcatId("tax-123");
        dto.setDisplay("Updated Display");

        Product existingProduct = new Product("OLD-REF", "OLD-CODE", "Old Leche", 5000, 3800, "cat-123", "tax-123", "Old Display", "dee29ece-5b13-4f71-bc9e-845dbccddea9");
        existingProduct.setId("prod-123");

        when(productRepository.findById("prod-123")).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.existsById("cat-123")).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Product updatedProduct = productService.updateProduct("prod-123", dto);

        // Assert
        assertNotNull(updatedProduct);
        assertEquals("Updated Leche", updatedProduct.getName());
        assertEquals(5200, updatedProduct.getPricesell());
        verify(productRepository, times(1)).save(existingProduct);
    }
}
