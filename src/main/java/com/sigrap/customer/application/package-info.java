/**
 * Application layer for the Customer module.
 * 
 * <p>This package contains the application layer components following hexagonal architecture:
 * <ul>
 *   <li>Use case interfaces (input ports) in {@code port.in}</li>
 *   <li>Command records in {@code port.in.command}</li>
 *   <li>Use case implementations (services) in {@code service}</li>
 * </ul>
 * 
 * <p>The application layer orchestrates domain logic and manages transactions.
 * It depends only on the domain layer and has no infrastructure dependencies.
 * 
 * @see com.sigrap.customer.domain
 * @see com.sigrap.customer.infrastructure
 */
package com.sigrap.customer.application;
