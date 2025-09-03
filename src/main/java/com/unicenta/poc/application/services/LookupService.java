package com.unicenta.poc.application.services;

import com.unicenta.poc.domain.AttributeSetInstance;
import com.unicenta.poc.domain.AttributeSetInstanceRepository;
import com.unicenta.poc.domain.Category;
import com.unicenta.poc.domain.CategoryRepository;
import com.unicenta.poc.domain.Location;
import com.unicenta.poc.domain.LocationRepository;
import com.unicenta.poc.domain.People;
import com.unicenta.poc.domain.PeopleRepository;
import com.unicenta.poc.domain.Product;
import com.unicenta.poc.domain.ProductRepository;
import com.unicenta.poc.domain.Supplier;
import com.unicenta.poc.domain.SupplierRepository;
import com.unicenta.poc.domain.Tax;
import com.unicenta.poc.domain.TaxRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LookupService {

    private final CategoryRepository categoryRepository;
    private final TaxRepository taxRepository;
    private final SupplierRepository supplierRepository;
    private final LocationRepository locationRepository;
    private final AttributeSetInstanceRepository attributeSetInstanceRepository;
    private final PeopleRepository peopleRepository;
    private final ProductRepository productRepository;

    /**
     * Get category name by ID (cached)
     *
     * @param id
     * @return
     */
    @Cacheable(value = "categoriesNames", key = "#id", unless = "#result == null")
    public String getCategoryName(String id) {
        return categoryRepository.findById(id)
                .map(Category::getName)
                .orElse("Unknown");
    }

    /**
     * Bulk load all categories as a cached map (if needed).This is safe because
     * it has no parameters → key is class + method.
     *
     * @return
     */
    @Cacheable(value = "allCategories")
    public Map<String, String> getAllCategoryMap() {
        System.out.println(">>> Loading from DB: getAllCategoryMap() <<<");
        return categoryRepository.findAll().stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
    }

    /**
     * Get tax name by taxcatId
     *
     * @param taxcatId
     * @return
     */
    @Cacheable(value = "taxesNames", key = "#taxcatId", unless = "#result == null")
    public String getTaxName(String taxcatId) {
        System.out.println(">>> Loading from DB: getTaxName() <<<");
        return taxRepository.findAllByTaxcatIdIn(List.of(taxcatId)).stream()
                .findFirst()
                .map(Tax::getName)
                .orElse("No Tax");
    }

    /**
     * Get tax rate by taxcatId
     *
     * @param taxcatId
     * @return
     */
    @Cacheable(value = "taxesRates", key = "#taxcatId", unless = "#result == null")
    public Double getTaxRate(String taxcatId) {
        return taxRepository.findAllByTaxcatIdIn(List.of(taxcatId)).stream()
                .findFirst()
                .map(Tax::getRate)
                .orElse(0.0);
    }

    /**
     * Bulk load all taxes (ideal for full refresh)
     *
     * @return
     */
    @Cacheable(value = "allTaxes")
    public Map<String, Tax> getAllTaxesMap() {
        System.out.println(">>> Loading from DB: getAllTaxesMap() <<<");
        return taxRepository.findAll().stream()
                .collect(Collectors.toMap(Tax::getTaxcatId, tax -> tax));
    }

    @Cacheable(value = "supplierNames")
    public String getSupplierName(String id) {
        System.out.println("#>>> LookupService.supplierNames.Loading from DB: getSupplierName().id: " + id + " <<<#");
        return supplierRepository.findById(id)
                .map(Supplier::getName)
                .orElse("Unknown");
    }

    @Cacheable(value = "attributeSetInstanceDescriptions")
    public String getAttributeSetInstanceDescription(String id) {
        if (id == null) {
            return null;
        }
        return attributeSetInstanceRepository.findById(id)
                .map(AttributeSetInstance::getDescription)
                .orElse(null);
    }

    @Cacheable(value = "locationNames", key = "#id", unless = "#result == null")
    public String getLocationName(String id) {
        return locationRepository.findById(id)
                .map(Location::getName)
                .orElse("Unknown Location");
    }

    @Cacheable(value = "userNames", key = "#id", unless = "#result == null")
    public String getUserName(String id) {
        return peopleRepository.findById(id)
                .map(People::getName)
                .orElse("Unknown User");
    }

    @Cacheable(value = "productNames")
    public String getProductName(String id) {
        System.out.println("#>>> LookupService.getProductName.Loading from DB id: (" + id + ") <<<#");
        return productRepository.findById(id)
                .map(Product::getName)
                .orElse("Unknown Product");
    }

    @Cacheable(value = "productReference", key = "#id", unless = "#result == null")
    public String getProductReference(String id) {
        return productRepository.findById(id)
                .map(Product::getReference)
                .orElse("Unknown Product");
    }

    @Cacheable(value = "productCode", key = "#id", unless = "#result == null")
    public String getProductCode(String id) {
        return productRepository.findById(id)
                .map(Product::getCode)
                .orElse("Unknown Product");
    }
}
