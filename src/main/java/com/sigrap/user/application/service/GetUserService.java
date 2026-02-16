package com.sigrap.user.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.user.application.port.in.GetUserUseCase;
import com.sigrap.user.domain.model.User;
import com.sigrap.user.domain.model.UserEmail;
import com.sigrap.user.domain.model.UserId;
import com.sigrap.user.domain.model.Username;
import com.sigrap.user.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Service implementing the GetUserUseCase.
 * This service handles user retrieval operations.
 */
@Service
@Transactional(readOnly = true)
public class GetUserService implements GetUserUseCase {
    
    private final UserRepositoryPort userRepository;
    
    public GetUserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public User getById(Long id) {
        UserId userId = new UserId(id);
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));
    }
    
    @Override
    public User getByUsername(String username) {
        Username usernameObj = new Username(username);
        return userRepository.findByUsername(usernameObj)
            .orElseThrow(() -> new ResourceNotFoundException("User with username '" + username + "' not found"));
    }
    
    @Override
    public User getByEmail(String email) {
        UserEmail emailObj = new UserEmail(email);
        return userRepository.findByEmail(emailObj)
            .orElseThrow(() -> new ResourceNotFoundException("User with email '" + email + "' not found"));
    }
    
    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }
    
    @Override
    public List<User> getAllEnabled() {
        return userRepository.findAllEnabled();
    }
}
