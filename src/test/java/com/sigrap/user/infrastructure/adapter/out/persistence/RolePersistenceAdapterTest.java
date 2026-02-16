package com.sigrap.user.infrastructure.adapter.out.persistence;

import com.sigrap.user.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for RolePersistenceAdapter.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RolePersistenceAdapterTest {

    @Autowired
    private RolePersistenceAdapter adapter;

    @Autowired
    private RoleJpaRepository jpaRepository;

    @Autowired
    private PermissionJpaRepository permissionJpaRepository;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
        permissionJpaRepository.deleteAll();
    }

    @Test
    void shouldSaveNewRole() {
        Role role = new Role(
            new RoleName("ADMIN"),
            "Administrator role"
        );

        Role saved = adapter.save(role);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("ADMIN", saved.getName().value());
        assertEquals("Administrator role", saved.getDescription());
    }

    @Test
    void shouldUpdateExistingRole() {
        Role role = new Role(
            new RoleName("USER"),
            "User role"
        );
        Role saved = adapter.save(role);

        Role updated = new Role(
            saved.getId(),
            new RoleName("USER"),
            saved.getPermissions(),
            "Updated user role"
        );
        Role result = adapter.save(updated);

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("Updated user role", result.getDescription());
    }

    @Test
    void shouldFindRoleById() {
        Role role = new Role(
            new RoleName("MANAGER"),
            "Manager role"
        );
        Role saved = adapter.save(role);

        Optional<Role> found = adapter.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("MANAGER", found.get().getName().value());
    }

    @Test
    void shouldReturnEmptyWhenRoleNotFound() {
        RoleId nonExistentId = new RoleId(999L);

        Optional<Role> found = adapter.findById(nonExistentId);

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindRoleByName() {
        Role role = new Role(
            new RoleName("SUPERVISOR"),
            "Supervisor role"
        );
        adapter.save(role);

        Optional<Role> found = adapter.findByName(new RoleName("SUPERVISOR"));

        assertTrue(found.isPresent());
        assertEquals("SUPERVISOR", found.get().getName().value());
    }

    @Test
    void shouldReturnEmptyWhenRoleNameNotFound() {
        Optional<Role> found = adapter.findByName(new RoleName("NONEXISTENT"));

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindAllRoles() {
        adapter.save(new Role(new RoleName("ROLE1"), "Role 1"));
        adapter.save(new Role(new RoleName("ROLE2"), "Role 2"));
        adapter.save(new Role(new RoleName("ROLE3"), "Role 3"));

        List<Role> roles = adapter.findAll();

        assertNotNull(roles);
        assertEquals(3, roles.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoRoles() {
        List<Role> roles = adapter.findAll();

        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }

    @Test
    void shouldReturnTrueWhenRoleNameExists() {
        adapter.save(new Role(new RoleName("EXISTING"), "Existing role"));

        boolean exists = adapter.existsByName(new RoleName("EXISTING"));

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenRoleNameDoesNotExist() {
        boolean exists = adapter.existsByName(new RoleName("NONEXISTENT"));

        assertFalse(exists);
    }

    @Test
    void shouldDeleteRoleById() {
        Role role = new Role(new RoleName("TEMP"), "Temporary role");
        Role saved = adapter.save(role);

        adapter.deleteById(saved.getId());

        Optional<Role> found = adapter.findById(saved.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldHandleRoleWithPermissions() {
        PermissionJpaEntity permEntity = new PermissionJpaEntity();
        permEntity.setName("READ_USERS");
        permEntity.setResource("USER");
        permEntity.setAction("READ");
        permEntity = permissionJpaRepository.save(permEntity);

        Permission permission = new Permission(
            new PermissionId(permEntity.getId()),
            new PermissionName(permEntity.getName()),
            permEntity.getResource(),
            permEntity.getAction()
        );

        Role role = new Role(
            new RoleName("ADMIN"),
            "Admin with permissions"
        );
        role.addPermission(permission);
        Role saved = adapter.save(role);

        Optional<Role> found = adapter.findById(saved.getId());
        assertTrue(found.isPresent());
        assertFalse(found.get().getPermissions().isEmpty());
    }

    @Test
    void shouldMapAllFieldsCorrectlyBetweenDomainAndJpa() {
        Role role = new Role(
            new RoleName("COMPLETE"),
            "Complete role description"
        );

        Role saved = adapter.save(role);
        Optional<Role> retrieved = adapter.findById(saved.getId());

        assertTrue(retrieved.isPresent());
        Role result = retrieved.get();
        assertEquals(saved.getId().value(), result.getId().value());
        assertEquals(saved.getName().value(), result.getName().value());
        assertEquals(saved.getDescription(), result.getDescription());
    }

    @Test
    void shouldEnforceUniqueRoleNameConstraint() {
        adapter.save(new Role(new RoleName("UNIQUE"), "Unique role"));

        assertThrows(Exception.class, () -> {
            adapter.save(new Role(new RoleName("UNIQUE"), "Another unique role"));
        });
    }

    @Test
    void shouldHandleRoleWithNullDescription() {
        Role role = new Role(new RoleName("NODESC"), null);

        Role saved = adapter.save(role);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("NODESC", saved.getName().value());
        assertNull(saved.getDescription());
    }
}
