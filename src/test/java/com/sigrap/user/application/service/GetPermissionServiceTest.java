package com.sigrap.user.application.service;

import com.sigrap.exception.ResourceNotFoundException;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPermissionServiceTest {
    
    @Mock
    private PermissionRepositoryPort permissionRepository;
    
    @InjectMocks
    private GetPermissionService getPermissionService;
    
    private Permission testPermission;
    
    @BeforeEach
    void setUp() {
        reset(permissionRepository);
        testPermission = new Permission(
            new PermissionId(1L),
            new PermissionName("READ_PRODUCT"),
            "PRODUCT",
            "READ"
        );
    }
    
    @Test
    void shouldGetPermissionById() {
        // Given
        when(permissionRepository.findById(any(PermissionId.class))).thenReturn(Optional.of(testPermission));
        
        // When
        Permission result = getPermissionService.getById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testPermission.getId(), result.getId());
        assertEquals(testPermission.getName(), result.getName());
        
        verify(permissionRepository).findById(any(PermissionId.class));
    }
    
    @Test
    void shouldThrowExceptionWhenPermissionNotFoundById() {
        // Given
        when(permissionRepository.findById(any(PermissionId.class))).thenReturn(Optional.empty());
        
        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> getPermissionService.getById(999L)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        
        verify(permissionRepository).findById(any(PermissionId.class));
    }
    
    @Test
    void shouldGetPermissionByName() {
        // Given
        when(permissionRepository.findByName(any(PermissionName.class))).thenReturn(Optional.of(testPermission));
        
        // When
        Permission result = getPermissionService.getByName("READ_PRODUCT");
        
        // Then
        assertNotNull(result);
        assertEquals(testPermission.getName(), result.getName());
        
        verify(permissionRepository).findByName(any(PermissionName.class));
    }
    
    @Test
    void shouldThrowExceptionWhenPermissionNotFoundByName() {
        // Given
        when(permissionRepository.findByName(any(PermissionName.class))).thenReturn(Optional.empty());
        
        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> getPermissionService.getByName("NONEXISTENT")
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        
        verify(permissionRepository).findByName(any(PermissionName.class));
    }
    
    @Test
    void shouldGetPermissionsByResource() {
        // Given
        Permission permission2 = new Permission(
            new PermissionId(2L),
            new PermissionName("WRITE_PRODUCT"),
            "PRODUCT",
            "WRITE"
        );
        when(permissionRepository.findByResource(anyString())).thenReturn(Arrays.asList(testPermission, permission2));
        
        // When
        List<Permission> result = getPermissionService.getByResource("PRODUCT");
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        verify(permissionRepository).findByResource("PRODUCT");
    }
    
    @Test
    void shouldGetAllPermissions() {
        // Given
        Permission permission2 = new Permission(
            new PermissionId(2L),
            new PermissionName("READ_SALE"),
            "SALE",
            "READ"
        );
        when(permissionRepository.findAll()).thenReturn(Arrays.asList(testPermission, permission2));
        
        // When
        List<Permission> result = getPermissionService.getAll();
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        verify(permissionRepository).findAll();
    }
}
