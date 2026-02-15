package com.sigrap.category.infrastructure.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for the Category module.
 * 
 * <p>This configuration class serves as the central wiring point for the category module
 * following hexagonal architecture principles. It ensures proper dependency injection
 * and component scanning for all layers of the category module.
 * 
 * <p><strong>Architecture Overview:</strong>
 * <ul>
 *   <li><strong>Domain Layer</strong> ({@code com.sigrap.category.domain}):
 *       Pure business logic with no framework dependencies. Contains domain entities,
 *       value objects, and repository port interfaces.</li>
 *   <li><strong>Application Layer</strong> ({@code com.sigrap.category.application}):
 *       Use case implementations annotated with {@code @Service}. Orchestrates domain
 *       logic and manages transactions.</li>
 *   <li><strong>Infrastructure Layer</strong> ({@code com.sigrap.category.infrastructure}):
 *       Adapters for REST controllers ({@code @RestController}) and persistence
 *       ({@code @Component}). Implements ports defined in domain/application layers.</li>
 * </ul>
 * 
 * <p><strong>Component Wiring:</strong>
 * <p>All components are auto-configured through Spring annotations:
 * <ul>
 *   <li><strong>Use Cases</strong>: Annotated with {@code @Service} and {@code @Transactional}
 *       <ul>
 *         <li>{@link com.sigrap.category.application.service.CreateCategoryService}</li>
 *         <li>{@link com.sigrap.category.application.service.GetCategoryService}</li>
 *         <li>{@link com.sigrap.category.application.service.UpdateCategoryService}</li>
 *         <li>{@link com.sigrap.category.application.service.DeleteCategoryService}</li>
 *       </ul>
 *   </li>
 *   <li><strong>Adapters</strong>: Annotated with {@code @Component} or {@code @RestController}
 *       <ul>
 *         <li>{@link com.sigrap.category.infrastructure.adapter.out.persistence.CategoryPersistenceAdapter}
 *             - Implements {@link com.sigrap.category.domain.port.CategoryRepositoryPort}</li>
 *         <li>{@link com.sigrap.category.infrastructure.adapter.in.rest.CategoryController}
 *             - REST API endpoint for category operations</li>
 *       </ul>
 *   </li>
 *   <li><strong>Mappers</strong>: MapStruct mappers with {@code componentModel = "spring"}
 *       <ul>
 *         <li>{@code CategoryPersistenceMapper} - Domain ↔ JPA entity mapping</li>
 *         <li>{@code CategoryResponseMapper} - Domain ↔ REST DTO mapping</li>
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
 *   <li>Explicit {@code @ComponentScan} for documentation and clarity</li>
 * </ul>
 * 
 * <p><strong>Note:</strong> This configuration class is primarily for documentation purposes.
 * All components are auto-discovered through Spring's component scanning mechanism.
 * Manual bean definitions are only needed for special cases not covered by auto-configuration.
 * 
 * @see com.sigrap.category.domain.port.CategoryRepositoryPort
 * @see com.sigrap.category.application.port.in.CreateCategoryUseCase
 * @see com.sigrap.category.application.port.in.GetCategoryUseCase
 * @see com.sigrap.category.application.port.in.UpdateCategoryUseCase
 * @see com.sigrap.category.application.port.in.DeleteCategoryUseCase
 * @since 1.0
 */
@Configuration
public class CategoryConfig {
    
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
    public CategoryConfig() {
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
