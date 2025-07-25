package com.unicenta.poc.interfaces;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.unicenta.poc.application.TaxService;
import com.unicenta.poc.domain.Tax;
import com.unicenta.poc.interfaces.dto.TaxDto;

@RestController
@RequestMapping("/api/v1/taxes")
@CrossOrigin(origins = "http://localhost:4200")
public class TaxController {

    private final TaxService taxService;

    public TaxController(TaxService taxService) {
        this.taxService = taxService;
    }

    @PostMapping
    public ResponseEntity<Tax> createTax(@Valid @RequestBody TaxDto dto) {
        Tax createdTax = taxService.createTax(dto);
        return new ResponseEntity<>(createdTax, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Tax>> getAllTaxes() {
        return ResponseEntity.ok(taxService.getAllTaxes());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tax> updateTax(@PathVariable String id, @Valid @RequestBody TaxDto dto) {
        return ResponseEntity.ok(taxService.updateTax(id, dto));
    }
}
