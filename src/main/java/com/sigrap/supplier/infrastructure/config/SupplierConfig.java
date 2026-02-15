package com.sigrap.supplier.infrastructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for the Supplier module.
 * 
 * <p>This configuration class serves as the central wiring point for the supplier module
 * following hexagonal architecture principles. It ensures proper dependency injection
 * and component scanning for all layers of the supplier module.
 * 
 * <p><strong>Architecture Overview:</strong>
 * <ul>
 *   <li><strong>Domain Layer</strong> ({@code com.sigrap.supplier.domain}):
 *       Pure business logic with no framework dependencies. Contains domain entities
 *       (Supplier, PurchaseOrder), value objects, and repository port interfaces.</li>
 *   <li><strong>Application Layer</strong> ({@code com.sigrap.supplier.application}):
 *       Use case implementations annotated with {@code @Service}. Orchestrates domain
 *       logic and manages transactions.</li>
 *   <li><strong>Infrastructure Layer</strong> ({@code com.sigrap.supplier.infrastructure}):
 *       Adapters for REST controllers ({@code @RestController}) and persistence
 *       ({@code @Component}). Implements ports defined in domain/application layers.</li>
 * </ul>
 * 
 * <p><strong>Component Wiring:</strong>
 * <p>All components are auto-configured through Spring annotations:
 * <ul>
 *   <li><strong>Use Cases</strong>: Annotated with {@code @Service} and {@code @Transactional}
 *       <ul>
 *         <li>{@link com.sigrap.supplier.application.service.CreateSupplierService}</li>
 *         <li>{@link com.sigrap.supplier.application.service.GetSupplierService}</li>
 *         <li>{@link com.sigrap.supplier.application.service.UpdateSupplierService}</li>
 *         <li>{@link com.sigrap.supplier.application.service.DeleteSupplierService}</li>
 *         <li>{@link com.sigrap.supplier.application.service.CreatePurchaseOrderService}</li>
 *         <li>{@link com.sigrap.supplier.application.service.GetPurchaseOrderService}</li>
 *         <li>{@link com.sigrap.supplier.application.service.UpdatePurchaseOrderService}</li>
 *         <li>{@link com.sigrap.supplier.application.service.DeletePurchaseOrderService}</li>
 *         <li>{@link com.sigrap.supplier.application.service.ApprovePurchaseOrderService}</li>
 *         <li>{@link com.sigrap.supplier.application.service.ReceivePurchaseOrderService}</li>
 *         <li>{@link com.sigrap.supplier.application.service.CancelPurchaseOrderService}</li>
 *       </ul>
 *   </li>
 *   <li><strong>Adapters</strong>: Annotated with {@code @Component} or {@code @RestController}
 *       <ul>
 *         <li>{@link com.sigrap.supplier.infrastructure.adapter.out.persistence.SupplierPersistenceAdapter}
 *             - Implements {@link com.sigrap.supplier.domain.port.SupplierRepositoryPort}</li>
 *         <li>{@link com.sigrap.supplier.infrastructure.adapter.out.persistence.PurchaseOrderPersistenceAdapter}
 *             - Implements {@link com.sigrap.supplier.domain.port.PurchaseOrderRepositoryPort}</li>
 *         <li>{@link com.sigrap.supplier.infrastructure.adapter.in.rest.SupplierController}
 *             - REST API endpoint for supplier operations</li>
 *         <li>{@link com.sigrap.supplier.infrastructure.adapter.in.rest.PurchaseOrderController}
 *             - REST API endpoint for purchase order operations</li>
 *       </ul>
 *   </li>
 *   <li><strong>Mappers</strong>: MapStruct mappers with {@code componentModel = "spring"}
 *       <ul>
 *         <li>{@code SupplierPersistenceMapper} - Domain ↔ JPA entity mapping</li>
 *         <li>{@code PurchaseOrderPersistenceMapper} - Domain ↔ JPA entity mapping</li>
 *         <li>{@code SupplierResponseMapper} - Domain ↔ REST DTO mapping</li>
 *         <li>{@code PurchaseOrderResponseMapper} - Domain ↔ REST DTO mapping</li>
 *       </ul>
 *   </li>
 * </ul>
 * 
 * <p><strong>Dependency Flow:</strong>
 * <pre>
 * REST Controller (Input Adapter)
 *         ↓
 *   Use Case (Application Service)
 *         ↓
 *   Domain Entity & Business Logic
 *         ↓
 *   Repository Port (Interface)
 *         ↓
 *   Persistence Adapter (Output Adapter)
 *         ↓
 *   JPA Repository & Database
 * </pre>
 * 
 * <p><strong>Spring Boot 4.0.2 Best Practices:</strong>
 * <ul>
 *   <li>Constructor-based dependency injection (no {@code @Autowired} needed)</li>
 *   <li>Component scanning enabled by default through {@code @SpringBootApplication}</li>
 *   <li>Transaction management at use case level with {@code @Transactional}</li>
 *   <li>Explicit {@code @Configuration} for documentation and clarity</li>
 * </ul>
 * 
 * <p><strong>Note:</strong> This configuration class is primarily for documentation purposes.
 * All components are auto-discovered through Spring's component scanning mechanism.
 * Manual bean definitions are only needed for special cases not covered by auto-configuration.
 * 
 * @see com.sigrap.supplier.domain.port.SupplierRepositoryPort
 * @see com.sigrap.supplier.domain.port.PurchaseOrderRepositoryPort
 * @see com.sigrap.supplier.application.port.in.CreateSupplierUseCase
 * @see com.sigrap.supplier.application.port.in.GetSupplierUseCase
 * @see com.sigrap.supplier.application.port.in.UpdateSupplierUseCase
 * @see com.sigrap.supplier.application.port.in.DeleteSupplierUseCase
 * @see com.sigrap.supplier.application.port.in.CreatePurchaseOrderUseCase
 * @see com.sigrap.supplier.application.port.in.GetPurchaseOrderUseCase
 * @see com.sigrap.supplier.application.port.in.UpdatePurchaseOrderUseCase
 * @see com.sigrap.supplier.application.port.in.DeletePurchaseOrderUseCase
 * @see com.sigrap.supplier.application.port.in.ApprovePurchaseOrderUseCase
 * @see com.sigrap.supplier.application.port.in.ReceivePurchaseOrderUseCase
 * @see com.sigrap.supplier.application.port.in.CancelPurchaseOrderUseCase
 * @since 1.0
 */
@Configuration
public class SupplierConfig {
    
    /**
     * Default constructor.
     * 
     * <p>No explicit bean definitions are required as all components use
     * Spring's auto-configuration through annotations:
     * <ul>
     *   <li>{@code @Service} for use case implementations</li>
     *   <li>{@code @Component} for adapters</li>
     *   <li>{@code @RestController} for REST endpoints</li>
     *   <li>{@code @Mapper(componentModel = "spring")} for MapStruct mappers</li>
     * </ul>
     * 
     * <p>The hexagonal architecture is maintained through:
     * <ul>
     *   <li>Port interfaces defined in domain/application layers</li>
     *   <li>Adapter implementations in infrastructure layer</li>
     *   <li>Dependency inversion - all dependencies point inward</li>
     * </ul>
     */
    public SupplierConfig() {
        // Auto-configuration handles all component wiring
        // This constructor is intentionally empty
    }
    
    // Future bean definitions can be added here if needed
    // For example:
    // - Custom validators
    // - Event publishers
    // - External service clients
    // - Module-specific configurations
}
