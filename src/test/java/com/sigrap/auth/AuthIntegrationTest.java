package com.sigrap.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.user.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Test
  void register_thenLogin_shouldSucceed() throws Exception {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setName("Integration Test User");
    registerRequest.setEmail("integration-test@example.com");
    registerRequest.setPassword("Password123!");

    MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.token").exists())
        .andExpect(jsonPath("$.email").value("integration-test@example.com"))
        .andExpect(jsonPath("$.name").value("Integration Test User"))
        .andReturn();

    AuthResponse registerResponse = objectMapper.readValue(
        registerResult.getResponse().getContentAsString(),
        AuthResponse.class);

    assertThat(registerResponse.getToken()).isNotNull();
    assertThat(userRepository.findByEmail("integration-test@example.com")).isPresent();

    AuthRequest loginRequest = new AuthRequest();
    loginRequest.setEmail("integration-test@example.com");
    loginRequest.setPassword("Password123!");

    MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists())
        .andExpect(jsonPath("$.email").value("integration-test@example.com"))
        .andReturn();

    AuthResponse loginResponse = objectMapper.readValue(
        loginResult.getResponse().getContentAsString(),
        AuthResponse.class);

    assertThat(loginResponse.getToken()).isNotNull();
  }

  @Test
  void register_withExistingEmail_shouldFail() throws Exception {
    RegisterRequest firstRegisterRequest = new RegisterRequest();
    firstRegisterRequest.setName("First User");
    firstRegisterRequest.setEmail("duplicate@example.com");
    firstRegisterRequest.setPassword("Password123!");

    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(firstRegisterRequest)))
        .andExpect(status().isCreated());

    RegisterRequest duplicateRegisterRequest = new RegisterRequest();
    duplicateRegisterRequest.setName("Duplicate User");
    duplicateRegisterRequest.setEmail("duplicate@example.com");
    duplicateRegisterRequest.setPassword("AnotherPassword123!");

    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(duplicateRegisterRequest)))
        .andExpect(status().isConflict());
  }

  @Test
  void login_withInvalidCredentials_shouldFail() throws Exception {
    AuthRequest invalidLoginRequest = new AuthRequest();
    invalidLoginRequest.setEmail("nonexistent@example.com");
    invalidLoginRequest.setPassword("InvalidPassword123!");

    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
        .andExpect(status().isUnauthorized());
  }
}