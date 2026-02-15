/**
 * Persistence output adapters for the Supplier module.
 * 
 * <p>This package contains JPA-based persistence implementations:
 * <ul>
 *   <li><strong>JPA Entities</strong>: Database table mappings with JPA annotations
 *       <ul>
 *         <li>{@link com.sigrap.supplier.infrastructure.adapter.out.persistence.SupplierJpaEntity}</li>
 *         <li>{@link com.sigrap.supplier.infrastructure.adapter.out.persistence.PurchaseOrderJpaEntity}</li>
 *       </ul>
 *   </li>
 *   <li><strong>JPA Repositories</strong>: Spring Data JPA repository interfaces
 *       <ul>
 *         <li>{@link com.sigrap.supplier.infrastructure.adapter.out.persistence.SupplierJpaRepository}</li>
 *         <li>{@link com.sigrap.supplier.infrastructure.adapter.out.persistence.PurchaseOrderJpaRepository}</li>
 *       </ul>
 *   </li>
 *   <li><strong>Persistence Adapters</strong>: Implementations of repository ports
 *       <ul>
 *         <li>{@link com.sigrap.supplier.infrastructure.adapter.out.persistence.SupplierPersistenceAdapter}</li>
 *         <li>{@link com.sigrap.supplier.infrastructure.adapter.out.persistence.PurchaseOrderPersistenceAdapter}</li>
 *       </ul>
 *   </li>
 *   <li><strong>Persistence Mappers</strong>: MapStruct mappers for domain ↔ JPA entity conversion
 *       <ul>
 *         <li>{@link com.sigrap.supplier.infrastructure.adapter.out.persistence.SupplierPersistenceMapper}</li>
 *         <li>{@link com.sigrap.supplier.infrastructure.adapter.out.persistence.PurchaseOrderPersistenceMapper}</li>
 *       </ul>
 *   </li>
 * </ul>
 * 
 * <p>Key principles:
 * <ul>
 *   <li>JPA entities are separate from domain entities</li>
 *   <li>Mappers handle the translation between the two</li>
 *   <li>Persistence adapters implement domain repository ports</li>
 *   <li>No business logic in this layer</li>
 * </ul>
 */
package com.sigrap.supplier.infrastructure.adapter.out.persistence;
