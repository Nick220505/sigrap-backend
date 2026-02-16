package com.sigrap.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Configuration class for method-level security.
 * Sets up security for method annotations like @PreAuthorize and @PostAuthorize.
 * This configuration is disabled in the test profile to allow integration tests
 * to run without method-level security checks.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@Profile("!test")
public class MethodSecurityConfig {

  private final CustomPermissionEvaluator permissionEvaluator;

  /**
   * Creates and configures the method security expression handler.
   * This enables custom permission evaluations in @PreAuthorize expressions.
   *
   * @return The configured expression handler
   */
  @Bean
  MethodSecurityExpressionHandler createExpressionHandler() {
    DefaultMethodSecurityExpressionHandler expressionHandler =
      new DefaultMethodSecurityExpressionHandler();
    expressionHandler.setPermissionEvaluator(permissionEvaluator);
    return expressionHandler;
  }
}
