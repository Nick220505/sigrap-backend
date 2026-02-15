/**
 * Domain ports package for Category repository interfaces.
 * <p>
 * This package contains output ports (interfaces) that define how the domain
 * layer communicates with external systems, particularly persistence.
 * </p>
 * <p>
 * Port characteristics:
 * <ul>
 *   <li>Defined in the domain layer</li>
 *   <li>Express domain needs, not database operations</li>
 *   <li>Return domain entities, not persistence models</li>
 *   <li>No framework dependencies</li>
 *   <li>Implemented by infrastructure adapters</li>
 * </ul>
 * </p>
 * <p>
 * This follows the Dependency Inversion Principle - the domain defines
 * what it needs, and infrastructure provides implementations.
 * </p>
 *
 * @since 1.0
 */
package com.sigrap.category.domain.port;
