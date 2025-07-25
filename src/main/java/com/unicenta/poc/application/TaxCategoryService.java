package com.unicenta.poc.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unicenta.poc.domain.TaxCategory;
import com.unicenta.poc.domain.TaxCategoryRepository;
import com.unicenta.poc.domain.exceptions.ResourceNotFoundException;

@Service
public class TaxCategoryService {

    private final TaxCategoryRepository taxCategoryRepository;

    public TaxCategoryService(TaxCategoryRepository taxCategoryRepository) {
        this.taxCategoryRepository = taxCategoryRepository;
    }

    public TaxCategory createTaxCategory(String name) {
        TaxCategory taxCategory = new TaxCategory(name);
        return taxCategoryRepository.save(taxCategory);
    }

    public List<TaxCategory> getAllTaxCategories() {
        return taxCategoryRepository.findAll();
    }

    @Transactional
    public TaxCategory updateTaxCategory(String id, String newName) {
        TaxCategory taxCategory = getTaxCategoryById(id);
        taxCategory.setName(newName);
        return taxCategoryRepository.save(taxCategory);
    }

    @Transactional(readOnly = true)
    public TaxCategory getTaxCategoryById(String id) {
        return taxCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }
}
