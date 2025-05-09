/**
 * Root package for SIGRAP (Sistema Integrado de Gestión y Registro de Artículos de Papelería).
 *
 * <p>SIGRAP is a comprehensive management system designed for stationery stores, providing
 * a complete solution for inventory management, sales tracking, and business operations.</p>
 *
 * <p>The system is organized into feature-based packages:
 * <ul>
 *   <li>{@link com.sigrap.auth} - Authentication and authorization</li>
 *   <li>{@link com.sigrap.category} - Product category management</li>
 *   <li>{@link com.sigrap.product} - Product inventory management</li>
 *   <li>{@link com.sigrap.user} - User account management</li>
 *   <li>{@link com.sigrap.common} - Shared utilities and components</li>
 *   <li>{@link com.sigrap.config} - Application configuration</li>
 *   <li>{@link com.sigrap.exception} - Global exception handling</li>
 * </ul></p>
 *
 * <p>Key architectural principles:
 * <ul>
 *   <li>Feature-based package organization</li>
 *   <li>Clear separation of concerns</li>
 *   <li>RESTful API design</li>
 *   <li>Secure authentication with JWT</li>
 *   <li>Comprehensive data validation</li>
 *   <li>Consistent error handling</li>
 * </ul></p>
 *
 * <p>Each feature package follows a consistent structure:
 * <ul>
 *   <li>Controllers - REST endpoints and request handling</li>
 *   <li>Services - Business logic implementation</li>
 *   <li>Repositories - Data access layer</li>
 *   <li>Models - Domain entities and DTOs</li>
 *   <li>Mappers - Object mapping between layers</li>
 * </ul></p>
 *
 * @see com.sigrap.SigrapBackendApplication
 * @version 1.0.0
 */
package com.sigrap;
