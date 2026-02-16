package com.sigrap.user.application.service;

import com.sigrap.exception.ResourceNotFoundException;
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
class DisableUserServiceTest {
    
    @Mock
    private UserRepositoryPort userRepository;
    
    @InjectMocks
    private DisableUserService disableUserService;
    
    @BeforeEach
    void setUp() {
        reset(userRepository);
    }
    
    @Test
    void shouldDisableEnabledUser() {
        // Given
        User enabledUser = new User(
            new UserId(1L),
            new Username("john_doe"),
            new UserEmail("john@example.com"),
            "hashed_password",
            new HashSet<>(),
            true,  // enabled
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(enabledUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        User result = disableUserService.disable(1L);
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEnabled());
        
        verify(userRepository).findById(any(UserId.class));
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.empty());
        
        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> disableUserService.disable(999L)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        
        verify(userRepository).findById(any(UserId.class));
        verify(userRepository, never()).save(any(User.class));
    }
}
