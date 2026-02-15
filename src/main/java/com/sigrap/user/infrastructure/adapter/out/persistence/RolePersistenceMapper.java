package com.sigrap.user.infrastructure.adapter.out.persistence;

import com.sigrap.user.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for converting between domain Role and RoleJpaEntity.
 * This mapper handles the translation between the domain model (pure POJOs)
 * and the persistence model (JPA entities).
 * 
 * The mapper uses custom methods to convert between value objects and their primitive representations.
 */
@Mapper(componentModel = "spring")
public interface RolePersistenceMapper {

    /**
     * Converts a domain Role to a JPA entity.
     * Maps value objects to their primitive representations.
     *
     * @param role the domain role
     * @return the JPA entity representation
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "roleIdToLong")
    @Mapping(target = "name", source = "name", qualifiedByName = "roleNameToString")
    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "permissionsToJpaEntities")
    RoleJpaEntity toJpaEntity(Role role);

    /**
     * Converts a JPA entity to a domain Role.
     * Maps primitive values to value objects.
     * Uses a custom implementation to handle the immutable domain entity.
     *
     * @param entity the JPA entity
     * @return the domain role
     */
    default Role toDomain(RoleJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        RoleId id = longToRoleId(entity.getId());
        RoleName name = stringToRoleName(entity.getName());
        Set<Permission> permissions = jpaEntitiesToPermissions(entity.getPermissions());
        
        return new Role(
            id,
            name,
            permissions,
            entity.getDescription()
        );
    }

    /**
     * Converts a RoleId value object to its Long representation.
     *
     * @param id the role ID value object
     * @return the Long value, or null if the ID is null
     */
    @Named("roleIdToLong")
    default Long roleIdToLong(RoleId id) {
        return id != null ? id.value() : null;
    }

    /**
     * Converts a Long to a RoleId value object.
     *
     * @param id the Long value
     * @return the RoleId value object, or null if the Long is null
     */
    @Named("longToRoleId")
    default RoleId longToRoleId(Long id) {
        return id != null ? new RoleId(id) : null;
    }

    /**
     * Converts a RoleName value object to its String representation.
     *
     * @param name the role name value object
     * @return the String value, or null if the name is null
     */
    @Named("roleNameToString")
    default String roleNameToString(RoleName name) {
        return name != null ? name.value() : null;
    }

    /**
     * Converts a String to a RoleName value object.
     *
     * @param name the String value
     * @return the RoleName value object, or null if the String is null
     */
    @Named("stringToRoleName")
    default RoleName stringToRoleName(String name) {
        return name != null ? new RoleName(name) : null;
    }

    /**
     * Converts a set of domain Permissions to JPA entities.
     *
     * @param permissions the domain permissions
     * @return the set of JPA entities
     */
    @Named("permissionsToJpaEntities")
    default Set<PermissionJpaEntity> permissionsToJpaEntities(Set<Permission> permissions) {
        if (permissions == null) {
            return Set.of();
        }
        PermissionPersistenceMapper permissionMapper = getPermissionMapper();
        return permissions.stream()
            .map(permissionMapper::toJpaEntity)
            .collect(Collectors.toSet());
    }

    /**
     * Converts a set of JPA entities to domain Permissions.
     *
     * @param entities the JPA entities
     * @return the set of domain permissions
     */
    @Named("jpaEntitiesToPermissions")
    default Set<Permission> jpaEntitiesToPermissions(Set<PermissionJpaEntity> entities) {
        if (entities == null) {
            return Set.of();
        }
        PermissionPersistenceMapper permissionMapper = getPermissionMapper();
        return entities.stream()
            .map(permissionMapper::toDomain)
            .collect(Collectors.toSet());
    }

    /**
     * Gets the PermissionPersistenceMapper instance.
     * This method should be implemented by MapStruct to inject the mapper.
     *
     * @return the permission persistence mapper
     */
    PermissionPersistenceMapper getPermissionMapper();
}
