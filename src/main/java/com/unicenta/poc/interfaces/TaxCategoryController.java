package com.unicenta.poc.interfaces;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unicenta.poc.application.TaxCategoryService;
import com.unicenta.poc.domain.TaxCategory;
import com.unicenta.poc.interfaces.dto.NameDto;

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

    @PutMapping("/{id}")
    public ResponseEntity<TaxCategory> updateTaxCategory(@PathVariable String id, @Valid @RequestBody NameDto dto) {
        return ResponseEntity.ok(taxCategoryService.updateTaxCategory(id, dto.getName()));
    }
}
