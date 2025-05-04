package com.sigrap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.sigrap.auth.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final Environment environment;

  private static final String[] PUBLIC_ENDPOINTS = {
      "/",
      "/api/auth/**",
      "/h2-console/**",
      "/actuator/**",
      "/api/status",
      "/error"
  };

  private static final String[] SWAGGER_WHITELIST = {
      "/swagger-ui.html",
      "/swagger-ui/**",
      "/api-docs/**",
      "/api-docs",
      "/v3/api-docs/**",
      "/swagger-resources/**",
      "/webjars/**"
  };

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
    return http
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authz -> {
          authz.requestMatchers(PUBLIC_ENDPOINTS).permitAll();
          authz.requestMatchers(SWAGGER_WHITELIST).permitAll();

          if (environment.acceptsProfiles(Profiles.of("dev", "local"))) {
            authz.requestMatchers("/api/**").permitAll();
          } else {
            authz.requestMatchers("/api/**").authenticated();
          }

          authz.anyRequest().authenticated();
        })
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .headers(headers -> headers
            .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
            .contentSecurityPolicy(csp -> csp.policyDirectives("frame-ancestors 'self'")))
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
}