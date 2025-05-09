package com.sigrap.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.user.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter for JWT-based authentication.
 * Intercepts requests to validate JWT tokens and set up Spring Security context.
 * Handles token extraction, validation, and user authentication.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  /**
   * Utility for JWT operations such as token validation and data extraction.
   * Used to process and validate incoming JWT tokens in request headers.
   */
  private final JwtUtil jwtUtil;

  /**
   * Service for loading user details during authentication.
   * Used to retrieve user information based on the subject extracted from JWT tokens.
   */
  private final UserService userService;

  /**
   * JSON mapper for converting objects to JSON responses.
   * Used particularly for formatting error responses when tokens are expired or invalid.
   */
  private final ObjectMapper objectMapper;

  /**
   * Processes each request to validate JWT token and set up authentication.
   * Extracts JWT from Authorization header, validates it, and sets up Spring Security context.
   * Handles token expiration with appropriate error responses.
   *
   * @param request HTTP request
   * @param response HTTP response
   * @param filterChain Filter chain to execute
   * @throws ServletException if the request cannot be processed
   * @throws IOException if an I/O error occurs during processing
   */
  @Override
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    final String jwt = authHeader.substring(7);
    String userEmail;

    try {
      userEmail = jwtUtil.extractUsername(jwt);

      if (
        userEmail != null &&
        SecurityContextHolder.getContext().getAuthentication() == null
      ) {
        UserDetails userDetails =
          this.userService.loadUserByUsername(userEmail);

        if (Boolean.TRUE.equals(jwtUtil.validateToken(jwt, userDetails))) {
          UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities()
            );

          authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
          );
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }

      filterChain.doFilter(request, response);
    } catch (ExpiredJwtException ex) {
      log.debug("JWT token expired: {}", ex.getMessage());

      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
      errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
      errorResponse.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
      errorResponse.put("message", "Token has expired");
      errorResponse.put("code", "TOKEN_EXPIRED");

      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
  }
}
