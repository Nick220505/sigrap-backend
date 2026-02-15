/**
 * REST input adapters for the Supplier module.
 * 
 * <p>This package contains REST controllers and DTOs for HTTP communication:
 * <ul>
 *   <li>{@link com.sigrap.supplier.infrastructure.adapter.in.rest.SupplierController} - 
 *       REST API for supplier operations</li>
 *   <li>{@link com.sigrap.supplier.infrastructure.adapter.in.rest.PurchaseOrderController} - 
 *       REST API for purchase order operations</li>
 *   <li>Request records for input validation</li>
 *   <li>Response records for output formatting</li>
 *   <li>Response mappers for domain to DTO conversion</li>
 * </ul>
 * 
 * <p>Controllers follow REST best practices:
 * <ul>
 *   <li>Proper HTTP methods (GET, POST, PUT, DELETE)</li>
 *   <li>Appropriate status codes (200, 201, 204, 404, etc.)</li>
 *   <li>Input validation using Bean Validation</li>
 *   <li>Clean separation from business logic</li>
 * </ul>
 */
package com.sigrap.supplier.infrastructure.adapter.in.rest;
