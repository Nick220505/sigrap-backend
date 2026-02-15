/**
 * Configuration classes for the Supplier module infrastructure layer.
 * 
 * <p>This package contains Spring configuration classes that wire together
 * all components of the supplier module following hexagonal architecture principles.
 * 
 * <p>Configuration classes:
 * <ul>
 *   <li>{@link com.sigrap.supplier.infrastructure.config.SupplierConfig} - 
 *       Main configuration for the supplier module</li>
 * </ul>
 * 
 * <p>Most components are auto-configured through Spring annotations
 * ({@code @Service}, {@code @Component}, {@code @RestController}),
 * so explicit bean definitions are rarely needed.
 */
package com.sigrap.supplier.infrastructure.config;
