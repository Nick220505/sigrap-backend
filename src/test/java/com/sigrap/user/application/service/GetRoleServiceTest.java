package com.sigrap.user.application.service;

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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetRoleServiceTest {
    
    @Mock
    private RoleRepositoryPort roleRepository;
    
    @InjectMocks
    private GetRoleService getRoleService;
    
    private Role testRole;
    
    @BeforeEach
    void setUp() {
        reset(roleRepository);
        testRole = new Role(
            new RoleId(1L),
            new RoleName("ADMIN"),
            new HashSet<>(),
            "Administrator role"
        );
    }
    
    @Test
    void shouldGetRoleById() {
        // Given
        when(roleRepository.findById(any(RoleId.class))).thenReturn(Optional.of(testRole));
        
        // When
        Role result = getRoleService.getById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testRole.getId(), result.getId());
        assertEquals(testRole.getName(), result.getName());
        
        verify(roleRepository).findById(any(RoleId.class));
    }
    
    @Test
    void shouldThrowExceptionWhenRoleNotFoundById() {
        // Given
        when(roleRepository.findById(any(RoleId.class))).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> getRoleService.getById(999L)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        
        verify(roleRepository).findById(any(RoleId.class));
    }
    
    @Test
    void shouldGetRoleByName() {
        // Given
        when(roleRepository.findByName(any(RoleName.class))).thenReturn(Optional.of(testRole));
        
        // When
        Role result = getRoleService.getByName("ADMIN");
        
        // Then
        assertNotNull(result);
        assertEquals(testRole.getName(), result.getName());
        
        verify(roleRepository).findByName(any(RoleName.class));
    }
    
    @Test
    void shouldThrowExceptionWhenRoleNotFoundByName() {
        // Given
        when(roleRepository.findByName(any(RoleName.class))).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> getRoleService.getByName("NONEXISTENT")
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        
        verify(roleRepository).findByName(any(RoleName.class));
    }
    
    @Test
    void shouldGetAllRoles() {
        // Given
        Role role2 = new Role(
            new RoleId(2L),
            new RoleName("USER"),
            new HashSet<>(),
            "User role"
        );
        when(roleRepository.findAll()).thenReturn(Arrays.asList(testRole, role2));
        
        // When
        List<Role> result = getRoleService.getAll();
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        verify(roleRepository).findAll();
    }
}
