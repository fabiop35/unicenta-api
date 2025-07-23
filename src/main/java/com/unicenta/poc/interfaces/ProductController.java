package com.unicenta.poc.interfaces;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.unicenta.poc.application.ProductService;
import com.unicenta.poc.domain.Product;
import com.unicenta.poc.interfaces.dto.ProductDto;
import com.unicenta.poc.interfaces.dto.ProductResponseDto;

@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductDto productDto) {
        System.out.println(">>>ProductController.productDto.categoryId: " + productDto.getCategoryId());
        Product createdProduct = productService.createProduct(productDto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * Fetches a single product by its ID. Will return 404 Not Found if the ID
     * does not exist, handled by GlobalExceptionHandler.
     *
     * @param id The UUID of the product.
     * @return The product data or a 404 error.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
}
