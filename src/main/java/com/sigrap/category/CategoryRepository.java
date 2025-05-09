package com.sigrap.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Category entity operations.
 * Provides basic CRUD operations for managing categories in the database.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {}
