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
import com.unicenta.poc.interfaces.dto.ProductRequestDto;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith({RestDocumentationExtension.class})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
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

        // Clear database state before each test
        //productRepository.deleteAll();
        categoryRepository.deleteAll();
        taxCategoryRepository.deleteAll();

        // Create fresh test data for each test
        savedCategory = categoryRepository.save(new Category("Lacteos"));
        savedTaxCategory = taxCategoryRepository.save(new TaxCategory("IVA 5%"));

    }

    @Test
    void createProduct_WhenValid_ShouldReturn201() throws Exception {
        ProductRequestDto dto = new ProductRequestDto();
        dto.setName("Leche Entera");
        dto.setReference("REF-LE-01-" + System.currentTimeMillis()); // Ensure uniqueness
        dto.setCode("770000000001-" + System.currentTimeMillis()); // Ensure uniqueness
        dto.setCodetype("EAN-13");
        dto.setPricebuy(3800.00);
        dto.setPricesell(4500.00);
        dto.setDisplay("Leche Entera 1L");
        dto.setCategoryId(savedCategory.getId());
        dto.setTaxcatId(savedTaxCategory.getId());
        dto.setIdSupplier("");
        System.out.println("***********#############################-----------------");
        MvcResult result = this.mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andDo(result1 -> {
                    System.out.println("ACTUAL RESPONSE: " + result1.getResponse().getContentAsString());
                    if (result1.getResponse().getStatus() != 201) {
                        System.out.println("ERROR RESPONSE: " + result1.getResponse().getContentAsString());
                        // System.out.println("ERROR RESPONSE: " + result1.getResponse().getContentAsString());
                    }
                })
                .andExpect(jsonPath("$.name", is("Leche Entera")))
                .andDo(document("products/create",
                        requestFields(
                                fieldWithPath("name").description("The official name of the product."),
                                fieldWithPath("reference").description("The internal reference code."),
                                fieldWithPath("code").description("The barcode (EAN-13, etc.)."),
                                fieldWithPath("codetype").description("The code type (EAN13, UPC, etc.)."),
                                fieldWithPath("pricebuy").description("The cost price of the product."),
                                fieldWithPath("pricesell").description("The selling price of the product."),
                                fieldWithPath("display").description("The name to show on the POS screen."),
                                fieldWithPath("categoryId").description("The UUID of the product's category."),
                                fieldWithPath("taxcatId").description("The UUID of the product's tax category."),
                                fieldWithPath("idSupplier").description("The id of the supplier.")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Unique product identifier (UUID)."),
                                fieldWithPath("name").description("Official product name."),
                                fieldWithPath("reference").description("Internal product reference code."),
                                fieldWithPath("code").description("Product barcode (EAN-13, etc.)."),
                                fieldWithPath("pricesell").description("Selling price."),
                                fieldWithPath("pricebuy").description("Cost price."),
                                fieldWithPath("categoryId").description("Category ID (UUID)."),
                                fieldWithPath("taxcatId").description("Tax category ID (UUID)."),
                                fieldWithPath("iscom").description("Whether product is a composite item.").optional(),
                                fieldWithPath("isscale").description("Whether product is sold by weight.").optional(),
                                fieldWithPath("display").description("Name shown on POS screen."),
                                fieldWithPath("stockcost").description("Current stock valuation cost.").optional(),
                                fieldWithPath("stockvolume").description("Current stock volume.").optional(),
                                fieldWithPath("stockunits").description("Current stock quantity.").optional(),
                                fieldWithPath("isvprice").description("Whether product has variable pricing.").optional(),
                                fieldWithPath("codetype").description("Barcode format (EAN-13, UPC, etc.)."),
                                fieldWithPath("warranty").description("Warranty period in days.").optional(),
                                fieldWithPath("isverpatrib").description("Tax attribute flag.").optional(),
                                fieldWithPath("printto").description("Printer destination ID.").optional(),
                                fieldWithPath("uom").description("Unit of measure ID.").optional(),
                                fieldWithPath("now").description("Current timestamp.").optional(),
                                fieldWithPath("memodate").description("Last modification timestamp.").optional(),
                                fieldWithPath("value").description("Internal value identifier.").optional(),
                                fieldWithPath("currency").description("Currency code (NDF = No Defined Currency).").optional(),
                                fieldWithPath("idSupplier").description("Supplier ID (nullable).").optional(),
                                fieldWithPath("new").description("Whether this is a new product.").optional(),
                                fieldWithPath("newProduct").description("Duplicate of 'new' flag.").optional()
                        )
                ))
                .andReturn(); // This closes the MockMvc chain

        // Debug print AFTER the chain is complete
        System.out.println("FULL RESPONSE: " + result.getResponse().getContentAsString());
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
                                fieldWithPath("content[].categoryName").description("The name of the product's category name."),
                                fieldWithPath("content[].reference").description("The name of the product's reference."),
                                fieldWithPath("content[].code").description("The name of the product's code."),
                                fieldWithPath("content[].codetype").description("The name of the product's codetype, i.e EAN-13."),
                                fieldWithPath("content[].pricesell").description("Selling price of the product."),
                                fieldWithPath("content[].pricebuy").description("Cost price of the product."),
                                fieldWithPath("content[].categoryId").description("The name of the product's categoryId."),
                                fieldWithPath("content[].taxcatId").description("The name of the product's tax taxcat id."),
                                fieldWithPath("content[].display").description("The name of the product's display."),
                                fieldWithPath("content[].taxRate").description("The name of the product's taxRate."),
                                fieldWithPath("content[].taxName").description("The name of the product's tax name."),
                                fieldWithPath("content[].idSupplier").description("The name of the product's id supplier."),
                                fieldWithPath("content[].supplierName").description("The name of the product's supplier Name."),
                                fieldWithPath("pageable").description("An object describing the pagination details."),
                                fieldWithPath("pageable.pageNumber").description("The current page number."),
                                fieldWithPath("pageable.pageSize").description("The number of items per page."),
                                fieldWithPath("pageable.sort.sorted").description("Whether the results are sorted."),
                                fieldWithPath("pageable.sort.unsorted").description("Whether the results are unsorted."),
                                fieldWithPath("pageable.sort.empty").description("True if no sorting criteria specified."),
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
                                fieldWithPath("sort.empty").description("True if no sorting criteria specified."),
                                fieldWithPath("numberOfElements").description("The number of elements on the current page."),
                                fieldWithPath("empty").description("True if the current page has no content."),
                                fieldWithPath("pageable.sort.empty").description("True if no sorting criteria is specified (boolean)."),
                                fieldWithPath("sort.empty").description("True if no sorting criteria is specified (boolean).")
                        )
                ));
    }
}
