/**
 * Authentication and authorization package for SIGRAP.
 *
 * <p>This package follows hexagonal architecture with clear separation between:
 * <ul>
 *   <li>Domain layer - Core authentication models and ports</li>
 *   <li>Application layer - Authentication use cases</li>
 *   <li>Infrastructure layer - JWT adapters, REST controllers, and security filters</li>
 * </ul></p>
 *
 * <p>Key components:
 * <ul>
 *   <li>{@link com.sigrap.auth.application.service.AuthenticateUserService} - Core authentication service</li>
 *   <li>{@link com.sigrap.auth.infrastructure.adapter.out.jwt.JwtUtil} - JWT token generation and validation</li>
 *   <li>{@link com.sigrap.auth.infrastructure.adapter.in.security.JwtAuthenticationFilter} - Security filter for JWT processing</li>
 *   <li>{@link com.sigrap.auth.infrastructure.adapter.out.security.UserDetailsServiceAdapter} - Spring Security integration</li>
 * </ul></p>
 */
package com.sigrap.auth;
