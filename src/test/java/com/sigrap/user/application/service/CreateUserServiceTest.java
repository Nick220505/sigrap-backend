package com.sigrap.user.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.user.application.port.in.command.CreateUserCommand;
import com.sigrap.user.application.port.out.PasswordHasherPort;
import com.sigrap.user.domain.model.User;
import com.sigrap.user.domain.model.UserEmail;
import com.sigrap.user.domain.model.UserId;
import com.sigrap.user.domain.model.Username;
import com.sigrap.user.domain.port.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {
    
    @Mock
    private UserRepositoryPort userRepository;
    
    @Mock
    private PasswordHasherPort passwordHasher;
    
    @Mock
    private EventPublisherPort eventPublisher;
    
    @InjectMocks
    private CreateUserService createUserService;
    
    @BeforeEach
    void setUp() {
        reset(userRepository, passwordHasher, eventPublisher);
    }
    
    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        CreateUserCommand command = new CreateUserCommand(
            "john_doe",
            "john@example.com",
            "SecurePass123"
        );
        
        when(userRepository.existsByUsername(any(Username.class))).thenReturn(false);
        when(userRepository.existsByEmail(any(UserEmail.class))).thenReturn(false);
        when(passwordHasher.hash(anyString())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new User(
                new UserId(1L),
                user.getUsername(),
                user.getEmail(),
                user.getHashedPassword(),
                new HashSet<>(),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
            );
        });
        
        // When
        User result = createUserService.create(command);
        
        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("john_doe", result.getUsername().value());
        assertEquals("john@example.com", result.getEmail().value());
        assertEquals("hashed_password", result.getHashedPassword());
        assertTrue(result.isEnabled());
        
        verify(userRepository).existsByUsername(any(Username.class));
        verify(userRepository).existsByEmail(any(UserEmail.class));
        verify(passwordHasher).hash("SecurePass123");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        // Given
        CreateUserCommand command = new CreateUserCommand(
            "existing_user",
            "new@example.com",
            "password"
        );
        
        when(userRepository.existsByUsername(any(Username.class))).thenReturn(true);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createUserService.create(command)
        );
        
        assertTrue(exception.getMessage().contains("already exists"));
        assertTrue(exception.getMessage().contains("existing_user"));
        
        verify(userRepository).existsByUsername(any(Username.class));
        verify(userRepository, never()).existsByEmail(any(UserEmail.class));
        verify(passwordHasher, never()).hash(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        CreateUserCommand command = new CreateUserCommand(
            "new_user",
            "existing@example.com",
            "password"
        );
        
        when(userRepository.existsByUsername(any(Username.class))).thenReturn(false);
        when(userRepository.existsByEmail(any(UserEmail.class))).thenReturn(true);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createUserService.create(command)
        );
        
        assertTrue(exception.getMessage().contains("already exists"));
        assertTrue(exception.getMessage().contains("existing@example.com"));
        
        verify(userRepository).existsByUsername(any(Username.class));
        verify(userRepository).existsByEmail(any(UserEmail.class));
        verify(passwordHasher, never()).hash(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenUsernameIsInvalid() {
        // Given
        CreateUserCommand command = new CreateUserCommand(
            "ab",  // Too short
            "test@example.com",
            "password"
        );
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> createUserService.create(command));
        
        verifyNoInteractions(userRepository, passwordHasher);
    }
    
    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        // Given
        CreateUserCommand command = new CreateUserCommand(
            "valid_user",
            "invalid-email",
            "password"
        );
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> createUserService.create(command));
        
        verifyNoInteractions(userRepository, passwordHasher);
    }
}
