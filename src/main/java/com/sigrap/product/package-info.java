/**
 * Product management package for SIGRAP.
 *
 * <p>This package contains all functionality related to product management including:
 * <ul>
 *   <li>Product CRUD operations</li>
 *   <li>Product inventory tracking</li>
 *   <li>Price management</li>
 * </ul></p>
 *
 * <p>The package uses a layered architecture:
 * <ul>
 *   <li>Controllers - REST endpoints for product operations</li>
 *   <li>Services - Business logic and validation</li>
 *   <li>Repositories - Data access layer</li>
 *   <li>Models - Domain entities and DTOs</li>
 * </ul></p>
 *
 * <p>Products are always associated with a {@link com.sigrap.category.Category}
 * for proper organization and filtering.</p>
 */
package com.sigrap.product;
