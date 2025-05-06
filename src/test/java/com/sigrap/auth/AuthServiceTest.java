package com.sigrap.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import com.sigrap.user.UserService;

import jakarta.persistence.EntityNotFoundException;

class AuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private UserService userService;

  @InjectMocks
  private AuthService authService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void register_shouldCreateNewUser_andReturnToken() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setName("Test User");
    registerRequest.setEmail("test@example.com");
    registerRequest.setPassword("Password123!");

    User savedUser = new User();
    savedUser.setId(1L);
    savedUser.setName("Test User");
    savedUser.setEmail("test@example.com");
    savedUser.setPassword("encodedPassword");

    when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
    when(passwordEncoder.encode("Password123!")).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    UserDetails userDetails = org.springframework.security.core.userdetails.User
        .withUsername("test@example.com")
        .password("encodedPassword")
        .authorities(java.util.Collections.emptyList())
        .build();

    when(userService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
    when(jwtUtil.generateToken(userDetails)).thenReturn("jwtToken");

    AuthResponse response = authService.register(registerRequest);

    assertThat(response).isNotNull();
    assertThat(response.getToken()).isEqualTo("jwtToken");
    assertThat(response.getEmail()).isEqualTo("test@example.com");
    assertThat(response.getName()).isEqualTo("Test User");

    verify(userRepository).save(any(User.class));
    verify(jwtUtil).generateToken(any(UserDetails.class));
  }

  @Test
  void register_shouldThrowException_whenEmailExists() {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setEmail("existing@example.com");

    when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> {
      authService.register(registerRequest);
    });

    verify(userRepository, times(0)).save(any(User.class));
  }

  @Test
  void authenticate_shouldReturnToken_whenCredentialsAreValid() {
    AuthRequest authRequest = new AuthRequest();
    authRequest.setEmail("test@example.com");
    authRequest.setPassword("Password123!");

    User user = new User();
    user.setId(1L);
    user.setName("Test User");
    user.setEmail("test@example.com");
    user.setPassword("encodedPassword");

    UserDetails userDetails = org.springframework.security.core.userdetails.User
        .withUsername("test@example.com")
        .password("encodedPassword")
        .authorities(java.util.Collections.emptyList())
        .build();

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
    when(userService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
    when(jwtUtil.generateToken(userDetails)).thenReturn("jwtToken");

    AuthResponse response = authService.authenticate(authRequest);

    assertThat(response).isNotNull();
    assertThat(response.getToken()).isEqualTo("jwtToken");
    assertThat(response.getEmail()).isEqualTo("test@example.com");
    assertThat(response.getName()).isEqualTo("Test User");

    verify(authenticationManager).authenticate(
        new UsernamePasswordAuthenticationToken("test@example.com", "Password123!"));
    verify(jwtUtil).generateToken(any(UserDetails.class));
  }

  @Test
  void authenticate_shouldThrowException_whenUserNotFound() {
    AuthRequest authRequest = new AuthRequest();
    authRequest.setEmail("nonexistent@example.com");
    authRequest.setPassword("Password123!");

    when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      authService.authenticate(authRequest);
    });
  }
}