package com.sigrap.config;

import com.sigrap.auth.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Core security configuration class for the application.
 * Configures Spring Security with JWT authentication, CORS, CSRF, and other security headers.
 *
 * <p>This configuration:
 * <ul>
 *   <li>Implements stateless JWT-based authentication</li>
 *   <li>Configures security headers for XSS protection</li>
 *   <li>Sets up environment-specific security rules</li>
 *   <li>Manages public and protected endpoints</li>
 *   <li>Configures password encoding</li>
 * </ul></p>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  /**
   * JWT authentication filter for processing and validating JWT tokens in requests.
   * Intercepts incoming requests to extract and validate JWT tokens before processing.
   */
  private final JwtAuthenticationFilter jwtAuthFilter;

  /**
   * Environment configuration for accessing application properties and profiles.
   * Used to determine current runtime environment for conditional security settings.
   */
  private final Environment environment;

  /**
   * List of public endpoint paths that do not require authentication.
   * These endpoints are accessible to all users without a valid JWT token.
   */
  private static final String[] PUBLIC_ENDPOINTS = {
    "/",
    "/api/auth/**",
    "/h2-console/**",
    "/actuator/**",
    "/api/status",
    "/error",
  };

  /**
   * List of Swagger documentation endpoint paths for API documentation.
   * These are either public or protected based on the current environment profile.
   */
  private static final String[] SWAGGER_WHITELIST = {
    "/swagger-ui.html",
    "/swagger-ui/**",
    "/api-docs/**",
    "/api-docs",
    "/v3/api-docs/**",
    "/swagger-resources/**",
    "/webjars/**",
  };

  /**
   * Configures the security filter chain with all security settings.
   *
   * @param http HttpSecurity to be configured
   * @param corsConfigurationSource CORS configuration to be applied
   * @return Configured SecurityFilterChain
   * @throws Exception if configuration fails
   */
  @Bean
  SecurityFilterChain filterChain(
    HttpSecurity http,
    CorsConfigurationSource corsConfigurationSource
  ) throws Exception {
    return http
      .cors(cors -> cors.configurationSource(corsConfigurationSource))
      .csrf(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(authz -> {
        authz.requestMatchers(PUBLIC_ENDPOINTS).permitAll();

        if (environment.acceptsProfiles(Profiles.of("dev", "local"))) {
          authz.requestMatchers(SWAGGER_WHITELIST).permitAll();
          authz.requestMatchers("/api/**").permitAll();
        } else {
          authz.requestMatchers(SWAGGER_WHITELIST).authenticated();
          authz.requestMatchers("/api/**").authenticated();
        }

        authz.anyRequest().authenticated();
      })
      .sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .addFilterBefore(
        jwtAuthFilter,
        UsernamePasswordAuthenticationFilter.class
      )
      .headers(headers ->
        headers
          .xssProtection(xss ->
            xss.headerValue(
              XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK
            )
          )
          .contentSecurityPolicy(csp ->
            csp.policyDirectives("frame-ancestors 'self'")
          )
      )
      .httpBasic(AbstractHttpConfigurer::disable)
      .formLogin(AbstractHttpConfigurer::disable)
      .build();
  }

  /**
   * Configures the password encoder for the application.
   * Uses BCrypt with default strength.
   *
   * @return Configured BCryptPasswordEncoder
   */
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Configures the authentication manager.
   *
   * @param config Authentication configuration to use
   * @return Configured AuthenticationManager
   * @throws Exception if configuration fails
   */
  @Bean
  AuthenticationManager authenticationManager(
    AuthenticationConfiguration config
  ) throws Exception {
    return config.getAuthenticationManager();
  }
}
