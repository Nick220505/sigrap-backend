package com.sigrap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Configuration class for OpenAPI/Swagger documentation.
 * Sets up the API documentation interface with proper metadata, security schemes, and server information.
 *
 * <p>This configuration provides:
 * <ul>
 *   <li>API title and description</li>
 *   <li>Version information</li>
 *   <li>License details</li>
 *   <li>Contact information</li>
 *   <li>JWT Bearer token authentication scheme</li>
 *   <li>Server configuration</li>
 * </ul></p>
 *
 * <p>The Swagger UI can be accessed at /swagger-ui.html when the application is running.</p>
 * <p>The OpenAPI JSON specification is available at /v3/api-docs</p>
 */
@Configuration
public class OpenApiConfig {

  /**
   * Creates and configures the OpenAPI documentation bean.
   * Includes JWT Bearer token authentication configuration for secured endpoints.
   *
   * @return Configured OpenAPI instance with complete API metadata and security schemes
   */
  @Bean
  OpenAPI openAPI() {
    return new OpenAPI()
      .info(
        new Info()
          .title("SIGRAP API")
          .description(
            "SIGRAP is a comprehensive management system designed to streamline operations " +
            "for stationery stores. This API provides endpoints for inventory management, " +
            "product management, sales tracking, customer management, supplier management, " +
            "user management, and employee attendance tracking.\n\n" +
            "## Authentication\n" +
            "Most endpoints require JWT Bearer token authentication. " +
            "Obtain a token by calling the `/api/auth/login` endpoint with valid credentials. " +
            "Include the token in the Authorization header: `Bearer <token>`"
          )
          .version("v1.0.0")
          .contact(
            new Contact()
              .name("SIGRAP Development Team")
              .email("dev@sigrap.com")
          )
          .license(
            new License()
              .name("MIT License")
              .url("https://opensource.org/licenses/MIT")
          )
      )
      .addServersItem(
        new Server()
          .url("/")
          .description("Default Server URL")
      )
      .components(
        new Components()
          .addSecuritySchemes("bearer-jwt",
            new SecurityScheme()
              .type(SecurityScheme.Type.HTTP)
              .scheme("bearer")
              .bearerFormat("JWT")
              .description("JWT authentication token. Obtain from /api/auth/login endpoint.")
          )
      )
      .addSecurityItem(
        new SecurityRequirement().addList("bearer-jwt")
      );
  }
}
