package com.sigrap.supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Supplier entity operations.
 * Provides basic CRUD operations for managing suppliers in the database.
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {}
