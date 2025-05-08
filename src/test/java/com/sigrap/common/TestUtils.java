package com.sigrap.common;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.auth.AuthResponse;
import com.sigrap.auth.RegisterRequest;

public class TestUtils {

  private TestUtils() {
  }

  public static void setupTestSecurityContext(String email) {
    List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    UserDetails userDetails = User.builder()
        .username(email)
        .password("password")
        .authorities(authorities)
        .build();
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(userDetails, null, authorities));
  }

  public static String registerTestUserAndGetToken(MockMvc mockMvc, ObjectMapper objectMapper,
      String name, String email, String password) throws Exception {
    RegisterRequest registerRequest = RegisterRequest.builder()
        .name(name)
        .email(email)
        .password(password)
        .build();

    MvcResult registerResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)))
        .andReturn();

    AuthResponse registerResponse = objectMapper.readValue(
        registerResult.getResponse().getContentAsString(),
        AuthResponse.class);

    return registerResponse.getToken();
  }

  public static MockHttpServletRequestBuilder protectedEndpointRequest(String method, String endpoint, String token) {
    MockHttpServletRequestBuilder requestBuilder;

    switch (method.toUpperCase()) {
      case "GET":
        requestBuilder = MockMvcRequestBuilders.get(endpoint);
        break;
      case "POST":
        requestBuilder = MockMvcRequestBuilders.post(endpoint);
        break;
      case "PUT":
        requestBuilder = MockMvcRequestBuilders.put(endpoint);
        break;
      case "DELETE":
        requestBuilder = MockMvcRequestBuilders.delete(endpoint);
        break;
      default:
        throw new IllegalArgumentException("Método HTTP no soportado: " + method);
    }

    return requestBuilder.header("Authorization", "Bearer " + token);
  }

  public static void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  public static String generateValidPassword() {
    return "Password123!";
  }

  public static String generateUniqueEmail(String prefix) {
    return prefix + "-" + System.currentTimeMillis() + "@test.com";
  }
}