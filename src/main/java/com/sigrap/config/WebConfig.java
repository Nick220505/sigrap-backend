package com.sigrap.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final Environment environment;

  @Override
  public void addViewControllers(@NonNull ViewControllerRegistry registry) {
    if (environment.acceptsProfiles(Profiles.of("dev", "test", "default"))) {
      registry.addRedirectViewController("/", "/swagger-ui.html");
    } else {
      registry.addViewController("/").setViewName("forward:/api/status");
    }
  }
}