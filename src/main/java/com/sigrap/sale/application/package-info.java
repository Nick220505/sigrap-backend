/**
 * Application layer for the Sale module.
 * 
 * <p>This package contains the application layer components following hexagonal architecture:
 * <ul>
 *   <li>Use case interfaces (input ports) in {@code port.in}</li>
 *   <li>Commands and queries in {@code port.in.command}</li>
 *   <li>Use case implementations (services) in {@code service}</li>
 * </ul>
 * 
 * <p>The application layer orchestrates domain logic and manages transactions,
 * but contains no infrastructure concerns or framework-specific code beyond Spring annotations.
 */
package com.sigrap.sale.application;
