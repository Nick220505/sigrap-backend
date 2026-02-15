package com.sigrap.user.infrastructure.adapter.out.persistence;

import com.sigrap.user.domain.model.Permission;
import com.sigrap.user.domain.model.PermissionId;
import com.sigrap.user.domain.model.PermissionName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between domain Permission and PermissionJpaEntity.
 * This mapper handles the translation between the domain model (pure POJOs)
 * and the persistence model (JPA entities).
 * 
 * The mapper uses custom methods to convert between value objects and their primitive representations.
 */
@Mapper(componentModel = "spring")
public interface PermissionPersistenceMapper {

    /**
     * Converts a domain Permission to a JPA entity.
     * Maps value objects to their primitive representations.
     *
     * @param permission the domain permission
     * @return the JPA entity representation
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "permissionIdToLong")
    @Mapping(target = "name", source = "name", qualifiedByName = "permissionNameToString")
    PermissionJpaEntity toJpaEntity(Permission permission);

    /**
     * Converts a JPA entity to a domain Permission.
     * Maps primitive values to value objects.
     * Uses a custom implementation to handle the immutable domain entity.
     *
     * @param entity the JPA entity
     * @return the domain permission
     */
    default Permission toDomain(PermissionJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        PermissionId id = longToPermissionId(entity.getId());
        PermissionName name = stringToPermissionName(entity.getName());
        
        return new Permission(
            id,
            name,
            entity.getResource(),
            entity.getAction()
        );
    }

    /**
     * Converts a PermissionId value object to its Long representation.
     *
     * @param id the permission ID value object
     * @return the Long value, or null if the ID is null
     */
    @Named("permissionIdToLong")
    default Long permissionIdToLong(PermissionId id) {
        return id != null ? id.value() : null;
    }

    /**
     * Converts a Long to a PermissionId value object.
     *
     * @param id the Long value
     * @return the PermissionId value object, or null if the Long is null
     */
    @Named("longToPermissionId")
    default PermissionId longToPermissionId(Long id) {
        return id != null ? new PermissionId(id) : null;
    }

    /**
     * Converts a PermissionName value object to its String representation.
     *
     * @param name the permission name value object
     * @return the String value, or null if the name is null
     */
    @Named("permissionNameToString")
    default String permissionNameToString(PermissionName name) {
        return name != null ? name.value() : null;
    }

    /**
     * Converts a String to a PermissionName value object.
     *
     * @param name the String value
     * @return the PermissionName value object, or null if the String is null
     */
    @Named("stringToPermissionName")
    default PermissionName stringToPermissionName(String name) {
        return name != null ? new PermissionName(name) : null;
    }
}
