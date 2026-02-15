package com.sigrap.user.infrastructure.adapter.in.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.config.BaseIntegrationTest;
import com.sigrap.user.domain.model.*;
import com.sigrap.user.domain.port.PermissionRepositoryPort;
import com.sigrap.user.domain.port.RoleRepositoryPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Integration test for RoleController.
 */
public class RoleControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoleRepositoryPort roleRepository;

    @Autowired
    private PermissionRepositoryPort permissionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Role testRole;
    private Permission testPermission;

    @BeforeEach
    void setUp() {
        testRole = new Role(
            new RoleName("ADMIN"),
            "Administrator role"
        );
        testRole = roleRepository.save(testRole);

        testPermission = new Permission(
            new PermissionName("READ_USERS"),
            "Read users permission",
            "USER",
            "READ"
        );
        testPermission = permissionRepository.save(testPermission);
    }

    @AfterEach
    void tearDown() {
        if (testRole != null && testRole.getId() != null) {
            try {
                roleRepository.deleteById(testRole.getId());
            } catch (Exception e) {
                // Ignore
            }
        }
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
    void shouldGetAllRoles() throws Exception {
        mockMvc.perform(get("/api/v2/roles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(testRole.getId().value()))
            .andExpect(jsonPath("$[0].name").value("ADMIN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetRoleById() throws Exception {
        mockMvc.perform(get("/api/v2/roles/{id}", testRole.getId().value()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testRole.getId().value()))
            .andExpect(jsonPath("$.name").value("ADMIN"))
            .andExpect(jsonPath("$.description").value("Administrator role"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetRoleByName() throws Exception {
        mockMvc.perform(get("/api/v2/roles/name/{name}", "ADMIN"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("ADMIN"))
            .andExpect(jsonPath("$.description").value("Administrator role"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateRole() throws Exception {
        RoleRequest request = new RoleRequest(
            "USER",
            "User role"
        );

        MvcResult result = mockMvc.perform(post("/api/v2/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("USER"))
            .andExpect(jsonPath("$.description").value("User role"))
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        RoleResponse createdRole = objectMapper.readValue(responseContent, RoleResponse.class);
        roleRepository.deleteById(new RoleId(createdRole.id()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateRole() throws Exception {
        RoleRequest updateRequest = new RoleRequest(
            "ADMIN",
            "Updated administrator role"
        );

        mockMvc.perform(put("/api/v2/roles/{id}", testRole.getId().value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testRole.getId().value()))
            .andExpect(jsonPath("$.description").value("Updated administrator role"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAssignPermissionToRole() throws Exception {
        mockMvc.perform(post("/api/v2/roles/{roleId}/permissions/{permissionId}",
                testRole.getId().value(),
                testPermission.getId().value()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testRole.getId().value()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRemovePermissionFromRole() throws Exception {
        testRole.assignPermission(testPermission);
        roleRepository.save(testRole);

        mockMvc.perform(delete("/api/v2/roles/{roleId}/permissions/{permissionId}",
                testRole.getId().value(),
                testPermission.getId().value()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testRole.getId().value()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteRole() throws Exception {
        Role roleToDelete = new Role(
            new RoleName("TEMP"),
            "Temporary role"
        );
        roleToDelete = roleRepository.save(roleToDelete);

        mockMvc.perform(delete("/api/v2/roles/{id}", roleToDelete.getId().value()))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v2/roles/{id}", roleToDelete.getId().value()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForMissingRequiredFields() throws Exception {
        RoleRequest invalidRequest = new RoleRequest(null, null);

        mockMvc.perform(post("/api/v2/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundForNonExistentRole() throws Exception {
        mockMvc.perform(get("/api/v2/roles/{id}", 99999L))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForBlankRoleName() throws Exception {
        RoleRequest request = new RoleRequest("", "Description");

        mockMvc.perform(post("/api/v2/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForRoleNameTooLong() throws Exception {
        String longName = "A".repeat(51);
        RoleRequest request = new RoleRequest(longName, "Description");

        mockMvc.perform(post("/api/v2/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForDuplicateRoleName() throws Exception {
        RoleRequest request = new RoleRequest("ADMIN", "Another admin role");

        mockMvc.perform(post("/api/v2/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenUpdatingNonExistentRole() throws Exception {
        RoleRequest updateRequest = new RoleRequest("TEST", "Test role");

        mockMvc.perform(put("/api/v2/roles/{id}", 99999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenDeletingNonExistentRole() throws Exception {
        mockMvc.perform(delete("/api/v2/roles/{id}", 99999L))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenAssigningPermissionToNonExistentRole() throws Exception {
        mockMvc.perform(post("/api/v2/roles/{roleId}/permissions/{permissionId}",
                99999L,
                testPermission.getId().value()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenAssigningNonExistentPermissionToRole() throws Exception {
        mockMvc.perform(post("/api/v2/roles/{roleId}/permissions/{permissionId}",
                testRole.getId().value(),
                99999L))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnEmptyArrayWhenNoRoles() throws Exception {
        roleRepository.deleteById(testRole.getId());

        mockMvc.perform(get("/api/v2/roles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundForNonExistentRoleName() throws Exception {
        mockMvc.perform(get("/api/v2/roles/name/{name}", "NONEXISTENT"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateRoleWithNullDescription() throws Exception {
        RoleRequest request = new RoleRequest("NOROLE", null);

        MvcResult result = mockMvc.perform(post("/api/v2/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("NOROLE"))
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        RoleResponse createdRole = objectMapper.readValue(responseContent, RoleResponse.class);
        roleRepository.deleteById(new RoleId(createdRole.id()));
    }
}
