package com.sigrap.config;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

class WebConfigTest {

  @Test
  void addViewControllers_shouldAddSwaggerRedirect_whenDevProfileActive() {
    Environment environment = mock(Environment.class);
    when(environment.acceptsProfiles(org.springframework.core.env.Profiles.of("dev", "local"))).thenReturn(true);

    WebConfig webConfig = new WebConfig(environment);
    ViewControllerRegistry registry = mock(ViewControllerRegistry.class);

    webConfig.addViewControllers(registry);

    verify(registry).addRedirectViewController("/", "/swagger-ui.html");
  }

  @Test
  void addViewControllers_shouldAddApiStatusForward_whenDevProfileNotActive() {
    Environment environment = mock(Environment.class);
    when(environment.acceptsProfiles(org.springframework.core.env.Profiles.of("dev", "local"))).thenReturn(false);

    WebConfig webConfig = new WebConfig(environment);
    ViewControllerRegistry registry = mock(ViewControllerRegistry.class);
    ViewControllerRegistration registration = mock(ViewControllerRegistration.class);
    when(registry.addViewController("/")).thenReturn(registration);

    webConfig.addViewControllers(registry);

    verify(registry).addViewController("/");
    verify(registration).setViewName("forward:/api/status");
  }
}