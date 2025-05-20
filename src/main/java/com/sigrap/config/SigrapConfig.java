package com.sigrap.config;

import java.util.TimeZone;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Global configuration class for the SIGRAP application.
 * Contains configurations for common components used throughout the application.
 */
@Configuration
public class SigrapConfig {

  /**
   * Configures the application's default timezone to America/Bogota (Colombia timezone, UTC-5).
   * This affects timestamp conversions throughout the application.
   *
   * @return Jackson customizer that sets the timezone for date serialization
   */
  @Bean
  Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
    return builder -> {
      builder.timeZone(TimeZone.getTimeZone("America/Bogota"));
    };
  }
}
