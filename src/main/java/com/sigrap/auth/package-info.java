/**
 * Authentication and authorization package for SIGRAP.
 *
 * <p>This package handles all security-related functionality including:
 * <ul>
 *   <li>User authentication via JWT tokens</li>
 *   <li>Security filters and configurations</li>
 *   <li>Login and registration endpoints</li>
 * </ul></p>
 *
 * <p>Key components:
 * <ul>
 *   <li>{@link com.sigrap.auth.AuthService} - Core authentication service</li>
 *   <li>{@link com.sigrap.auth.JwtUtil} - JWT token generation and validation</li>
 *   <li>{@link com.sigrap.auth.JwtAuthenticationFilter} - Security filter for JWT processing</li>
 * </ul></p>
 */
package com.sigrap.auth;
