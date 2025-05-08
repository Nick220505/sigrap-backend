package com.sigrap.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.category.Category;
import com.sigrap.category.CategoryRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
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

    @BeforeEach
    void setup() {
        List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = User.builder()
                .username("test@example.com")
                .password("password")
                .authorities(authorities)
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities));

        testCategory = Category.builder()
                .name("Test Category")
                .description("Category for testing products")
                .build();
        testCategory = categoryRepository.save(testCategory);
    }

    @Test
    void crudOperations_shouldSucceed() throws Exception {
        ProductData productData = ProductData.builder()
                .name("Test Product")
                .description("This is a test product")
                .costPrice(new BigDecimal("10.00"))
                .salePrice(new BigDecimal("15.00"))
                .categoryId(testCategory.getId())
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("This is a test product"))
                .andExpect(jsonPath("$.costPrice").value(10.00))
                .andExpect(jsonPath("$.salePrice").value(15.00))
                .andExpect(jsonPath("$.category.id").value(testCategory.getId()))
                .andExpect(jsonPath("$.category.name").value("Test Category"))
                .andReturn();

        ProductInfo createdProduct = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                ProductInfo.class);

        Integer productId = createdProduct.getId();
        assertThat(productId).isNotNull();

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("This is a test product"))
                .andExpect(jsonPath("$.costPrice").value(10.00))
                .andExpect(jsonPath("$.salePrice").value(15.00))
                .andExpect(jsonPath("$.category.id").value(testCategory.getId()));

        MvcResult getAllResult = mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andReturn();

        List<ProductInfo> products = objectMapper.readValue(
                getAllResult.getResponse().getContentAsString(),
                new TypeReference<List<ProductInfo>>() {
                });

        assertThat(products).isNotEmpty();
        assertThat(products.stream().anyMatch(p -> p.getId().equals(productId))).isTrue();

        ProductData updatedData = ProductData.builder()
                .name("Updated Product")
                .description("This is an updated test product")
                .costPrice(new BigDecimal("20.00"))
                .salePrice(new BigDecimal("30.00"))
                .categoryId(testCategory.getId())
                .build();

        mockMvc.perform(put("/api/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.description").value("This is an updated test product"))
                .andExpect(jsonPath("$.costPrice").value(20.00))
                .andExpect(jsonPath("$.salePrice").value(30.00));

        Product updatedProduct = productRepository.findById(productId)
                .orElseThrow();
        assertThat(updatedProduct.getName()).isEqualTo("Updated Product");
        assertThat(updatedProduct.getDescription()).isEqualTo("This is an updated test product");
        assertThat(updatedProduct.getCostPrice()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertThat(updatedProduct.getSalePrice()).isEqualByComparingTo(new BigDecimal("30.00"));

        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isNoContent());

        assertThat(productRepository.findById(productId)).isEmpty();
    }

    @Test
    void createProduct_withInvalidCategory_shouldFail() throws Exception {
        ProductData productData = ProductData.builder()
                .name("Invalid Category Product")
                .description("This product has an invalid category")
                .costPrice(new BigDecimal("10.00"))
                .salePrice(new BigDecimal("15.00"))
                .categoryId(999)
                .build();

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productData)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getNonExistentProduct_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateNonExistentProduct_shouldReturnNotFound() throws Exception {
        ProductData productData = ProductData.builder()
                .name("Non-existent Product")
                .costPrice(new BigDecimal("10.00"))
                .salePrice(new BigDecimal("15.00"))
                .build();

        mockMvc.perform(put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productData)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNonExistentProduct_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMultipleProducts_shouldSucceed() throws Exception {
        Product product1 = Product.builder()
                .name("Product 1")
                .description("Description 1")
                .costPrice(new BigDecimal("10.00"))
                .salePrice(new BigDecimal("15.00"))
                .category(testCategory)
                .build();
        product1 = productRepository.save(product1);

        Product product2 = Product.builder()
                .name("Product 2")
                .description("Description 2")
                .costPrice(new BigDecimal("20.00"))
                .salePrice(new BigDecimal("25.00"))
                .category(testCategory)
                .build();
        product2 = productRepository.save(product2);

        List<Integer> idsToDelete = Arrays.asList(product1.getId(), product2.getId());

        mockMvc.perform(delete("/api/products/delete-many")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idsToDelete)))
                .andExpect(status().isNoContent());

        assertThat(productRepository.findById(product1.getId())).isEmpty();
        assertThat(productRepository.findById(product2.getId())).isEmpty();
    }
}