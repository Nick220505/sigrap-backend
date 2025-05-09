package com.sigrap.auth;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.user.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class JwtAuthenticationFilterTest {

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private UserService userService;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private ServletOutputStream outputStream;

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

  @Test
  void doFilterInternal_shouldNotReauthenticate_whenAlreadyAuthenticated() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
    when(jwtUtil.extractUsername("validToken")).thenReturn("test@example.com");

    Authentication existingAuth = new UsernamePasswordAuthenticationToken(
        "test@example.com", null, Collections.emptyList());

    SecurityContextHolder.getContext().setAuthentication(existingAuth);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(jwtUtil, times(1)).extractUsername("validToken");
    verify(userService, never()).loadUserByUsername(anyString());
    verify(jwtUtil, never()).validateToken(anyString(), any(UserDetails.class));
  }

  @Test
  void doFilterInternal_shouldHandleExpiredToken() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("Bearer expiredToken");
    when(jwtUtil.extractUsername("expiredToken"))
        .thenThrow(new ExpiredJwtException(null, null, "Token has expired"));
    when(response.getOutputStream()).thenReturn(outputStream);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
    verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
    verify(objectMapper).writeValue(any(ServletOutputStream.class), argThat(map -> map instanceof Map &&
        ((Map<?, ?>) map).get("status").equals(HttpStatus.UNAUTHORIZED.value()) &&
        ((Map<?, ?>) map).get("error").equals(HttpStatus.UNAUTHORIZED.getReasonPhrase()) &&
        ((Map<?, ?>) map).get("message").equals("Token has expired") &&
        ((Map<?, ?>) map).get("code").equals("TOKEN_EXPIRED") &&
        ((Map<?, ?>) map).get("timestamp") != null));
    verify(filterChain, never()).doFilter(request, response);
  }
}