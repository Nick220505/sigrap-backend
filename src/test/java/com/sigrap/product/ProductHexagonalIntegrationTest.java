package com.sigrap.product;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.category.Category;
import com.sigrap.category.CategoryRepository;
import com.sigrap.config.BaseIntegrationTest;
import com.sigrap.product.infrastructure.adapter.in.rest.ProductRequest;
import com.sigrap.product.infrastructure.adapter.in.rest.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the Product module using hexagonal architecture.
 * Tests the complete flow from REST API through use cases to persistence.
 * 
 * This test validates:
 * - All layers working together (REST → Application → Domain → Infrastructure)
 * - End-to-end CRUD operations
 * - Backward compatibility with existing API contracts
 * - Business rule enforcement
 * - Data persistence and retrieval
 */
@DisplayName("Product Hexagonal Architecture Integration Tests")
class ProductHexagonalIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private Category testCategory;

    @BeforeEach
    void setup() {
        // Setup security context
        List<SimpleGrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        UserDetails userDetails = User.builder()
                .username("test@example.com")
                .password("password")
                .authorities(authorities)
                .build();
        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities)
                );

        // Create test category
        testCategory = Category.builder()
                .name("Electronics")
                .description("Electronic products")
                .build();
        testCategory = categoryRepository.save(testCategory);
    }

    @Test
    @DisplayName("Complete CRUD operations should succeed")
    void crudOperations_shouldSucceed() throws Exception {
        // CREATE
        ProductRequest createRequest = new ProductRequest(
                "Laptop",
                "High-performance laptop",
                BigDecimal.valueOf(800.00),
                BigDecimal.valueOf(1200.00),
                50,
                10,
                testCategory.getId()
        );

        MvcResult createResult = mockMvc
                .perform(
                        post("/api/v2/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.description").value("High-performance laptop"))
                .andExpect(jsonPath("$.costPrice").value(800.00))
                .andExpect(jsonPath("$.salePrice").value(1200.00))
                .andExpect(jsonPath("$.stock").value(50))
                .andExpect(jsonPath("$.minimumStockThreshold").value(10))
                .andReturn();

        ProductResponse createdProduct = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                ProductResponse.class
        );

        Long productId = createdProduct.id();
        assertThat(productId).isNotNull();

        // READ - Get by ID
        mockMvc
                .perform(get("/api/v2/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.description").value("High-performance laptop"));

        // READ - Get all
        MvcResult getAllResult = mockMvc
                .perform(get("/api/v2/products"))
                .andExpect(status().isOk())
                .andReturn();

        List<ProductResponse> products = objectMapper.readValue(
                getAllResult.getResponse().getContentAsString(),
                new TypeReference<List<ProductResponse>>() {}
        );

        assertThat(products).isNotEmpty();
        assertThat(
                products.stream().anyMatch(p -> p.id().equals(productId))
        ).isTrue();

        // UPDATE
        ProductRequest updateRequest = new ProductRequest(
                "Premium Laptop",
                "High-performance premium laptop",
                BigDecimal.valueOf(900.00),
                BigDecimal.valueOf(1400.00),
                45,
                15,
                testCategory.getId()
        );

        mockMvc
                .perform(
                        put("/api/v2/products/{id}", productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Premium Laptop"))
                .andExpect(jsonPath("$.description").value("High-performance premium laptop"))
                .andExpect(jsonPath("$.costPrice").value(900.00))
                .andExpect(jsonPath("$.salePrice").value(1400.00))
                .andExpect(jsonPath("$.stock").value(45))
                .andExpect(jsonPath("$.minimumStockThreshold").value(15));

        // Verify persistence
        Product updatedProduct = productRepository
                .findById(productId.intValue())
                .orElseThrow();
        assertThat(updatedProduct.getName()).isEqualTo("Premium Laptop");
        assertThat(updatedProduct.getDescription()).isEqualTo("High-performance premium laptop");

        // DELETE
        mockMvc
                .perform(delete("/api/v2/products/{id}", productId))
                .andExpect(status().isNoContent());

        assertThat(productRepository.findById(productId.intValue())).isEmpty();
    }

    @Test
    @DisplayName("Get non-existent product should return 404")
    void getNonExistentProduct_shouldReturnNotFound() throws Exception {
        mockMvc
                .perform(get("/api/v2/products/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update non-existent product should return 404")
    void updateNonExistentProduct_shouldReturnNotFound() throws Exception {
        ProductRequest request = new ProductRequest(
                "Non-existent Product",
                "This product does not exist",
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(150.00),
                10,
                5,
                testCategory.getId()
        );

        mockMvc
                .perform(
                        put("/api/v2/products/999999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete non-existent product should return 404")
    void deleteNonExistentProduct_shouldReturnNotFound() throws Exception {
        mockMvc
                .perform(delete("/api/v2/products/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Create product with invalid category should fail")
    void createProduct_withInvalidCategory_shouldFail() throws Exception {
        ProductRequest request = new ProductRequest(
                "Invalid Product",
                "Product with invalid category",
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(150.00),
                10,
                5,
                999999L
        );

        mockMvc
                .perform(
                        post("/api/v2/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete multiple products should succeed")
    void deleteMultipleProducts_shouldSucceed() throws Exception {
        // Create test products
        Product product1 = Product.builder()
                .name("Product 1")
                .description("Description 1")
                .costPrice(BigDecimal.valueOf(50.00))
                .salePrice(BigDecimal.valueOf(75.00))
                .category(testCategory)
                .stock(20)
                .minimumStockThreshold(5)
                .build();
        product1 = productRepository.save(product1);

        Product product2 = Product.builder()
                .name("Product 2")
                .description("Description 2")
                .costPrice(BigDecimal.valueOf(60.00))
                .salePrice(BigDecimal.valueOf(90.00))
                .category(testCategory)
                .stock(30)
                .minimumStockThreshold(10)
                .build();
        product2 = productRepository.save(product2);

        List<Long> idsToDelete = Arrays.asList(
                product1.getId().longValue(),
                product2.getId().longValue()
        );

        mockMvc
                .perform(
                        delete("/api/v2/products/delete-many")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(idsToDelete))
                )
                .andExpect(status().isNoContent());

        assertThat(productRepository.findById(product1.getId())).isEmpty();
        assertThat(productRepository.findById(product2.getId())).isEmpty();
    }

    @Test
    @DisplayName("Business rules should be enforced - negative price")
    void createProduct_withNegativePrice_shouldFail() throws Exception {
        ProductRequest request = new ProductRequest(
                "Invalid Product",
                "Product with negative price",
                BigDecimal.valueOf(-100.00),
                BigDecimal.valueOf(150.00),
                10,
                5,
                testCategory.getId()
        );

        mockMvc
                .perform(
                        post("/api/v2/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Business rules should be enforced - negative stock")
    void createProduct_withNegativeStock_shouldFail() throws Exception {
        ProductRequest request = new ProductRequest(
                "Invalid Product",
                "Product with negative stock",
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(150.00),
                -10,
                5,
                testCategory.getId()
        );

        mockMvc
                .perform(
                        post("/api/v2/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }
}
