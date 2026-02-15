package com.sigrap.user.application.service;

import com.sigrap.user.application.port.in.CreatePermissionUseCase;
import com.sigrap.user.application.port.in.command.CreatePermissionCommand;
import com.sigrap.user.domain.model.Permission;
import com.sigrap.user.domain.model.PermissionName;
import com.sigrap.user.domain.port.PermissionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the CreatePermissionUseCase.
 * This service orchestrates the creation of a new permission.
 */
@Service
@Transactional
public class CreatePermissionService implements CreatePermissionUseCase {
    
    private final PermissionRepositoryPort permissionRepository;
    
    public CreatePermissionService(PermissionRepositoryPort permissionRepository) {
        this.permissionRepository = permissionRepository;
    }
    
    @Override
    public Permission create(CreatePermissionCommand command) {
        // Create value object (validates format)
        PermissionName name = new PermissionName(command.name());
        
        // Business rule: permission name must be unique
        if (permissionRepository.existsByName(name)) {
            throw new IllegalArgumentException(
                "Permission with name '" + name.value() + "' already exists"
            );
        }
        
        // Create domain entity
        Permission permission = new Permission(name, command.resource(), command.action());
        
        // Persist through port and return with generated ID
        return permissionRepository.save(permission);
    }
}
