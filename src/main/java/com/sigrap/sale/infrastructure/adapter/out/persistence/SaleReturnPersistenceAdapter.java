package com.sigrap.sale.infrastructure.adapter.out.persistence;

import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleReturn;
import com.sigrap.sale.domain.model.SaleReturnId;
import com.sigrap.sale.domain.model.SaleReturnStatus;
import com.sigrap.sale.domain.port.SaleReturnRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementation for SaleReturn repository port.
 * This adapter bridges the domain layer with the JPA persistence infrastructure.
 * 
 * <p>Responsibilities:
 * <ul>
 *   <li>Implements the SaleReturnRepositoryPort interface from the domain layer</li>
 *   <li>Translates between domain entities and JPA entities using the mapper</li>
 *   <li>Delegates actual persistence operations to the JPA repository</li>
 *   <li>Handles the impedance mismatch between domain and persistence models</li>
 * </ul>
 * 
 * <p>This is an output adapter in hexagonal architecture terminology.
 */
@Component
public class SaleReturnPersistenceAdapter implements SaleReturnRepositoryPort {

    private final SaleReturnJpaRepository jpaRepository;
    private final SaleReturnPersistenceMapper mapper;

    /**
     * Constructor for dependency injection.
     *
     * @param jpaRepository the Spring Data JPA repository
     * @param mapper the MapStruct mapper for entity conversion
     */
    public SaleReturnPersistenceAdapter(
            SaleReturnJpaRepository jpaRepository,
            SaleReturnPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * Saves a sale return to the database.
     * Converts the domain entity to a JPA entity, persists it, and converts back.
     *
     * @param saleReturn the domain sale return to save
     * @return the saved sale return with generated ID if it was new
     */
    @Override
    public SaleReturn save(SaleReturn saleReturn) {
        SaleReturnJpaEntity entity = mapper.toJpaEntity(saleReturn);
        SaleReturnJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    /**
     * Finds a sale return by its identifier.
     *
     * @param id the sale return identifier
     * @return an Optional containing the domain sale return if found, empty otherwise
     */
    @Override
    public Optional<SaleReturn> findById(SaleReturnId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    /**
     * Retrieves all sale returns from the database.
     *
     * @return a list of all domain sale returns
     */
    @Override
    public List<SaleReturn> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Finds all sale returns for a specific sale.
     *
     * @param saleId the sale identifier
     * @return a list of sale returns for the sale
     */
    @Override
    public List<SaleReturn> findBySaleId(SaleId saleId) {
        return jpaRepository.findBySaleId(saleId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Finds all sale returns with a specific status.
     *
     * @param status the sale return status
     * @return a list of sale returns with the given status
     */
    @Override
    public List<SaleReturn> findByStatus(SaleReturnStatus status) {
        return jpaRepository.findByStatus(status.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Deletes a sale return by its identifier.
     *
     * @param id the sale return identifier
     */
    @Override
    public void deleteById(SaleReturnId id) {
        jpaRepository.deleteById(id.value());
    }
}
