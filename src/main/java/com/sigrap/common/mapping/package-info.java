/**
 * Custom mapping annotations and utilities for object mapping in SIGRAP.
 *
 * <p>This package provides specialized MapStruct annotations and utilities that standardize
 * and simplify the mapping between different layers of the application:
 * <ul>
 *   <li>Entity to DTO mapping</li>
 *   <li>DTO to Entity mapping</li>
 *   <li>Audit field handling</li>
 * </ul></p>
 *
 * <p>Key components:
 * <ul>
 *   <li>{@link com.sigrap.common.mapping.EntityToInfo} - Annotation for entity to DTO mapping</li>
 *   <li>{@link com.sigrap.common.mapping.DataToEntity} - Annotation for DTO to entity mapping</li>
 * </ul></p>
 *
 * <p>Usage examples:
 * <pre>
 * {@code
 * @Mapper(componentModel = "spring")
 * public interface ProductMapper {
 *     @EntityToInfo
 *     ProductInfo toInfo(Product product);
 *
 *     @DataToEntity
 *     Product toEntity(ProductData data);
 * }
 * }</pre></p>
 *
 * <p>Benefits:
 * <ul>
 *   <li>Consistent handling of audit fields (createdAt, updatedAt)</li>
 *   <li>Automatic ID field management</li>
 *   <li>Standardized mapping patterns across the application</li>
 *   <li>Reduced boilerplate in mapper interfaces</li>
 * </ul></p>
 *
 * <p>These annotations are designed to work with MapStruct and automatically handle common
 * mapping scenarios in the application, particularly focusing on the mapping between
 * JPA entities and their DTO representations.</p>
 */
package com.sigrap.common.mapping;
