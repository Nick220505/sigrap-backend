package com.sigrap.user.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.user.application.port.in.GetRoleUseCase;
import com.sigrap.user.domain.model.Role;
import com.sigrap.user.domain.model.RoleId;
import com.sigrap.user.domain.model.RoleName;
import com.sigrap.user.domain.port.RoleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Service implementing the GetRoleUseCase.
 * This service handles role retrieval operations.
 */
@Service
@Transactional(readOnly = true)
public class GetRoleService implements GetRoleUseCase {
    
    private final RoleRepositoryPort roleRepository;
    
    public GetRoleService(RoleRepositoryPort roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    @Override
    public Role getById(Long id) {
        RoleId roleId = new RoleId(id);
        return roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role with ID " + id + " not found"));
    }
    
    @Override
    public Role getByName(String name) {
        RoleName roleName = new RoleName(name);
        return roleRepository.findByName(roleName)
            .orElseThrow(() -> new ResourceNotFoundException("Role with name '" + name + "' not found"));
    }
    
    @Override
    public List<Role> getAll() {
        return roleRepository.findAll();
    }
}
