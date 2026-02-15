package com.sigrap.audit.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Spring configuration for the audit module.
 * 
 * <p>This configuration class enables async processing for event listeners
 * and wires all components together. Most components are auto-configured
 * through Spring's component scanning and dependency injection.</p>
 * 
 * <p>Key features enabled:
 * <ul>
 *   <li>Async event processing for audit logging</li>
 *   <li>Component scanning for adapters and services</li>
 *   <li>JPA repository scanning</li>
 * </ul>
 */
@Configuration
@EnableAsync
public class AuditModuleConfig {
    
    // All components are auto-configured via @Component, @Service, and @Repository
    // This configuration class is primarily for enabling async processing
    // and providing a central place for any future manual bean configuration
}
