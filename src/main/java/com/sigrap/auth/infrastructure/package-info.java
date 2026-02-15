/**
 * Infrastructure layer for the auth module.
 * Contains adapters that connect the application layer to external systems and frameworks.
 * 
 * <p>This layer includes:
 * <ul>
 *   <li>REST controllers (input adapters) for HTTP endpoints</li>
 *   <li>JWT adapters (output adapters) for token operations</li>
 *   <li>Security adapters (output adapters) for password encoding</li>
 *   <li>Persistence adapters (output adapters) for user data access</li>
 *   <li>Configuration classes for Spring wiring</li>
 * </ul>
 * 
 * <p>This is the outermost layer in hexagonal architecture and depends on both
 * the domain and application layers.
 */
package com.sigrap.auth.infrastructure;
