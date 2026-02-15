package com.sigrap.product.infrastructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for the Product module.
 * 
 * <p>This configuration class serves as the central wiring point for the product module
 * following hexagonal architecture principles. It ensures proper dependency injection
 * and component scanning for all layers of the product module.
 * 
 * <p><strong>Architecture Overview:</strong>
 * <ul>
 *   <li><strong>Domain Layer</strong> ({@code com.sigrap.product.domain}):
 *       Pure business logic with no framework dependencies. Contains domain entities,
 *       value objects, and repository port interfaces.</li>
 *   <li><strong>Application Layer</strong> ({@code com.sigrap.product.application}):
 *       Use case implementations annotated with {@code @Service}. Orchestrates domain
 *       logic and manages transactions.</li>
 *   <li><strong>Infrastructure Layer</strong> ({@code com.sigrap.product.infrastructure}):
 *       Adapters for REST controllers ({@code @RestController}) and persistence
 *       ({@code @Component}). Implements ports defined in domain/application layers.</li>
 * </ul>
 * 
 * <p><strong>Component Wiring:</strong>
 * <p>All components are auto-configured through Spring annotations:
 * <ul>
 *   <li><strong>Use Cases</strong>: Annotated with {@code @Service} and {@code @Transactional}
 *       <ul>
 *         <li>{@link com.sigrap.product.application.service.CreateProductService}</li>
 *         <li>{@link com.sigrap.product.application.service.GetProductService}</li>
 *         <li>{@link com.sigrap.product.application.service.UpdateProductService}</li>
 *         <li>{@link com.sigrap.product.application.service.DeleteProductService}</li>
 *       </ul>
 *   </li>
 *   <li><strong>Adapters</strong>: Annotated with {@code @Component} or {@code @RestController}
 *       <ul>
 *         <li>{@link com.sigrap.product.infrastructure.adapter.out.persistence.ProductPersistenceAdapter}
 *             - Implements {@link com.sigrap.product.domain.port.ProductRepositoryPort}</li>
 *         <li>{@link com.sigrap.product.infrastructure.adapter.in.rest.ProductController}
 *             - REST API endpoint for product operations</li>
 *       </ul>
 *   </li>
 *   <li><strong>Mappers</strong>: MapStruct mappers with {@code componentModel = "spring"}
 *       <ul>
 *         <li>{@code ProductPersistenceMapper} - Domain ↔ JPA entity mapping</li>
 *         <li>{@code ProductResponseMapper} - Domain ↔ REST DTO mapping</li>
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
 * @see com.sigrap.product.domain.port.ProductRepositoryPort
 * @see com.sigrap.product.application.port.in.CreateProductUseCase
 * @see com.sigrap.product.application.port.in.GetProductUseCase
 * @see com.sigrap.product.application.port.in.UpdateProductUseCase
 * @see com.sigrap.product.application.port.in.DeleteProductUseCase
 * @since 1.0
 */
@Configuration
public class ProductConfig {
    
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
    public ProductConfig() {
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
