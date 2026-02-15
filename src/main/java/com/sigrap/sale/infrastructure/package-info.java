/**
 * Infrastructure layer for the Sale module.
 * Contains adapters that connect the application core to external systems.
 * 
 * <p>This layer includes:
 * <ul>
 *   <li>REST adapters (input adapters) - Controllers for HTTP endpoints</li>
 *   <li>Persistence adapters (output adapters) - JPA repositories and entities</li>
 *   <li>Configuration classes - Spring configuration for the module</li>
 * </ul>
 * 
 * <p>All dependencies point inward toward the domain and application layers.
 */
package com.sigrap.sale.infrastructure;
