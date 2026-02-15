package com.sigrap.user.infrastructure.adapter.out.persistence;

import com.sigrap.user.domain.model.Role;
import com.sigrap.user.domain.model.RoleId;
import com.sigrap.user.domain.model.RoleName;
import com.sigrap.user.domain.port.RoleRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementation for Role repository port.
 * This adapter bridges the domain layer with the JPA persistence infrastructure.
 * 
 * <p>Responsibilities:
 * <ul>
 *   <li>Implements the RoleRepositoryPort interface from the domain layer</li>
 *   <li>Translates between domain entities and JPA entities using the mapper</li>
 *   <li>Delegates actual persistence operations to the JPA repository</li>
 *   <li>Handles the impedance mismatch between domain and persistence models</li>
 * </ul>
 * 
 * <p>This is an output adapter in hexagonal architecture terminology.
 */
@Component
public class RolePersistenceAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository jpaRepository;
    private final RolePersistenceMapper mapper;

    /**
     * Constructor for dependency injection.
     *
     * @param jpaRepository the Spring Data JPA repository
     * @param mapper the MapStruct mapper for entity conversion
     */
    public RolePersistenceAdapter(
            RoleJpaRepository jpaRepository,
            RolePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * Saves a role to the database.
     * Converts the domain entity to a JPA entity, persists it, and converts back.
     *
     * @param role the domain role to save
     * @return the saved role with generated ID if it was new
     */
    @Override
    public Role save(Role role) {
        RoleJpaEntity entity = mapper.toJpaEntity(role);
        RoleJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    /**
     * Finds a role by its identifier.
     *
     * @param id the role identifier
     * @return an Optional containing the domain role if found, empty otherwise
     */
    @Override
    public Optional<Role> findById(RoleId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    /**
     * Finds a role by name.
     *
     * @param name the role name
     * @return an Optional containing the domain role if found, empty otherwise
     */
    @Override
    public Optional<Role> findByName(RoleName name) {
        return jpaRepository.findByName(name.value())
                .map(mapper::toDomain);
    }

    /**
     * Retrieves all roles from the database.
     *
     * @return a list of all domain roles
     */
    @Override
    public List<Role> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Checks if a role with the given name exists.
     *
     * @param name the role name to check
     * @return true if a role with this name exists, false otherwise
     */
    @Override
    public boolean existsByName(RoleName name) {
        return jpaRepository.existsByName(name.value());
    }

    /**
     * Deletes a role by its identifier.
     *
     * @param id the role identifier
     */
    @Override
    public void deleteById(RoleId id) {
        jpaRepository.deleteById(id.value());
    }
}
