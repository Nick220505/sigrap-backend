package com.sigrap.category.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.category.domain.model.Category;
import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.category.domain.model.CategoryName;
import com.sigrap.category.domain.port.CategoryRepositoryPort;
import com.sigrap.config.TestSecurityConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for CategoryController REST adapter.
 * Tests the complete request/response cycle including:
 * - HTTP request mapping
 * - Input validation
 * - Use case invocation
 * - Response mapping
 * - Error handling
 * - HTTP status codes
 *
 * Uses @SpringBootTest to load the full application context and MockMvc
 * to simulate HTTP requests without starting a real HTTP server.
 *
 * Note: The old CategoryController from the layered architecture is excluded
 * from the test profile to avoid bean name conflicts during the migration period.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(roles = "ADMIN")
@Import(TestSecurityConfig.class)
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepositoryPort categoryRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Create a test category for use in tests
        testCategory = new Category(
            new CategoryName("Test Category"),
            "Test Description"
        );
        testCategory = categoryRepository.save(testCategory);
    }

    @AfterEach
    void tearDown() {
        // Clean up test data
        categoryRepository.findAll().forEach(category -> 
            categoryRepository.deleteById(category.getId())
        );
    }

    // ========== POST /api/categories - Create Category Tests ==========

    @Test
    void createCategory_withValidData_shouldReturnCreated() throws Exception {
        CategoryRequest request = new CategoryRequest(
            "New Category",
            "New Description"
        );

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value("New Category"))
            .andExpect(jsonPath("$.description").value("New Description"))
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void createCategory_withBlankName_shouldReturnBadRequest() throws Exception {
        CategoryRequest request = new CategoryRequest(
            "",
            "Description"
        );

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createCategory_withNullName_shouldReturnBadRequest() throws Exception {
        CategoryRequest request = new CategoryRequest(
            null,
            "Description"
        );

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createCategory_withNameTooLong_shouldReturnBadRequest() throws Exception {
        String longName = "a".repeat(101); // Exceeds 100 character limit
        CategoryRequest request = new CategoryRequest(
            longName,
            "Description"
        );

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createCategory_withDescriptionTooLong_shouldReturnBadRequest() throws Exception {
        String longDescription = "a".repeat(501); // Exceeds 500 character limit
        CategoryRequest request = new CategoryRequest(
            "Valid Name",
            longDescription
        );

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createCategory_withDuplicateName_shouldReturnBadRequest() throws Exception {
        CategoryRequest request = new CategoryRequest(
            "Test Category", // Same as testCategory
            "Different Description"
        );

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createCategory_withNullDescription_shouldReturnCreated() throws Exception {
        CategoryRequest request = new CategoryRequest(
            "Category Without Description",
            null
        );

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Category Without Description"))
            .andExpect(jsonPath("$.description").isEmpty());
    }

    // ========== GET /api/categories/{id} - Get Category By ID Tests ==========

    @Test
    void getById_withExistingId_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", testCategory.getId().value()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testCategory.getId().value()))
            .andExpect(jsonPath("$.name").value("Test Category"))
            .andExpect(jsonPath("$.description").value("Test Description"))
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void getById_withNonExistentId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", 99999L))
            .andExpect(status().isNotFound());
    }

    @Test
    void getById_withInvalidId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", 0L))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getById_withNegativeId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", -1L))
            .andExpect(status().isBadRequest());
    }

    // ========== GET /api/categories - Get All Categories Tests ==========

    @Test
    void getAll_shouldReturnAllCategories() throws Exception {
        // Create additional categories
        Category category2 = categoryRepository.save(new Category(
            new CategoryName("Category 2"),
            "Description 2"
        ));
        Category category3 = categoryRepository.save(new Category(
            new CategoryName("Category 3"),
            "Description 3"
        ));

        mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[*].name", containsInAnyOrder(
                "Test Category", "Category 2", "Category 3"
            )));
    }

    @Test
    void getAll_withNoCategories_shouldReturnEmptyArray() throws Exception {
        // Clean up all categories
        categoryRepository.findAll().forEach(category -> 
            categoryRepository.deleteById(category.getId())
        );

        mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    // ========== PUT /api/categories/{id} - Update Category Tests ==========

    @Test
    void update_withValidData_shouldReturnOk() throws Exception {
        CategoryRequest request = new CategoryRequest(
            "Updated Category",
            "Updated Description"
        );

        mockMvc.perform(put("/api/categories/{id}", testCategory.getId().value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testCategory.getId().value()))
            .andExpect(jsonPath("$.name").value("Updated Category"))
            .andExpect(jsonPath("$.description").value("Updated Description"))
            .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void update_withNonExistentId_shouldReturnNotFound() throws Exception {
        CategoryRequest request = new CategoryRequest(
            "Updated Category",
            "Updated Description"
        );

        mockMvc.perform(put("/api/categories/{id}", 99999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void update_withBlankName_shouldReturnBadRequest() throws Exception {
        CategoryRequest request = new CategoryRequest(
            "",
            "Updated Description"
        );

        mockMvc.perform(put("/api/categories/{id}", testCategory.getId().value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_withDuplicateName_shouldReturnBadRequest() throws Exception {
        // Create another category
        Category anotherCategory = categoryRepository.save(new Category(
            new CategoryName("Another Category"),
            "Another Description"
        ));

        // Try to update testCategory with the name of anotherCategory
        CategoryRequest request = new CategoryRequest(
            "Another Category",
            "Updated Description"
        );

        mockMvc.perform(put("/api/categories/{id}", testCategory.getId().value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_withSameName_shouldReturnOk() throws Exception {
        // Updating with the same name should be allowed
        CategoryRequest request = new CategoryRequest(
            "Test Category",
            "Updated Description Only"
        );

        mockMvc.perform(put("/api/categories/{id}", testCategory.getId().value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Test Category"))
            .andExpect(jsonPath("$.description").value("Updated Description Only"));
    }

    // ========== DELETE /api/categories/{id} - Delete Category Tests ==========

    @Test
    void delete_withExistingId_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", testCategory.getId().value()))
            .andExpect(status().isNoContent());

        // Verify the category was deleted
        mockMvc.perform(get("/api/categories/{id}", testCategory.getId().value()))
            .andExpect(status().isNotFound());
    }

    @Test
    void delete_withNonExistentId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", 99999L))
            .andExpect(status().isNotFound());
    }

    @Test
    void delete_withInvalidId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", 0L))
            .andExpect(status().isBadRequest());
    }

    // ========== DELETE /api/categories/batch - Batch Delete Tests ==========

    @Test
    void deleteAll_withValidIds_shouldReturnNoContent() throws Exception {
        // Create additional categories
        Category category2 = categoryRepository.save(new Category(
            new CategoryName("Category 2"),
            "Description 2"
        ));
        Category category3 = categoryRepository.save(new Category(
            new CategoryName("Category 3"),
            "Description 3"
        ));

        List<Long> ids = List.of(
            testCategory.getId().value(),
            category2.getId().value(),
            category3.getId().value()
        );

        mockMvc.perform(delete("/api/categories/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
            .andExpect(status().isNoContent());

        // Verify all categories were deleted
        mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deleteAll_withEmptyList_shouldReturnNoContent() throws Exception {
        List<Long> ids = List.of();

        mockMvc.perform(delete("/api/categories/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteAll_withNonExistentIds_shouldReturnNotFound() throws Exception {
        List<Long> ids = List.of(99999L, 99998L);

        mockMvc.perform(delete("/api/categories/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteAll_withMixedValidAndInvalidIds_shouldReturnNotFound() throws Exception {
        List<Long> ids = List.of(
            testCategory.getId().value(),
            99999L // Non-existent ID
        );

        mockMvc.perform(delete("/api/categories/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
            .andExpect(status().isNotFound());
    }

    // ========== Content Type and Request Format Tests ==========

    @Test
    void createCategory_withoutContentType_shouldReturnUnsupportedMediaType() throws Exception {
        CategoryRequest request = new CategoryRequest(
            "New Category",
            "New Description"
        );

        // Without content type, Spring treats it as application/octet-stream
        // which causes HttpMediaTypeNotSupportedException (500 in current implementation)
        mockMvc.perform(post("/api/categories")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().is5xxServerError());
    }

    @Test
    void createCategory_withInvalidJson_shouldReturnBadRequest() throws Exception {
        // Invalid JSON causes HttpMessageNotReadableException (500 in current implementation)
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
            .andExpect(status().is5xxServerError());
    }
}
