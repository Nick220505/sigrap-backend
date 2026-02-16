package com.sigrap.customer.infrastructure.adapter.out.persistence;

import com.sigrap.customer.domain.model.Customer;
import com.sigrap.customer.domain.model.CustomerId;
import com.sigrap.customer.domain.model.CustomerEmail;
import com.sigrap.customer.domain.port.CustomerRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementation for Customer repository port.
 * This adapter bridges the domain layer with the JPA persistence infrastructure.
 * 
 * <p>Responsibilities:
 * <ul>
 *   <li>Implements the CustomerRepositoryPort interface from the domain layer</li>
 *   <li>Translates between domain entities and JPA entities using the mapper</li>
 *   <li>Delegates actual persistence operations to the JPA repository</li>
 *   <li>Handles the impedance mismatch between domain and persistence models</li>
 * </ul>
 * 
 * <p>This is an output adapter in hexagonal architecture terminology.
 */
@Component
public class CustomerPersistenceAdapter implements CustomerRepositoryPort {

    private final CustomerJpaRepository jpaRepository;
    private final CustomerPersistenceMapper mapper;

    /**
     * Constructor for dependency injection.
     *
     * @param jpaRepository the Spring Data JPA repository
     * @param mapper the MapStruct mapper for entity conversion
     */
    public CustomerPersistenceAdapter(
            CustomerJpaRepository jpaRepository,
            CustomerPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * Saves a customer to the database.
     * Converts the domain entity to a JPA entity, persists it, and converts back.
     *
     * @param customer the domain customer to save
     * @return the saved customer with generated ID if it was new
     */
    @Override
    public Customer save(Customer customer) {
        CustomerJpaEntity entity = mapper.toJpaEntity(customer);
        CustomerJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    /**
     * Finds a customer by their identifier.
     *
     * @param id the customer identifier
     * @return an Optional containing the domain customer if found, empty otherwise
     */
    @Override
    public Optional<Customer> findById(CustomerId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    /**
     * Retrieves all customers from the database.
     *
     * @return a list of all domain customers
     */
    @Override
    public List<Customer> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Checks if a customer with the given email exists.
     *
     * @param email the customer email to check
     * @return true if a customer with this email exists, false otherwise
     */
    @Override
    public boolean existsByEmail(CustomerEmail email) {
        return jpaRepository.existsByEmail(email.value());
    }

    /**
     * Checks if a customer with the given email exists, excluding a specific customer ID.
     * Useful for update operations to check email uniqueness.
     *
     * @param email the customer email to check
     * @param excludeId the customer ID to exclude from the check
     * @return true if another customer with the email exists, false otherwise
     */
    @Override
    public boolean existsByEmailAndIdNot(CustomerEmail email, CustomerId excludeId) {
        return jpaRepository.existsByEmailAndIdNot(email.value(), excludeId.value());
    }

    /**
     * Deletes a customer by their identifier.
     *
     * @param id the customer identifier
     */
    @Override
    public void deleteById(CustomerId id) {
        jpaRepository.deleteById(id.value());
    }

    /**
     * Deletes multiple customers by their identifiers.
     *
     * @param ids the list of customer identifiers to delete
     */
    @Override
    public void deleteAllById(List<CustomerId> ids) {
        List<Long> longIds = ids.stream()
                .map(CustomerId::value)
                .toList();
        jpaRepository.deleteAllById(longIds);
    }

    /**
     * Counts the total number of customers.
     *
     * @return the total count of customers
     */
    @Override
    public long count() {
        return jpaRepository.count();
    }

    /**
     * Saves multiple customers at once.
     *
     * @param customers the list of customers to save
     * @return the list of saved customers
     */
    @Override
    public List<Customer> saveAll(List<Customer> customers) {
        List<CustomerJpaEntity> entities = customers.stream()
                .map(mapper::toJpaEntity)
                .toList();
        List<CustomerJpaEntity> savedEntities = jpaRepository.saveAll(entities);
        return savedEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }
}
