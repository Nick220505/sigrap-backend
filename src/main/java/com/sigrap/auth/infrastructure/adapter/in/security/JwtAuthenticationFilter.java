package com.sigrap.auth.infrastructure.adapter.in.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.auth.infrastructure.adapter.out.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Authentication Filter for processing JWT tokens in HTTP requests.
 * 
 * <p>This filter intercepts incoming requests, extracts JWT tokens from the Authorization header,
 * validates them, and sets up Spring Security authentication context if the token is valid.
 * 
 * <p>This is part of the infrastructure layer in hexagonal architecture,
 * adapting HTTP requests to our authentication mechanism.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    /**
     * Constructor for dependency injection.
     *
     * @param jwtUtil the JWT utility for token operations
     * @param userDetailsService the service for loading user details
     * @param objectMapper the JSON object mapper for error responses
     */
    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            UserDetailsService userDetailsService,
            ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    /**
     * Filters incoming requests to extract and validate JWT tokens.
     * 
     * <p>Process:
     * 1. Extract JWT token from Authorization header
     * 2. Extract username (email) from token
     * 3. Load user details if not already authenticated
     * 4. Validate token and set authentication in SecurityContext
     * 5. Handle any JWT-related exceptions
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain to continue processing
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        
        // Skip if no Authorization header or not a Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract token (remove "Bearer " prefix)
            final String jwt = authHeader.substring(7);
            
            // Extract username (email) from token
            final String userEmail = jwtUtil.extractUsername(jwt);
            
            // If we have a username and no authentication is set yet
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                
                // Validate token
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    // Set authentication details
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Set authentication in SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            
            // Continue filter chain
            filterChain.doFilter(request, response);
            
        } catch (ExpiredJwtException e) {
            // Handle expired token specifically
            handleExpiredToken(response, e);
        } catch (Exception e) {
            // Handle other JWT-related exceptions
            handleInvalidToken(response, e);
        }
    }

    /**
     * Handles expired JWT token by sending an appropriate error response.
     *
     * @param response the HTTP response
     * @param e the expired JWT exception
     * @throws IOException if an I/O error occurs
     */
    private void handleExpiredToken(HttpServletResponse response, ExpiredJwtException e) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
        errorResponse.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        errorResponse.put("message", "Token has expired");
        errorResponse.put("code", "TOKEN_EXPIRED");
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    /**
     * Handles invalid JWT token by sending an appropriate error response.
     *
     * @param response the HTTP response
     * @param e the exception
     * @throws IOException if an I/O error occurs
     */
    private void handleInvalidToken(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
        errorResponse.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        errorResponse.put("message", "Invalid or malformed token");
        errorResponse.put("code", "INVALID_TOKEN");
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
