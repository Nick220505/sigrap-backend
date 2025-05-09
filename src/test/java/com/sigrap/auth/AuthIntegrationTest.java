package com.sigrap.auth;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
    RegisterRequest registerRequest = RegisterRequest.builder()
      .name("Integration Test User")
      .email("integration-test@example.com")
      .password("Password123!")
      .build();

    MvcResult registerResult = mockMvc
      .perform(
        post("/api/auth/register")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(registerRequest))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.token").exists())
      .andExpect(jsonPath("$.email").value("integration-test@example.com"))
      .andExpect(jsonPath("$.name").value("Integration Test User"))
      .andReturn();

    AuthResponse registerResponse = objectMapper.readValue(
      registerResult.getResponse().getContentAsString(),
      AuthResponse.class
    );

    assertThat(registerResponse.getToken()).isNotNull();
    assertThat(
      userRepository.findByEmail("integration-test@example.com")
    ).isPresent();

    AuthRequest loginRequest = AuthRequest.builder()
      .email("integration-test@example.com")
      .password("Password123!")
      .build();

    MvcResult loginResult = mockMvc
      .perform(
        post("/api/auth/login")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(loginRequest))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.token").exists())
      .andExpect(jsonPath("$.email").value("integration-test@example.com"))
      .andReturn();

    AuthResponse loginResponse = objectMapper.readValue(
      loginResult.getResponse().getContentAsString(),
      AuthResponse.class
    );

    assertThat(loginResponse.getToken()).isNotNull();
  }

  @Test
  void register_withExistingEmail_shouldFail() throws Exception {
    RegisterRequest firstRegisterRequest = RegisterRequest.builder()
      .name("First User")
      .email("duplicate@example.com")
      .password("Password123!")
      .build();

    mockMvc
      .perform(
        post("/api/auth/register")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(firstRegisterRequest))
      )
      .andExpect(status().isCreated());

    RegisterRequest duplicateRegisterRequest = RegisterRequest.builder()
      .name("Duplicate User")
      .email("duplicate@example.com")
      .password("AnotherPassword123!")
      .build();

    mockMvc
      .perform(
        post("/api/auth/register")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(duplicateRegisterRequest))
      )
      .andExpect(status().isConflict());
  }

  @Test
  void login_withInvalidCredentials_shouldFail() throws Exception {
    AuthRequest invalidLoginRequest = AuthRequest.builder()
      .email("nonexistent@example.com")
      .password("InvalidPassword123!")
      .build();

    mockMvc
      .perform(
        post("/api/auth/login")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidLoginRequest))
      )
      .andExpect(status().isUnauthorized());
  }
}
