package com.sigrap.supplier;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing supplier operations.
 * Handles business logic for creating, reading, updating, and deleting suppliers.
 */
@Service
@RequiredArgsConstructor
public class SupplierService {

  /**
   * Repository for database operations on suppliers.
   * Provides CRUD functionality and custom queries for supplier entities.
   */
  private final SupplierRepository supplierRepository;

  /**
   * Mapper for converting between Supplier entities and DTOs.
   * Handles object transformation for API responses and database operations.
   */
  private final SupplierMapper supplierMapper;

  /**
   * Retrieves all suppliers from the database.
   *
   * @return List of all suppliers mapped to SupplierInfo objects
   */
  @Transactional(readOnly = true)
  public List<SupplierInfo> findAll() {
    return supplierRepository
      .findAll()
      .stream()
      .map(supplierMapper::toInfo)
      .toList();
  }

  /**
   * Finds a supplier by its ID.
   *
   * @param id The ID of the supplier to find
   * @return The found supplier mapped to SupplierInfo
   * @throws EntityNotFoundException if the supplier is not found
   */
  @Transactional(readOnly = true)
  public SupplierInfo findById(Long id) {
    Supplier supplier = supplierRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Supplier not found with id: " + id)
      );
    return supplierMapper.toInfo(supplier);
  }

  /**
   * Creates a new supplier.
   *
   * @param supplierData The data for creating the supplier
   * @return The created supplier mapped to SupplierInfo
   */
  @Transactional
  public SupplierInfo create(SupplierData supplierData) {
    Supplier supplier = supplierMapper.toEntity(supplierData);
    Supplier savedSupplier = supplierRepository.save(supplier);
    return supplierMapper.toInfo(savedSupplier);
  }

  /**
   * Updates an existing supplier.
   *
   * @param id The ID of the supplier to update
   * @param supplierData The new data for the supplier
   * @return The updated supplier mapped to SupplierInfo
   * @throws EntityNotFoundException if the supplier is not found
   */
  @Transactional
  public SupplierInfo update(Long id, SupplierData supplierData) {
    Supplier supplier = supplierRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Supplier not found with id: " + id)
      );
    supplierMapper.updateEntityFromData(supplierData, supplier);
    Supplier updatedSupplier = supplierRepository.save(supplier);
    return supplierMapper.toInfo(updatedSupplier);
  }

  /**
   * Deletes a supplier by its ID.
   *
   * @param id The ID of the supplier to delete
   * @throws EntityNotFoundException if the supplier is not found
   */
  @Transactional
  public void delete(Long id) {
    Supplier supplier = supplierRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Supplier not found with id: " + id)
      );
    supplierRepository.delete(supplier);
  }

  /**
   * Deletes multiple suppliers by their IDs.
   * Validates all IDs exist before performing the deletion.
   *
   * @param ids List of supplier IDs to delete
   * @throws EntityNotFoundException if any of the suppliers is not found
   */
  @Transactional
  public void deleteAllById(List<Long> ids) {
    ids.forEach(id -> {
      if (!supplierRepository.existsById(id)) {
        throw new EntityNotFoundException(
          "Supplier with id " + id + " not found"
        );
      }
    });
    supplierRepository.deleteAllById(ids);
  }
}
