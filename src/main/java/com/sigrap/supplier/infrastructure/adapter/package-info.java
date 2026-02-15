/**
 * Adapter implementations for the Supplier module infrastructure layer.
 * 
 * <p>This package contains both input and output adapters:
 * <ul>
 *   <li><strong>Input Adapters</strong> ({@code adapter.in}): REST controllers that translate
 *       HTTP requests to use case calls</li>
 *   <li><strong>Output Adapters</strong> ({@code adapter.out}): Persistence implementations
 *       that translate between domain entities and JPA entities</li>
 * </ul>
 * 
 * <p>Adapters are the bridge between the hexagonal core (domain + application)
 * and the external world (HTTP, databases, external services).
 */
package com.sigrap.supplier.infrastructure.adapter;
