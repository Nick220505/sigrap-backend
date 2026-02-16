package com.sigrap.user.application.service;

import com.sigrap.exception.ResourceNotFoundException;

import com.sigrap.user.application.port.in.EnableUserUseCase;
import com.sigrap.user.domain.model.User;
import com.sigrap.user.domain.model.UserId;
import com.sigrap.user.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the EnableUserUseCase.
 * This service handles user account activation.
 */
@Service
@Transactional
public class EnableUserService implements EnableUserUseCase {
    
    private final UserRepositoryPort userRepository;
    
    public EnableUserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public User enable(Long id) {
        UserId userId = new UserId(id);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));
        
        user.enable();
        return userRepository.save(user);
    }
}
