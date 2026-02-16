package com.sigrap.user.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.user.application.port.in.GetPermissionUseCase;
import com.sigrap.user.domain.model.Permission;
import com.sigrap.user.domain.model.PermissionId;
import com.sigrap.user.domain.model.PermissionName;
import com.sigrap.user.domain.port.PermissionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Service implementing the GetPermissionUseCase.
 * This service handles permission retrieval operations.
 */
@Service
@Transactional(readOnly = true)
public class GetPermissionService implements GetPermissionUseCase {
    
    private final PermissionRepositoryPort permissionRepository;
    
    public GetPermissionService(PermissionRepositoryPort permissionRepository) {
        this.permissionRepository = permissionRepository;
    }
    
    @Override
    public Permission getById(Long id) {
        PermissionId permissionId = new PermissionId(id);
        return permissionRepository.findById(permissionId)
            .orElseThrow(() -> new ResourceNotFoundException("Permission with ID " + id + " not found"));
    }
    
    @Override
    public Permission getByName(String name) {
        PermissionName permissionName = new PermissionName(name);
        return permissionRepository.findByName(permissionName)
            .orElseThrow(() -> new ResourceNotFoundException("Permission with name '" + name + "' not found"));
    }
    
    @Override
    public List<Permission> getByResource(String resource) {
        return permissionRepository.findByResource(resource);
    }
    
    @Override
    public List<Permission> getAll() {
        return permissionRepository.findAll();
    }
}
