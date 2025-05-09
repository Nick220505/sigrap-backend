package com.sigrap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

  @Bean
  OpenAPI openAPI() {
    return new OpenAPI()
      .info(
        new Info()
          .title("SIGRAP API")
          .description(
            "SIGRAP is a comprehensive management system designed to streamline operations" +
            " for stationery stores. This API provides endpoints for inventory and" +
            " product management."
          )
          .version("v1.0.0")
          .license(
            new License()
              .name("MIT License")
              .url("https://opensource.org/licenses/MIT")
          )
      )
      .addServersItem(new Server().url("/").description("Default Server URL"));
  }
}
