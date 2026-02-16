package com.sigrap.employee.infrastructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for the Employee module.
 * 
 * <p>This configuration class serves as the central wiring point for the employee module
 * following hexagonal architecture principles. It ensures proper dependency injection
 * and component scanning for all layers of the employee module.
 * 
 * <p><strong>Architecture Overview:</strong>
 * <ul>
 *   <li><strong>Domain Layer</strong> ({@code com.sigrap.employee.domain}):
 *       Pure business logic with no framework dependencies. Contains domain entities
 *       (Attendance, Schedule), value objects, and repository port interfaces.</li>
 *   <li><strong>Application Layer</strong> ({@code com.sigrap.employee.application}):
 *       Use case implementations annotated with {@code @Service}. Orchestrates domain
 *       logic and manages transactions.</li>
 *   <li><strong>Infrastructure Layer</strong> ({@code com.sigrap.employee.infrastructure}):
 *       Adapters for REST controllers ({@code @RestController}) and persistence
 *       ({@code @Component}). Implements ports defined in domain/application layers.</li>
 * </ul>
 * 
 * <p><strong>Component Wiring:</strong>
 * <p>All components are auto-configured through Spring annotations:
 * <ul>
 *   <li><strong>Use Cases</strong>: Annotated with {@code @Service} and {@code @Transactional}
 *       <ul>
 *         <li>{@link com.sigrap.employee.application.service.ClockInService}</li>
 *         <li>{@link com.sigrap.employee.application.service.ClockOutService}</li>
 *         <li>{@link com.sigrap.employee.application.service.GetAttendanceService}</li>
 *         <li>{@link com.sigrap.employee.application.service.CreateScheduleService}</li>
 *         <li>{@link com.sigrap.employee.application.service.GetScheduleService}</li>
 *         <li>{@link com.sigrap.employee.application.service.UpdateScheduleService}</li>
 *         <li>{@link com.sigrap.employee.application.service.DeleteScheduleService}</li>
 *       </ul>
 *   </li>
 *   <li><strong>Adapters</strong>: Annotated with {@code @Component} or {@code @RestController}
 *       <ul>
 *         <li>{@link com.sigrap.employee.infrastructure.adapter.out.persistence.AttendancePersistenceAdapter}
 *             - Implements {@link com.sigrap.employee.domain.port.AttendanceRepositoryPort}</li>
 *         <li>{@link com.sigrap.employee.infrastructure.adapter.out.persistence.SchedulePersistenceAdapter}
 *             - Implements {@link com.sigrap.employee.domain.port.ScheduleRepositoryPort}</li>
 *         <li>{@link com.sigrap.employee.infrastructure.adapter.in.rest.AttendanceController}
 *             - REST API endpoint for attendance operations</li>
 *         <li>{@link com.sigrap.employee.infrastructure.adapter.in.rest.ScheduleController}
 *             - REST API endpoint for schedule operations</li>
 *       </ul>
 *   </li>
 *   <li><strong>Mappers</strong>: MapStruct mappers with {@code componentModel = "spring"}
 *       <ul>
 *         <li>{@code AttendancePersistenceMapper} - Domain ↔ JPA entity mapping</li>
 *         <li>{@code SchedulePersistenceMapper} - Domain ↔ JPA entity mapping</li>
 *         <li>{@code AttendanceResponseMapper} - Domain ↔ REST DTO mapping</li>
 *         <li>{@code ScheduleResponseMapper} - Domain ↔ REST DTO mapping</li>
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
 * @see com.sigrap.employee.domain.port.AttendanceRepositoryPort
 * @see com.sigrap.employee.domain.port.ScheduleRepositoryPort
 * @see com.sigrap.employee.application.port.in.ClockInUseCase
 * @see com.sigrap.employee.application.port.in.ClockOutUseCase
 * @see com.sigrap.employee.application.port.in.GetAttendanceUseCase
 * @see com.sigrap.employee.application.port.in.CreateScheduleUseCase
 * @see com.sigrap.employee.application.port.in.GetScheduleUseCase
 * @see com.sigrap.employee.application.port.in.UpdateScheduleUseCase
 * @see com.sigrap.employee.application.port.in.DeleteScheduleUseCase
 * @since 1.0
 */
@Configuration
public class EmployeeConfig {
    
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
    public EmployeeConfig() {
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
