package com.unicenta.poc.interfaces;

import com.unicenta.poc.application.SupplierService;
import com.unicenta.poc.domain.StockDiary;
import com.unicenta.poc.domain.Supplier;
import com.unicenta.poc.interfaces.dto.SupplierDto;

import jakarta.validation.Valid;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/suppliers")
@CrossOrigin(origins = "http://localhost:4200, https://localhost:4200, http://192.168.10.3:4200, https://192.168.10.3:4200")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ResponseEntity<Page<Supplier>> getAllSuppliers(Pageable pageable) {
        return ResponseEntity.ok(supplierService.getAllSuppliers(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable String id) {
        System.out.println(">>> SupplierController.getSupplierById.id: " + id + " <<<");
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @PostMapping
    public ResponseEntity<Supplier> createSupplier(@Valid @RequestBody SupplierDto supplierDto) {
        Supplier createdSupplier = supplierService.createSupplier(supplierDto);
        return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable String id, @Valid @RequestBody SupplierDto supplierDto) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, supplierDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable String id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Supplier>> searchSuppliers(@RequestParam String term) {
        return ResponseEntity.ok(supplierService.searchSuppliers(term));
    }

    /**
     * Retrieves all StockDiary records for a specific supplier.
     *
     * @param id The ID of the supplier.
     * @return A list of StockDiary records.
     */
    @GetMapping("/{id}/stockdiary")
    public ResponseEntity<List<StockDiary>> getStockDiaryBySupplier(@PathVariable("id") String id) {
        return ResponseEntity.ok(supplierService.getStockDiaryBySupplier(id));
    }
}
