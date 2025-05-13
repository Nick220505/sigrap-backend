package com.sigrap.product;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.category.Category;
import com.sigrap.category.CategoryRepository;
import com.sigrap.config.BaseTestConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@Import(BaseTestConfiguration.class)
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
class ProductIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  private Category testCategory;
  private Product testProduct;
  private ProductData testProductData;

  @BeforeEach
  void setUp() {
    testCategory = Category.builder()
      .name("Test Category")
      .description("Test Category Description")
      .build();
    testCategory = categoryRepository.save(testCategory);

    testProduct = Product.builder()
      .name("Test Product")
      .description("Test Product Description")
      .costPrice(BigDecimal.valueOf(10.99))
      .salePrice(BigDecimal.valueOf(15.99))
      .category(testCategory)
      .build();
    testProduct = productRepository.save(testProduct);

    testProductData = ProductData.builder()
      .name("Test Product")
      .description("Test Product Description")
      .costPrice(BigDecimal.valueOf(10.99))
      .salePrice(BigDecimal.valueOf(15.99))
      .categoryId(testCategory.getId().intValue())
      .build();
  }

  @AfterEach
  void tearDown() {
    productRepository.deleteAll();
    categoryRepository.deleteAll();
  }

  @Test
  void crudOperations_shouldSucceed() throws Exception {
    // Create
    mockMvc
      .perform(
        post("/api/products")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(testProductData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.name").value(testProductData.getName()));

    // Read
    mockMvc
      .perform(get("/api/products/{id}", testProduct.getId()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value(testProduct.getName()));

    // Update
    testProductData.setName("Updated Product");
    mockMvc
      .perform(
        put("/api/products/{id}", testProduct.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(testProductData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("Updated Product"));

    // Delete
    mockMvc
      .perform(delete("/api/products/{id}", testProduct.getId()))
      .andExpect(status().isNoContent());
  }

  @Test
  void getNonExistentProduct_shouldReturnNotFound() throws Exception {
    mockMvc
      .perform(get("/api/products/{id}", 999L))
      .andExpect(status().isNotFound());
  }

  @Test
  void updateNonExistentProduct_shouldReturnNotFound() throws Exception {
    mockMvc
      .perform(
        put("/api/products/{id}", 999L)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(testProductData))
      )
      .andExpect(status().isNotFound());
  }

  @Test
  void deleteNonExistentProduct_shouldReturnNotFound() throws Exception {
    mockMvc
      .perform(delete("/api/products/{id}", 999L))
      .andExpect(status().isNotFound());
  }

  @Test
  void createProduct_withInvalidCategory_shouldFail() throws Exception {
    testProductData.setCategoryId(999);
    mockMvc
      .perform(
        post("/api/products")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(testProductData))
      )
      .andExpect(status().isNotFound());
  }

  @Test
  void deleteMultipleProducts_shouldSucceed() throws Exception {
    List<Integer> productIds = List.of(testProduct.getId());
    mockMvc
      .perform(
        delete("/api/products/delete-many")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(productIds))
      )
      .andExpect(status().isNoContent());
  }
}
