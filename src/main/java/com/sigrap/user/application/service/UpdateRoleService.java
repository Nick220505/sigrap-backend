package com.sigrap.user.application.service;

import com.sigrap.user.application.port.in.UpdateRoleUseCase;
import com.sigrap.user.application.port.in.command.UpdateRoleCommand;
import com.sigrap.user.domain.model.Role;
import com.sigrap.user.domain.model.RoleId;
import com.sigrap.user.domain.model.RoleName;
import com.sigrap.user.domain.port.RoleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the UpdateRoleUseCase.
 * This service handles role update operations.
 */
@Service
@Transactional
public class UpdateRoleService implements UpdateRoleUseCase {
    
    private final RoleRepositoryPort roleRepository;
    
    public UpdateRoleService(RoleRepositoryPort roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    @Override
    public Role update(Long id, UpdateRoleCommand command) {
        RoleId roleId = new RoleId(id);
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new IllegalArgumentException("Role with ID " + id + " not found"));
        
        // Update name if provided
        if (command.name() != null && !command.name().isBlank()) {
            RoleName newName = new RoleName(command.name());
            
            // Business rule: role name must be unique
            if (!role.getName().equals(newName) && roleRepository.existsByName(newName)) {
                throw new IllegalArgumentException(
                    "Role with name '" + newName.value() + "' already exists"
                );
            }
            
            role.updateName(newName);
        }
        
        // Update description if provided
        if (command.description() != null) {
            role.updateDescription(command.description());
        }
        
        // Persist changes
        return roleRepository.save(role);
    }
}
