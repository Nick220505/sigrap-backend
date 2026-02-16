package com.sigrap.user.application.service;

import com.sigrap.exception.ResourceNotFoundException;

import com.sigrap.user.application.port.in.AssignPermissionUseCase;
import com.sigrap.user.domain.model.Permission;
import com.sigrap.user.domain.model.PermissionId;
import com.sigrap.user.domain.model.Role;
import com.sigrap.user.domain.model.RoleId;
import com.sigrap.user.domain.port.PermissionRepositoryPort;
import com.sigrap.user.domain.port.RoleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the AssignPermissionUseCase.
 * This service handles permission assignment to roles.
 */
@Service
@Transactional
public class AssignPermissionService implements AssignPermissionUseCase {
    
    private final RoleRepositoryPort roleRepository;
    private final PermissionRepositoryPort permissionRepository;
    
    public AssignPermissionService(RoleRepositoryPort roleRepository, PermissionRepositoryPort permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }
    
    @Override
    public Role assignPermission(Long roleId, Long permissionId) {
        RoleId roleIdObj = new RoleId(roleId);
        PermissionId permissionIdObj = new PermissionId(permissionId);
        
        Role role = roleRepository.findById(roleIdObj)
            .orElseThrow(() -> new ResourceNotFoundException("Role with ID " + roleId + " not found"));
        
        Permission permission = permissionRepository.findById(permissionIdObj)
            .orElseThrow(() -> new ResourceNotFoundException("Permission with ID " + permissionId + " not found"));
        
        role.addPermission(permission);
        return roleRepository.save(role);
    }
}
