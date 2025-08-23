package com.unicenta.poc.application;

import com.unicenta.poc.interfaces.dto.ProductResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class ProductServicePerformanceTest {

    @Autowired
    private ProductService productService;
    
    //test using System.nanoTime()
    @Test
    void performanceTest_GetAllProducts_With1000Products() {
        Pageable pageable = PageRequest.of(0, 1000);

        long start = System.nanoTime();

        Page<ProductResponseDto> result = productService.getAllProducts(pageable);

        long durationMs = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("Fetched %d products in %d ms%n", result.getTotalElements(), durationMs);
        assertThat(result).isNotNull();
        assertThat(durationMs).isLessThan(1000); // Should complete under 1 second
    }
}