package com.unicenta.poc.application;

import com.unicenta.poc.domain.TaxCategory;
import com.unicenta.poc.domain.TaxCategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

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
}
