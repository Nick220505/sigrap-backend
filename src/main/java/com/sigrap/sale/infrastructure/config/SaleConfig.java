package com.sigrap.sale.infrastructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for the Sale module.
 * 
 * <p>This configuration class serves as the central wiring point for the sale module
 * following hexagonal architecture principles. It ensures proper dependency injection
 * and component scanning for all layers of the sale module.
 * 
 * <p><strong>Architecture Overview:</strong>
 * <ul>
 *   <li><strong>Domain Layer</strong> ({@code com.sigrap.sale.domain}):
 *       Pure business logic with no framework dependencies. Contains domain entities
 *       (Sale, SaleItem, SaleReturn), value objects, and repository port interfaces.</li>
 *   <li><strong>Application Layer</strong> ({@code com.sigrap.sale.application}):
 *       Use case implementations annotated with {@code @Service}. Orchestrates domain
 *       logic and manages transactions.</li>
 *   <li><strong>Infrastructure Layer</strong> ({@code com.sigrap.sale.infrastructure}):
 *       Adapters for REST controllers ({@code @RestController}) and persistence
 *       ({@code @Component}). Implements ports defined in domain/application layers.</li>
 * </ul>
 * 
 * <p><strong>Component Wiring:</strong>
 * <p>All components are auto-configured through Spring annotations:
 * <ul>
 *   <li><strong>Use Cases</strong>: Annotated with {@code @Service} and {@code @Transactional}
 *       <ul>
 *         <li>{@link com.sigrap.sale.application.service.CreateSaleService}</li>
 *         <li>{@link com.sigrap.sale.application.service.GetSaleService}</li>
 *         <li>{@link com.sigrap.sale.application.service.UpdateSaleService}</li>
 *         <li>{@link com.sigrap.sale.application.service.DeleteSaleService}</li>
 *         <li>{@link com.sigrap.sale.application.service.CompleteSaleService}</li>
 *         <li>{@link com.sigrap.sale.application.service.CancelSaleService}</li>
 *         <li>{@link com.sigrap.sale.application.service.AddSaleItemService}</li>
 *         <li>{@link com.sigrap.sale.application.service.UpdateSaleItemService}</li>
 *         <li>{@link com.sigrap.sale.application.service.RemoveSaleItemService}</li>
 *         <li>{@link com.sigrap.sale.application.service.CreateSaleReturnService}</li>
 *         <li>{@link com.sigrap.sale.application.service.GetSaleReturnService}</li>
 *         <li>{@link com.sigrap.sale.application.service.ApproveSaleReturnService}</li>
 *         <li>{@link com.sigrap.sale.application.service.RejectSaleReturnService}</li>
 *         <li>{@link com.sigrap.sale.application.service.CompleteSaleReturnService}</li>
 *       </ul>
 *   </li>
 *   <li><strong>Adapters</strong>: Annotated with {@code @Component} or {@code @RestController}
 *       <ul>
 *         <li>{@link com.sigrap.sale.infrastructure.adapter.out.persistence.SalePersistenceAdapter}
 *             - Implements {@link com.sigrap.sale.domain.port.SaleRepositoryPort}</li>
 *         <li>{@link com.sigrap.sale.infrastructure.adapter.out.persistence.SaleItemPersistenceAdapter}
 *             - Implements {@link com.sigrap.sale.domain.port.SaleItemRepositoryPort}</li>
 *         <li>{@link com.sigrap.sale.infrastructure.adapter.out.persistence.SaleReturnPersistenceAdapter}
 *             - Implements {@link com.sigrap.sale.domain.port.SaleReturnRepositoryPort}</li>
 *         <li>{@link com.sigrap.sale.infrastructure.adapter.in.rest.SaleController}
 *             - REST API endpoint for sale operations (mapped to /api/v2/sales)</li>
 *         <li>{@link com.sigrap.sale.infrastructure.adapter.in.rest.SaleReturnController}
 *             - REST API endpoint for sale return operations (mapped to /api/v2/sale-returns)</li>
 *       </ul>
 *   </li>
 *   <li><strong>Mappers</strong>: MapStruct mappers with {@code componentModel = "spring"}
 *       <ul>
 *         <li>{@code SalePersistenceMapper} - Domain ↔ JPA entity mapping for Sale</li>
 *         <li>{@code SaleItemPersistenceMapper} - Domain ↔ JPA entity mapping for SaleItem</li>
 *         <li>{@code SaleReturnPersistenceMapper} - Domain ↔ JPA entity mapping for SaleReturn</li>
 *         <li>{@code SaleResponseMapper} - Domain ↔ REST DTO mapping for Sale</li>
 *         <li>{@code SaleReturnResponseMapper} - Domain ↔ REST DTO mapping for SaleReturn</li>
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
 * @see com.sigrap.sale.domain.port.SaleRepositoryPort
 * @see com.sigrap.sale.domain.port.SaleItemRepositoryPort
 * @see com.sigrap.sale.domain.port.SaleReturnRepositoryPort
 * @see com.sigrap.sale.application.port.in.CreateSaleUseCase
 * @see com.sigrap.sale.application.port.in.GetSaleUseCase
 * @see com.sigrap.sale.application.port.in.UpdateSaleUseCase
 * @see com.sigrap.sale.application.port.in.DeleteSaleUseCase
 * @see com.sigrap.sale.application.port.in.CompleteSaleUseCase
 * @see com.sigrap.sale.application.port.in.CancelSaleUseCase
 * @see com.sigrap.sale.application.port.in.CreateSaleReturnUseCase
 * @see com.sigrap.sale.application.port.in.GetSaleReturnUseCase
 * @see com.sigrap.sale.application.port.in.ApproveSaleReturnUseCase
 * @see com.sigrap.sale.application.port.in.RejectSaleReturnUseCase
 * @see com.sigrap.sale.application.port.in.CompleteSaleReturnUseCase
 * @since 1.0
 */
@Configuration
public class SaleConfig {
    
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
    public SaleConfig() {
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
