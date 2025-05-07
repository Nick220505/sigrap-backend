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
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setName("Error Test User");
    registerRequest.setEmail("error-handling-test@example.com");
    registerRequest.setPassword("Password123!");

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
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setName("Validation Test User");
    registerRequest.setEmail("validation-test@example.com");
    registerRequest.setPassword("Password123!");

    String registerResponseJson = mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    AuthResponse registerResponse = objectMapper.readValue(registerResponseJson, AuthResponse.class);
    String token = registerResponse.getToken();

    ProductData invalidProduct = new ProductData();
    invalidProduct.setCostPrice(new BigDecimal("10.00"));
    invalidProduct.setSalePrice(new BigDecimal("15.00"));

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
    AuthRequest invalidLoginRequest = new AuthRequest();
    invalidLoginRequest.setEmail("nonexistent@example.com");
    invalidLoginRequest.setPassword("wrongpassword");

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
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setName("Duplicate User");
    registerRequest.setEmail("duplicate@example.com");
    registerRequest.setPassword("Password123!");

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
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setName("Invalid Input Test User");
    registerRequest.setEmail("invalid-input-test@example.com");
    registerRequest.setPassword("Password123!");

    String registerResponseJson = mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    AuthResponse registerResponse = objectMapper.readValue(registerResponseJson, AuthResponse.class);
    String token = registerResponse.getToken();

    ProductData invalidProduct = new ProductData();
    invalidProduct.setName("Invalid Product");
    invalidProduct.setCostPrice(new BigDecimal("-10.00"));
    invalidProduct.setSalePrice(new BigDecimal("15.00"));

    mockMvc.perform(post("/api/products")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidProduct)))
        .andExpect(status().isBadRequest());
  }
}