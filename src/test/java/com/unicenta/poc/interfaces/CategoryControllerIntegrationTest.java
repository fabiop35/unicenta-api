package com.unicenta.poc.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.unicenta.poc.domain.Category;
import com.unicenta.poc.domain.CategoryRepository;
import com.unicenta.poc.interfaces.dto.NameDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

// Static imports for MockMvc matchers and actions
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;

// STATIC IMPORTS FOR SPRING REST DOCS
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith({RestDocumentationExtension.class})
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // This setup is required for Spring REST Docs
    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                // >>> REMOVE THESE TWO LINES FROM HERE <<<
                // .alwaysDo(preprocessRequest(prettyPrint()))
                // .alwaysDo(preprocessResponse(prettyPrint()))
                .build();
        categoryRepository.deleteAll();
    }

    @Test
    void createCategory_WhenValid_ShouldReturn201() throws Exception {
        NameDto dto = new NameDto();
        dto.setName("Bebidas");

        this.mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Bebidas")))
                .andDo(document("categories/create",
                        requestFields(
                                fieldWithPath("name").description("The name of the category.")
                        ),
                        responseFields(
                                fieldWithPath("id").description("The unique identifier of the category."),
                                fieldWithPath("name").description("The name of the category.")
                        )
                ));
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategories() throws Exception {
        categoryRepository.save(new Category("Lacteos"));
        categoryRepository.save(new Category("Granos"));

        this.mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(document("categories/get-all"));
    }

    @Test
    void getCategoryById_WhenExists_ShouldReturnCategory() throws Exception {
        Category saved = categoryRepository.save(new Category("Frutas"));

        this.mockMvc.perform(get("/api/v1/categories/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Frutas")))
                .andDo(document("categories/get-one",
                        pathParameters(
                                parameterWithName("id").description("The ID of the category to retrieve.")
                        )
                ));
    }

    @Test
    void updateCategory_WhenExists_ShouldReturn200AndUpdatedCategory() throws Exception {
        Category savedCategory = categoryRepository.save(new Category("Original Name"));
        NameDto updateDto = new NameDto();
        updateDto.setName("Updated Name");

        this.mockMvc.perform(put("/api/v1/categories/{id}", savedCategory.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andDo(document("categories/update",
                        pathParameters(
                                parameterWithName("id").description("The ID of the category to update.")
                        ),
                        requestFields(
                                fieldWithPath("name").description("The new name for the category.")
                        )
                ));
    }

    @Test
    void deleteCategory_WhenExists_ShouldReturn204() throws Exception {
        Category savedCategory = categoryRepository.save(new Category("To be deleted"));

        this.mockMvc.perform(delete("/api/v1/categories/{id}", savedCategory.getId()))
                .andExpect(status().isNoContent())
                .andDo(document("categories/delete",
                        pathParameters(
                                parameterWithName("id").description("The ID of the category to delete.")
                        )
                ));
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
