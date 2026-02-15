package com.sigrap.user.infrastructure.adapter.out.persistence;

import com.sigrap.user.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for converting between domain User and UserJpaEntity.
 * This mapper handles the translation between the domain model (pure POJOs)
 * and the persistence model (JPA entities).
 * 
 * The mapper uses custom methods to convert between value objects and their primitive representations.
 */
@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    /**
     * Converts a domain User to a JPA entity.
     * Maps value objects to their primitive representations.
     *
     * @param user the domain user
     * @return the JPA entity representation
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "userIdToLong")
    @Mapping(target = "username", source = "username", qualifiedByName = "usernameToString")
    @Mapping(target = "email", source = "email", qualifiedByName = "userEmailToString")
    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToJpaEntities")
    UserJpaEntity toJpaEntity(User user);

    /**
     * Converts a JPA entity to a domain User.
     * Maps primitive values to value objects.
     * Uses a custom implementation to handle the immutable domain entity.
     *
     * @param entity the JPA entity
     * @return the domain user
     */
    default User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        UserId id = longToUserId(entity.getId());
        Username username = stringToUsername(entity.getUsername());
        UserEmail email = stringToUserEmail(entity.getEmail());
        Set<Role> roles = jpaEntitiesToRoles(entity.getRoles());
        
        return new User(
            id,
            username,
            email,
            entity.getHashedPassword(),
            roles,
            entity.isEnabled(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Converts a UserId value object to its Long representation.
     *
     * @param id the user ID value object
     * @return the Long value, or null if the ID is null
     */
    @Named("userIdToLong")
    default Long userIdToLong(UserId id) {
        return id != null ? id.value() : null;
    }

    /**
     * Converts a Long to a UserId value object.
     *
     * @param id the Long value
     * @return the UserId value object, or null if the Long is null
     */
    @Named("longToUserId")
    default UserId longToUserId(Long id) {
        return id != null ? new UserId(id) : null;
    }

    /**
     * Converts a Username value object to its String representation.
     *
     * @param username the username value object
     * @return the String value, or null if the username is null
     */
    @Named("usernameToString")
    default String usernameToString(Username username) {
        return username != null ? username.value() : null;
    }

    /**
     * Converts a String to a Username value object.
     *
     * @param username the String value
     * @return the Username value object, or null if the String is null
     */
    @Named("stringToUsername")
    default Username stringToUsername(String username) {
        return username != null ? new Username(username) : null;
    }

    /**
     * Converts a UserEmail value object to its String representation.
     *
     * @param email the user email value object
     * @return the String value, or null if the email is null
     */
    @Named("userEmailToString")
    default String userEmailToString(UserEmail email) {
        return email != null ? email.value() : null;
    }

    /**
     * Converts a String to a UserEmail value object.
     *
     * @param email the String value
     * @return the UserEmail value object, or null if the String is null
     */
    @Named("stringToUserEmail")
    default UserEmail stringToUserEmail(String email) {
        return email != null ? new UserEmail(email) : null;
    }

    /**
     * Converts a set of domain Roles to JPA entities.
     *
     * @param roles the domain roles
     * @return the set of JPA entities
     */
    @Named("rolesToJpaEntities")
    default Set<RoleJpaEntity> rolesToJpaEntities(Set<Role> roles) {
        if (roles == null) {
            return Set.of();
        }
        // Manual conversion to avoid circular dependency
        return roles.stream()
            .map(role -> {
                RoleJpaEntity entity = new RoleJpaEntity();
                entity.setId(role.getId() != null ? role.getId().value() : null);
                entity.setName(role.getName().value());
                entity.setDescription(role.getDescription());
                // Don't map permissions here to avoid deep nesting
                entity.setPermissions(new HashSet<>());
                return entity;
            })
            .collect(Collectors.toSet());
    }

    /**
     * Converts a set of JPA entities to domain Roles.
     *
     * @param entities the JPA entities
     * @return the set of domain roles
     */
    @Named("jpaEntitiesToRoles")
    default Set<Role> jpaEntitiesToRoles(Set<RoleJpaEntity> entities) {
        if (entities == null) {
            return Set.of();
        }
        // Manual conversion to avoid circular dependency
        return entities.stream()
            .map(entity -> {
                RoleId id = entity.getId() != null ? new RoleId(entity.getId()) : null;
                RoleName name = new RoleName(entity.getName());
                // Convert permissions
                Set<Permission> permissions = entity.getPermissions() != null
                    ? entity.getPermissions().stream()
                        .map(permEntity -> new Permission(
                            permEntity.getId() != null ? new PermissionId(permEntity.getId()) : null,
                            new PermissionName(permEntity.getName()),
                            permEntity.getResource(),
                            permEntity.getAction()
                        ))
                        .collect(Collectors.toSet())
                    : new HashSet<>();
                return new Role(id, name, permissions, entity.getDescription());
            })
            .collect(Collectors.toSet());
    }
}
