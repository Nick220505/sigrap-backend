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
 * Integration tests for UserPersistenceAdapter.
 * Tests the adapter with a real database (H2) using Spring Boot test context.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserPersistenceAdapterTest {

    @Autowired
    private UserPersistenceAdapter adapter;

    @Autowired
    private UserJpaRepository jpaRepository;

    @Autowired
    private RoleJpaRepository roleJpaRepository;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
        roleJpaRepository.deleteAll();
    }

    @Test
    void shouldSaveNewUser() {
        User user = new User(
            new Username("johndoe"),
            new UserEmail("john.doe@example.com"),
            "hashedPassword123",
            "John",
            "Doe"
        );

        User saved = adapter.save(user);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("johndoe", saved.getUsername().value());
        assertEquals("john.doe@example.com", saved.getEmail().value());
        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
        assertTrue(saved.isEnabled());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldUpdateExistingUser() {
        User user = new User(
            new Username("janedoe"),
            new UserEmail("jane.doe@example.com"),
            "hashedPassword456",
            "Jane",
            "Doe"
        );
        User saved = adapter.save(user);

        User updated = new User(
            saved.getId(),
            saved.getUsername(),
            new UserEmail("jane.smith@example.com"),
            "newHashedPassword",
            "Jane",
            "Smith",
            saved.isEnabled(),
            saved.getRoles(),
            saved.getCreatedAt(),
            saved.getUpdatedAt()
        );
        User result = adapter.save(updated);

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("jane.smith@example.com", result.getEmail().value());
        assertEquals("Smith", result.getLastName());
    }

    @Test
    void shouldFindUserById() {
        User user = new User(
            new Username("bobwilson"),
            new UserEmail("bob.wilson@example.com"),
            "hashedPassword789",
            "Bob",
            "Wilson"
        );
        User saved = adapter.save(user);

        Optional<User> found = adapter.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("bobwilson", found.get().getUsername().value());
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        UserId nonExistentId = new UserId(999L);

        Optional<User> found = adapter.findById(nonExistentId);

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindUserByUsername() {
        User user = new User(
            new Username("alicebrown"),
            new UserEmail("alice.brown@example.com"),
            "hashedPassword",
            "Alice",
            "Brown"
        );
        adapter.save(user);

        Optional<User> found = adapter.findByUsername(new Username("alicebrown"));

        assertTrue(found.isPresent());
        assertEquals("alicebrown", found.get().getUsername().value());
        assertEquals("alice.brown@example.com", found.get().getEmail().value());
    }

    @Test
    void shouldReturnEmptyWhenUsernameNotFound() {
        Optional<User> found = adapter.findByUsername(new Username("nonexistent"));

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindUserByEmail() {
        User user = new User(
            new Username("charlie"),
            new UserEmail("charlie@example.com"),
            "hashedPassword",
            "Charlie",
            "Brown"
        );
        adapter.save(user);

        Optional<User> found = adapter.findByEmail(new UserEmail("charlie@example.com"));

        assertTrue(found.isPresent());
        assertEquals("charlie", found.get().getUsername().value());
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {
        Optional<User> found = adapter.findByEmail(new UserEmail("nonexistent@example.com"));

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindAllUsers() {
        adapter.save(new User(
            new Username("user1"),
            new UserEmail("user1@example.com"),
            "password1",
            "User",
            "One"
        ));
        adapter.save(new User(
            new Username("user2"),
            new UserEmail("user2@example.com"),
            "password2",
            "User",
            "Two"
        ));
        adapter.save(new User(
            new Username("user3"),
            new UserEmail("user3@example.com"),
            "password3",
            "User",
            "Three"
        ));

        List<User> users = adapter.findAll();

        assertNotNull(users);
        assertEquals(3, users.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoUsers() {
        List<User> users = adapter.findAll();

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void shouldFindAllEnabledUsers() {
        User enabledUser1 = adapter.save(new User(
            new Username("enabled1"),
            new UserEmail("enabled1@example.com"),
            "password",
            "Enabled",
            "One"
        ));
        
        User enabledUser2 = adapter.save(new User(
            new Username("enabled2"),
            new UserEmail("enabled2@example.com"),
            "password",
            "Enabled",
            "Two"
        ));
        
        User disabledUser = adapter.save(new User(
            new Username("disabled"),
            new UserEmail("disabled@example.com"),
            "password",
            "Disabled",
            "User"
        ));
        disabledUser.disable();
        adapter.save(disabledUser);

        List<User> enabledUsers = adapter.findAllEnabled();

        assertNotNull(enabledUsers);
        assertEquals(2, enabledUsers.size());
        assertTrue(enabledUsers.stream().allMatch(User::isEnabled));
    }

    @Test
    void shouldReturnTrueWhenUsernameExists() {
        adapter.save(new User(
            new Username("existinguser"),
            new UserEmail("existing@example.com"),
            "password",
            "Existing",
            "User"
        ));

        boolean exists = adapter.existsByUsername(new Username("existinguser"));

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenUsernameDoesNotExist() {
        boolean exists = adapter.existsByUsername(new Username("nonexistent"));

        assertFalse(exists);
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        adapter.save(new User(
            new Username("testuser"),
            new UserEmail("test@example.com"),
            "password",
            "Test",
            "User"
        ));

        boolean exists = adapter.existsByEmail(new UserEmail("test@example.com"));

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        boolean exists = adapter.existsByEmail(new UserEmail("nonexistent@example.com"));

        assertFalse(exists);
    }

    @Test
    void shouldDeleteUserById() {
        User user = new User(
            new Username("tempuser"),
            new UserEmail("temp@example.com"),
            "password",
            "Temp",
            "User"
        );
        User saved = adapter.save(user);

        adapter.deleteById(saved.getId());

        Optional<User> found = adapter.findById(saved.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldHandleUserWithRoles() {
        Role adminRole = new Role(new RoleName("ADMIN"), "Administrator role");
        RoleJpaEntity roleEntity = new RoleJpaEntity();
        roleEntity.setName("ADMIN");
        roleEntity.setDescription("Administrator role");
        roleEntity = roleJpaRepository.save(roleEntity);
        
        Role savedRole = new Role(
            new RoleId(roleEntity.getId()),
            new RoleName(roleEntity.getName()),
            roleEntity.getDescription(),
            Set.of()
        );

        User user = new User(
            new Username("adminuser"),
            new UserEmail("admin@example.com"),
            "password",
            "Admin",
            "User"
        );
        user.assignRole(savedRole);
        User saved = adapter.save(user);

        Optional<User> found = adapter.findById(saved.getId());
        assertTrue(found.isPresent());
        assertFalse(found.get().getRoles().isEmpty());
    }

    @Test
    void shouldPreserveTimestampsWhenSaving() {
        User user = new User(
            new Username("timestampuser"),
            new UserEmail("timestamp@example.com"),
            "password",
            "Timestamp",
            "User"
        );

        User saved = adapter.save(user);

        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldMapAllFieldsCorrectlyBetweenDomainAndJpa() {
        User user = new User(
            new Username("completeuser"),
            new UserEmail("complete@example.com"),
            "hashedPassword123",
            "Complete",
            "User"
        );

        User saved = adapter.save(user);
        Optional<User> retrieved = adapter.findById(saved.getId());

        assertTrue(retrieved.isPresent());
        User result = retrieved.get();
        assertEquals(saved.getId().value(), result.getId().value());
        assertEquals(saved.getUsername().value(), result.getUsername().value());
        assertEquals(saved.getEmail().value(), result.getEmail().value());
        assertEquals(saved.getPasswordHash(), result.getPasswordHash());
        assertEquals(saved.getFirstName(), result.getFirstName());
        assertEquals(saved.getLastName(), result.getLastName());
        assertEquals(saved.isEnabled(), result.isEnabled());
    }

    @Test
    void shouldEnforceUniqueUsernameConstraint() {
        adapter.save(new User(
            new Username("uniqueuser"),
            new UserEmail("unique1@example.com"),
            "password",
            "Unique",
            "One"
        ));

        assertThrows(Exception.class, () -> {
            adapter.save(new User(
                new Username("uniqueuser"),
                new UserEmail("unique2@example.com"),
                "password",
                "Unique",
                "Two"
            ));
        });
    }

    @Test
    void shouldEnforceUniqueEmailConstraint() {
        adapter.save(new User(
            new Username("user1"),
            new UserEmail("unique@example.com"),
            "password",
            "User",
            "One"
        ));

        assertThrows(Exception.class, () -> {
            adapter.save(new User(
                new Username("user2"),
                new UserEmail("unique@example.com"),
                "password",
                "User",
                "Two"
            ));
        });
    }
}
