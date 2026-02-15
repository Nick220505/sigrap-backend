/**
 * Persistence output adapter for Category module.
 * <p>
 * This package contains JPA-based persistence implementations that connect
 * the application to the database.
 * </p>
 * <p>
 * Persistence adapter responsibilities:
 * <ul>
 *   <li>Implement repository ports from the domain layer</li>
 *   <li>Define JPA entities (persistence models)</li>
 *   <li>Define Spring Data JPA repository interfaces</li>
 *   <li>Map between domain entities and JPA entities</li>
 *   <li>Handle database-specific concerns</li>
 * </ul>
 * </p>
 * <p>
 * Key principle: JPA entities are separate from domain entities.
 * Domain entities are pure POJOs, while JPA entities contain persistence annotations.
 * </p>
 *
 * @since 1.0
 */
package com.sigrap.category.infrastructure.adapter.out.persistence;
