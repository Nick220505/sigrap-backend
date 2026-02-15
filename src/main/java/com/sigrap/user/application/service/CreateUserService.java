package com.sigrap.user.application.service;

import com.sigrap.user.application.port.in.CreateUserUseCase;
import com.sigrap.user.application.port.in.command.CreateUserCommand;
import com.sigrap.user.application.port.out.PasswordHasherPort;
import com.sigrap.user.domain.model.User;
import com.sigrap.user.domain.model.UserEmail;
import com.sigrap.user.domain.model.Username;
import com.sigrap.user.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the CreateUserUseCase.
 * This service orchestrates the creation of a new user by:
 * <ul>
 *   <li>Validating business rules (unique username and email)</li>
 *   <li>Hashing the password</li>
 *   <li>Creating the domain entity</li>
 *   <li>Persisting through the repository port</li>
 * </ul>
 */
@Service
@Transactional
public class CreateUserService implements CreateUserUseCase {
    
    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    
    public CreateUserService(UserRepositoryPort userRepository, PasswordHasherPort passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }
    
    @Override
    public User create(CreateUserCommand command) {
        // Create value objects (validates format)
        Username username = new Username(command.username());
        UserEmail email = new UserEmail(command.email());
        
        // Business rule: username must be unique
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                "User with username '" + username.value() + "' already exists"
            );
        }
        
        // Business rule: email must be unique
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                "User with email '" + email.value() + "' already exists"
            );
        }
        
        // Hash the password
        String hashedPassword = passwordHasher.hash(command.password());
        
        // Create domain entity
        User user = new User(username, email, hashedPassword);
        
        // Persist through port and return with generated ID
        return userRepository.save(user);
    }
}
