package com.sigrap.supplier.infrastructure.adapter.out.persistence;

import com.sigrap.supplier.domain.model.Supplier;
import com.sigrap.supplier.domain.model.SupplierId;
import com.sigrap.supplier.domain.model.SupplierEmail;
import com.sigrap.supplier.domain.port.SupplierRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementation for Supplier repository port.
 * This adapter bridges the domain layer with the JPA persistence infrastructure.
 * 
 * <p>Responsibilities:
 * <ul>
 *   <li>Implements the SupplierRepositoryPort interface from the domain layer</li>
 *   <li>Translates between domain entities and JPA entities using the mapper</li>
 *   <li>Delegates actual persistence operations to the JPA repository</li>
 *   <li>Handles the impedance mismatch between domain and persistence models</li>
 * </ul>
 * 
 * <p>This is an output adapter in hexagonal architecture terminology.
 */
@Component
public class SupplierPersistenceAdapter implements SupplierRepositoryPort {

    private final SupplierJpaRepository jpaRepository;
    private final SupplierPersistenceMapper mapper;

    /**
     * Constructor for dependency injection.
     *
     * @param jpaRepository the Spring Data JPA repository
     * @param mapper the MapStruct mapper for entity conversion
     */
    public SupplierPersistenceAdapter(
            SupplierJpaRepository jpaRepository,
            SupplierPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * Saves a supplier to the database.
     * Converts the domain entity to a JPA entity, persists it, and converts back.
     *
     * @param supplier the domain supplier to save
     * @return the saved supplier with generated ID if it was new
     */
    @Override
    public Supplier save(Supplier supplier) {
        SupplierJpaEntity entity = mapper.toJpaEntity(supplier);
        SupplierJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    /**
     * Finds a supplier by its identifier.
     *
     * @param id the supplier identifier
     * @return an Optional containing the domain supplier if found, empty otherwise
     */
    @Override
    public Optional<Supplier> findById(SupplierId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    /**
     * Retrieves all suppliers from the database.
     *
     * @return a list of all domain suppliers
     */
    @Override
    public List<Supplier> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Checks if a supplier with the given email exists.
     *
     * @param email the supplier email to check
     * @return true if a supplier with this email exists, false otherwise
     */
    @Override
    public boolean existsByEmail(SupplierEmail email) {
        return jpaRepository.existsByEmail(email.value());
    }

    /**
     * Checks if a supplier with the given email exists, excluding a specific supplier.
     *
     * @param email the supplier email to check
     * @param id the supplier ID to exclude from the check
     * @return true if another supplier with this email exists, false otherwise
     */
    @Override
    public boolean existsByEmailAndIdNot(SupplierEmail email, SupplierId id) {
        return jpaRepository.existsByEmailAndIdNot(email.value(), id.value());
    }

    /**
     * Deletes a supplier by its identifier.
     *
     * @param id the supplier identifier
     */
    @Override
    public void deleteById(SupplierId id) {
        jpaRepository.deleteById(id.value());
    }

    /**
     * Deletes multiple suppliers by their identifiers.
     *
     * @param ids the list of supplier identifiers to delete
     */
    @Override
    public void deleteAllById(List<SupplierId> ids) {
        List<Long> longIds = ids.stream()
                .map(SupplierId::value)
                .toList();
        jpaRepository.deleteAllById(longIds);
    }

    /**
     * Counts the total number of suppliers.
     *
     * @return the total count of suppliers
     */
    @Override
    public long count() {
        return jpaRepository.count();
    }

    /**
     * Saves multiple suppliers at once.
     *
     * @param suppliers the list of suppliers to save
     * @return the list of saved suppliers
     */
    @Override
    public List<Supplier> saveAll(List<Supplier> suppliers) {
        List<SupplierJpaEntity> entities = suppliers.stream()
                .map(mapper::toJpaEntity)
                .toList();
        List<SupplierJpaEntity> savedEntities = jpaRepository.saveAll(entities);
        return savedEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }
}
