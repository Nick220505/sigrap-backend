package com.sigrap.auth.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.config.TestSecurityConfig;
import com.sigrap.user.infrastructure.adapter.out.persistence.RoleJpaEntity;
import com.sigrap.user.infrastructure.adapter.out.persistence.RoleJpaRepository;
import com.sigrap.user.infrastructure.adapter.out.persistence.UserJpaEntity;
import com.sigrap.user.infrastructure.adapter.out.persistence.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AuthController REST adapter.
 * Tests the complete authentication flow including:
 * - User login
 * - User registration
 * - Token validation
 * - Input validation
 * - Error handling
 * - HTTP status codes
 *
 * Uses @SpringBootTest to load the full application context and MockMvc
 * to simulate HTTP requests without starting a real HTTP server.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(TestSecurityConfig.class)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private RoleJpaRepository roleJpaRepository;

    private BCryptPasswordEncoder passwordEncoder;
    private RoleJpaEntity userRole;
    private UserJpaEntity testUser;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        
        // Ensure USER role exists
        userRole = roleJpaRepository.findByName("USER")
            .orElseGet(() -> {
                RoleJpaEntity role = new RoleJpaEntity();
                role.setName("USER");
                return roleJpaRepository.save(role);
            });

        // Create a test user
        Set<RoleJpaEntity> roles = new HashSet<>();
        roles.add(userRole);

        testUser = UserJpaEntity.builder()
            .username("Test User")
            .email("testuser@example.com")
            .hashedPassword(passwordEncoder.encode("Password123!"))
            .enabled(true)
            .roles(roles)
            .build();
        testUser = userJpaRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        // Clean up test data
        userJpaRepository.deleteAll();
    }

    // ========== POST /api/v2/auth/login - Login Tests ==========

    @Test
    void login_withValidCredentials_shouldReturnOk() throws Exception {
        AuthRequest request = new AuthRequest(
            "testuser@example.com",
            "Password123!"
        );

        mockMvc.perform(post("/api/v2/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isString())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.expiresAt").exists())
            .andExpect(jsonPath("$.email").value("testuser@example.com"))
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.authenticatedAt").exists())
            .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void login_withInvalidEmail_shouldReturnBadRequest() throws Exception {
        AuthRequest request = new AuthRequest(
            "nonexistent@example.com",
            "Password123!"
        );

        mockMvc.perform(post("/api/v2/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void login_withInvalidPassword_shouldReturnBadRequest() throws Exception {
        AuthRequest request = new AuthRequest(
            "testuser@example.com",
            "WrongPassword!"
        );

        mockMvc.perform(post("/api/v2/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void login_withBlankEmail_shouldReturnBadRequest() throws Exception {
        AuthRequest request = new AuthRequest(
            "",
            "Password123!"
        );

        mockMvc.perform(post("/api/v2/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void login_withBlankPassword_shouldReturnBadRequest() throws Exception {
        AuthRequest request = new AuthRequest(
            "testuser@example.com",
            ""
        );

        mockMvc.perform(post("/api/v2/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void login_withInvalidEmailFormat_shouldReturnBadRequest() throws Exception {
        AuthRequest request = new AuthRequest(
            "not-an-email",
            "Password123!"
        );

        mockMvc.perform(post("/api/v2/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void login_withShortPassword_shouldReturnBadRequest() throws Exception {
        AuthRequest request = new AuthRequest(
            "testuser@example.com",
            "Pass1!"
        );

        mockMvc.perform(post("/api/v2/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void login_withDisabledUser_shouldReturnBadRequest() throws Exception {
        // Create disabled user
        Set<RoleJpaEntity> roles = new HashSet<>();
        roles.add(userRole);

        UserJpaEntity disabledUser = UserJpaEntity.builder()
            .username("Disabled User")
            .email("disabled@example.com")
            .hashedPassword(passwordEncoder.encode("Password123!"))
            .enabled(false)
            .roles(roles)
            .build();
        userJpaRepository.save(disabledUser);

        AuthRequest request = new AuthRequest(
            "disabled@example.com",
            "Password123!"
        );

        mockMvc.perform(post("/api/v2/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    // ========== POST /api/v2/auth/register - Registration Tests ==========

    @Test
    void register_withValidData_shouldReturnCreated() throws Exception {
        RegisterRequest request = new RegisterRequest(
            "New User",
            "newuser@example.com",
            "Password123!"
        );

        mockMvc.perform(post("/api/v2/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").isString())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.expiresAt").exists())
            .andExpect(jsonPath("$.email").value("newuser@example.com"))
            .andExpect(jsonPath("$.name").value("New User"))
            .andExpect(jsonPath("$.authenticatedAt").exists())
            .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void register_withExistingEmail_shouldReturnBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest(
            "Another User",
            "testuser@example.com", // Already exists
            "Password123!"
        );

        mockMvc.perform(post("/api/v2/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void register_withBlankName_shouldReturnBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest(
            "",
            "newuser@example.com",
            "Password123!"
        );

        mockMvc.perform(post("/api/v2/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void register_withShortName_shouldReturnBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest(
            "A",
            "newuser@example.com",
            "Password123!"
        );

        mockMvc.perform(post("/api/v2/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void register_withLongName_shouldReturnBadRequest() throws Exception {
        String longName = "a".repeat(51);
        RegisterRequest request = new RegisterRequest(
            longName,
            "newuser@example.com",
            "Password123!"
        );

        mockMvc.perform(post("/api/v2/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void register_withInvalidEmailFormat_shouldReturnBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest(
            "New User",
            "not-an-email",
            "Password123!"
        );

        mockMvc.perform(post("/api/v2/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void register_withShortPassword_shouldReturnBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest(
            "New User",
            "newuser@example.com",
            "Pass1!"
        );

        mockMvc.perform(post("/api/v2/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void register_withBlankEmail_shouldReturnBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest(
            "New User",
            "",
            "Password123!"
        );

        mockMvc.perform(post("/api/v2/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void register_withBlankPassword_shouldReturnBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest(
            "New User",
            "newuser@example.com",
            ""
        );

        mockMvc.perform(post("/api/v2/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    // ========== GET /api/v2/auth/validate - Token Validation Tests ==========

    @Test
    void validateToken_withValidToken_shouldReturnOk() throws Exception {
        // First, login to get a valid token
        AuthRequest loginRequest = new AuthRequest(
            "testuser@example.com",
            "Password123!"
        );

        String loginResponse = mockMvc.perform(post("/api/v2/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        AuthResponse authResponse = objectMapper.readValue(loginResponse, AuthResponse.class);
        String token = authResponse.token();

        // Now validate the token
        mockMvc.perform(get("/api/v2/auth/validate")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(true))
            .andExpect(jsonPath("$.email").value("testuser@example.com"));
    }

    @Test
    void validateToken_withoutBearerPrefix_shouldReturnOk() throws Exception {
        // First, login to get a valid token
        AuthRequest loginRequest = new AuthRequest(
            "testuser@example.com",
            "Password123!"
        );

        String loginResponse = mockMvc.perform(post("/api/v2/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        AuthResponse authResponse = objectMapper.readValue(loginResponse, AuthResponse.class);
        String token = authResponse.token();

        // Validate without Bearer prefix
        mockMvc.perform(get("/api/v2/auth/validate")
                .header("Authorization", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(true))
            .andExpect(jsonPath("$.email").value("testuser@example.com"));
    }

    @Test
    void validateToken_withInvalidToken_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v2/auth/validate")
                .header("Authorization", "Bearer invalid.token.here"))
            .andExpect(status().isUnauthorized()); // Invalid token returns 401
    }

    @Test
    void validateToken_withoutAuthorizationHeader_shouldReturnInternalServerError() throws Exception {
        // Missing required header returns 500 (MissingRequestHeaderException)
        mockMvc.perform(get("/api/v2/auth/validate"))
            .andExpect(status().isInternalServerError());
    }

    // ========== Content Type and Request Format Tests ==========

    @Test
    void login_withoutContentType_shouldReturnUnsupportedMediaType() throws Exception {
        AuthRequest request = new AuthRequest(
            "testuser@example.com",
            "Password123!"
        );

        mockMvc.perform(post("/api/v2/auth/login")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().is5xxServerError());
    }

    @Test
    void login_withInvalidJson_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/v2/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
            .andExpect(status().is5xxServerError());
    }

    @Test
    void register_withoutContentType_shouldReturnUnsupportedMediaType() throws Exception {
        RegisterRequest request = new RegisterRequest(
            "New User",
            "newuser@example.com",
            "Password123!"
        );

        mockMvc.perform(post("/api/v2/auth/register")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().is5xxServerError());
    }

    // ========== End-to-End Flow Tests ==========

    @Test
    void completeAuthFlow_registerLoginValidate_shouldSucceed() throws Exception {
        // 1. Register a new user
        RegisterRequest registerRequest = new RegisterRequest(
            "Flow Test User",
            "flowtest@example.com",
            "Password123!"
        );

        String registerResponse = mockMvc.perform(post("/api/v2/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        AuthResponse registerAuthResponse = objectMapper.readValue(registerResponse, AuthResponse.class);
        String registerToken = registerAuthResponse.token();

        // 2. Validate the registration token
        mockMvc.perform(get("/api/v2/auth/validate")
                .header("Authorization", "Bearer " + registerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(true))
            .andExpect(jsonPath("$.email").value("flowtest@example.com"));

        // 3. Login with the same credentials
        AuthRequest loginRequest = new AuthRequest(
            "flowtest@example.com",
            "Password123!"
        );

        String loginResponse = mockMvc.perform(post("/api/v2/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        AuthResponse loginAuthResponse = objectMapper.readValue(loginResponse, AuthResponse.class);
        String loginToken = loginAuthResponse.token();

        // 4. Validate the login token
        mockMvc.perform(get("/api/v2/auth/validate")
                .header("Authorization", "Bearer " + loginToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(true))
            .andExpect(jsonPath("$.email").value("flowtest@example.com"));
    }
}
