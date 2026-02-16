package com.sigrap.user.infrastructure.adapter.out.persistence;

import com.sigrap.user.domain.model.User;
import com.sigrap.user.domain.model.UserEmail;
import com.sigrap.user.domain.model.UserId;
import com.sigrap.user.domain.model.Username;
import com.sigrap.user.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementation for User repository port.
 * This adapter bridges the domain layer with the JPA persistence infrastructure.
 * 
 * <p>Responsibilities:
 * <ul>
 *   <li>Implements the UserRepositoryPort interface from the domain layer</li>
 *   <li>Translates between domain entities and JPA entities using the mapper</li>
 *   <li>Delegates actual persistence operations to the JPA repository</li>
 *   <li>Handles the impedance mismatch between domain and persistence models</li>
 * </ul>
 * 
 * <p>This is an output adapter in hexagonal architecture terminology.
 */
@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;
    private final UserPersistenceMapper mapper;

    /**
     * Constructor for dependency injection.
     *
     * @param jpaRepository the Spring Data JPA repository
     * @param mapper the MapStruct mapper for entity conversion
     */
    public UserPersistenceAdapter(
            UserJpaRepository jpaRepository,
            UserPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * Saves a user to the database.
     * Converts the domain entity to a JPA entity, persists it, and converts back.
     *
     * @param user the domain user to save
     * @return the saved user with generated ID if it was new
     */
    @Override
    public User save(User user) {
        UserJpaEntity entity = mapper.toJpaEntity(user);
        UserJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    /**
     * Finds a user by its identifier.
     *
     * @param id the user identifier
     * @return an Optional containing the domain user if found, empty otherwise
     */
    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    /**
     * Finds a user by username.
     *
     * @param username the username
     * @return an Optional containing the domain user if found, empty otherwise
     */
    @Override
    public Optional<User> findByUsername(Username username) {
        return jpaRepository.findByUsername(username.value())
                .map(mapper::toDomain);
    }

    /**
     * Finds a user by email.
     *
     * @param email the email address
     * @return an Optional containing the domain user if found, empty otherwise
     */
    @Override
    public Optional<User> findByEmail(UserEmail email) {
        return jpaRepository.findByEmail(email.value())
                .map(mapper::toDomain);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a list of all domain users
     */
    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Retrieves all enabled users from the database.
     *
     * @return a list of enabled domain users
     */
    @Override
    public List<User> findAllEnabled() {
        return jpaRepository.findByEnabledTrue().stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Checks if a user with the given username exists.
     *
     * @param username the username to check
     * @return true if a user with this username exists, false otherwise
     */
    @Override
    public boolean existsByUsername(Username username) {
        return jpaRepository.existsByUsername(username.value());
    }

    /**
     * Checks if a user with the given email exists.
     *
     * @param email the email to check
     * @return true if a user with this email exists, false otherwise
     */
    @Override
    public boolean existsByEmail(UserEmail email) {
        return jpaRepository.existsByEmail(email.value());
    }

    /**
     * Deletes a user by its identifier.
     *
     * @param id the user identifier
     */
    @Override
    public void deleteById(UserId id) {
        jpaRepository.deleteById(id.value());
    }

    /**
     * Counts the total number of users.
     *
     * @return the total count of users
     */
    @Override
    public long count() {
        return jpaRepository.count();
    }

    /**
     * Saves multiple users at once.
     *
     * @param users the list of users to save
     * @return the list of saved users
     */
    @Override
    public List<User> saveAll(List<User> users) {
        List<UserJpaEntity> entities = users.stream()
                .map(mapper::toJpaEntity)
                .toList();
        List<UserJpaEntity> savedEntities = jpaRepository.saveAll(entities);
        return savedEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }
}
