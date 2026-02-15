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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for PermissionPersistenceAdapter.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PermissionPersistenceAdapterTest {

    @Autowired
    private PermissionPersistenceAdapter adapter;

    @Autowired
    private PermissionJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
    }

    @Test
    void shouldSaveNewPermission() {
        Permission permission = new Permission(
            new PermissionName("READ_USERS"),
            "Read users permission",
            "USER",
            "READ"
        );

        Permission saved = adapter.save(permission);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("READ_USERS", saved.getName().value());
        assertEquals("Read users permission", saved.getDescription());
        assertEquals("USER", saved.getResource());
        assertEquals("READ", saved.getAction());
    }

    @Test
    void shouldUpdateExistingPermission() {
        Permission permission = new Permission(
            new PermissionName("WRITE_USERS"),
            "Write users permission",
            "USER",
            "WRITE"
        );
        Permission saved = adapter.save(permission);

        Permission updated = new Permission(
            saved.getId(),
            new PermissionName("WRITE_USERS"),
            "Updated write users permission",
            "USER",
            "WRITE"
        );
        Permission result = adapter.save(updated);

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("Updated write users permission", result.getDescription());
    }

    @Test
    void shouldFindPermissionById() {
        Permission permission = new Permission(
            new PermissionName("DELETE_USERS"),
            "Delete users permission",
            "USER",
            "DELETE"
        );
        Permission saved = adapter.save(permission);

        Optional<Permission> found = adapter.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("DELETE_USERS", found.get().getName().value());
    }

    @Test
    void shouldReturnEmptyWhenPermissionNotFound() {
        PermissionId nonExistentId = new PermissionId(999L);

        Optional<Permission> found = adapter.findById(nonExistentId);

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindPermissionByName() {
        Permission permission = new Permission(
            new PermissionName("UPDATE_USERS"),
            "Update users permission",
            "USER",
            "UPDATE"
        );
        adapter.save(permission);

        Optional<Permission> found = adapter.findByName(new PermissionName("UPDATE_USERS"));

        assertTrue(found.isPresent());
        assertEquals("UPDATE_USERS", found.get().getName().value());
    }

    @Test
    void shouldReturnEmptyWhenPermissionNameNotFound() {
        Optional<Permission> found = adapter.findByName(new PermissionName("NONEXISTENT"));

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindAllPermissions() {
        adapter.save(new Permission(
            new PermissionName("PERM1"),
            "Permission 1",
            "RESOURCE1",
            "ACTION1"
        ));
        adapter.save(new Permission(
            new PermissionName("PERM2"),
            "Permission 2",
            "RESOURCE2",
            "ACTION2"
        ));
        adapter.save(new Permission(
            new PermissionName("PERM3"),
            "Permission 3",
            "RESOURCE3",
            "ACTION3"
        ));

        List<Permission> permissions = adapter.findAll();

        assertNotNull(permissions);
        assertEquals(3, permissions.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoPermissions() {
        List<Permission> permissions = adapter.findAll();

        assertNotNull(permissions);
        assertTrue(permissions.isEmpty());
    }

    @Test
    void shouldFindPermissionsByResource() {
        adapter.save(new Permission(
            new PermissionName("READ_PRODUCTS"),
            "Read products",
            "PRODUCT",
            "READ"
        ));
        adapter.save(new Permission(
            new PermissionName("WRITE_PRODUCTS"),
            "Write products",
            "PRODUCT",
            "WRITE"
        ));
        adapter.save(new Permission(
            new PermissionName("READ_USERS"),
            "Read users",
            "USER",
            "READ"
        ));

        List<Permission> productPermissions = adapter.findByResource("PRODUCT");

        assertNotNull(productPermissions);
        assertEquals(2, productPermissions.size());
        assertTrue(productPermissions.stream()
            .allMatch(p -> p.getResource().equals("PRODUCT")));
    }

    @Test
    void shouldReturnEmptyListWhenNoPermissionsForResource() {
        List<Permission> permissions = adapter.findByResource("NONEXISTENT");

        assertNotNull(permissions);
        assertTrue(permissions.isEmpty());
    }

    @Test
    void shouldReturnTrueWhenPermissionNameExists() {
        adapter.save(new Permission(
            new PermissionName("EXISTING"),
            "Existing permission",
            "RESOURCE",
            "ACTION"
        ));

        boolean exists = adapter.existsByName(new PermissionName("EXISTING"));

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenPermissionNameDoesNotExist() {
        boolean exists = adapter.existsByName(new PermissionName("NONEXISTENT"));

        assertFalse(exists);
    }

    @Test
    void shouldDeletePermissionById() {
        Permission permission = new Permission(
            new PermissionName("TEMP"),
            "Temporary permission",
            "TEMP",
            "TEMP"
        );
        Permission saved = adapter.save(permission);

        adapter.deleteById(saved.getId());

        Optional<Permission> found = adapter.findById(saved.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldMapAllFieldsCorrectlyBetweenDomainAndJpa() {
        Permission permission = new Permission(
            new PermissionName("COMPLETE"),
            "Complete permission description",
            "COMPLETE_RESOURCE",
            "COMPLETE_ACTION"
        );

        Permission saved = adapter.save(permission);
        Optional<Permission> retrieved = adapter.findById(saved.getId());

        assertTrue(retrieved.isPresent());
        Permission result = retrieved.get();
        assertEquals(saved.getId().value(), result.getId().value());
        assertEquals(saved.getName().value(), result.getName().value());
        assertEquals(saved.getDescription(), result.getDescription());
        assertEquals(saved.getResource(), result.getResource());
        assertEquals(saved.getAction(), result.getAction());
    }

    @Test
    void shouldEnforceUniquePermissionNameConstraint() {
        adapter.save(new Permission(
            new PermissionName("UNIQUE"),
            "Unique permission",
            "RESOURCE",
            "ACTION"
        ));

        assertThrows(Exception.class, () -> {
            adapter.save(new Permission(
                new PermissionName("UNIQUE"),
                "Another unique permission",
                "RESOURCE",
                "ACTION"
            ));
        });
    }

    @Test
    void shouldHandlePermissionWithNullDescription() {
        Permission permission = new Permission(
            new PermissionName("NODESC"),
            null,
            "RESOURCE",
            "ACTION"
        );

        Permission saved = adapter.save(permission);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("NODESC", saved.getName().value());
        assertNull(saved.getDescription());
    }

    @Test
    void shouldHandleMultiplePermissionsForSameResource() {
        adapter.save(new Permission(
            new PermissionName("CREATE_CATEGORY"),
            "Create category",
            "CATEGORY",
            "CREATE"
        ));
        adapter.save(new Permission(
            new PermissionName("READ_CATEGORY"),
            "Read category",
            "CATEGORY",
            "READ"
        ));
        adapter.save(new Permission(
            new PermissionName("UPDATE_CATEGORY"),
            "Update category",
            "CATEGORY",
            "UPDATE"
        ));
        adapter.save(new Permission(
            new PermissionName("DELETE_CATEGORY"),
            "Delete category",
            "CATEGORY",
            "DELETE"
        ));

        List<Permission> categoryPermissions = adapter.findByResource("CATEGORY");

        assertEquals(4, categoryPermissions.size());
    }
}
