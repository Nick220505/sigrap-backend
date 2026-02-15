package com.sigrap.sale.infrastructure.adapter.out.persistence;

import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleReturn;
import com.sigrap.sale.domain.model.SaleReturnId;
import com.sigrap.sale.domain.model.SaleReturnNumber;
import com.sigrap.sale.domain.model.SaleReturnStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between domain SaleReturn and SaleReturnJpaEntity.
 * This mapper handles the translation between the domain model (pure POJOs)
 * and the persistence model (JPA entities).
 * 
 * The mapper uses custom methods to convert between value objects 
 * (SaleReturnId, SaleReturnNumber, SaleId) and enums (SaleReturnStatus) 
 * and their primitive representations used in the JPA entity.
 */
@Mapper(componentModel = "spring")
public interface SaleReturnPersistenceMapper {

    /**
     * Converts a domain SaleReturn to a JPA entity.
     * Maps value objects and enums to their primitive representations.
     *
     * @param saleReturn the domain sale return
     * @return the JPA entity representation
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "saleReturnIdToLong")
    @Mapping(target = "returnNumber", source = "returnNumber", qualifiedByName = "saleReturnNumberToString")
    @Mapping(target = "saleId", source = "saleId", qualifiedByName = "saleIdToLong")
    @Mapping(target = "status", source = "status", qualifiedByName = "saleReturnStatusToString")
    SaleReturnJpaEntity toJpaEntity(SaleReturn saleReturn);

    /**
     * Converts a JPA entity to a domain SaleReturn.
     * Maps primitive values to value objects and enums.
     * Uses a custom implementation to handle the immutable domain entity.
     *
     * @param entity the JPA entity
     * @return the domain sale return
     */
    default SaleReturn toDomain(SaleReturnJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        SaleReturnId id = longToSaleReturnId(entity.getId());
        SaleReturnNumber returnNumber = stringToSaleReturnNumber(entity.getReturnNumber());
        SaleId saleId = longToSaleId(entity.getSaleId());
        SaleReturnStatus status = stringToSaleReturnStatus(entity.getStatus());
        
        return new SaleReturn(
            id,
            returnNumber,
            saleId,
            entity.getReturnDate(),
            entity.getReason(),
            status,
            entity.getRefundAmount(),
            entity.getNotes(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Converts a SaleReturnId value object to its Long representation.
     *
     * @param id the sale return ID value object
     * @return the Long value, or null if the ID is null
     */
    @Named("saleReturnIdToLong")
    default Long saleReturnIdToLong(SaleReturnId id) {
        return id != null ? id.value() : null;
    }

    /**
     * Converts a Long to a SaleReturnId value object.
     *
     * @param id the Long value
     * @return the SaleReturnId value object, or null if the Long is null
     */
    @Named("longToSaleReturnId")
    default SaleReturnId longToSaleReturnId(Long id) {
        return id != null ? new SaleReturnId(id) : null;
    }

    /**
     * Converts a SaleReturnNumber value object to its String representation.
     *
     * @param returnNumber the return number value object
     * @return the String value, or null if the return number is null
     */
    @Named("saleReturnNumberToString")
    default String saleReturnNumberToString(SaleReturnNumber returnNumber) {
        return returnNumber != null ? returnNumber.value() : null;
    }

    /**
     * Converts a String to a SaleReturnNumber value object.
     *
     * @param returnNumber the String value
     * @return the SaleReturnNumber value object, or null if the String is null
     */
    @Named("stringToSaleReturnNumber")
    default SaleReturnNumber stringToSaleReturnNumber(String returnNumber) {
        return returnNumber != null ? new SaleReturnNumber(returnNumber) : null;
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

    /**
     * Converts a SaleReturnStatus enum to its String representation.
     *
     * @param status the sale return status enum
     * @return the String value, or null if the enum is null
     */
    @Named("saleReturnStatusToString")
    default String saleReturnStatusToString(SaleReturnStatus status) {
        return status != null ? status.name() : null;
    }

    /**
     * Converts a String to a SaleReturnStatus enum.
     *
     * @param status the String value
     * @return the SaleReturnStatus enum, or null if the String is null
     */
    @Named("stringToSaleReturnStatus")
    default SaleReturnStatus stringToSaleReturnStatus(String status) {
        return status != null ? SaleReturnStatus.valueOf(status) : null;
    }
}
