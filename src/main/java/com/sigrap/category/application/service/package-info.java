/**
 * Application services for Category use case implementations.
 * <p>
 * This package contains service classes that implement the use case interfaces
 * defined in the application.port.in package.
 * </p>
 * <p>
 * Application service characteristics:
 * <ul>
 *   <li>Implement input port interfaces</li>
 *   <li>Orchestrate domain logic</li>
 *   <li>Use output ports for external dependencies</li>
 *   <li>Manage transactions (typically with @Transactional)</li>
 *   <li>Enforce security (with @PreAuthorize if needed)</li>
 *   <li>Thin layer - delegate to domain for business logic</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
package com.sigrap.category.application.service;
