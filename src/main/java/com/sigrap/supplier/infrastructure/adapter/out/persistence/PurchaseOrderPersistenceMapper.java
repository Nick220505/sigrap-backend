package com.sigrap.supplier.infrastructure.adapter.out.persistence;

import com.sigrap.supplier.domain.model.PurchaseOrder;
import com.sigrap.supplier.domain.model.PurchaseOrderId;
import com.sigrap.supplier.domain.model.PurchaseOrderNumber;
import com.sigrap.supplier.domain.model.PurchaseOrderStatus;
import com.sigrap.supplier.domain.model.SupplierId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between domain PurchaseOrder and PurchaseOrderJpaEntity.
 * This mapper handles the translation between the domain model (pure POJOs)
 * and the persistence model (JPA entities).
 * 
 * The mapper uses custom methods to convert between value objects
 * (PurchaseOrderId, PurchaseOrderNumber, SupplierId, PurchaseOrderStatus)
 * and their primitive representations used in the JPA entity.
 */
@Mapper(componentModel = "spring")
public interface PurchaseOrderPersistenceMapper {

    /**
     * Converts a domain PurchaseOrder to a JPA entity.
     * Maps value objects to their primitive representations.
     *
     * @param purchaseOrder the domain purchase order
     * @return the JPA entity representation
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "purchaseOrderIdToLong")
    @Mapping(target = "orderNumber", source = "orderNumber", qualifiedByName = "purchaseOrderNumberToString")
    @Mapping(target = "supplierId", source = "supplierId", qualifiedByName = "supplierIdToLong")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToJpa")
    PurchaseOrderJpaEntity toJpaEntity(PurchaseOrder purchaseOrder);

    /**
     * Converts a JPA entity to a domain PurchaseOrder.
     * Maps primitive values to value objects.
     * Uses a custom implementation to handle the immutable domain entity.
     *
     * @param entity the JPA entity
     * @return the domain purchase order
     */
    default PurchaseOrder toDomain(PurchaseOrderJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        PurchaseOrderId id = longToPurchaseOrderId(entity.getId());
        PurchaseOrderNumber orderNumber = stringToPurchaseOrderNumber(entity.getOrderNumber());
        SupplierId supplierId = longToSupplierId(entity.getSupplierId());
        PurchaseOrderStatus status = jpaToStatus(entity.getStatus());
        
        return new PurchaseOrder(
            id,
            orderNumber,
            supplierId,
            entity.getOrderDate(),
            entity.getExpectedDeliveryDate(),
            status,
            entity.getTotalAmount(),
            entity.getNotes(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Converts a PurchaseOrderId value object to its Long representation.
     *
     * @param id the purchase order ID value object
     * @return the Long value, or null if the ID is null
     */
    @Named("purchaseOrderIdToLong")
    default Long purchaseOrderIdToLong(PurchaseOrderId id) {
        return id != null ? id.value() : null;
    }

    /**
     * Converts a Long to a PurchaseOrderId value object.
     *
     * @param id the Long value
     * @return the PurchaseOrderId value object, or null if the Long is null
     */
    @Named("longToPurchaseOrderId")
    default PurchaseOrderId longToPurchaseOrderId(Long id) {
        return id != null ? new PurchaseOrderId(id) : null;
    }

    /**
     * Converts a PurchaseOrderNumber value object to its String representation.
     *
     * @param orderNumber the purchase order number value object
     * @return the String value, or null if the order number is null
     */
    @Named("purchaseOrderNumberToString")
    default String purchaseOrderNumberToString(PurchaseOrderNumber orderNumber) {
        return orderNumber != null ? orderNumber.value() : null;
    }

    /**
     * Converts a String to a PurchaseOrderNumber value object.
     *
     * @param orderNumber the String value
     * @return the PurchaseOrderNumber value object, or null if the String is null
     */
    @Named("stringToPurchaseOrderNumber")
    default PurchaseOrderNumber stringToPurchaseOrderNumber(String orderNumber) {
        return orderNumber != null ? new PurchaseOrderNumber(orderNumber) : null;
    }

    /**
     * Converts a SupplierId value object to its Long representation.
     *
     * @param supplierId the supplier ID value object
     * @return the Long value, or null if the ID is null
     */
    @Named("supplierIdToLong")
    default Long supplierIdToLong(SupplierId supplierId) {
        return supplierId != null ? supplierId.value() : null;
    }

    /**
     * Converts a Long to a SupplierId value object.
     *
     * @param supplierId the Long value
     * @return the SupplierId value object, or null if the Long is null
     */
    @Named("longToSupplierId")
    default SupplierId longToSupplierId(Long supplierId) {
        return supplierId != null ? new SupplierId(supplierId) : null;
    }

    /**
     * Converts a domain PurchaseOrderStatus to JPA enum.
     *
     * @param status the domain status
     * @return the JPA status enum, or null if the status is null
     */
    @Named("statusToJpa")
    default PurchaseOrderJpaEntity.PurchaseOrderStatusJpa statusToJpa(PurchaseOrderStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case PENDING -> PurchaseOrderJpaEntity.PurchaseOrderStatusJpa.PENDING;
            case APPROVED -> PurchaseOrderJpaEntity.PurchaseOrderStatusJpa.APPROVED;
            case RECEIVED -> PurchaseOrderJpaEntity.PurchaseOrderStatusJpa.RECEIVED;
            case CANCELLED -> PurchaseOrderJpaEntity.PurchaseOrderStatusJpa.CANCELLED;
        };
    }

    /**
     * Converts a JPA status enum to domain PurchaseOrderStatus.
     *
     * @param status the JPA status enum
     * @return the domain status, or null if the status is null
     */
    @Named("jpaToStatus")
    default PurchaseOrderStatus jpaToStatus(PurchaseOrderJpaEntity.PurchaseOrderStatusJpa status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case PENDING -> PurchaseOrderStatus.PENDING;
            case APPROVED -> PurchaseOrderStatus.APPROVED;
            case RECEIVED -> PurchaseOrderStatus.RECEIVED;
            case CANCELLED -> PurchaseOrderStatus.CANCELLED;
        };
    }
}
