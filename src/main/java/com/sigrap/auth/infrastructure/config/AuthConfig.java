package com.sigrap.auth.infrastructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the auth module.
 * 
 * <p>This configuration class is currently minimal as most components are auto-configured
 * via Spring's component scanning (@Service, @Component annotations).
 * 
 * <p>Components that are auto-configured:
 * <ul>
 *   <li>Use case implementations (@Service)</li>
 *   <li>Adapters (@Component)</li>
 *   <li>MapStruct mappers (@Mapper with componentModel = "spring")</li>
 * </ul>
 * 
 * <p>This class can be extended in the future if manual bean configuration is needed.
 */
@Configuration
public class AuthConfig {
  // All components are auto-configured via annotations
  // This class is a placeholder for future manual configuration if needed
}
