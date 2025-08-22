package com.unicenta.poc.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unicenta.poc.domain.Category;
import com.unicenta.poc.domain.Product;
import com.unicenta.poc.domain.ProductRepository;
import com.unicenta.poc.domain.CategoryRepository;
import com.unicenta.poc.domain.Tax;
import com.unicenta.poc.domain.TaxCategoryRepository;
import com.unicenta.poc.domain.TaxRepository;
import com.unicenta.poc.domain.exceptions.ResourceNotFoundException;
import com.unicenta.poc.interfaces.dto.ProductDto;
import com.unicenta.poc.interfaces.dto.ProductResponseDto;


@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TaxCategoryRepository taxCategoryRepository;
    private final TaxRepository taxRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, TaxCategoryRepository taxCategoryRepository, TaxRepository taxRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.taxCategoryRepository = taxCategoryRepository;
        this.taxRepository = taxRepository;
    }

    @Transactional
    public Product createProduct(ProductDto dto) {
        // First, ensure the referenced category and tax category exist to avoid foreign key errors
        categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Cannot create product. Category with ID " + dto.getCategoryId() + " not found."));
        // A similar check for TaxCategoryRepository would go here if it was injected
        taxCategoryRepository.findById(dto.getTaxcatId())
                .orElseThrow(() -> new ResourceNotFoundException("Cannot create product. Tax Category with ID " + dto.getTaxcatId() + " not found."));

        Product product = new Product(dto.getReference(), dto.getCode(), dto.getName(), dto.getPricesell(), dto.getPricebuy(), dto.getCategoryId(), dto.getTaxcatId(), dto.getName());
        return productRepository.save(product);
    }

    /**
     * Retrieves a paginated list of all products, enriching them with category
     * names and tax information.
     *
     * @param pageable The pagination information (page number, size, and sort).
     * @return A Page of ProductResponseDto.
     */
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {

        // Fetch the paginated products from the database
        Page<Product> productPage = productRepository.findAll(pageable);
        List<Product> productsOnPage = productPage.getContent();
        if (productsOnPage.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }
        // Get all unique category IDs from the current page of products
        Set<String> categoryIds = productsOnPage.stream()
                .map(Product::getCategoryId)
                .collect(Collectors.toSet());

        // Fetch all necessary categories in a single query
        Map<String, String> categoryMap = categoryRepository.findAllById(categoryIds).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        //get all the tax categories  by product
        Set<String> taxCategoryIds = productsOnPage.stream()
                .map(Product::getTaxcatId)
                .collect(Collectors.toSet());

        List<Tax> taxes = taxRepository.findAllByTaxcatIdIn(new ArrayList<>(taxCategoryIds));
        Map<String, String> taxNameMap = new HashMap<>();
        Map<String, Double> taxRateMap = new HashMap<>();

        taxes.forEach(tax -> {
            taxNameMap.put(tax.getTaxcatId(), tax.getName());
            taxRateMap.put(tax.getTaxcatId(), tax.getRate());
        });

        //System.out.println("Printing values using forEach() with a lambda expression:");
        //taxRateMap.forEach((key, value) -> {System.out.println(key); System.out.println(value);});
        // Map the Product entities to ProductResponseDto
        List<ProductResponseDto> dtos = productsOnPage.stream()
                .map(product -> ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .reference(product.getReference())
                .code(product.getCode())
                .codetype(product.getCodetype())
                .pricesell(product.getPricesell())
                .pricebuy(product.getPricebuy())
                .categoryId(product.getCategoryId())
                .categoryName(categoryMap.getOrDefault(product.getCategoryId(), "Unknown"))
                .taxcatId(product.getTaxcatId())
                .taxName(taxNameMap.getOrDefault(product.getTaxcatId(), "No Tax"))
                .taxRate(taxRateMap.getOrDefault(product.getTaxcatId(), 0.0))
                .display(product.getDisplay())
                .build())
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, productPage.getTotalElements());
    }

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

        Tax tax = taxRepository.findByTaxcatId(product.getTaxcatId())
                .orElse(new Tax());

        return mapToProductResponseDto(product, categoryName, tax);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductByName(String name) {
        
        List<Product> products = productRepository.findTop10ByNameContainingIgnoreCase(name);
        List<ProductResponseDto> productsDtos = new ArrayList<>();
        products.forEach(
                (Product e) -> {
                    //System.out.println(e);
                    String categoryName = categoryRepository.findById(e.getCategoryId())
                            .map(Category::getName)
                            .orElse("N/A");
                    ProductResponseDto dto = new ProductResponseDto();
                    dto.setId(e.getId());
                    dto.setName(e.getName());
                    dto.setReference(e.getReference());
                    dto.setCode(e.getCode());
                    dto.setCodetype(e.getCodetype());
                    dto.setPricesell(e.getPricesell());
                    dto.setPricebuy(e.getPricebuy());
                    dto.setDisplay(e.getDisplay());
                    dto.setCategoryId(e.getCategoryId());
                    dto.setTaxcatId(e.getTaxcatId());
                    dto.setCategoryName(categoryName);
                    productsDtos.add(dto);
                });

        return productsDtos;
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductByCode(String code) {
        List<Product> products = productRepository.findTop10ByCodeContainingIgnoreCase(code);
        List<ProductResponseDto> productsDtos = new ArrayList<>();
        products.forEach(
                (Product e) -> {
                    //System.out.println(e);
                    String categoryName = categoryRepository.findById(e.getCategoryId())
                            .map(Category::getName)
                            .orElse("N/A");
                    ProductResponseDto dto = new ProductResponseDto();
                    dto.setId(e.getId());
                    dto.setName(e.getName());
                    dto.setReference(e.getReference());
                    dto.setCode(e.getCode());
                    dto.setCodetype(e.getCodetype());
                    dto.setPricesell(e.getPricesell());
                    dto.setPricebuy(e.getPricebuy());
                    dto.setDisplay(e.getDisplay());
                    dto.setCategoryId(e.getCategoryId());
                    dto.setTaxcatId(e.getTaxcatId());
                    dto.setCategoryName(categoryName);
                    productsDtos.add(dto);
                });

        return productsDtos;
    }

    /**
     * Private helper method to map a Product entity to its DTO.
     */
    private ProductResponseDto mapToProductResponseDto(Product product, String categoryName, Tax tax) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setReference(product.getReference());
        dto.setCode(product.getCode());
        dto.setCodetype(product.getCodetype());
        dto.setPricesell(product.getPricesell());
        dto.setPricebuy(product.getPricebuy());
        dto.setDisplay(product.getDisplay());
        dto.setCategoryId(product.getCategoryId());
        dto.setTaxcatId(product.getTaxcatId());
        dto.setCategoryName(categoryName);
        dto.setTaxName(tax.getName());
        dto.setTaxRate(tax.getRate());
        return dto;
    }

    @Transactional
    public Product updateProduct(String id, ProductDto dto) {
        // Ensure the product exists before trying to update
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Validate foreign keys exist
        if (!categoryRepository.existsById(dto.getCategoryId())) {
            throw new ResourceNotFoundException("Cannot update product. Category with ID " + dto.getCategoryId() + " not found.");
        }
        // A similar check for TaxCategoryRepository would go here

        // Map DTO fields to the existing product entity
        product.setName(dto.getName());
        product.setReference(dto.getReference());
        product.setCode(dto.getCode());
        product.setDisplay(dto.getDisplay());
        product.setPricebuy(dto.getPricebuy());
        product.setPricesell(dto.getPricesell());
        product.setCategoryId(dto.getCategoryId());
        product.setTaxcatId(dto.getTaxcatId());
        product.markNotNew();

        return productRepository.save(product);
    }

}
