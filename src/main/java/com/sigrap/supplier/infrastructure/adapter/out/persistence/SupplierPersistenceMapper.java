package com.sigrap.supplier.infrastructure.adapter.out.persistence;

import com.sigrap.supplier.domain.model.Supplier;
import com.sigrap.supplier.domain.model.SupplierId;
import com.sigrap.supplier.domain.model.SupplierName;
import com.sigrap.supplier.domain.model.SupplierEmail;
import com.sigrap.supplier.domain.model.SupplierPhone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between domain Supplier and SupplierJpaEntity.
 * This mapper handles the translation between the domain model (pure POJOs)
 * and the persistence model (JPA entities).
 * 
 * The mapper uses custom methods to convert between value objects
 * (SupplierId, SupplierName, SupplierEmail, SupplierPhone)
 * and their primitive representations used in the JPA entity.
 */
@Mapper(componentModel = "spring")
public interface SupplierPersistenceMapper {

    /**
     * Converts a domain Supplier to a JPA entity.
     * Maps value objects to their primitive representations.
     *
     * @param supplier the domain supplier
     * @return the JPA entity representation
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "supplierIdToLong")
    @Mapping(target = "name", source = "name", qualifiedByName = "supplierNameToString")
    @Mapping(target = "email", source = "email", qualifiedByName = "supplierEmailToString")
    @Mapping(target = "phone", source = "phone", qualifiedByName = "supplierPhoneToString")
    SupplierJpaEntity toJpaEntity(Supplier supplier);

    /**
     * Converts a JPA entity to a domain Supplier.
     * Maps primitive values to value objects.
     * Uses a custom implementation to handle the immutable domain entity.
     *
     * @param entity the JPA entity
     * @return the domain supplier
     */
    default Supplier toDomain(SupplierJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        SupplierId id = longToSupplierId(entity.getId());
        SupplierName name = stringToSupplierName(entity.getName());
        SupplierEmail email = stringToSupplierEmail(entity.getEmail());
        SupplierPhone phone = stringToSupplierPhone(entity.getPhone());
        
        return new Supplier(
            id,
            name,
            entity.getContactName(),
            email,
            phone,
            entity.getAddress(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Converts a SupplierId value object to its Long representation.
     *
     * @param id the supplier ID value object
     * @return the Long value, or null if the ID is null
     */
    @Named("supplierIdToLong")
    default Long supplierIdToLong(SupplierId id) {
        return id != null ? id.value() : null;
    }

    /**
     * Converts a Long to a SupplierId value object.
     *
     * @param id the Long value
     * @return the SupplierId value object, or null if the Long is null
     */
    @Named("longToSupplierId")
    default SupplierId longToSupplierId(Long id) {
        return id != null ? new SupplierId(id) : null;
    }

    /**
     * Converts a SupplierName value object to its String representation.
     *
     * @param name the supplier name value object
     * @return the String value, or null if the name is null
     */
    @Named("supplierNameToString")
    default String supplierNameToString(SupplierName name) {
        return name != null ? name.value() : null;
    }

    /**
     * Converts a String to a SupplierName value object.
     *
     * @param name the String value
     * @return the SupplierName value object, or null if the String is null
     */
    @Named("stringToSupplierName")
    default SupplierName stringToSupplierName(String name) {
        return name != null ? new SupplierName(name) : null;
    }

    /**
     * Converts a SupplierEmail value object to its String representation.
     *
     * @param email the supplier email value object
     * @return the String value, or null if the email is null
     */
    @Named("supplierEmailToString")
    default String supplierEmailToString(SupplierEmail email) {
        return email != null ? email.value() : null;
    }

    /**
     * Converts a String to a SupplierEmail value object.
     *
     * @param email the String value
     * @return the SupplierEmail value object, or null if the String is null
     */
    @Named("stringToSupplierEmail")
    default SupplierEmail stringToSupplierEmail(String email) {
        return email != null ? new SupplierEmail(email) : null;
    }

    /**
     * Converts a SupplierPhone value object to its String representation.
     *
     * @param phone the supplier phone value object
     * @return the String value, or null if the phone is null
     */
    @Named("supplierPhoneToString")
    default String supplierPhoneToString(SupplierPhone phone) {
        return phone != null ? phone.value() : null;
    }

    /**
     * Converts a String to a SupplierPhone value object.
     *
     * @param phone the String value
     * @return the SupplierPhone value object, or null if the String is null
     */
    @Named("stringToSupplierPhone")
    default SupplierPhone stringToSupplierPhone(String phone) {
        return phone != null ? new SupplierPhone(phone) : null;
    }
}
