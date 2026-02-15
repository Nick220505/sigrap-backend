package com.sigrap.user.application.service;

import com.sigrap.user.application.port.in.UpdatePermissionUseCase;
import com.sigrap.user.application.port.in.command.UpdatePermissionCommand;
import com.sigrap.user.domain.model.Permission;
import com.sigrap.user.domain.model.PermissionId;
import com.sigrap.user.domain.model.PermissionName;
import com.sigrap.user.domain.port.PermissionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the UpdatePermissionUseCase.
 * This service handles permission update operations.
 */
@Service
@Transactional
public class UpdatePermissionService implements UpdatePermissionUseCase {
    
    private final PermissionRepositoryPort permissionRepository;
    
    public UpdatePermissionService(PermissionRepositoryPort permissionRepository) {
        this.permissionRepository = permissionRepository;
    }
    
    @Override
    public Permission update(Long id, UpdatePermissionCommand command) {
        PermissionId permissionId = new PermissionId(id);
        Permission permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new IllegalArgumentException("Permission with ID " + id + " not found"));
        
        // Update name if provided
        if (command.name() != null && !command.name().isBlank()) {
            PermissionName newName = new PermissionName(command.name());
            
            // Business rule: permission name must be unique
            if (!permission.getName().equals(newName) && permissionRepository.existsByName(newName)) {
                throw new IllegalArgumentException(
                    "Permission with name '" + newName.value() + "' already exists"
                );
            }
            
            permission.updateName(newName);
        }
        
        // Update resource if provided
        if (command.resource() != null && !command.resource().isBlank()) {
            permission.updateResource(command.resource());
        }
        
        // Update action if provided
        if (command.action() != null && !command.action().isBlank()) {
            permission.updateAction(command.action());
        }
        
        // Persist changes
        return permissionRepository.save(permission);
    }
}
