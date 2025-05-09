/**
 * Exception handling package for SIGRAP.
 *
 * <p>This package provides centralized exception handling for the application:
 * <ul>
 *   <li>Global exception handler for REST endpoints</li>
 *   <li>Common exception types and error responses</li>
 *   <li>Error message formatting and localization</li>
 * </ul></p>
 *
 * <p>The {@link com.sigrap.exception.GlobalExceptionHandler} provides consistent
 * error handling across the application by:
 * <ul>
 *   <li>Converting exceptions to appropriate HTTP responses</li>
 *   <li>Formatting error messages for API consumers</li>
 *   <li>Handling validation errors from request bodies</li>
 *   <li>Managing security-related exceptions</li>
 * </ul></p>
 */
package com.sigrap.exception;
