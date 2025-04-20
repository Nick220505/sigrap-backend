package com.sigrap.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

  @Bean
  CorsConfigurationSource corsConfigurationSource(Environment environment) {
    CorsConfiguration config = new CorsConfiguration();

    String allowedOrigins = environment.getProperty("cors.allowed.origins");

    config.setAllowCredentials(true);
    if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
      Arrays.stream(allowedOrigins.split(","))
          .map(String::trim)
          .forEach(config::addAllowedOrigin);
    }
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    config.addExposedHeader("Content-Disposition");

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}