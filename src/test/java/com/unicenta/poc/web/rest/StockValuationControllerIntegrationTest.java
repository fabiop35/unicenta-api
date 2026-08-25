package com.unicenta.poc.web.rest;

import com.unicenta.poc.domain.Category;
import com.unicenta.poc.domain.CategoryRepository;
import com.unicenta.poc.domain.Product;
import com.unicenta.poc.domain.ProductRepository;
import com.unicenta.poc.domain.TaxCategory;
import com.unicenta.poc.domain.TaxCategoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        // The schema for this test is provided by the container's init script
        // (mysql-test-schema.sql); the H2 fixture schema.sql must NOT run here.
        "spring.sql.init.mode=never"
})
class StockValuationControllerIntegrationTest {

    @Container
    @ServiceConnection
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("unicentaopos")
            .withUsername("unicenta")
            .withPassword("unicenta")
            .withCommand("--lower-case-table-names=1")
            .withInitScript("mysql-test-schema.sql");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TaxCategoryRepository taxCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Category category;
    private TaxCategory taxCategory;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM stockdiary");
        jdbcTemplate.execute("DELETE FROM stockcurrent");
        jdbcTemplate.execute("DELETE FROM products");
        jdbcTemplate.execute("DELETE FROM LOCATIONS");
        categoryRepository.deleteAll();
        taxCategoryRepository.deleteAll();

