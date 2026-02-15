package com.sigrap.user.application.service;

import com.sigrap.user.application.port.in.UpdateUserUseCase;
import com.sigrap.user.application.port.in.command.UpdateUserCommand;
import com.sigrap.user.application.port.out.PasswordHasherPort;
import com.sigrap.user.domain.model.User;
import com.sigrap.user.domain.model.UserEmail;
import com.sigrap.user.domain.model.UserId;
import com.sigrap.user.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the UpdateUserUseCase.
 * This service handles user update operations.
 */
@Service
@Transactional
public class UpdateUserService implements UpdateUserUseCase {
    
    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    
    public UpdateUserService(UserRepositoryPort userRepository, PasswordHasherPort passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }
    
    @Override
    public User update(Long id, UpdateUserCommand command) {
        UserId userId = new UserId(id);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User with ID " + id + " not found"));
        
        // Update email if provided
        if (command.email() != null && !command.email().isBlank()) {
            UserEmail newEmail = new UserEmail(command.email());
            
            // Business rule: email must be unique
            if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException(
                    "User with email '" + newEmail.value() + "' already exists"
                );
            }
            
            user.updateEmail(newEmail);
        }
        
        // Update password if provided
        if (command.password() != null && !command.password().isBlank()) {
            String hashedPassword = passwordHasher.hash(command.password());
            user.updatePassword(hashedPassword);
        }
        
        // Persist changes
        return userRepository.save(user);
    }
}
