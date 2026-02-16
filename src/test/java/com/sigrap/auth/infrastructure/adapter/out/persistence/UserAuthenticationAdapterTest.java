package com.sigrap.auth.infrastructure.adapter.out.persistence;

import com.sigrap.auth.domain.model.Credentials;
import com.sigrap.auth.domain.model.Email;
import com.sigrap.auth.domain.model.Password;
import com.sigrap.auth.domain.model.RegistrationData;
import com.sigrap.auth.domain.port.PasswordEncoderPort;
import com.sigrap.auth.domain.port.UserAuthenticationPort.UserInfo;
import com.sigrap.user.infrastructure.adapter.out.persistence.RoleJpaEntity;
import com.sigrap.user.infrastructure.adapter.out.persistence.RoleJpaRepository;
import com.sigrap.user.infrastructure.adapter.out.persistence.UserJpaEntity;
import com.sigrap.user.infrastructure.adapter.out.persistence.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserAuthenticationAdapter.
 * Tests the adapter with mocked repositories and password encoder.
 */
@ExtendWith(MockitoExtension.class)
class UserAuthenticationAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private RoleJpaRepository roleJpaRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    private UserAuthenticationAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserAuthenticationAdapter(
            userJpaRepository,
            roleJpaRepository,
            passwordEncoder
        );
    }

    @Test
    void shouldAuthenticateValidCredentials() {
        // Given
        Email email = new Email("user@example.com");
        Password password = new Password("Password123!");
        Credentials credentials = new Credentials(email, password);

        UserJpaEntity user = createEnabledUser(email.value(), "hashedPassword");
        when(userJpaRepository.findByEmail(email.value())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "hashedPassword")).thenReturn(true);

        // When & Then
        assertDoesNotThrow(() -> adapter.authenticate(credentials));
        verify(userJpaRepository).findByEmail(email.value());
        verify(passwordEncoder).matches(password, "hashedPassword");
    }

    @Test
    void shouldThrowExceptionForNonExistentUser() {
        // Given
        Email email = new Email("nonexistent@example.com");
        Password password = new Password("Password123!");
        Credentials credentials = new Credentials(email, password);

        when(userJpaRepository.findByEmail(email.value())).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> adapter.authenticate(credentials)
        );
        assertEquals("Invalid email or password", exception.getMessage());
        verify(passwordEncoder, never()).matches(any(), anyString());
    }

    @Test
    void shouldThrowExceptionForDisabledUser() {
        // Given
        Email email = new Email("disabled@example.com");
        Password password = new Password("Password123!");
        Credentials credentials = new Credentials(email, password);

        UserJpaEntity user = createDisabledUser(email.value(), "hashedPassword");
        when(userJpaRepository.findByEmail(email.value())).thenReturn(Optional.of(user));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> adapter.authenticate(credentials)
        );
        assertEquals("User account is disabled", exception.getMessage());
        verify(passwordEncoder, never()).matches(any(), anyString());
    }

    @Test
    void shouldThrowExceptionForIncorrectPassword() {
        // Given
        Email email = new Email("user@example.com");
        Password password = new Password("WrongPassword123!");
        Credentials credentials = new Credentials(email, password);

        UserJpaEntity user = createEnabledUser(email.value(), "hashedPassword");
        when(userJpaRepository.findByEmail(email.value())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "hashedPassword")).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> adapter.authenticate(credentials)
        );
        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void shouldFindUserByEmail() {
        // Given
        Email email = new Email("user@example.com");
        UserJpaEntity user = createEnabledUser(email.value(), "hashedPassword");
        when(userJpaRepository.findByEmail(email.value())).thenReturn(Optional.of(user));

        // When
        Optional<UserInfo> result = adapter.findUserByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(email.value(), result.get().email().value());
        assertEquals("Test User", result.get().name());
        verify(userJpaRepository).findByEmail(email.value());
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        // Given
        Email email = new Email("nonexistent@example.com");
        when(userJpaRepository.findByEmail(email.value())).thenReturn(Optional.empty());

        // When
        Optional<UserInfo> result = adapter.findUserByEmail(email);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnTrueWhenUserExists() {
        // Given
        Email email = new Email("existing@example.com");
        when(userJpaRepository.existsByEmail(email.value())).thenReturn(true);

        // When
        boolean exists = adapter.existsByEmail(email);

        // Then
        assertTrue(exists);
        verify(userJpaRepository).existsByEmail(email.value());
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotExist() {
        // Given
        Email email = new Email("nonexistent@example.com");
        when(userJpaRepository.existsByEmail(email.value())).thenReturn(false);

        // When
        boolean exists = adapter.existsByEmail(email);

        // Then
        assertFalse(exists);
    }

    @Test
    void shouldRegisterNewUser() {
        // Given
        RegistrationData registrationData = new RegistrationData(
            "New User",
            new Email("newuser@example.com"),
            new Password("Password123!")
        );
        String encodedPassword = "encodedPassword123";

        RoleJpaEntity userRole = createRole("USER");
        when(roleJpaRepository.findByName("USER")).thenReturn(Optional.of(userRole));

        UserJpaEntity savedUser = createEnabledUser("newuser@example.com", encodedPassword);
        when(userJpaRepository.save(any(UserJpaEntity.class))).thenReturn(savedUser);

        // When
        UserInfo result = adapter.registerUser(registrationData, encodedPassword);

        // Then
        assertNotNull(result);
        assertEquals("newuser@example.com", result.email().value());
        assertEquals("Test User", result.name());
        verify(roleJpaRepository).findByName("USER");
        verify(userJpaRepository).save(any(UserJpaEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenUserRoleNotFound() {
        // Given
        RegistrationData registrationData = new RegistrationData(
            "New User",
            new Email("newuser@example.com"),
            new Password("Password123!")
        );
        String encodedPassword = "encodedPassword123";

        when(roleJpaRepository.findByName("USER")).thenReturn(Optional.empty());

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> adapter.registerUser(registrationData, encodedPassword)
        );
        assertTrue(exception.getMessage().contains("Default USER role not found"));
        verify(userJpaRepository, never()).save(any());
    }

    @Test
    void shouldUpdateLastLogin() {
        // Given
        Email email = new Email("user@example.com");
        UserJpaEntity user = createEnabledUser(email.value(), "hashedPassword");
        when(userJpaRepository.findByEmail(email.value())).thenReturn(Optional.of(user));

        // When & Then
        assertDoesNotThrow(() -> adapter.updateLastLogin(email));
        verify(userJpaRepository).findByEmail(email.value());
    }

    @Test
    void shouldHandleUpdateLastLoginForNonExistentUser() {
        // Given
        Email email = new Email("nonexistent@example.com");
        when(userJpaRepository.findByEmail(email.value())).thenReturn(Optional.empty());

        // When & Then
        assertDoesNotThrow(() -> adapter.updateLastLogin(email));
    }

    @Test
    void shouldMapUserInfoCorrectly() {
        // Given
        Email email = new Email("user@example.com");
        UserJpaEntity user = createEnabledUser(email.value(), "hashedPassword");
        when(userJpaRepository.findByEmail(email.value())).thenReturn(Optional.of(user));

        // When
        Optional<UserInfo> result = adapter.findUserByEmail(email);

        // Then
        assertTrue(result.isPresent());
        UserInfo userInfo = result.get();
        assertEquals(email.value(), userInfo.email().value());
        assertEquals("Test User", userInfo.name());
        assertEquals("hashedPassword", userInfo.encodedPassword());
        assertEquals("USER", userInfo.role());
        assertNotNull(userInfo.lastLogin());
    }

    // Helper methods

    private UserJpaEntity createEnabledUser(String email, String hashedPassword) {
        RoleJpaEntity role = createRole("USER");
        Set<RoleJpaEntity> roles = new HashSet<>();
        roles.add(role);

        return UserJpaEntity.builder()
            .id(1L)
            .username("Test User")
            .email(email)
            .hashedPassword(hashedPassword)
            .enabled(true)
            .roles(roles)
            .build();
    }

    private UserJpaEntity createDisabledUser(String email, String hashedPassword) {
        RoleJpaEntity role = createRole("USER");
        Set<RoleJpaEntity> roles = new HashSet<>();
        roles.add(role);

        return UserJpaEntity.builder()
            .id(1L)
            .username("Disabled User")
            .email(email)
            .hashedPassword(hashedPassword)
            .enabled(false)
            .roles(roles)
            .build();
    }

    private RoleJpaEntity createRole(String name) {
        RoleJpaEntity role = new RoleJpaEntity();
        role.setId(1L);
        role.setName(name);
        return role;
    }
}
