package com.sigrap.config;

import com.sigrap.auth.infrastructure.adapter.in.security.JwtAuthenticationFilter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@TestConfiguration
@EnableWebSecurity
@TestPropertySource(
  properties = "spring.main.allow-bean-definition-overriding=true"
)
public class BaseTestConfiguration {

  @MockitoBean
  private JwtAuthenticationFilter jwtAuthFilter;

  @Bean
  @Primary
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .securityMatcher("/**")
      .csrf(AbstractHttpConfigurer::disable)
      .sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .authorizeHttpRequests(auth -> auth.requestMatchers("/**").permitAll());
    return http.build();
  }
}
