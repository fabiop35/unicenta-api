package com.unicenta.poc.interfaces;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.unicenta.poc.application.ProductService;
import com.unicenta.poc.domain.Product;
import com.unicenta.poc.interfaces.dto.ProductDto;
import com.unicenta.poc.interfaces.dto.ProductResponseDto;


@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "http://localhost:4200, https://localhost:4200, http://192.168.10.3:4200, https://192.168.10.3:4200")
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
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(Pageable pageable) {
        System.out.println(">>> ProductController.getAllProducts(pageable) <<<");
        return ResponseEntity.ok(productService.getAllProducts(pageable));
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
        System.out.println(">>> ProductController.getProductById.id: " + id + " <<<");
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/search")
    public List<ProductResponseDto> getProductByName(@RequestParam(name = "name", required = false) String name) {
        System.out.println(">>> ProductController.getProductByName.name: " + name + " <<<");
        return productService.getProductByName(name);
    }

    @GetMapping("/searchByCode")
    public List<ProductResponseDto> getProductByCode(@RequestParam(name = "code", required = false) String code) {
        System.out.println(">>> ProductController.getProductByCode.code: " + code + " <<<");
        return productService.getProductByCode(code);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @Valid @RequestBody ProductDto dto) {
        System.out.println(">>> ProductController.updateProduct( id: " + id+" )");
        System.out.println(">>> ProductController.idSupplier: " + dto.getIdSupplier());
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @GetMapping("/getNextProductReference")
    public ResponseEntity<Map<String, String>> getNextProductReference() {
        System.out.println(">>> ProductController.getNextProductReference <<<<<<");
        String reference = productService.getProductRerence();
        return ResponseEntity.ok(Map.of("reference", reference));
    }
}
