package com.sigrap.customer.infrastructure.adapter.out.persistence;

import com.sigrap.customer.domain.model.Customer;
import com.sigrap.customer.domain.model.CustomerId;
import com.sigrap.customer.domain.model.CustomerName;
import com.sigrap.customer.domain.model.CustomerEmail;
import com.sigrap.customer.domain.model.CustomerPhone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between domain Customer and CustomerJpaEntity.
 * This mapper handles the translation between the domain model (pure POJOs)
 * and the persistence model (JPA entities).
 * 
 * The mapper uses custom methods to convert between value objects 
 * (CustomerId, CustomerName, CustomerEmail, CustomerPhone) and their primitive representations 
 * used in the JPA entity.
 */
@Mapper(componentModel = "spring")
public interface CustomerPersistenceMapper {

    /**
     * Converts a domain Customer to a JPA entity.
     * Maps value objects to their primitive representations.
     *
     * @param customer the domain customer
     * @return the JPA entity representation
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "customerIdToLong")
    @Mapping(target = "fullName", source = "fullName", qualifiedByName = "customerNameToString")
    @Mapping(target = "email", source = "email", qualifiedByName = "customerEmailToString")
    @Mapping(target = "phoneNumber", source = "phoneNumber", qualifiedByName = "customerPhoneToString")
    CustomerJpaEntity toJpaEntity(Customer customer);

    /**
     * Converts a JPA entity to a domain Customer.
     * Maps primitive values to value objects.
     * Uses a custom implementation to handle the immutable domain entity.
     *
     * @param entity the JPA entity
     * @return the domain customer
     */
    default Customer toDomain(CustomerJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        CustomerId id = longToCustomerId(entity.getId());
        CustomerName fullName = stringToCustomerName(entity.getFullName());
        CustomerEmail email = stringToCustomerEmail(entity.getEmail());
        CustomerPhone phoneNumber = stringToCustomerPhone(entity.getPhoneNumber());
        
        return new Customer(
            id,
            fullName,
            entity.getDocumentId(),
            email,
            phoneNumber,
            entity.getAddress(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Converts a CustomerId value object to its Long representation.
     *
     * @param id the customer ID value object
     * @return the Long value, or null if the ID is null
     */
    @Named("customerIdToLong")
    default Long customerIdToLong(CustomerId id) {
        return id != null ? id.value() : null;
    }

    /**
     * Converts a Long to a CustomerId value object.
     *
     * @param id the Long value
     * @return the CustomerId value object, or null if the Long is null
     */
    @Named("longToCustomerId")
    default CustomerId longToCustomerId(Long id) {
        return id != null ? new CustomerId(id) : null;
    }

    /**
     * Converts a CustomerName value object to its String representation.
     *
     * @param name the customer name value object
     * @return the String value, or null if the name is null
     */
    @Named("customerNameToString")
    default String customerNameToString(CustomerName name) {
        return name != null ? name.value() : null;
    }

    /**
     * Converts a String to a CustomerName value object.
     *
     * @param name the String value
     * @return the CustomerName value object, or null if the String is null
     */
    @Named("stringToCustomerName")
    default CustomerName stringToCustomerName(String name) {
        return name != null ? new CustomerName(name) : null;
    }

    /**
     * Converts a CustomerEmail value object to its String representation.
     *
     * @param email the customer email value object
     * @return the String value, or null if the email is null
     */
    @Named("customerEmailToString")
    default String customerEmailToString(CustomerEmail email) {
        return email != null ? email.value() : null;
    }

    /**
     * Converts a String to a CustomerEmail value object.
     *
     * @param email the String value
     * @return the CustomerEmail value object, or null if the String is null
     */
    @Named("stringToCustomerEmail")
    default CustomerEmail stringToCustomerEmail(String email) {
        return email != null ? new CustomerEmail(email) : null;
    }

    /**
     * Converts a CustomerPhone value object to its String representation.
     *
     * @param phone the customer phone value object
     * @return the String value, or null if the phone is null
     */
    @Named("customerPhoneToString")
    default String customerPhoneToString(CustomerPhone phone) {
        return phone != null ? phone.value() : null;
    }

    /**
     * Converts a String to a CustomerPhone value object.
     *
     * @param phone the String value
     * @return the CustomerPhone value object, or null if the String is null or blank
     */
    @Named("stringToCustomerPhone")
    default CustomerPhone stringToCustomerPhone(String phone) {
        return (phone != null && !phone.isBlank()) ? new CustomerPhone(phone) : null;
    }
}
