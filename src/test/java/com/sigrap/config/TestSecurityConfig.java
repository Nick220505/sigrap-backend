package com.sigrap.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Test configuration to disable the main security configuration during tests.
 * This configuration is imported by BaseIntegrationTest and takes precedence over
 * the application's security configuration.
 */
@TestConfiguration
public class TestSecurityConfig {

  /**
   * Creates a test-specific SecurityFilterChain that permits all requests.
   * This bean takes precedence over the application's security filter chain.
   *
   * @param http the HttpSecurity to configure
   * @return a configured SecurityFilterChain
   * @throws Exception if an error occurs
   */
  @Bean
  @Primary
  @Order(Integer.MIN_VALUE)
  SecurityFilterChain testSecurityFilterChain(HttpSecurity http)
    throws Exception {
    http
      .securityMatchers(matchers ->
        matchers.requestMatchers(new AntPathRequestMatcher("/**"))
      )
      .csrf(AbstractHttpConfigurer::disable)
      .cors(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

    return http.build();
  }
}
