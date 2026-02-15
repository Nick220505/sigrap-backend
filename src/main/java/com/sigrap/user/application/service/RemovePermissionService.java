package com.sigrap.user.application.service;

import com.sigrap.user.application.port.in.RemovePermissionUseCase;
import com.sigrap.user.domain.model.Permission;
import com.sigrap.user.domain.model.PermissionId;
import com.sigrap.user.domain.model.Role;
import com.sigrap.user.domain.model.RoleId;
import com.sigrap.user.domain.port.PermissionRepositoryPort;
import com.sigrap.user.domain.port.RoleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the RemovePermissionUseCase.
 * This service handles permission removal from roles.
 */
@Service
@Transactional
public class RemovePermissionService implements RemovePermissionUseCase {
    
    private final RoleRepositoryPort roleRepository;
    private final PermissionRepositoryPort permissionRepository;
    
    public RemovePermissionService(RoleRepositoryPort roleRepository, PermissionRepositoryPort permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }
    
    @Override
    public Role removePermission(Long roleId, Long permissionId) {
        RoleId roleIdObj = new RoleId(roleId);
        PermissionId permissionIdObj = new PermissionId(permissionId);
        
        Role role = roleRepository.findById(roleIdObj)
            .orElseThrow(() -> new IllegalArgumentException("Role with ID " + roleId + " not found"));
        
        Permission permission = permissionRepository.findById(permissionIdObj)
            .orElseThrow(() -> new IllegalArgumentException("Permission with ID " + permissionId + " not found"));
        
        role.removePermission(permission);
        return roleRepository.save(role);
    }
}
