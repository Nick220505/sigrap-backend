package com.sigrap.sale.infrastructure.adapter.out.persistence;

import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleItem;
import com.sigrap.sale.domain.model.SaleItemId;
import com.sigrap.sale.domain.port.SaleItemRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementation for SaleItem repository port.
 * This adapter bridges the domain layer with the JPA persistence infrastructure.
 * 
 * <p>Responsibilities:
 * <ul>
 *   <li>Implements the SaleItemRepositoryPort interface from the domain layer</li>
 *   <li>Translates between domain entities and JPA entities using the mapper</li>
 *   <li>Delegates actual persistence operations to the JPA repository</li>
 *   <li>Handles the impedance mismatch between domain and persistence models</li>
 * </ul>
 * 
 * <p>This is an output adapter in hexagonal architecture terminology.
 */
@Component
public class SaleItemPersistenceAdapter implements SaleItemRepositoryPort {

    private final SaleItemJpaRepository jpaRepository;
    private final SaleItemPersistenceMapper mapper;

    /**
     * Constructor for dependency injection.
     *
     * @param jpaRepository the Spring Data JPA repository
     * @param mapper the MapStruct mapper for entity conversion
     */
    public SaleItemPersistenceAdapter(
            SaleItemJpaRepository jpaRepository,
            SaleItemPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * Saves a sale item to the database.
     * Converts the domain entity to a JPA entity, persists it, and converts back.
     *
     * @param saleItem the domain sale item to save
     * @return the saved sale item with generated ID if it was new
     */
    @Override
    public SaleItem save(SaleItem saleItem) {
        SaleItemJpaEntity entity = mapper.toJpaEntity(saleItem);
        SaleItemJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    /**
     * Finds a sale item by its identifier.
     *
     * @param id the sale item identifier
     * @return an Optional containing the domain sale item if found, empty otherwise
     */
    @Override
    public Optional<SaleItem> findById(SaleItemId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    /**
     * Finds all sale items for a specific sale.
     *
     * @param saleId the sale identifier
     * @return a list of sale items for the sale
     */
    @Override
    public List<SaleItem> findBySaleId(SaleId saleId) {
        return jpaRepository.findBySaleId(saleId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Deletes a sale item by its identifier.
     *
     * @param id the sale item identifier
     */
    @Override
    public void deleteById(SaleItemId id) {
        jpaRepository.deleteById(id.value());
    }
}
