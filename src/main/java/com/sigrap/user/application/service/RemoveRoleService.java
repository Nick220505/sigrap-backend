package com.sigrap.user.application.service;

import com.sigrap.user.application.port.in.RemoveRoleUseCase;
import com.sigrap.user.domain.model.Role;
import com.sigrap.user.domain.model.RoleId;
import com.sigrap.user.domain.model.User;
import com.sigrap.user.domain.model.UserId;
import com.sigrap.user.domain.port.RoleRepositoryPort;
import com.sigrap.user.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the RemoveRoleUseCase.
 * This service handles role removal from users.
 */
@Service
@Transactional
public class RemoveRoleService implements RemoveRoleUseCase {
    
    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    
    public RemoveRoleService(UserRepositoryPort userRepository, RoleRepositoryPort roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    
    @Override
    public User removeRole(Long userId, Long roleId) {
        UserId userIdObj = new UserId(userId);
        RoleId roleIdObj = new RoleId(roleId);
        
        User user = userRepository.findById(userIdObj)
            .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
        
        Role role = roleRepository.findById(roleIdObj)
            .orElseThrow(() -> new IllegalArgumentException("Role with ID " + roleId + " not found"));
        
        user.removeRole(role);
        return userRepository.save(user);
    }
}
