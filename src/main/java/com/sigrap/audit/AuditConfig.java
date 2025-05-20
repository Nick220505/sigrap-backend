package com.sigrap.audit;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration class for the audit subsystem.
 * Enables Aspect-Oriented Programming and asynchronous event processing.
 */
@Configuration
@EnableAspectJAutoProxy
@EnableAsync
public class AuditConfig {}
