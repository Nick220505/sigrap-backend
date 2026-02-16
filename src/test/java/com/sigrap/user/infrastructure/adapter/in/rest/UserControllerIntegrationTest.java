package com.sigrap.user.infrastructure.adapter.in.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.config.BaseIntegrationTest;
import com.sigrap.user.domain.model.*;
import com.sigrap.user.domain.port.UserRepositoryPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Integration test for UserController.
 * Tests the /api/v2/users endpoints.
 */
public class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepositoryPort userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(
            new Username("johndoe"),
            new UserEmail("john.doe@example.com"),
            "hashedPassword123"
        );
        testUser = userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        if (testUser != null && testUser.getId() != null) {
            try {
                userRepository.deleteById(testUser.getId());
            } catch (Exception e) {
                // Ignore if already deleted
            }
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/v2/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(testUser.getId().value()))
            .andExpect(jsonPath("$[0].username").value("johndoe"))
            .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetUserById() throws Exception {
        mockMvc.perform(get("/api/v2/users/{id}", testUser.getId().value()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testUser.getId().value()))
            .andExpect(jsonPath("$.username").value("johndoe"))
            .andExpect(jsonPath("$.email").value("john.doe@example.com"))
            .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetUserByUsername() throws Exception {
        mockMvc.perform(get("/api/v2/users/username/{username}", "johndoe"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("johndoe"))
            .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetUserByEmail() throws Exception {
        mockMvc.perform(get("/api/v2/users/email/{email}", "john.doe@example.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("johndoe"))
            .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllEnabledUsers() throws Exception {
        User disabledUser = new User(
            new Username("disabled"),
            new UserEmail("disabled@example.com"),
            "password"
        );
        disabledUser.disable();
        disabledUser = userRepository.save(disabledUser);

        mockMvc.perform(get("/api/v2/users/enabled"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[?(@.username == 'johndoe')]").exists())
            .andExpect(jsonPath("$[?(@.username == 'disabled')]").doesNotExist());

        if (disabledUser != null && disabledUser.getId() != null) {
            userRepository.deleteById(disabledUser.getId());
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateUser() throws Exception {
        UserRequest request = new UserRequest(
            "janedoe",
            "jane.doe@example.com",
            "password123"
        );

        MvcResult result = mockMvc.perform(post("/api/v2/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("janedoe"))
            .andExpect(jsonPath("$.email").value("jane.doe@example.com"))
            .andExpect(jsonPath("$.enabled").value(true))
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        UserResponse createdUser = objectMapper.readValue(responseContent, UserResponse.class);
        userRepository.deleteById(new UserId(createdUser.id()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateUser() throws Exception {
        UpdateUserRequest updateRequest = new UpdateUserRequest(
            "john.updated@example.com",
            "newPassword123"
        );

        mockMvc.perform(put("/api/v2/users/{id}", testUser.getId().value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testUser.getId().value()))
            .andExpect(jsonPath("$.email").value("john.updated@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldEnableUser() throws Exception {
        testUser.disable();
        userRepository.save(testUser);

        mockMvc.perform(post("/api/v2/users/{id}/enable", testUser.getId().value()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDisableUser() throws Exception {
        mockMvc.perform(post("/api/v2/users/{id}/disable", testUser.getId().value()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.enabled").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteUser() throws Exception {
        User userToDelete = new User(
            new Username("deleteme"),
            new UserEmail("delete.me@example.com"),
            "password"
        );
        userToDelete = userRepository.save(userToDelete);

        mockMvc.perform(delete("/api/v2/users/{id}", userToDelete.getId().value()))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v2/users/{id}", userToDelete.getId().value()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForInvalidEmail() throws Exception {
        UserRequest invalidRequest = new UserRequest(
            "testuser",
            "not-an-email",
            "password123"
        );

        mockMvc.perform(post("/api/v2/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForMissingRequiredFields() throws Exception {
        UserRequest invalidRequest = new UserRequest(
            null,
            null,
            null
        );

        mockMvc.perform(post("/api/v2/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundForNonExistentUser() throws Exception {
        mockMvc.perform(get("/api/v2/users/{id}", 99999L))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForBlankUsername() throws Exception {
        UserRequest request = new UserRequest(
            "",
            "test@example.com",
            "password123"
        );

        mockMvc.perform(post("/api/v2/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForUsernameTooLong() throws Exception {
        String longUsername = "a".repeat(51);
        UserRequest request = new UserRequest(
            longUsername,
            "test@example.com",
            "password123"
        );

        mockMvc.perform(post("/api/v2/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForEmailTooLong() throws Exception {
        String longEmail = "a".repeat(250) + "@example.com";
        UserRequest request = new UserRequest(
            "testuser",
            longEmail,
            "password123"
        );

        mockMvc.perform(post("/api/v2/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForDuplicateUsername() throws Exception {
        UserRequest request = new UserRequest(
            "johndoe",
            "another@example.com",
            "password123"
        );

        mockMvc.perform(post("/api/v2/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForDuplicateEmail() throws Exception {
        UserRequest request = new UserRequest(
            "anotheruser",
            "john.doe@example.com",
            "password123"
        );

        mockMvc.perform(post("/api/v2/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenUpdatingNonExistentUser() throws Exception {
        UpdateUserRequest updateRequest = new UpdateUserRequest(
            "test@example.com",
            "password123"
        );

        mockMvc.perform(put("/api/v2/users/{id}", 99999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenDeletingNonExistentUser() throws Exception {
        mockMvc.perform(delete("/api/v2/users/{id}", 99999L))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenEnablingNonExistentUser() throws Exception {
        mockMvc.perform(post("/api/v2/users/{id}/enable", 99999L))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenDisablingNonExistentUser() throws Exception {
        mockMvc.perform(post("/api/v2/users/{id}/disable", 99999L))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnEmptyArrayWhenNoUsers() throws Exception {
        userRepository.deleteById(testUser.getId());

        mockMvc.perform(get("/api/v2/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForInvalidIdInUpdate() throws Exception {
        UpdateUserRequest updateRequest = new UpdateUserRequest(
            "test@example.com",
            "password123"
        );

        mockMvc.perform(put("/api/v2/users/{id}", 0L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForInvalidIdInDelete() throws Exception {
        mockMvc.perform(delete("/api/v2/users/{id}", 0L))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForInvalidIdInGet() throws Exception {
        mockMvc.perform(get("/api/v2/users/{id}", -1L))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundForNonExistentUsername() throws Exception {
        mockMvc.perform(get("/api/v2/users/username/{username}", "nonexistent"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundForNonExistentEmail() throws Exception {
        mockMvc.perform(get("/api/v2/users/email/{email}", "nonexistent@example.com"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldHandleUserWithSpecialCharactersInName() throws Exception {
        UserRequest request = new UserRequest(
            "jose.maria",
            "jose.maria@example.com",
            "password123"
        );

        MvcResult result = mockMvc.perform(post("/api/v2/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("jose.maria"))
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        UserResponse createdUser = objectMapper.readValue(responseContent, UserResponse.class);
        userRepository.deleteById(new UserId(createdUser.id()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowUpdateWithSameEmail() throws Exception {
        UpdateUserRequest updateRequest = new UpdateUserRequest(
            "john.doe@example.com",
            "newPassword123"
        );

        mockMvc.perform(put("/api/v2/users/{id}", testUser.getId().value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenUpdatingWithDuplicateEmail() throws Exception {
        User anotherUser = new User(
            new Username("anotheruser"),
            new UserEmail("another@example.com"),
            "password"
        );
        anotherUser = userRepository.save(anotherUser);

        UpdateUserRequest updateRequest = new UpdateUserRequest(
            "another@example.com",
            "password123"
        );

        mockMvc.perform(put("/api/v2/users/{id}", testUser.getId().value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isBadRequest());

        userRepository.deleteById(anotherUser.getId());
    }
}
