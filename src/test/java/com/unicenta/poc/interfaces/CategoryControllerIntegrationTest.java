package com.unicenta.poc.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;

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

import com.unicenta.poc.domain.Category;
import com.unicenta.poc.domain.CategoryRepository;
import com.unicenta.poc.interfaces.dto.NameDto;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }

    // ... create, get, delete tests ...
    @Test
    void updateCategory_WhenExists_ShouldReturn200AndUpdatedCategory() throws Exception {
        Category savedCategory = categoryRepository.save(new Category("Original Name"));
        NameDto updateDto = new NameDto();
        updateDto.setName("Updated Name");

        mockMvc.perform(put("/api/v1/categories/" + savedCategory.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedCategory.getId())))
                .andExpect(jsonPath("$.name", is("Updated Name")));
    }

    @Test
    void updateCategory_WhenNotFound_ShouldReturn404() throws Exception {
        NameDto updateDto = new NameDto();
        updateDto.setName("Updated Name");

        mockMvc.perform(put("/api/v1/categories/non-existent-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }
}
