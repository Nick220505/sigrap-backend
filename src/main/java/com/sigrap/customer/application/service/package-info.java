/**
 * Service implementations for Customer use cases.
 * 
 * <p>This package contains the concrete implementations of use case interfaces.
 * Services orchestrate domain logic, enforce business rules, and manage transactions.
 * 
 * <p>All services:
 * <ul>
 *   <li>Implement input port interfaces</li>
 *   <li>Use output ports (repository) for persistence</li>
 *   <li>Are annotated with {@code @Service} for Spring dependency injection</li>
 *   <li>Are annotated with {@code @Transactional} for transaction management</li>
 *   <li>Contain no infrastructure concerns</li>
 * </ul>
 */
package com.sigrap.customer.application.service;
