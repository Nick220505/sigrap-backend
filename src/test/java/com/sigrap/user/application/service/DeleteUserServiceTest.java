package com.sigrap.user.application.service;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserServiceTest {
    
    @Mock
    private UserRepositoryPort userRepository;
    
    @InjectMocks
    private DeleteUserService deleteUserService;
    
    @BeforeEach
    void setUp() {
        reset(userRepository);
    }
    
    @Test
    void shouldDeleteUserSuccessfully() {
        // Given
        User testUser = new User(
            new UserId(1L),
            new Username("john_doe"),
            new UserEmail("john@example.com"),
            "hashed_password",
            new HashSet<>(),
            true,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(any(UserId.class));
        
        // When
        deleteUserService.delete(1L);
        
        // Then
        verify(userRepository).findById(any(UserId.class));
        verify(userRepository).deleteById(any(UserId.class));
    }
    
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> deleteUserService.delete(999L)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        
        verify(userRepository).findById(any(UserId.class));
        verify(userRepository, never()).deleteById(any(UserId.class));
    }
}
