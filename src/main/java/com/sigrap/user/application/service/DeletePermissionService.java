package com.sigrap.user.application.service;

import com.sigrap.exception.ResourceNotFoundException;

import com.sigrap.user.application.port.in.DeletePermissionUseCase;
import com.sigrap.user.domain.model.PermissionId;
import com.sigrap.user.domain.port.PermissionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the DeletePermissionUseCase.
 * This service handles permission deletion operations.
 */
@Service
@Transactional
public class DeletePermissionService implements DeletePermissionUseCase {
    
    private final PermissionRepositoryPort permissionRepository;
    
    public DeletePermissionService(PermissionRepositoryPort permissionRepository) {
        this.permissionRepository = permissionRepository;
    }
    
    @Override
    public void delete(Long id) {
        PermissionId permissionId = new PermissionId(id);
        
        // Verify permission exists before deletion
        if (!permissionRepository.findById(permissionId).isPresent()) {
            throw new ResourceNotFoundException("Permission with ID " + id + " not found");
        }
        
        permissionRepository.deleteById(permissionId);
    }
}
