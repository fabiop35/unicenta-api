package com.unicenta.poc.interfaces;

import com.unicenta.poc.application.TaxCategoryService;
import com.unicenta.poc.domain.TaxCategory;
import com.unicenta.poc.interfaces.dto.NameDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tax-categories")
@CrossOrigin(origins = "http://localhost:4200")
public class TaxCategoryController {

    private final TaxCategoryService taxCategoryService;

    public TaxCategoryController(TaxCategoryService taxCategoryService) {
        this.taxCategoryService = taxCategoryService;
    }

    @PostMapping
    public ResponseEntity<TaxCategory> createTaxCategory(@Valid @RequestBody NameDto dto) {
        TaxCategory createdTaxCategory = taxCategoryService.createTaxCategory(dto.getName());
        return new ResponseEntity<>(createdTaxCategory, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TaxCategory>> getAllTaxCategories() {
        return ResponseEntity.ok(taxCategoryService.getAllTaxCategories());
    }
}
