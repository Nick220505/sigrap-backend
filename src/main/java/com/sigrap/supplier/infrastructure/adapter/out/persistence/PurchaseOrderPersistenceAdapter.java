package com.sigrap.supplier.infrastructure.adapter.out.persistence;

import com.sigrap.supplier.domain.model.PurchaseOrder;
import com.sigrap.supplier.domain.model.PurchaseOrderId;
import com.sigrap.supplier.domain.model.PurchaseOrderStatus;
import com.sigrap.supplier.domain.model.SupplierId;
import com.sigrap.supplier.domain.port.PurchaseOrderRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementation for PurchaseOrder repository port.
 * This adapter bridges the domain layer with the JPA persistence infrastructure.
 * 
 * <p>Responsibilities:
 * <ul>
 *   <li>Implements the PurchaseOrderRepositoryPort interface from the domain layer</li>
 *   <li>Translates between domain entities and JPA entities using the mapper</li>
 *   <li>Delegates actual persistence operations to the JPA repository</li>
 *   <li>Handles the impedance mismatch between domain and persistence models</li>
 * </ul>
 * 
 * <p>This is an output adapter in hexagonal architecture terminology.
 */
@Component
public class PurchaseOrderPersistenceAdapter implements PurchaseOrderRepositoryPort {

    private final PurchaseOrderJpaRepository jpaRepository;
    private final PurchaseOrderPersistenceMapper mapper;

    /**
     * Constructor for dependency injection.
     *
     * @param jpaRepository the Spring Data JPA repository
     * @param mapper the MapStruct mapper for entity conversion
     */
    public PurchaseOrderPersistenceAdapter(
            PurchaseOrderJpaRepository jpaRepository,
            PurchaseOrderPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * Saves a purchase order to the database.
     * Converts the domain entity to a JPA entity, persists it, and converts back.
     *
     * @param purchaseOrder the domain purchase order to save
     * @return the saved purchase order with generated ID if it was new
     */
    @Override
    public PurchaseOrder save(PurchaseOrder purchaseOrder) {
        PurchaseOrderJpaEntity entity = mapper.toJpaEntity(purchaseOrder);
        PurchaseOrderJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    /**
     * Finds a purchase order by its identifier.
     *
     * @param id the purchase order identifier
     * @return an Optional containing the domain purchase order if found, empty otherwise
     */
    @Override
    public Optional<PurchaseOrder> findById(PurchaseOrderId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    /**
     * Retrieves all purchase orders from the database.
     *
     * @return a list of all domain purchase orders
     */
    @Override
    public List<PurchaseOrder> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Finds all purchase orders for a specific supplier.
     *
     * @param supplierId the supplier identifier
     * @return a list of purchase orders for the supplier
     */
    @Override
    public List<PurchaseOrder> findBySupplierId(SupplierId supplierId) {
        return jpaRepository.findBySupplierId(supplierId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Finds all purchase orders with a specific status.
     *
     * @param status the purchase order status
     * @return a list of purchase orders with the given status
     */
    @Override
    public List<PurchaseOrder> findByStatus(PurchaseOrderStatus status) {
        PurchaseOrderJpaEntity.PurchaseOrderStatusJpa jpaStatus = mapStatusToJpa(status);
        return jpaRepository.findByStatus(jpaStatus).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Deletes a purchase order by its identifier.
     *
     * @param id the purchase order identifier
     */
    @Override
    public void deleteById(PurchaseOrderId id) {
        jpaRepository.deleteById(id.value());
    }

    /**
     * Counts the total number of purchase orders.
     *
     * @return the total count of purchase orders
     */
    @Override
    public long count() {
        return jpaRepository.count();
    }

    /**
     * Saves multiple purchase orders at once.
     *
     * @param purchaseOrders the list of purchase orders to save
     * @return the list of saved purchase orders
     */
    @Override
    public List<PurchaseOrder> saveAll(List<PurchaseOrder> purchaseOrders) {
        List<PurchaseOrderJpaEntity> entities = purchaseOrders.stream()
                .map(mapper::toJpaEntity)
                .toList();
        List<PurchaseOrderJpaEntity> savedEntities = jpaRepository.saveAll(entities);
        return savedEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Helper method to map domain status to JPA status.
     *
     * @param status the domain status
     * @return the JPA status enum
     */
    private PurchaseOrderJpaEntity.PurchaseOrderStatusJpa mapStatusToJpa(PurchaseOrderStatus status) {
        return switch (status) {
            case PENDING -> PurchaseOrderJpaEntity.PurchaseOrderStatusJpa.PENDING;
            case APPROVED -> PurchaseOrderJpaEntity.PurchaseOrderStatusJpa.APPROVED;
            case RECEIVED -> PurchaseOrderJpaEntity.PurchaseOrderStatusJpa.RECEIVED;
            case CANCELLED -> PurchaseOrderJpaEntity.PurchaseOrderStatusJpa.CANCELLED;
        };
    }
}
