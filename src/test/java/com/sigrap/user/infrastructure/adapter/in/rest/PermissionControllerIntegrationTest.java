package com.sigrap.user.infrastructure.adapter.in.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.config.BaseIntegrationTest;
import com.sigrap.user.domain.model.*;
import com.sigrap.user.domain.port.PermissionRepositoryPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Integration test for PermissionController.
 */
public class PermissionControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PermissionRepositoryPort permissionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Permission testPermission;

    @BeforeEach
    void setUp() {
        testPermission = new Permission(
            new PermissionName("READ_USERS"),
            "USER",
            "READ"
        );
        testPermission = permissionRepository.save(testPermission);
    }

    @AfterEach
    void tearDown() {
        if (testPermission != null && testPermission.getId() != null) {
            try {
                permissionRepository.deleteById(testPermission.getId());
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllPermissions() throws Exception {
        mockMvc.perform(get("/api/v2/permissions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(testPermission.getId().value()))
            .andExpect(jsonPath("$[0].name").value("READ_USERS"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetPermissionById() throws Exception {
        mockMvc.perform(get("/api/v2/permissions/{id}", testPermission.getId().value()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testPermission.getId().value()))
            .andExpect(jsonPath("$.name").value("READ_USERS"))
            .andExpect(jsonPath("$.resource").value("USER"))
            .andExpect(jsonPath("$.action").value("READ"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetPermissionByName() throws Exception {
        mockMvc.perform(get("/api/v2/permissions/name/{name}", "READ_USERS"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("READ_USERS"))
            .andExpect(jsonPath("$.resource").value("USER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetPermissionsByResource() throws Exception {
        Permission permission2 = new Permission(
            new PermissionName("WRITE_USERS"),
            
            "USER",
            "WRITE"
        );
        permission2 = permissionRepository.save(permission2);

        mockMvc.perform(get("/api/v2/permissions/resource/{resource}", "USER"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2));

        if (permission2 != null && permission2.getId() != null) {
            permissionRepository.deleteById(permission2.getId());
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreatePermission() throws Exception {
        PermissionRequest request = new PermissionRequest(
            "DELETE_USERS",
            "USER",
            "DELETE"
        );

        MvcResult result = mockMvc.perform(post("/api/v2/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("DELETE_USERS"))
            .andExpect(jsonPath("$.resource").value("USER"))
            .andExpect(jsonPath("$.action").value("DELETE"))
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        PermissionResponse createdPermission = objectMapper.readValue(responseContent, PermissionResponse.class);
        permissionRepository.deleteById(new PermissionId(createdPermission.id()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdatePermission() throws Exception {
        PermissionRequest updateRequest = new PermissionRequest(
            "READ_USERS",
            "USER",
            "READ"
        );

        mockMvc.perform(put("/api/v2/permissions/{id}", testPermission.getId().value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testPermission.getId().value()))
            .andExpect(jsonPath("$.name").value("READ_USERS"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeletePermission() throws Exception {
        Permission permissionToDelete = new Permission(
            new PermissionName("TEMP"),
            
            "TEMP",
            "TEMP"
        );
        permissionToDelete = permissionRepository.save(permissionToDelete);

        mockMvc.perform(delete("/api/v2/permissions/{id}", permissionToDelete.getId().value()))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v2/permissions/{id}", permissionToDelete.getId().value()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForMissingRequiredFields() throws Exception {
        PermissionRequest invalidRequest = new PermissionRequest(null, null, null);

        mockMvc.perform(post("/api/v2/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundForNonExistentPermission() throws Exception {
        mockMvc.perform(get("/api/v2/permissions/{id}", 99999L))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForBlankPermissionName() throws Exception {
        PermissionRequest request = new PermissionRequest("", "RESOURCE", "ACTION");

        mockMvc.perform(post("/api/v2/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForPermissionNameTooLong() throws Exception {
        String longName = "A".repeat(101);
        PermissionRequest request = new PermissionRequest(longName, "RESOURCE", "ACTION");

        mockMvc.perform(post("/api/v2/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForDuplicatePermissionName() throws Exception {
        PermissionRequest request = new PermissionRequest("READ_USERS", "USER", "READ");

        mockMvc.perform(post("/api/v2/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenUpdatingNonExistentPermission() throws Exception {
        PermissionRequest updateRequest = new PermissionRequest("TEST", "TEST", "TEST");

        mockMvc.perform(put("/api/v2/permissions/{id}", 99999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenDeletingNonExistentPermission() throws Exception {
        mockMvc.perform(delete("/api/v2/permissions/{id}", 99999L))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnEmptyArrayWhenNoPermissions() throws Exception {
        permissionRepository.deleteById(testPermission.getId());

        mockMvc.perform(get("/api/v2/permissions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundForNonExistentPermissionName() throws Exception {
        mockMvc.perform(get("/api/v2/permissions/name/{name}", "NONEXISTENT"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnEmptyArrayForNonExistentResource() throws Exception {
        mockMvc.perform(get("/api/v2/permissions/resource/{resource}", "NONEXISTENT"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldHandleMultiplePermissionsForSameResource() throws Exception {
        Permission perm2 = permissionRepository.save(new Permission(
            new PermissionName("WRITE_USERS"),
            
            "USER",
            "WRITE"
        ));
        Permission perm3 = permissionRepository.save(new Permission(
            new PermissionName("DELETE_USERS_PERM"),
            
            "USER",
            "DELETE"
        ));

        mockMvc.perform(get("/api/v2/permissions/resource/{resource}", "USER"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(3));

        permissionRepository.deleteById(perm2.getId());
        permissionRepository.deleteById(perm3.getId());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForBlankResource() throws Exception {
        PermissionRequest request = new PermissionRequest("VALID_NAME", "", "ACTION");

        mockMvc.perform(post("/api/v2/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForBlankAction() throws Exception {
        PermissionRequest request = new PermissionRequest("VALID_NAME", "RESOURCE", "");

        mockMvc.perform(post("/api/v2/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}
