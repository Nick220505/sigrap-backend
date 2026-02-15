package com.sigrap.user.application.service;

import com.sigrap.user.application.port.in.command.CreateRoleCommand;
import com.sigrap.user.domain.model.Role;
import com.sigrap.user.domain.model.RoleId;
import com.sigrap.user.domain.model.RoleName;
import com.sigrap.user.domain.port.RoleRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateRoleServiceTest {
    
    @Mock
    private RoleRepositoryPort roleRepository;
    
    @InjectMocks
    private CreateRoleService createRoleService;
    
    @BeforeEach
    void setUp() {
        reset(roleRepository);
    }
    
    @Test
    void shouldCreateRoleSuccessfully() {
        // Given
        CreateRoleCommand command = new CreateRoleCommand("ADMIN", "Administrator role");
        
        when(roleRepository.existsByName(any(RoleName.class))).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            return new Role(
                new RoleId(1L),
                role.getName(),
                new HashSet<>(),
                role.getDescription()
            );
        });
        
        // When
        Role result = createRoleService.create(command);
        
        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("ADMIN", result.getName().value());
        assertEquals("Administrator role", result.getDescription());
        
        verify(roleRepository).existsByName(any(RoleName.class));
        verify(roleRepository).save(any(Role.class));
    }
    
    @Test
    void shouldThrowExceptionWhenRoleNameAlreadyExists() {
        // Given
        CreateRoleCommand command = new CreateRoleCommand("EXISTING_ROLE", "Description");
        
        when(roleRepository.existsByName(any(RoleName.class))).thenReturn(true);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createRoleService.create(command)
        );
        
        assertTrue(exception.getMessage().contains("already exists"));
        assertTrue(exception.getMessage().contains("EXISTING_ROLE"));
        
        verify(roleRepository).existsByName(any(RoleName.class));
        verify(roleRepository, never()).save(any(Role.class));
    }
    
    @Test
    void shouldCreateRoleWithNullDescription() {
        // Given
        CreateRoleCommand command = new CreateRoleCommand("USER", null);
        
        when(roleRepository.existsByName(any(RoleName.class))).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            return new Role(
                new RoleId(2L),
                role.getName(),
                new HashSet<>(),
                role.getDescription()
            );
        });
        
        // When
        Role result = createRoleService.create(command);
        
        // Then
        assertNotNull(result);
        assertNull(result.getDescription());
        
        verify(roleRepository).save(any(Role.class));
    }
}
