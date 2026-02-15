/**
 * Output ports (driven ports) for Category external dependencies.
 * <p>
 * This package contains interfaces that define services the application needs
 * from external systems (e.g., persistence, external APIs, messaging).
 * </p>
 * <p>
 * Output port characteristics:
 * <ul>
 *   <li>Define what the application needs from external systems</li>
 *   <li>Implemented by infrastructure adapters</li>
 *   <li>Enable dependency inversion</li>
 *   <li>Facilitate testing with mocks/stubs</li>
 * </ul>
 * </p>
 * <p>
 * Note: Repository ports are defined in the domain layer (domain.port package)
 * as they are core to the domain model. This package is for additional
 * application-specific output ports if needed.
 * </p>
 *
 * @since 1.0
 */
package com.sigrap.category.application.port.out;
