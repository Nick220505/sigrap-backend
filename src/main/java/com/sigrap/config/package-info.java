/**
 * Application configuration package for SIGRAP.
 *
 * <p>This package contains all configuration classes that set up the application:
 * <ul>
 *   <li>Security configurations</li>
 *   <li>CORS settings</li>
 *   <li>Web MVC configurations</li>
 *   <li>OpenAPI/Swagger documentation setup</li>
 *   <li>Data seeding configuration</li>
 * </ul></p>
 *
 * <p>Configuration classes use Spring's {@code @Configuration} annotation and follow
 * Spring Boot's auto-configuration patterns. Many configurations are environment-aware
 * and adjust their behavior based on active profiles.</p>
 *
 * <p>Most classes in this package are loaded during application startup and provide
 * the foundational setup for the application's features and security.</p>
 */
package com.sigrap.config;
