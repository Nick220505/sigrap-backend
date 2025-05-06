package com.sigrap.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;

class GlobalExceptionHandlerTest {

  private MockMvc mockMvc;

  @RestController
  static class TestController {

    @GetMapping("/test/entity-not-found")
    public void throwEntityNotFoundException() {
      throw new EntityNotFoundException("Entity not found");
    }

    @GetMapping("/test/data-integrity-violation")
    public void throwDataIntegrityViolationException() {
      throw new DataIntegrityViolationException("Data integrity violation");
    }

    @GetMapping("/test/bad-credentials")
    public void throwBadCredentialsException() {
      throw new BadCredentialsException("Invalid credentials");
    }

    @GetMapping("/test/illegal-argument")
    public void throwIllegalArgumentException() {
      throw new IllegalArgumentException("Invalid argument");
    }

    @GetMapping("/test/email-exists")
    public void throwEmailExistsException() {
      throw new IllegalArgumentException("Email already exists");
    }

    @GetMapping("/test/generic-exception")
    public void throwGenericException() {
      throw new RuntimeException("Something went wrong");
    }
  }

  @BeforeEach
  void setup() {
    mockMvc = standaloneSetup(new TestController())
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  void handleEntityNotFoundException() throws Exception {
    mockMvc.perform(get("/test/entity-not-found")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Entity not found"));
  }

  @Test
  void handleDataIntegrityViolationException() throws Exception {
    mockMvc.perform(get("/test/data-integrity-violation")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.error").value("Conflict"))
        .andExpect(jsonPath("$.message").value("Data integrity violation"));
  }

  @Test
  void handleBadCredentialsException() throws Exception {
    mockMvc.perform(get("/test/bad-credentials")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(401))
        .andExpect(jsonPath("$.error").value("Unauthorized"))
        .andExpect(jsonPath("$.message").value("Invalid credentials"));
  }

  @Test
  void handleIllegalArgumentException() throws Exception {
    mockMvc.perform(get("/test/illegal-argument")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.message").value("Invalid argument"));
  }

  @Test
  void handleEmailExistsException() throws Exception {
    mockMvc.perform(get("/test/email-exists")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.error").value("Conflict"))
        .andExpect(jsonPath("$.message").value("Email already exists"));
  }

  @Test
  void handleGenericException() throws Exception {
    mockMvc.perform(get("/test/generic-exception")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.error").value("Internal Server Error"))
        .andExpect(jsonPath("$.message").value("Something went wrong"));
  }
}