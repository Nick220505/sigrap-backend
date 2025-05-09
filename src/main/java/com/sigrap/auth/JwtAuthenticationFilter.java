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

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserService userService;
  private final ObjectMapper objectMapper;

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
