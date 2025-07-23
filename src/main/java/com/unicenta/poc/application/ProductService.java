package com.unicenta.poc.application;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unicenta.poc.domain.Category;
import com.unicenta.poc.domain.Product;
import com.unicenta.poc.domain.ProductRepository;
import com.unicenta.poc.domain.CategoryRepository;
import com.unicenta.poc.domain.TaxCategoryRepository;
import com.unicenta.poc.domain.exceptions.ResourceNotFoundException;
import com.unicenta.poc.interfaces.dto.ProductDto;
import com.unicenta.poc.interfaces.dto.ProductResponseDto;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TaxCategoryRepository taxCategoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, TaxCategoryRepository taxCategoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.taxCategoryRepository = taxCategoryRepository;
    }

    @Transactional
    public Product createProduct(ProductDto dto) {
        // First, ensure the referenced category and tax category exist to avoid foreign key errors
        categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Cannot create product. Category with ID " + dto.getCategoryId() + " not found."));
        // A similar check for TaxCategoryRepository would go here if it was injected
        taxCategoryRepository.findById(dto.getTaxcatId())
                .orElseThrow(() -> new ResourceNotFoundException("Cannot create product. Tax Category with ID " + dto.getTaxcatId() + " not found."));

        Product product = new Product(dto.getReference(), dto.getCode(), dto.getName(), dto.getPricesell(), dto.getPricebuy(), dto.getCategoryId(), dto.getTaxcatId(), dto.getDisplay());
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        //return productRepository.findAll();
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            return List.of();
        }

        // 1. Collect all unique category IDs from the products list.
        List<String> categoryIds = products.stream()
                .map(Product::getCategoryId)
                .distinct()
                .collect(Collectors.toList());

        // 2. Fetch all corresponding categories in a single database query.
        Map<String, String> categoryMap = categoryRepository.findAllById(categoryIds).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        // 3. Map the Product entities to ProductResponseDto, enriching with the category name.
        return products.stream()
                .map(product -> {
                    ProductResponseDto dto = new ProductResponseDto();
                    dto.setId(product.getId());
                    dto.setName(product.getName());
                    dto.setReference(product.getReference());
                    dto.setCode(product.getCode());
                    dto.setPricesell(product.getPricesell());
                    dto.setPricebuy(product.getPricebuy());
                    dto.setDisplay(product.getDisplay());
                    dto.setCategoryId(product.getCategoryId());
                    dto.setTaxcatId(product.getTaxcatId());
                    // Get the category name from the map, providing a default if not found.
                    dto.setCategoryName(categoryMap.getOrDefault(product.getCategoryId(), "N/A"));
                    return dto;
                }).collect(Collectors.toList());
    }

    /*public ProductResponseDto getProductByIdOld(String id) {

        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            return new ProductResponseDto();
        }

        Map<String, String> categoryMap = categoryRepository.findById(product.get().getCategoryId()).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.get().getId());
        dto.setName(product.get().getName());
        dto.setReference(product.get().getReference());
        dto.setCode(product.get().getCode());
        dto.setPricesell(product.get().getPricesell());
        dto.setPricebuy(product.get().getPricebuy());
        dto.setDisplay(product.get().getDisplay());
        dto.setCategoryId(product.get().getCategoryId());
        dto.setTaxcatId(product.get().getTaxcatId());
        // Get the category name from the map, providing a default if not found.
        dto.setCategoryName(categoryMap.getOrDefault(product.get().getCategoryId(), "N/A"));
        return dto;


        //return productRepository.findById(id)
              //  .orElseThrow(() -> new RuntimeException(">>> Product not found with id: " + id)); 
    }*/
    /**
     * Fetches a single product by its ID and throws a custom exception if not
     * found.
     *
     * @param id The UUID of the product.
     * @return A ProductResponseDto object.
     */
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        String categoryName = categoryRepository.findById(product.getCategoryId())
                .map(Category::getName)
                .orElse("N/A");

        return mapToProductResponseDto(product, categoryName);
    }

    /**
     * Private helper method to map a Product entity to its DTO.
     */
    private ProductResponseDto mapToProductResponseDto(Product product, String categoryName) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setReference(product.getReference());
        dto.setCode(product.getCode());
        dto.setPricesell(product.getPricesell());
        dto.setPricebuy(product.getPricebuy());
        dto.setDisplay(product.getDisplay());
        dto.setCategoryId(product.getCategoryId());
        dto.setTaxcatId(product.getTaxcatId());
        dto.setCategoryName(categoryName);
        return dto;
    }

}
