package com.unicenta.poc.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.unicenta.poc.domain.Supplier;
import com.unicenta.poc.domain.SupplierRepository;
import com.unicenta.poc.interfaces.dto.SupplierDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SupplierControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        supplierRepository.deleteAll();
    }

    @Test
    void createSupplier_WhenValid_ShouldReturn201() throws Exception {
        SupplierDto dto = new SupplierDto();
        dto.setSearchkey("S-001");
        dto.setName("New Supplier");
        dto.setMaxdebt(5000.0);
        dto.setEmail("test@example.com");

        mockMvc.perform(post("/api/v1/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("New Supplier")))
                .andExpect(jsonPath("$.searchkey", is("S-001")));
    }

    @Test
    void createSupplier_WhenSearchKeyIsDuplicate_ShouldReturn409() throws Exception {
        supplierRepository.save(new Supplier("S-001", "Existing Supplier", 1000));

        SupplierDto dto = new SupplierDto();
        dto.setSearchkey("S-001");
        dto.setName("Another Supplier");
        dto.setMaxdebt(2000.0);

        mockMvc.perform(post("/api/v1/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateSupplier_WhenExists_ShouldReturn200() throws Exception {
        Supplier savedSupplier = supplierRepository.save(new Supplier("S-002", "Original Name", 1000));

        SupplierDto dto = new SupplierDto();
        dto.setSearchkey("S-002-UPDATED");
        dto.setName("Updated Name");
        dto.setMaxdebt(9999.0);

        mockMvc.perform(put("/api/v1/suppliers/" + savedSupplier.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.maxdebt", is(9999.0)));
    }

    @Test
    void deleteSupplier_WhenExists_ShouldReturn204() throws Exception {
        Supplier savedSupplier = supplierRepository.save(new Supplier("S-003", "To Delete", 100));

        mockMvc.perform(delete("/api/v1/suppliers/" + savedSupplier.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void getSupplierById_WhenNotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/suppliers/non-existent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllSuppliers_ShouldReturnPaginatedResult() throws Exception {
        for (int i = 1; i <= 25; i++) {
            supplierRepository.save(new Supplier("S-" + i, "Supplier " + i, 100));
        }

        mockMvc.perform(get("/api/v1/suppliers?page=2&size=10&sort=name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalElements", is(25)))
                .andExpect(jsonPath("$.totalPages", is(3)))
                .andExpect(jsonPath("$.number", is(2)));
    }
}
