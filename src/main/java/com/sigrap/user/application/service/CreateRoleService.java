package com.sigrap.user.application.service;

import com.sigrap.user.application.port.in.CreateRoleUseCase;
import com.sigrap.user.application.port.in.command.CreateRoleCommand;
import com.sigrap.user.domain.model.Role;
import com.sigrap.user.domain.model.RoleName;
import com.sigrap.user.domain.port.RoleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the CreateRoleUseCase.
 * This service orchestrates the creation of a new role.
 */
@Service
@Transactional
public class CreateRoleService implements CreateRoleUseCase {
    
    private final RoleRepositoryPort roleRepository;
    
    public CreateRoleService(RoleRepositoryPort roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    @Override
    public Role create(CreateRoleCommand command) {
        // Create value object (validates format)
        RoleName name = new RoleName(command.name());
        
        // Business rule: role name must be unique
        if (roleRepository.existsByName(name)) {
            throw new IllegalArgumentException(
                "Role with name '" + name.value() + "' already exists"
            );
        }
        
        // Create domain entity
        Role role = new Role(name, command.description());
        
        // Persist through port and return with generated ID
        return roleRepository.save(role);
    }
}
