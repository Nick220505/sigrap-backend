package com.sigrap.sale.infrastructure.adapter.out.persistence;

import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleItem;
import com.sigrap.sale.domain.model.SaleItemId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between domain SaleItem and SaleItemJpaEntity.
 * This mapper handles the translation between the domain model (pure POJOs)
 * and the persistence model (JPA entities).
 * 
 * The mapper uses custom methods to convert between value objects 
 * (SaleItemId, SaleId) and their primitive representations used in the JPA entity.
 */
@Mapper(componentModel = "spring")
public interface SaleItemPersistenceMapper {

    /**
     * Converts a domain SaleItem to a JPA entity.
     * Maps value objects to their primitive representations.
     *
     * @param saleItem the domain sale item
     * @return the JPA entity representation
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "saleItemIdToLong")
    @Mapping(target = "saleId", source = "saleId", qualifiedByName = "saleIdToLong")
    SaleItemJpaEntity toJpaEntity(SaleItem saleItem);

    /**
     * Converts a JPA entity to a domain SaleItem.
     * Maps primitive values to value objects.
     * Uses a custom implementation to handle the immutable domain entity.
     *
     * @param entity the JPA entity
     * @return the domain sale item
     */
    default SaleItem toDomain(SaleItemJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        SaleItemId id = longToSaleItemId(entity.getId());
        SaleId saleId = longToSaleId(entity.getSaleId());
        
        return new SaleItem(
            id,
            saleId,
            entity.getProductId(),
            entity.getQuantity(),
            entity.getUnitPrice()
        );
    }

    /**
     * Converts a SaleItemId value object to its Long representation.
     *
     * @param id the sale item ID value object
     * @return the Long value, or null if the ID is null
     */
    @Named("saleItemIdToLong")
    default Long saleItemIdToLong(SaleItemId id) {
        return id != null ? id.value() : null;
    }

    /**
     * Converts a Long to a SaleItemId value object.
     *
     * @param id the Long value
     * @return the SaleItemId value object, or null if the Long is null
     */
    @Named("longToSaleItemId")
    default SaleItemId longToSaleItemId(Long id) {
        return id != null ? new SaleItemId(id) : null;
    }

    /**
     * Converts a SaleId value object to its Long representation.
     *
     * @param id the sale ID value object
     * @return the Long value, or null if the ID is null
     */
    @Named("saleIdToLong")
    default Long saleIdToLong(SaleId id) {
        return id != null ? id.value() : null;
    }

    /**
     * Converts a Long to a SaleId value object.
     *
     * @param id the Long value
     * @return the SaleId value object, or null if the Long is null
     */
    @Named("longToSaleId")
    default SaleId longToSaleId(Long id) {
        return id != null ? new SaleId(id) : null;
    }
}
