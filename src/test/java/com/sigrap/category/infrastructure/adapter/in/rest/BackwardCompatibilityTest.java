package com.sigrap.category.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.category.application.port.in.CreateCategoryUseCase;
import com.sigrap.category.application.port.in.command.CreateCategoryCommand;
import com.sigrap.category.domain.model.Category;
import com.sigrap.config.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Backward compatibility tests for CategoryController.
 * Verifies that the hexagonal architecture implementation maintains
 * backward compatibility with the old layered architecture API.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
@Import(TestSecurityConfig.class)
class BackwardCompatibilityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CreateCategoryUseCase createCategoryUseCase;

    @Test
    @DisplayName("Should support legacy /delete-many endpoint for backward compatibility")
    void shouldSupportLegacyDeleteManyEndpoint() throws Exception {
        // Given: Create test categories
        Category category1 = createCategoryUseCase.create(
            new CreateCategoryCommand("Test Category 1", "Description 1")
        );
        Category category2 = createCategoryUseCase.create(
            new CreateCategoryCommand("Test Category 2", "Description 2")
        );
        Category category3 = createCategoryUseCase.create(
            new CreateCategoryCommand("Test Category 3", "Description 3")
        );

        List<Long> idsToDelete = List.of(
            category1.getId().value(),
            category2.getId().value(),
            category3.getId().value()
        );

        // When: Call legacy /delete-many endpoint
        mockMvc.perform(delete("/api/categories/delete-many")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idsToDelete)))
            // Then: Should return 204 No Content
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should support new /batch endpoint")
    void shouldSupportNewBatchEndpoint() throws Exception {
        // Given: Create test categories
        Category category1 = createCategoryUseCase.create(
            new CreateCategoryCommand("Batch Test 1", "Description 1")
        );
        Category category2 = createCategoryUseCase.create(
            new CreateCategoryCommand("Batch Test 2", "Description 2")
        );

        List<Long> idsToDelete = List.of(
            category1.getId().value(),
            category2.getId().value()
        );

        // When: Call new /batch endpoint
        mockMvc.perform(delete("/api/categories/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idsToDelete)))
            // Then: Should return 204 No Content
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Both endpoints should behave identically")
    void bothEndpointsShouldBehaveIdentically() throws Exception {
        // Given: Create test categories for legacy endpoint
        Category legacyCategory1 = createCategoryUseCase.create(
            new CreateCategoryCommand("Legacy 1", "Description")
        );
        Category legacyCategory2 = createCategoryUseCase.create(
            new CreateCategoryCommand("Legacy 2", "Description")
        );

        // Given: Create test categories for new endpoint
        Category newCategory1 = createCategoryUseCase.create(
            new CreateCategoryCommand("New 1", "Description")
        );
        Category newCategory2 = createCategoryUseCase.create(
            new CreateCategoryCommand("New 2", "Description")
        );

        List<Long> legacyIds = List.of(
            legacyCategory1.getId().value(),
            legacyCategory2.getId().value()
        );

        List<Long> newIds = List.of(
            newCategory1.getId().value(),
            newCategory2.getId().value()
        );

        // When/Then: Both endpoints should return same status
        mockMvc.perform(delete("/api/categories/delete-many")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(legacyIds)))
            .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/categories/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newIds)))
            .andExpect(status().isNoContent());
    }
}
