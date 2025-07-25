package com.unicenta.poc.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unicenta.poc.domain.Tax;
import com.unicenta.poc.domain.TaxRepository;
import com.unicenta.poc.domain.exceptions.ResourceNotFoundException;
import com.unicenta.poc.interfaces.dto.TaxDto;

@Service
public class TaxService {

    private final TaxRepository taxRepository;

    public TaxService(TaxRepository taxRepository) {
        this.taxRepository = taxRepository;
    }

    public Tax createTax(TaxDto dto) {
        Tax tax = new Tax(dto.getName(), dto.getCategoryId(), dto.getRate());
        return taxRepository.save(tax);
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
        tax.setCategoryId(dto.getCategoryId());
        return taxRepository.save(tax);
    }
}
