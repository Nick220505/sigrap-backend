package com.sigrap.user.application.service;

import com.sigrap.exception.ResourceNotFoundException;

import com.sigrap.user.application.port.in.DeleteUserUseCase;
import com.sigrap.user.domain.model.UserId;
import com.sigrap.user.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the DeleteUserUseCase.
 * This service handles user deletion operations.
 */
@Service
@Transactional
public class DeleteUserService implements DeleteUserUseCase {
    
    private final UserRepositoryPort userRepository;
    
    public DeleteUserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public void delete(Long id) {
        UserId userId = new UserId(id);
        
        // Verify user exists before deletion
        if (!userRepository.findById(userId).isPresent()) {
            throw new ResourceNotFoundException("User with ID " + id + " not found");
        }
        
        userRepository.deleteById(userId);
    }
}
