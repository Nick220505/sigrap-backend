package com.sigrap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuration class for Cross-Origin Resource Sharing (CORS) settings.
 * Provides environment-aware CORS configuration for the application.
 *
 * <p>In production, only allows requests from verified Vercel deployments.
 * In development, allows requests from the local Angular development server.</p>
 *
 * <p>This configuration is essential for security and proper functioning of
 * the frontend-backend communication in both development and production
 * environments.</p>
 */
@Configuration
public class CorsConfig {

  /**
   * Creates and configures the CORS configuration source bean.
   *
   * @param environment Spring environment to determine the active profile
   * @return Configured CorsConfigurationSource with appropriate settings
   */
  @Bean
  CorsConfigurationSource corsConfigurationSource(Environment environment) {
    CorsConfiguration config = new CorsConfiguration();

    if (environment.acceptsProfiles(Profiles.of("prod"))) {
      config.addAllowedOriginPattern("https://*sigrap*.vercel.app");
    } else {
      config.addAllowedOrigin("http://localhost:4200");
    }

    config.setAllowCredentials(true);
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    config.addExposedHeader("Content-Disposition");

    UrlBasedCorsConfigurationSource source =
      new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
