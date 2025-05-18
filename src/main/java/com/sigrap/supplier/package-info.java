/**
 * Supplier management package for SIGRAP.
 *
 * <p>This package contains all functionality related to supplier management including:
 * <ul>
 *   <li>Supplier CRUD operations</li>
 *   <li>Supplier catalog management</li>
 *   <li>Order processing for suppliers</li>
 *   <li>Payment tracking</li>
 * </ul></p>
 *
 * <p>The package uses a layered architecture:
 * <ul>
 *   <li>Controllers - REST endpoints for supplier operations</li>
 *   <li>Services - Business logic and validation</li>
 *   <li>Repositories - Data access layer</li>
 *   <li>Models - Domain entities and DTOs</li>
 * </ul></p>
 *
 * <p>Suppliers are a critical part of the inventory management system,
 * providing products that are then tracked in the inventory system.</p>
 *
 * <p>Key Features:
 * <ul>
 *   <li>Supplier registration and maintenance</li>
 *   <li>Supplier status tracking</li>
 *   <li>Contact information management</li>
 *   <li>Payment method management</li>
 *   <li>Supplier categorization</li>
 *   <li>Purchase order creation, status management, and itemization</li>
 * </ul></p>
 *
 * <p>Core Components:
 * <ul>
 *   <li>{@link com.sigrap.supplier.Supplier} - Entity representing supplier data</li>
 *   <li>{@link com.sigrap.supplier.SupplierController} - REST API endpoints for suppliers</li>
 *   <li>{@link com.sigrap.supplier.SupplierService} - Business logic for suppliers</li>
 *   <li>{@link com.sigrap.supplier.SupplierRepository} - Data access for suppliers</li>
 *   <li>{@link com.sigrap.supplier.SupplierMapper} - DTO/Entity conversion for suppliers</li>
 *   <li>{@link com.sigrap.supplier.SupplierData} - Input DTO for suppliers</li>
 *   <li>{@link com.sigrap.supplier.SupplierInfo} - Output DTO for suppliers</li>
 *   <li>{@link com.sigrap.supplier.PurchaseOrder} - Entity for purchase orders</li>
 *   <li>{@link com.sigrap.supplier.PurchaseOrderController} - REST API for purchase orders</li>
 *   <li>{@link com.sigrap.supplier.PurchaseOrderService} - Business logic for purchase orders</li>
 *   <li>{@link com.sigrap.supplier.PurchaseOrderRepository} - Data access for purchase orders</li>
 *   <li>{@link com.sigrap.supplier.PurchaseOrderMapper} - DTO/Entity conversion for orders</li>
 *   <li>{@link com.sigrap.supplier.PurchaseOrderData} - Input DTO for orders</li>
 *   <li>{@link com.sigrap.supplier.PurchaseOrderInfo} - Output DTO for orders</li>
 *   <li>{@link com.sigrap.supplier.PurchaseOrderItem} - Entity for items in an order</li>
 * </ul></p>
 *
 * <p>Domain-specific Enumerations:
 * <ul>
 *   <li>{@link com.sigrap.supplier.SupplierStatus} - Possible supplier relationship statuses</li>
 *   <li>{@link com.sigrap.supplier.PaymentMethod} - Supported payment methods</li>
 *   <li>{@link com.sigrap.supplier.PurchaseOrder.Status} - Statuses for purchase orders</li>
 *   <li>{@link com.sigrap.supplier.PurchaseOrderItem.Status} - Statuses for items in an order</li>
 * </ul></p>
 *
 * <p>This package follows the feature-based architecture pattern, where all components
 * related to the supplier feature are contained within this package, rather than being
 * separated by technical layers.</p>
 *
 * <p>Usage example for supplier creation:
 * <pre>
 * // Create supplier data
 * SupplierData supplierData = SupplierData.builder()
 *     .name("Office Depot")
 *     .email("contact@officedepot.com")
 *     .phone("123-456-7890")
 *     .address("123 Main St, City")
 *     .paymentMethod(PaymentMethod.CREDIT_CARD)
 *     .status(SupplierStatus.ACTIVE)
 *     .build();
 *
 * // Call supplier service to create the supplier
 * SupplierInfo createdSupplier = supplierService.create(supplierData);
 * </pre></p>
 *
 * @since 1.0
 */
package com.sigrap.supplier;
