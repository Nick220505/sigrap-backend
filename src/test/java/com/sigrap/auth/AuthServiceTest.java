package com.sigrap.auth;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import com.sigrap.user.UserService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
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

  @Test
  void register_shouldCreateNewUser_andReturnToken() {
    RegisterRequest registerRequest = RegisterRequest.builder()
        .name("Test User")
        .email("test@example.com")
        .password("Password123!")
        .build();

    User savedUser = User.builder()
        .id(1L)
        .name("Test User")
        .email("test@example.com")
        .password("encodedPassword")
        .build();

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
    RegisterRequest registerRequest = RegisterRequest.builder()
        .email("existing@example.com")
        .build();

    when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> {
      authService.register(registerRequest);
    });

    verify(userRepository, times(0)).save(any(User.class));
  }

  @Test
  void authenticate_shouldReturnToken_whenCredentialsAreValid() {
    AuthRequest authRequest = AuthRequest.builder()
        .email("test@example.com")
        .password("Password123!")
        .build();

    User user = User.builder()
        .id(1L)
        .name("Test User")
        .email("test@example.com")
        .password("encodedPassword")
        .build();

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
    AuthRequest authRequest = AuthRequest.builder()
        .email("nonexistent@example.com")
        .password("Password123!")
        .build();

    when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      authService.authenticate(authRequest);
    });
  }
}