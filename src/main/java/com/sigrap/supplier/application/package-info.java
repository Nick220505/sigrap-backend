/**
 * Application layer for the Supplier module.
 * This layer contains use case implementations that orchestrate domain logic.
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Implements input ports (use case interfaces)</li>
 *   <li>Uses output ports (repository interfaces) from domain layer</li>
 *   <li>Manages transactions and security</li>
 *   <li>Depends only on domain layer</li>
 * </ul>
 */
package com.sigrap.supplier.application;
