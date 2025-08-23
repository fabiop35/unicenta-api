package com.unicenta.poc.application;

import java.util.List;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unicenta.poc.domain.Tax;
import com.unicenta.poc.domain.TaxRepository;
import com.unicenta.poc.domain.exceptions.ResourceNotFoundException;
import com.unicenta.poc.interfaces.dto.TaxDto;

@Service
public class TaxService {

    private final TaxRepository taxRepository;
    private final CacheManager cacheManager;

    public TaxService(TaxRepository taxRepository, CacheManager cacheManager) {
        this.taxRepository = taxRepository;
        this.cacheManager = cacheManager;
    }
    
    @Transactional
    public Tax createTax(TaxDto dto) {
        Tax tax = new Tax(dto.getName(), dto.getTaxcatId(), dto.getRate());
        Tax saved = taxRepository.save(tax);
        cacheManager.getCache("allTaxes").invalidate();
        return saved;
    }

    public List<Tax> getAllTaxes() {
        return taxRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Tax getTaxById(String id) {
        return taxRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Transactional
    public Tax updateTax(String id, TaxDto dto) {
        Tax tax = getTaxById(id);
        tax.setName(dto.getName());
        tax.setRate(dto.getRate());
        tax.setTaxcatId(dto.getTaxcatId());
        tax.markNotNew();
        Tax updated = taxRepository.save(tax);
        cacheManager.getCache("allTaxes").invalidate();
        return updated;
    }
}
