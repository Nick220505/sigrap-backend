package com.sigrap.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration class that handles root URL mapping based on environment.
 *
 * <p>This configuration provides environment-specific behavior:
 * <ul>
 *   <li>In development environments (dev, local): Redirects root URL to Swagger UI</li>
 *   <li>In production environment: Forwards root URL to API status endpoint</li>
 * </ul></p>
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final Environment environment;

  /**
   * Configures view controllers for handling root URL requests.
   * The behavior changes based on the active environment profile.
   *
   * @param registry ViewControllerRegistry to be configured
   */
  @Override
  public void addViewControllers(@NonNull ViewControllerRegistry registry) {
    if (environment.acceptsProfiles(Profiles.of("dev", "local"))) {
      registry.addRedirectViewController("/", "/swagger-ui.html");
    } else {
      registry.addViewController("/").setViewName("forward:/api/status");
    }
  }
}
