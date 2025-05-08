package com.sigrap.config;

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
class SecurityIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void accessProtectedEndpoint_withoutAuthentication_shouldReturnUnauthorized() throws Exception {
    mockMvc.perform(get("/api/products"))
        .andExpect(status().isForbidden());
  }

  @Test
  void accessProtectedEndpoint_withValidToken_shouldSucceed() throws Exception {
    RegisterRequest registerRequest = RegisterRequest.builder()
        .name("Security Test User")
        .email("security-test@example.com")
        .password("Password123!")
        .build();

    String registerResponseJson = mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    AuthResponse registerResponse = objectMapper.readValue(registerResponseJson, AuthResponse.class);
    String token = registerResponse.getToken();

    mockMvc.perform(get("/api/categories")
        .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk());
  }

  @Test
  void badRequest_shouldReturnAppropriateErrorResponse() throws Exception {
    RegisterRequest registerRequest = RegisterRequest.builder()
        .name("Error Test User")
        .email("error-test@example.com")
        .password("Password123!")
        .build();

    String registerResponseJson = mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    AuthResponse registerResponse = objectMapper.readValue(registerResponseJson, AuthResponse.class);
    String token = registerResponse.getToken();

    ProductData invalidProduct = ProductData.builder().build();

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
  void invalidLogin_shouldReturnAppropriateErrorResponse() throws Exception {
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
  void invalidProductData_shouldReturnAppropriateErrorResponse() throws Exception {
    RegisterRequest registerRequest = RegisterRequest.builder()
        .name("Data Test User")
        .email("data-test@example.com")
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

  @Test
  void accessApiStatus_withoutAuthentication_shouldSucceed() throws Exception {
    mockMvc.perform(get("/api/status"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"))
        .andExpect(jsonPath("$.application").exists());
  }
}