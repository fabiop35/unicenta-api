package com.unicenta.poc.application;

import com.unicenta.poc.domain.Supplier;
import com.unicenta.poc.domain.SupplierRepository;
import com.unicenta.poc.domain.exceptions.ResourceNotFoundException;

import java.util.Optional;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;
    @InjectMocks
    private SupplierService supplierService;

    @Test
    void getSupplierById_WhenFound_ShouldReturnSupplier() {
        Supplier supplier = new Supplier("S-001", "Test Supplier", 1000);
        when(supplierRepository.findById("test-id")).thenReturn(Optional.of(supplier));

        Supplier found = supplierService.getSupplierById("test-id");

        assertNotNull(found);
        assertEquals("Test Supplier", found.getName());
    }

    @Test
    void getSupplierById_WhenNotFound_ShouldThrowException() {
        when(supplierRepository.findById("test-id")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> supplierService.getSupplierById("test-id"));
    }
}
