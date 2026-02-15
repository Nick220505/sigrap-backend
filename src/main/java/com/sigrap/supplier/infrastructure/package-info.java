/**
 * Infrastructure layer for the Supplier module.
 * 
 * <p>This package contains all infrastructure concerns including:
 * <ul>
 *   <li>Input adapters (REST controllers)</li>
 *   <li>Output adapters (persistence implementations)</li>
 *   <li>Configuration classes</li>
 *   <li>Framework-specific code</li>
 * </ul>
 * 
 * <p>Following hexagonal architecture principles, this layer:
 * <ul>
 *   <li>Depends on both application and domain layers</li>
 *   <li>Implements ports defined in domain/application layers</li>
 *   <li>Contains all framework dependencies (Spring, JPA, etc.)</li>
 *   <li>Translates between external formats and domain models</li>
 * </ul>
 * 
 * @see com.sigrap.supplier.domain
 * @see com.sigrap.supplier.application
 */
package com.sigrap.supplier.infrastructure;
