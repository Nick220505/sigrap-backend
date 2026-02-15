package com.sigrap.sale.infrastructure.adapter.out.persistence;

import com.sigrap.sale.domain.model.PaymentMethod;
import com.sigrap.sale.domain.model.Sale;
import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleNumber;
import com.sigrap.sale.domain.model.SaleStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between domain Sale and SaleJpaEntity.
 * This mapper handles the translation between the domain model (pure POJOs)
 * and the persistence model (JPA entities).
 * 
 * The mapper uses custom methods to convert between value objects 
 * (SaleId, SaleNumber) and enums (PaymentMethod, SaleStatus) and their 
 * primitive representations used in the JPA entity.
 */
@Mapper(componentModel = "spring")
public interface SalePersistenceMapper {

    /**
     * Converts a domain Sale to a JPA entity.
     * Maps value objects and enums to their primitive representations.
     *
     * @param sale the domain sale
     * @return the JPA entity representation
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "saleIdToLong")
    @Mapping(target = "saleNumber", source = "saleNumber", qualifiedByName = "saleNumberToString")
    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "paymentMethodToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "saleStatusToString")
    SaleJpaEntity toJpaEntity(Sale sale);

    /**
     * Converts a JPA entity to a domain Sale.
     * Maps primitive values to value objects and enums.
     * Uses a custom implementation to handle the immutable domain entity.
     *
     * @param entity the JPA entity
     * @return the domain sale
     */
    default Sale toDomain(SaleJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        SaleId id = longToSaleId(entity.getId());
        SaleNumber saleNumber = stringToSaleNumber(entity.getSaleNumber());
        PaymentMethod paymentMethod = stringToPaymentMethod(entity.getPaymentMethod());
        SaleStatus status = stringToSaleStatus(entity.getStatus());
        
        return new Sale(
            id,
            saleNumber,
            entity.getCustomerId(),
            entity.getEmployeeId(),
            entity.getSaleDate(),
            entity.getTotalAmount(),
            paymentMethod,
            status,
            entity.getNotes(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
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
     * Converts a SaleNumber value object to its String representation.
     *
     * @param saleNumber the sale number value object
     * @return the String value, or null if the sale number is null
     */
    @Named("saleNumberToString")
    default String saleNumberToString(SaleNumber saleNumber) {
        return saleNumber != null ? saleNumber.value() : null;
    }

    /**
     * Converts a String to a SaleNumber value object.
     *
     * @param saleNumber the String value
     * @return the SaleNumber value object, or null if the String is null
     */
    @Named("stringToSaleNumber")
    default SaleNumber stringToSaleNumber(String saleNumber) {
        return saleNumber != null ? new SaleNumber(saleNumber) : null;
    }

    /**
     * Converts a PaymentMethod enum to its String representation.
     *
     * @param paymentMethod the payment method enum
     * @return the String value, or null if the enum is null
     */
    @Named("paymentMethodToString")
    default String paymentMethodToString(PaymentMethod paymentMethod) {
        return paymentMethod != null ? paymentMethod.name() : null;
    }

    /**
     * Converts a String to a PaymentMethod enum.
     *
     * @param paymentMethod the String value
     * @return the PaymentMethod enum, or null if the String is null
     */
    @Named("stringToPaymentMethod")
    default PaymentMethod stringToPaymentMethod(String paymentMethod) {
        return paymentMethod != null ? PaymentMethod.valueOf(paymentMethod) : null;
    }

    /**
     * Converts a SaleStatus enum to its String representation.
     *
     * @param status the sale status enum
     * @return the String value, or null if the enum is null
     */
    @Named("saleStatusToString")
    default String saleStatusToString(SaleStatus status) {
        return status != null ? status.name() : null;
    }

    /**
     * Converts a String to a SaleStatus enum.
     *
     * @param status the String value
     * @return the SaleStatus enum, or null if the String is null
     */
    @Named("stringToSaleStatus")
    default SaleStatus stringToSaleStatus(String status) {
        return status != null ? SaleStatus.valueOf(status) : null;
    }
}
