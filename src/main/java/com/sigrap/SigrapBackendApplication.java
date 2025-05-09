package com.sigrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for SIGRAP (Sistema Integrado de Gestión y Registro de Artículos de Papelería).
 *
 * <p>SIGRAP is a comprehensive management system for stationery stores, providing:
 * <ul>
 *   <li>Inventory Management - Track products, stock levels, and categories</li>
 *   <li>User Management - Handle employee accounts and access control</li>
 *   <li>Security - JWT-based authentication and authorization</li>
 *   <li>API Documentation - OpenAPI/Swagger integration</li>
 * </ul></p>
 *
 * <p>Key Features:
 * <ul>
 *   <li>RESTful API architecture</li>
 *   <li>Secure authentication with JWT</li>
 *   <li>Comprehensive exception handling</li>
 *   <li>Data validation and sanitization</li>
 *   <li>Audit logging for critical operations</li>
 * </ul></p>
 *
 * <p>Technology Stack:
 * <ul>
 *   <li>Spring Boot 3.x - Application framework</li>
 *   <li>Spring Security - Authentication and authorization</li>
 *   <li>Spring Data JPA - Data persistence</li>
 *   <li>H2 Database - Development database</li>
 *   <li>Lombok - Boilerplate reduction</li>
 *   <li>MapStruct - Object mapping</li>
 * </ul></p>
 *
 * <p>For API documentation, visit /swagger-ui.html when the application is running.</p>
 *
 * @see com.sigrap.config.SecurityConfig
 * @see com.sigrap.config.OpenApiConfig
 * @see com.sigrap.auth.AuthController
 */
@SpringBootApplication
public class SigrapBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(SigrapBackendApplication.class, args);
  }
}
