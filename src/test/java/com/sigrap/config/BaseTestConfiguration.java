package com.sigrap.config;

import com.sigrap.auth.JwtAuthenticationFilter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;

@TestConfiguration
@EnableWebSecurity
@TestPropertySource(
  properties = "spring.main.allow-bean-definition-overriding=true"
)
public class BaseTestConfiguration {

  @MockBean
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
