package com.sigrap.user.application.service;

import com.sigrap.user.application.port.in.DisableUserUseCase;
import com.sigrap.user.domain.model.User;
import com.sigrap.user.domain.model.UserId;
import com.sigrap.user.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the DisableUserUseCase.
 * This service handles user account deactivation.
 */
@Service
@Transactional
public class DisableUserService implements DisableUserUseCase {
    
    private final UserRepositoryPort userRepository;
    
    public DisableUserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public User disable(Long id) {
        UserId userId = new UserId(id);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User with ID " + id + " not found"));
        
        user.disable();
        return userRepository.save(user);
    }
}
