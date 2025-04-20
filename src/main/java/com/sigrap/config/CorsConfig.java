package com.sigrap.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

  @Value("${cors.allowed.origins}")
  private String allowedOrigins;

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();

    config.setAllowCredentials(true);
    if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
      Arrays.stream(allowedOrigins.split(","))
          .map(String::trim)
          .forEach(config::addAllowedOriginPattern);
    }
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    config.addExposedHeader("Content-Disposition");

    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }
}