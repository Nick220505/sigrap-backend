package com.sigrap.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product entity operations.
 * Provides basic CRUD operations for managing products in the database.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {}
