package com.sigrap.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Central Jackson configuration.
 *
 * Provides a single shared ObjectMapper bean so that components which depend
 * on com.fasterxml.jackson.databind.ObjectMapper (for example audit
 * infrastructure and tests) can be autowired successfully under Spring Boot 4.
 */
@Configuration
public class JacksonConfig {

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules();
    return mapper;
  }
}
