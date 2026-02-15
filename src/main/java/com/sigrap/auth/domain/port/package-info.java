/**
 * Port interfaces for the authentication domain.
 * Defines the contracts for external services needed by the authentication domain.
 * These interfaces are implemented by infrastructure adapters.
 *
 * <p>Ports include:
 * <ul>
 *   <li>TokenGeneratorPort - for generating JWT tokens</li>
 *   <li>TokenValidatorPort - for validating JWT tokens</li>
 *   <li>PasswordEncoderPort - for encoding and validating passwords</li>
 *   <li>UserAuthenticationPort - for user authentication operations</li>
 * </ul></p>
 */
package com.sigrap.auth.domain.port;
