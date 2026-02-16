package com.sigrap.user.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.user.domain.model.*;
import com.sigrap.user.domain.port.RoleRepositoryPort;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveRoleServiceTest {
    
    @Mock
    private UserRepositoryPort userRepository;
    
    @Mock
    private RoleRepositoryPort roleRepository;
    
    @InjectMocks
    private RemoveRoleService removeRoleService;
    
    @BeforeEach
    void setUp() {
        reset(userRepository, roleRepository);
    }
    
    @Test
    void shouldRemoveRoleFromUser() {
        // Given
        Role role = new Role(
            new RoleId(1L),
            new RoleName("ADMIN"),
            new HashSet<>(),
            "Administrator role"
        );
        
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        
        User user = new User(
            new UserId(1L),
            new Username("john_doe"),
            new UserEmail("john@example.com"),
            "hashed_password",
            roles,
            true,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(user));
        when(roleRepository.findById(any(RoleId.class))).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        User result = removeRoleService.removeRole(1L, 1L);
        
        // Then
        assertNotNull(result);
        assertFalse(result.hasRole(role));
        
        verify(userRepository).findById(any(UserId.class));
        verify(roleRepository).findById(any(RoleId.class));
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.empty());
        
        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> removeRoleService.removeRole(999L, 1L)
        );
        
        assertTrue(exception.getMessage().contains("User"));
        assertTrue(exception.getMessage().contains("not found"));
        
        verify(userRepository).findById(any(UserId.class));
        verify(roleRepository, never()).findById(any(RoleId.class));
    }
}
