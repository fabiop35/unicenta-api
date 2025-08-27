package com.unicenta.poc.interfaces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

// STATIC IMPORTS FOR SPRING REST DOCS
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.unicenta.poc.domain.Category;
import com.unicenta.poc.domain.CategoryRepository;
import com.unicenta.poc.domain.Product;
import com.unicenta.poc.domain.ProductRepository;
import com.unicenta.poc.domain.TaxCategory;
import com.unicenta.poc.domain.TaxCategoryRepository;
import com.unicenta.poc.interfaces.dto.ProductDto;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith({RestDocumentationExtension.class})
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TaxCategoryRepository taxCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Category savedCategory;
    private TaxCategory savedTaxCategory;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        savedCategory = categoryRepository.save(new Category("Lacteos"));
        savedTaxCategory = taxCategoryRepository.save(new TaxCategory("IVA 5%"));
       
    }

    @Test
    void createProduct_WhenValid_ShouldReturn201() throws Exception {
        Category savedCategory = categoryRepository.save(new Category("Original Name"));
        ProductDto dto = new ProductDto();
        dto.setName("Leche Entera");
        dto.setReference("REF-LE-01");
        dto.setCode("770000000001");
        dto.setPricebuy(3800.00);
        dto.setPricesell(4500.00);
        dto.setDisplay("Leche Entera 1L");
        dto.setCategoryId(savedCategory.getId());
        dto.setTaxcatId(savedTaxCategory.getId());

        this.mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Leche Entera")))
                .andDo(document("products/create",
                        requestFields(
                                fieldWithPath("name").description("The official name of the product."),
                                fieldWithPath("reference").description("The internal reference code."),
                                fieldWithPath("code").description("The barcode (EAN-13, etc.)."),
                                fieldWithPath("pricebuy").description("The cost price of the product."),
                                fieldWithPath("pricesell").description("The selling price of the product."),
                                fieldWithPath("display").description("The name to show on the POS screen."),
                                fieldWithPath("categoryId").description("The UUID of the product's category."),
                                fieldWithPath("taxcatId").description("The UUID of the product's tax category.")
                        ),
                        responseFields(
                                // This documents the fields in the Product DTO
                                fieldWithPath("id").description("The unique identifier for the product."),
                                fieldWithPath("name").description("Product name."),
                                fieldWithPath("reference").description("Internal reference."),
                                // ... and so on for all fields in the response ...
                                fieldWithPath("categoryId").description("ID of the category."),
                                fieldWithPath("taxcatId").description("ID of the tax category.")
                        )
                ));
    }

    @Test
    void getAllProducts_ShouldReturnPaginatedListOfProducts() throws Exception {
        // Arrange: Create more products than the page size
        for (int i = 0; i < 15; i++) {
            Product p = new Product("REF-" + i, "CODE-" + i, "Product " + i, 10, 8, savedCategory.getId(), savedTaxCategory.getId(), "Display " + i, "dee29ece-5b13-4f71-bc9e-845dbccddea9");
            productRepository.save(p);
        }

        // Act & Assert
        this.mockMvc.perform(get("/api/v1/products?page=1&size=5&sort=name,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalPages", is(3)))
                .andExpect(jsonPath("$.totalElements", is(15)))
                .andExpect(jsonPath("$.number", is(1)))
                .andDo(document("products/get-all-paginated",
                        queryParameters(
                                parameterWithName("page").description("The page number to retrieve (0-indexed).").optional(),
                                parameterWithName("size").description("The number of items per page.").optional(),
                                parameterWithName("sort").description("The sorting criteria in the format: `property,(asc|desc)`. Default is unsorted.").optional()
                        ),
                        responseFields(
                                fieldWithPath("content[]").description("A list of products on the current page."),
                                fieldWithPath("content[].id").description("Product ID."),
                                fieldWithPath("content[].name").description("Product Name."),
                                fieldWithPath("content[].categoryName").description("The name of the product's category."),
                                // ... other product fields ...
                                fieldWithPath("pageable").description("An object describing the pagination details."),
                                fieldWithPath("pageable.pageNumber").description("The current page number."),
                                fieldWithPath("pageable.pageSize").description("The number of items per page."),
                                fieldWithPath("pageable.sort.sorted").description("Whether the results are sorted."),
                                fieldWithPath("pageable.sort.unsorted").description("Whether the results are unsorted."),
                                fieldWithPath( "pageable.sort.empty"),
                                fieldWithPath("pageable.offset").description("The offset of the current page."),
                                fieldWithPath("pageable.paged").description("Whether the request is paged."),
                                fieldWithPath("pageable.unpaged").description("Whether the request is unpaged."),
                                fieldWithPath("last").description("True if this is the last page."),
                                fieldWithPath("totalPages").description("The total number of pages."),
                                fieldWithPath("totalElements").description("The total number of items across all pages."),
                                fieldWithPath("first").description("True if this is the first page."),
                                fieldWithPath("size").description("The number of items on this page."),
                                fieldWithPath("number").description("The current page number (0-indexed)."),
                                fieldWithPath("sort.sorted").description("Whether the results are sorted."),
                                fieldWithPath("sort.unsorted").description("Whether the results are unsorted."),
                                fieldWithPath( "sort.empty"),
                                fieldWithPath("numberOfElements").description("The number of elements on the current page."),
                                fieldWithPath("empty").description("True if the current page has no content.")
                        )
                ));
    }
}
