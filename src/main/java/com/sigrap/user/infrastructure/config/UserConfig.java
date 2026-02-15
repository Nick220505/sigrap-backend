package com.sigrap.user.infrastructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for the User module.
 * 
 * <p>This configuration class is responsible for wiring the User module components.
 * Most components are auto-configured via Spring annotations (@Service, @Component, @Repository),
 * so this class is primarily a placeholder for any manual bean configuration if needed in the future.
 * 
 * <p>The hexagonal architecture components are wired as follows:
 * <ul>
 *   <li>Domain layer: Pure POJOs, no Spring dependencies</li>
 *   <li>Application layer: Use case services annotated with @Service</li>
 *   <li>Infrastructure layer:
 *     <ul>
 *       <li>Persistence adapters: @Component</li>
 *       <li>REST controllers: @RestController</li>
 *       <li>JPA repositories: @Repository (Spring Data)</li>
 *       <li>Mappers: @Mapper (MapStruct) with componentModel = "spring"</li>
 *     </ul>
 *   </li>
 * </ul>
 */
@Configuration
public class UserConfig {
    // All components are auto-configured via annotations
    // This class can be used for manual bean configuration if needed
}
