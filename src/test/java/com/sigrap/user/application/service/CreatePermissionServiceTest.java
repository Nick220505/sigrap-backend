package com.sigrap.user.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;

import com.sigrap.user.application.port.in.command.CreatePermissionCommand;
import com.sigrap.user.domain.model.Permission;
import com.sigrap.user.domain.model.PermissionId;
import com.sigrap.user.domain.model.PermissionName;
import com.sigrap.user.domain.port.PermissionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePermissionServiceTest {
    
    @Mock
    private PermissionRepositoryPort permissionRepository;
    
        @Mock
    private EventPublisherPort eventPublisher;

    @InjectMocks
    private CreatePermissionService createPermissionService;
    
    @BeforeEach
    void setUp() {
        reset(permissionRepository, eventPublisher);
    }
    
    @Test
    void shouldCreatePermissionSuccessfully() {
        // Given
        CreatePermissionCommand command = new CreatePermissionCommand(
            "READ_PRODUCT",
            "PRODUCT",
            "READ"
        );
        
        when(permissionRepository.existsByName(any(PermissionName.class))).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenAnswer(invocation -> {
            Permission permission = invocation.getArgument(0);
            return new Permission(
                new PermissionId(1L),
                permission.getName(),
                permission.getResource(),
                permission.getAction()
            );
        });
        
        // When
        Permission result = createPermissionService.create(command);
        
        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("READ_PRODUCT", result.getName().value());
        assertEquals("PRODUCT", result.getResource());
        assertEquals("READ", result.getAction());
        
        verify(permissionRepository).existsByName(any(PermissionName.class));
        verify(permissionRepository).save(any(Permission.class));
    }
    
    @Test
    void shouldThrowExceptionWhenPermissionNameAlreadyExists() {
        // Given
        CreatePermissionCommand command = new CreatePermissionCommand(
            "EXISTING_PERMISSION",
            "RESOURCE",
            "ACTION"
        );
        
        when(permissionRepository.existsByName(any(PermissionName.class))).thenReturn(true);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createPermissionService.create(command)
        );
        
        assertTrue(exception.getMessage().contains("already exists"));
        assertTrue(exception.getMessage().contains("EXISTING_PERMISSION"));
        
        verify(permissionRepository).existsByName(any(PermissionName.class));
        verify(permissionRepository, never()).save(any(Permission.class));
    }
    
    @Test
    void shouldThrowExceptionWhenResourceIsBlank() {
        // Given
        CreatePermissionCommand command = new CreatePermissionCommand(
            "PERMISSION",
            "   ",
            "ACTION"
        );
        
        when(permissionRepository.existsByName(any(PermissionName.class))).thenReturn(false);
        
        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> createPermissionService.create(command)
        );
        
        verify(permissionRepository).existsByName(any(PermissionName.class));
        verify(permissionRepository, never()).save(any(Permission.class));
    }
    
    @Test
    void shouldThrowExceptionWhenActionIsBlank() {
        // Given
        CreatePermissionCommand command = new CreatePermissionCommand(
            "PERMISSION",
            "RESOURCE",
            "   "
        );
        
        when(permissionRepository.existsByName(any(PermissionName.class))).thenReturn(false);
        
        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> createPermissionService.create(command)
        );
        
        verify(permissionRepository).existsByName(any(PermissionName.class));
        verify(permissionRepository, never()).save(any(Permission.class));
    }
}

