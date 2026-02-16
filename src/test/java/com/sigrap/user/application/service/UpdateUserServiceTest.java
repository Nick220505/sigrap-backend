package com.sigrap.user.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.user.application.port.in.command.UpdateUserCommand;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserServiceTest {
    
    @Mock
    private UserRepositoryPort userRepository;
    
    @Mock
    private PasswordHasherPort passwordHasher;
    
    @InjectMocks
    private UpdateUserService updateUserService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        reset(userRepository, passwordHasher);
        testUser = new User(
            new UserId(1L),
            new Username("john_doe"),
            new UserEmail("john@example.com"),
            "old_hashed_password",
            new HashSet<>(),
            true,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
    
    @Test
    void shouldUpdateUserEmail() {
        // Given
        UpdateUserCommand command = new UpdateUserCommand("newemail@example.com", null);
        
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(any(UserEmail.class))).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        User result = updateUserService.update(1L, command);
        
        // Then
        assertNotNull(result);
        assertEquals("newemail@example.com", result.getEmail().value());
        
        verify(userRepository).findById(any(UserId.class));
        verify(userRepository).existsByEmail(any(UserEmail.class));
        verify(userRepository).save(any(User.class));
        verify(passwordHasher, never()).hash(anyString());
    }
    
    @Test
    void shouldUpdateUserPassword() {
        // Given
        UpdateUserCommand command = new UpdateUserCommand(null, "NewPassword123");
        
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(testUser));
        when(passwordHasher.hash(anyString())).thenReturn("new_hashed_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        User result = updateUserService.update(1L, command);
        
        // Then
        assertNotNull(result);
        assertEquals("new_hashed_password", result.getHashedPassword());
        
        verify(userRepository).findById(any(UserId.class));
        verify(passwordHasher).hash("NewPassword123");
        verify(userRepository).save(any(User.class));
        verify(userRepository, never()).existsByEmail(any(UserEmail.class));
    }
    
    @Test
    void shouldUpdateBothEmailAndPassword() {
        // Given
        UpdateUserCommand command = new UpdateUserCommand("newemail@example.com", "NewPassword123");
        
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(any(UserEmail.class))).thenReturn(false);
        when(passwordHasher.hash(anyString())).thenReturn("new_hashed_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        User result = updateUserService.update(1L, command);
        
        // Then
        assertNotNull(result);
        assertEquals("newemail@example.com", result.getEmail().value());
        assertEquals("new_hashed_password", result.getHashedPassword());
        
        verify(userRepository).findById(any(UserId.class));
        verify(userRepository).existsByEmail(any(UserEmail.class));
        verify(passwordHasher).hash("NewPassword123");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        UpdateUserCommand command = new UpdateUserCommand("newemail@example.com", null);
        
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.empty());
        
        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> updateUserService.update(999L, command)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        
        verify(userRepository).findById(any(UserId.class));
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenNewEmailAlreadyExists() {
        // Given
        UpdateUserCommand command = new UpdateUserCommand("existing@example.com", null);
        
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(any(UserEmail.class))).thenReturn(true);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> updateUserService.update(1L, command)
        );
        
        assertTrue(exception.getMessage().contains("already exists"));
        
        verify(userRepository).findById(any(UserId.class));
        verify(userRepository).existsByEmail(any(UserEmail.class));
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldNotUpdateWhenEmailIsBlank() {
        // Given
        UpdateUserCommand command = new UpdateUserCommand("   ", null);
        
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        User result = updateUserService.update(1L, command);
        
        // Then
        assertEquals("john@example.com", result.getEmail().value());
        
        verify(userRepository).findById(any(UserId.class));
        verify(userRepository, never()).existsByEmail(any(UserEmail.class));
    }
}
