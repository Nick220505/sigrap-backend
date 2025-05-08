package com.sigrap.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

class AuthControllerTest {

  private MockMvc mockMvc;
  private AuthService authService;
  private ObjectMapper objectMapper;

  @ControllerAdvice
  public static class TestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
      return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
      return ResponseEntity.badRequest().build();
    }
  }

  @BeforeEach
  void setup() {
    authService = mock(AuthService.class);
    objectMapper = new ObjectMapper();
    AuthController controller = new AuthController(authService);

    mockMvc = standaloneSetup(controller)
        .setControllerAdvice(new TestExceptionHandler())
        .build();
  }

  @Test
  void register_shouldReturnCreated_whenRegistrationIsSuccessful() throws Exception {
    RegisterRequest registerRequest = RegisterRequest.builder()
        .name("Test User")
        .email("test@example.com")
        .password("Password123!")
        .build();

    AuthResponse authResponse = AuthResponse.builder()
        .token("jwtToken")
        .email("test@example.com")
        .name("Test User")
        .build();

    when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.token").value("jwtToken"))
        .andExpect(jsonPath("$.email").value("test@example.com"))
        .andExpect(jsonPath("$.name").value("Test User"));

    verify(authService).register(any(RegisterRequest.class));
  }

  @Test
  void register_shouldReturnBadRequest_whenEmailAlreadyExists() throws Exception {
    RegisterRequest registerRequest = RegisterRequest.builder()
        .name("Test User")
        .email("existing@example.com")
        .password("Password123!")
        .build();

    doThrow(new IllegalArgumentException("Email already exists"))
        .when(authService).register(any(RegisterRequest.class));

    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isBadRequest());

    verify(authService).register(any(RegisterRequest.class));
  }

  @Test
  void login_shouldReturnOk_whenAuthenticationIsSuccessful() throws Exception {
    AuthRequest authRequest = AuthRequest.builder()
        .email("test@example.com")
        .password("Password123!")
        .build();

    AuthResponse authResponse = AuthResponse.builder()
        .token("jwtToken")
        .email("test@example.com")
        .name("Test User")
        .build();

    when(authService.authenticate(any(AuthRequest.class))).thenReturn(authResponse);

    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(authRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("jwtToken"))
        .andExpect(jsonPath("$.email").value("test@example.com"))
        .andExpect(jsonPath("$.name").value("Test User"));

    verify(authService).authenticate(any(AuthRequest.class));
  }

  @Test
  void login_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
    AuthRequest authRequest = AuthRequest.builder()
        .email("nonexistent@example.com")
        .password("Password123!")
        .build();

    doThrow(new EntityNotFoundException("User not found"))
        .when(authService).authenticate(any(AuthRequest.class));

    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(authRequest)))
        .andExpect(status().isNotFound());

    verify(authService).authenticate(any(AuthRequest.class));
  }
}