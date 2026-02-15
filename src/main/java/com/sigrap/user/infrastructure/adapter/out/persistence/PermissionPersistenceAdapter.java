package com.sigrap.user.infrastructure.adapter.out.persistence;

import com.sigrap.user.domain.model.Permission;
import com.sigrap.user.domain.model.PermissionId;
import com.sigrap.user.domain.model.PermissionName;
import com.sigrap.user.domain.port.PermissionRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementation for Permission repository port.
 * This adapter bridges the domain layer with the JPA persistence infrastructure.
 * 
 * <p>Responsibilities:
 * <ul>
 *   <li>Implements the PermissionRepositoryPort interface from the domain layer</li>
 *   <li>Translates between domain entities and JPA entities using the mapper</li>
 *   <li>Delegates actual persistence operations to the JPA repository</li>
 *   <li>Handles the impedance mismatch between domain and persistence models</li>
 * </ul>
 * 
 * <p>This is an output adapter in hexagonal architecture terminology.
 */
@Component
public class PermissionPersistenceAdapter implements PermissionRepositoryPort {

    private final PermissionJpaRepository jpaRepository;
    private final PermissionPersistenceMapper mapper;

    /**
     * Constructor for dependency injection.
     *
     * @param jpaRepository the Spring Data JPA repository
     * @param mapper the MapStruct mapper for entity conversion
     */
    public PermissionPersistenceAdapter(
            PermissionJpaRepository jpaRepository,
            PermissionPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * Saves a permission to the database.
     * Converts the domain entity to a JPA entity, persists it, and converts back.
     *
     * @param permission the domain permission to save
     * @return the saved permission with generated ID if it was new
     */
    @Override
    public Permission save(Permission permission) {
        PermissionJpaEntity entity = mapper.toJpaEntity(permission);
        PermissionJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    /**
     * Finds a permission by its identifier.
     *
     * @param id the permission identifier
     * @return an Optional containing the domain permission if found, empty otherwise
     */
    @Override
    public Optional<Permission> findById(PermissionId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    /**
     * Finds a permission by name.
     *
     * @param name the permission name
     * @return an Optional containing the domain permission if found, empty otherwise
     */
    @Override
    public Optional<Permission> findByName(PermissionName name) {
        return jpaRepository.findByName(name.value())
                .map(mapper::toDomain);
    }

    /**
     * Retrieves all permissions from the database.
     *
     * @return a list of all domain permissions
     */
    @Override
    public List<Permission> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Finds all permissions for a specific resource.
     *
     * @param resource the resource name
     * @return a list of permissions for the resource
     */
    @Override
    public List<Permission> findByResource(String resource) {
        return jpaRepository.findByResource(resource).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Checks if a permission with the given name exists.
     *
     * @param name the permission name to check
     * @return true if a permission with this name exists, false otherwise
     */
    @Override
    public boolean existsByName(PermissionName name) {
        return jpaRepository.existsByName(name.value());
    }

    /**
     * Deletes a permission by its identifier.
     *
     * @param id the permission identifier
     */
    @Override
    public void deleteById(PermissionId id) {
        jpaRepository.deleteById(id.value());
    }
}
