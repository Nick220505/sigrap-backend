package com.sigrap.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.cors.CorsConfigurationSource;

class CorsConfigTest {

  @Test
  void corsConfigurationSource_shouldReturnCorsConfiguration_whenProdProfile() {
    MockEnvironment environment = new MockEnvironment();
    environment.setActiveProfiles("prod");

    CorsConfig corsConfig = new CorsConfig();

    CorsConfigurationSource source = corsConfig.corsConfigurationSource(environment);

    assertNotNull(source);
  }

  @Test
  void corsConfigurationSource_shouldReturnCorsConfiguration_whenNotProdProfile() {
    MockEnvironment environment = new MockEnvironment();
    environment.setActiveProfiles("dev");

    CorsConfig corsConfig = new CorsConfig();

    CorsConfigurationSource source = corsConfig.corsConfigurationSource(environment);

    assertNotNull(source);
  }
}