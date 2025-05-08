package com.sigrap.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.auth.AuthRequest;
import com.sigrap.auth.AuthResponse;
import com.sigrap.auth.RegisterRequest;
import com.sigrap.product.ProductData;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ErrorHandlingIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void entityNotFound_shouldReturnAppropriateErrorResponse() throws Exception {
    RegisterRequest registerRequest = RegisterRequest.builder()
        .name("Error Test User")
        .email("error-handling-test@example.com")
        .password("Password123!")
        .build();

    String registerResponseJson = mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    AuthResponse registerResponse = objectMapper.readValue(registerResponseJson, AuthResponse.class);
    String token = registerResponse.getToken();

    mockMvc.perform(get("/api/products/999")
        .header("Authorization", "Bearer " + token))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"));
  }

  @Test
  void validationErrors_shouldReturnAppropriateErrorResponse() throws Exception {
    RegisterRequest registerRequest = RegisterRequest.builder()
        .name("Validation Test User")
        .email("validation-test@example.com")
        .password("Password123!")
        .build();

    String registerResponseJson = mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    AuthResponse registerResponse = objectMapper.readValue(registerResponseJson, AuthResponse.class);
    String token = registerResponse.getToken();

    ProductData invalidProduct = ProductData.builder()
        .costPrice(new BigDecimal("10.00"))
        .salePrice(new BigDecimal("15.00"))
        .build();

    mockMvc.perform(post("/api/products")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidProduct)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.errors").exists());
  }

  @Test
  void badCredentials_shouldReturnAppropriateErrorResponse() throws Exception {
    AuthRequest invalidLoginRequest = AuthRequest.builder()
        .email("nonexistent@example.com")
        .password("wrongpassword")
        .build();

    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(401))
        .andExpect(jsonPath("$.error").value("Unauthorized"));
  }

  @Test
  void businessRuleViolation_shouldReturnAppropriateErrorResponse() throws Exception {
    RegisterRequest registerRequest = RegisterRequest.builder()
        .name("Duplicate User")
        .email("duplicate@example.com")
        .password("Password123!")
        .build();

    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.error").value("Conflict"));
  }

  @Test
  void invalidInput_shouldReturnAppropriateErrorResponse() throws Exception {
    RegisterRequest registerRequest = RegisterRequest.builder()
        .name("Invalid Input Test User")
        .email("invalid-input-test@example.com")
        .password("Password123!")
        .build();

    String registerResponseJson = mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    AuthResponse registerResponse = objectMapper.readValue(registerResponseJson, AuthResponse.class);
    String token = registerResponse.getToken();

    ProductData invalidProduct = ProductData.builder()
        .name("Invalid Product")
        .costPrice(new BigDecimal("-10.00"))
        .salePrice(new BigDecimal("15.00"))
        .build();

    mockMvc.perform(post("/api/products")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidProduct)))
        .andExpect(status().isBadRequest());
  }
}