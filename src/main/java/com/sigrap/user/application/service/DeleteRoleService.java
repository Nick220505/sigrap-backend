package com.sigrap.user.application.service;

import com.sigrap.exception.ResourceNotFoundException;

import com.sigrap.user.application.port.in.DeleteRoleUseCase;
import com.sigrap.user.domain.model.RoleId;
import com.sigrap.user.domain.port.RoleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the DeleteRoleUseCase.
 * This service handles role deletion operations.
 */
@Service
@Transactional
public class DeleteRoleService implements DeleteRoleUseCase {
    
    private final RoleRepositoryPort roleRepository;
    
    public DeleteRoleService(RoleRepositoryPort roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    @Override
    public void delete(Long id) {
        RoleId roleId = new RoleId(id);
        
        // Verify role exists before deletion
        if (!roleRepository.findById(roleId).isPresent()) {
            throw new ResourceNotFoundException("Role with ID " + id + " not found");
        }
        
        roleRepository.deleteById(roleId);
    }
}
