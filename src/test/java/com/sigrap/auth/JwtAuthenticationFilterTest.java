package com.sigrap.auth;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.sigrap.user.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class JwtAuthenticationFilterTest {

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private UserService userService;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @InjectMocks
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  private SecurityContext securityContextBackup;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    securityContextBackup = SecurityContextHolder.getContext();
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.setContext(securityContextBackup);
  }

  @Test
  void doFilterInternal_shouldContinueFilterChain_whenNoAuthorizationHeader() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn(null);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(jwtUtil, never()).extractUsername(anyString());
  }

  @Test
  void doFilterInternal_shouldContinueFilterChain_whenAuthorizationHeaderNotBearer()
      throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNzd29yZA==");

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(jwtUtil, never()).extractUsername(anyString());
  }

  @Test
  void doFilterInternal_shouldAuthenticate_whenValidToken() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
    when(jwtUtil.extractUsername("validToken")).thenReturn("test@example.com");

    UserDetails userDetails = User.withUsername("test@example.com")
        .password("password")
        .authorities(Collections.emptyList())
        .build();

    when(userService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
    when(jwtUtil.validateToken("validToken", userDetails)).thenReturn(true);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(jwtUtil, times(1)).extractUsername("validToken");
    verify(jwtUtil, times(1)).validateToken("validToken", userDetails);
  }

  @Test
  void doFilterInternal_shouldNotAuthenticate_whenInvalidToken() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
    when(jwtUtil.extractUsername("invalidToken")).thenReturn("test@example.com");

    UserDetails userDetails = User.withUsername("test@example.com")
        .password("password")
        .authorities(Collections.emptyList())
        .build();

    when(userService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
    when(jwtUtil.validateToken("invalidToken", userDetails)).thenReturn(false);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(jwtUtil, times(1)).extractUsername("invalidToken");
    verify(jwtUtil, times(1)).validateToken("invalidToken", userDetails);
  }

  @Test
  void doFilterInternal_shouldNotAuthenticate_whenUsernameIsNull() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("Bearer someToken");
    when(jwtUtil.extractUsername("someToken")).thenReturn(null);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(jwtUtil, times(1)).extractUsername("someToken");
    verify(userService, never()).loadUserByUsername(anyString());
  }
}