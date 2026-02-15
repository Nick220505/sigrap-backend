package com.sigrap.sale.infrastructure.adapter.out.persistence;

import com.sigrap.sale.domain.model.Sale;
import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleStatus;
import com.sigrap.sale.domain.port.SaleRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementation for Sale repository port.
 * This adapter bridges the domain layer with the JPA persistence infrastructure.
 * 
 * <p>Responsibilities:
 * <ul>
 *   <li>Implements the SaleRepositoryPort interface from the domain layer</li>
 *   <li>Translates between domain entities and JPA entities using the mapper</li>
 *   <li>Delegates actual persistence operations to the JPA repository</li>
 *   <li>Handles the impedance mismatch between domain and persistence models</li>
 * </ul>
 * 
 * <p>This is an output adapter in hexagonal architecture terminology.
 */
@Component
public class SalePersistenceAdapter implements SaleRepositoryPort {

    private final SaleJpaRepository jpaRepository;
    private final SalePersistenceMapper mapper;

    /**
     * Constructor for dependency injection.
     *
     * @param jpaRepository the Spring Data JPA repository
     * @param mapper the MapStruct mapper for entity conversion
     */
    public SalePersistenceAdapter(
            SaleJpaRepository jpaRepository,
            SalePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * Saves a sale to the database.
     * Converts the domain entity to a JPA entity, persists it, and converts back.
     *
     * @param sale the domain sale to save
     * @return the saved sale with generated ID if it was new
     */
    @Override
    public Sale save(Sale sale) {
        SaleJpaEntity entity = mapper.toJpaEntity(sale);
        SaleJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    /**
     * Finds a sale by its identifier.
     *
     * @param id the sale identifier
     * @return an Optional containing the domain sale if found, empty otherwise
     */
    @Override
    public Optional<Sale> findById(SaleId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    /**
     * Retrieves all sales from the database.
     *
     * @return a list of all domain sales
     */
    @Override
    public List<Sale> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Finds all sales for a specific customer.
     *
     * @param customerId the customer identifier
     * @return a list of sales for the customer
     */
    @Override
    public List<Sale> findByCustomerId(Long customerId) {
        return jpaRepository.findByCustomerId(customerId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Finds all sales with a specific status.
     *
     * @param status the sale status
     * @return a list of sales with the given status
     */
    @Override
    public List<Sale> findByStatus(SaleStatus status) {
        return jpaRepository.findByStatus(status.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Deletes a sale by its identifier.
     *
     * @param id the sale identifier
     */
    @Override
    public void deleteById(SaleId id) {
        jpaRepository.deleteById(id.value());
    }
}
