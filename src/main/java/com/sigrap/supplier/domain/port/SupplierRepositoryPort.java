package com.sigrap.supplier.domain.port;

import com.sigrap.supplier.domain.model.Supplier;
import com.sigrap.supplier.domain.model.SupplierId;
import com.sigrap.supplier.domain.model.SupplierEmail;

import java.util.List;
import java.util.Optional;

/**
 * Repository port (output port) for Supplier aggregate.
 * This interface is defined in the domain layer and expresses domain needs,
 * not database operations. It will be implemented by infrastructure adapters.
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Defined in domain layer (no framework dependencies)</li>
 *   <li>Returns domain entities, not persistence entities</li>
 *   <li>Uses domain value objects for parameters</li>
 *   <li>Expresses business intent, not technical operations</li>
 * </ul>
 */
public interface SupplierRepositoryPort {

    /**
     * Saves a supplier (create or update).
     * If the supplier is new (no ID), it will be created.
     * If the supplier has an ID, it will be updated.
     *
     * @param supplier the supplier to save
     * @return the saved supplier with generated ID if it was new
     */
    Supplier save(Supplier supplier);

    /**
     * Finds a supplier by its identifier.
     *
     * @param id the supplier identifier
     * @return an Optional containing the supplier if found, empty otherwise
     */
    Optional<Supplier> findById(SupplierId id);

    /**
     * Retrieves all suppliers.
     *
     * @return a list of all suppliers, empty list if none exist
     */
    List<Supplier> findAll();

    /**
     * Checks if a supplier with the given email exists.
     * Used to enforce business rule: supplier emails must be unique.
     *
     * @param email the supplier email to check
     * @return true if a supplier with this email exists, false otherwise
     */
    boolean existsByEmail(SupplierEmail email);

    /**
     * Checks if a supplier with the given email exists, excluding a specific supplier.
     * Used when updating a supplier to ensure email uniqueness.
     *
     * @param email the supplier email to check
     * @param id the supplier ID to exclude from the check
     * @return true if another supplier with this email exists, false otherwise
     */
    boolean existsByEmailAndIdNot(SupplierEmail email, SupplierId id);

    /**
     * Deletes a supplier by its identifier.
     *
     * @param id the supplier identifier
     */
    void deleteById(SupplierId id);

    /**
     * Deletes multiple suppliers by their identifiers.
     * Useful for batch operations.
     *
     * @param ids the list of supplier identifiers to delete
     */
    void deleteAllById(List<SupplierId> ids);


    /**
     * Counts the total number of suppliers.
     *
     * @return the total count of suppliers
     */
    long count();

    /**
     * Saves multiple suppliers at once.
     * Useful for batch operations.
     *
     * @param suppliers the list of suppliers to save
     * @return the list of saved suppliers
     */
    List<Supplier> saveAll(List<Supplier> suppliers);

}