        category = categoryRepository.save(new Category("General"));
        taxCategory = taxCategoryRepository.save(new TaxCategory("IVA 19%"));
    }

    @Test
    void getInventoryValueSummary_returnsTotalValueAcrossAllLocations() throws Exception {
        String loc1 = saveLocation("Main Store", "Main St 1");
        String loc2 = saveLocation("Warehouse", "Warehouse Rd 2");

        Product p1 = saveProduct("REF-P1", "CODE-P1", "Product One", 1000.0);
        Product p2 = saveProduct("REF-P2", "CODE-P2", "Product Two", 2000.0);

        jdbcTemplate.update("INSERT INTO stockcurrent (location, product, units) VALUES (?, ?, ?)",
                loc1, p1.getId(), 10.0);
        jdbcTemplate.update("INSERT INTO stockcurrent (location, product, units) VALUES (?, ?, ?)",
                loc2, p2.getId(), 5.0);

        // total = 10 * 1000 + 5 * 2000 = 20000
        mockMvc.perform(get("/api/v1/stock/valuation/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalValue", is(20000.0)))
                .andExpect(jsonPath("$.itemCount", is(2)))
                .andExpect(jsonPath("$.productCount", is(2)))
                .andExpect(jsonPath("$.asOf").exists())
                .andExpect(jsonPath("$.breakdowns", hasSize(2)));
    }

    @Test
    void getInventoryValueSummary_filteredByLocation() throws Exception {
        String loc1 = saveLocation("Main Store", "Main St 1");
        String loc2 = saveLocation("Warehouse", "Warehouse Rd 2");

        Product p1 = saveProduct("REF-P1", "CODE-P1", "Product One", 1000.0);
        Product p2 = saveProduct("REF-P2", "CODE-P2", "Product Two", 2000.0);

        jdbcTemplate.update("INSERT INTO stockcurrent (location, product, units) VALUES (?, ?, ?)",
                loc1, p1.getId(), 10.0);
        jdbcTemplate.update("INSERT INTO stockcurrent (location, product, units) VALUES (?, ?, ?)",
                loc2, p2.getId(), 5.0);

        mockMvc.perform(get("/api/v1/stock/valuation/summary").param("locationId", loc1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalValue", is(10000.0)))
                .andExpect(jsonPath("$.itemCount", is(1)))
                .andExpect(jsonPath("$.productCount", is(1)))
                .andExpect(jsonPath("$.breakdowns", hasSize(1)))
                .andExpect(jsonPath("$.breakdowns[0].locationName", is("Main Store")))
                .andExpect(jsonPath("$.breakdowns[0].totalValue", is(10000.0)));
    }

    @Test
    void getInventoryValueByLocation_returnsBreakdownPerLocation() throws Exception {
        String loc1 = saveLocation("Main Store", "Main St 1");
        String loc2 = saveLocation("Warehouse", "Warehouse Rd 2");

        Product p1 = saveProduct("REF-P1", "CODE-P1", "Product One", 1000.0);
        Product p2 = saveProduct("REF-P2", "CODE-P2", "Product Two", 2000.0);
        Product p3 = saveProduct("REF-P3", "CODE-P3", "Product Three", 500.0);

        jdbcTemplate.update("INSERT INTO stockcurrent (location, product, units) VALUES (?, ?, ?)",
                loc1, p1.getId(), 10.0);
        jdbcTemplate.update("INSERT INTO stockcurrent (location, product, units) VALUES (?, ?, ?)",
                loc1, p3.getId(), 4.0);
        jdbcTemplate.update("INSERT INTO stockcurrent (location, product, units) VALUES (?, ?, ?)",
                loc2, p2.getId(), 5.0);

        // Main Store = 10 * 1000 + 4 * 500 = 12000 (2 items, 2 products)
        // Warehouse = 5 * 2000 = 10000 (1 item, 1 product)
        mockMvc.perform(get("/api/v1/stock/valuation/by-location"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.locationName=='Main Store')].totalValue", contains(12000.0)))
                .andExpect(jsonPath("$[?(@.locationName=='Main Store')].itemCount", contains(2)))
                .andExpect(jsonPath("$[?(@.locationName=='Main Store')].productCount", contains(2)))
                .andExpect(jsonPath("$[?(@.locationName=='Warehouse')].totalValue", contains(10000.0)));
    }

    @Test
    void getInventoryValuation_usesWeightedAverageCostFromStockDiaryWithPricebuyFallback() throws Exception {
        String loc1 = saveLocation("Main Store", "Main St 1");

        Product p1 = saveProduct("REF-P1", "CODE-P1", "Product One", 1000.0);
        Product p2 = saveProduct("REF-P2", "CODE-P2", "Product Two", 2000.0);

        // p1: two stock-ins at different costs -> WAC = (10*1000 + 10*2000) / 20 = 1500,
        // invested = 30000 (matches the API convention: positive units = stock entering)
        saveDiaryMovement(loc1, p1.getId(), -1, 10.0, 1000.0);
        saveDiaryMovement(loc1, p1.getId(), 1, 10.0, 2000.0);
        // out-movement carrying a high price must not pollute cost basis or invested
        saveDiaryMovement(loc1, p1.getId(), 1, -5.0, 30000.0);
        jdbcTemplate.update("INSERT INTO stockcurrent (location, product, units) VALUES (?, ?, ?)",
                loc1, p1.getId(), 15.0);

        // p2: no priced stock-in history -> falls back to products.pricebuy
        jdbcTemplate.update("INSERT INTO stockcurrent (location, product, units) VALUES (?, ?, ?)",
                loc1, p2.getId(), 5.0);

        // total value = 15 * 1500 + 5 * 2000 = 32500, total invested = 30000 + 0
        // retail (pricesell = pricebuy * 1.2): p1 -> 15*1200=18000, p2 -> 5*2400=12000
        mockMvc.perform(get("/api/v1/stock/valuation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalValue", is(32500.0)))
                .andExpect(jsonPath("$.totalInvested", is(30000.0)))
                .andExpect(jsonPath("$.totalRetailValue", is(30000.0)))
                .andExpect(jsonPath("$.totalPotentialMargin", is(-2500.0)))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[?(@.productId=='" + p1.getId() + "')].costPrice", contains(1500.0)))
                .andExpect(jsonPath("$.items[?(@.productId=='" + p1.getId() + "')].costSource",
                        contains("STOCKDIARY_WAC")))
                .andExpect(jsonPath("$.items[?(@.productId=='" + p1.getId() + "')].invested",
                        contains(30000.0)))
                .andExpect(jsonPath("$.items[?(@.productId=='" + p1.getId() + "')].itemValue", contains(22500.0)))
                .andExpect(jsonPath("$.items[?(@.productId=='" + p1.getId() + "')].priceSell", contains(1200.0)))
                .andExpect(jsonPath("$.items[?(@.productId=='" + p1.getId() + "')].retailValue",
                        contains(18000.0)))
                .andExpect(jsonPath("$.items[?(@.productId=='" + p1.getId() + "')].potentialMargin",
                        contains(-4500.0)))
                .andExpect(jsonPath("$.items[?(@.productId=='" + p2.getId() + "')].costPrice", contains(2000.0)))
                .andExpect(jsonPath("$.items[?(@.productId=='" + p2.getId() + "')].costSource",
                        contains("PRICEBUY_FALLBACK")))
                .andExpect(jsonPath("$.items[?(@.productId=='" + p2.getId() + "')].invested", contains(0.0)))
                .andExpect(jsonPath("$.items[?(@.productId=='" + p2.getId() + "')].itemValue", contains(10000.0)))
                .andExpect(jsonPath("$.items[?(@.productId=='" + p2.getId() + "')].priceSell", contains(2400.0)))
                .andExpect(jsonPath("$.items[?(@.productId=='" + p2.getId() + "')].retailValue",
                        contains(12000.0)))
                .andExpect(jsonPath("$.items[?(@.productId=='" + p2.getId() + "')].potentialMargin",
                        contains(2000.0)));
    }

    private void saveDiaryMovement(String locationId, String productId, int reason,
            double units, double price) {
        jdbcTemplate.update(
                "INSERT INTO stockdiary (id, datenew, reason, location, product, units, price) "
                + "VALUES (?, NOW(), ?, ?, ?, ?, ?)",
                UUID.randomUUID().toString(), reason, locationId, productId, units, price);
    }

    private String saveLocation(String name, String address) {
        String id = UUID.randomUUID().toString();
        jdbcTemplate.update("INSERT INTO LOCATIONS (id, name, address) VALUES (?, ?, ?)", id, name, address);
        return id;
    }

    private Product saveProduct(String reference, String code, String name, double pricebuy) {
        return productRepository.save(new Product(
                reference, code, name, pricebuy * 1.2, pricebuy,
                category.getId(), taxCategory.getId(), name, "supplier-1"));
    }
}
