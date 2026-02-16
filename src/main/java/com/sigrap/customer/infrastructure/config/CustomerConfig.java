package com.sigrap.customer.infrastructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for the Customer module.
 * 
 * <p>This configuration class serves as the central wiring point for the customer module
 * following hexagonal architecture principles. It ensures proper dependency injection
 * and component scanning for all layers of the customer module.
 * 
 * <p><strong>Architecture Overview:</strong>
 * <ul>
 *   <li><strong>Domain Layer</strong> ({@code com.sigrap.customer.domain}):
 *       Pure business logic with no framework dependencies. Contains domain entities,
 *       value objects, and repository port interfaces.</li>
 *   <li><strong>Application Layer</strong> ({@code com.sigrap.customer.application}):
 *       Use case implementations annotated with {@code @Service}. Orchestrates domain
 *       logic and manages transactions.</li>
 *   <li><strong>Infrastructure Layer</strong> ({@code com.sigrap.customer.infrastructure}):
 *       Adapters for REST controllers ({@code @RestController}) and persistence
 *       ({@code @Component}). Implements ports defined in domain/application layers.</li>
 * </ul>
 * 
 * <p><strong>Component Wiring:</strong>
 * <p>All components are auto-configured through Spring annotations:
 * <ul>
 *   <li><strong>Use Cases</strong>: Annotated with {@code @Service} and {@code @Transactional}
 *       <ul>
 *         <li>{@link com.sigrap.customer.application.service.CreateCustomerService}</li>
 *         <li>{@link com.sigrap.customer.application.service.GetCustomerService}</li>
 *         <li>{@link com.sigrap.customer.application.service.UpdateCustomerService}</li>
 *         <li>{@link com.sigrap.customer.application.service.DeleteCustomerService}</li>
 *       </ul>
 *   </li>
 *   <li><strong>Adapters</strong>: Annotated with {@code @Component} or {@code @RestController}
 *       <ul>
 *         <li>{@link com.sigrap.customer.infrastructure.adapter.out.persistence.CustomerPersistenceAdapter}
 *             - Implements {@link com.sigrap.customer.domain.port.CustomerRepositoryPort}</li>
 *         <li>{@link com.sigrap.customer.infrastructure.adapter.in.rest.CustomerController}
 *             - REST API endpoint for customer operations (mapped to /api/customers)</li>
 *       </ul>
 *   </li>
 *   <li><strong>Mappers</strong>: MapStruct mappers with {@code componentModel = "spring"}
 *       <ul>
 *         <li>{@code CustomerPersistenceMapper} - Domain ↔ JPA entity mapping</li>
 *         <li>{@code CustomerResponseMapper} - Domain ↔ REST DTO mapping</li>
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
 * @see com.sigrap.customer.domain.port.CustomerRepositoryPort
 * @see com.sigrap.customer.application.port.in.CreateCustomerUseCase
 * @see com.sigrap.customer.application.port.in.GetCustomerUseCase
 * @see com.sigrap.customer.application.port.in.UpdateCustomerUseCase
 * @see com.sigrap.customer.application.port.in.DeleteCustomerUseCase
 * @since 1.0
 */
@Configuration
public class CustomerConfig {
    
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
    public CustomerConfig() {
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
