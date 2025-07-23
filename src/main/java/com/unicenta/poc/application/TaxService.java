package com.unicenta.poc.application;

import com.unicenta.poc.domain.Tax;
import com.unicenta.poc.domain.TaxRepository;
import com.unicenta.poc.interfaces.dto.TaxDto;
import org.springframework.stereotype.Service;
import java.util.List;

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
}
