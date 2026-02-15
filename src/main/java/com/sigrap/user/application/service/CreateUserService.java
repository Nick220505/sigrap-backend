package com.sigrap.user.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.audit.domain.event.EntityCreatedEvent;
import com.sigrap.audit.domain.model.EntityType;
import com.sigrap.user.application.port.in.CreateUserUseCase;
import com.sigrap.user.application.port.in.command.CreateUserCommand;
import com.sigrap.user.application.port.out.PasswordHasherPort;
import com.sigrap.user.domain.model.User;
import com.sigrap.user.domain.model.UserEmail;
import com.sigrap.user.domain.model.Username;
import com.sigrap.user.domain.port.UserRepositoryPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class CreateUserService implements CreateUserUseCase {
    
    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final EventPublisherPort eventPublisher;
    
    public CreateUserService(UserRepositoryPort userRepository, PasswordHasherPort passwordHasher, EventPublisherPort eventPublisher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public User create(CreateUserCommand command) {
        long startTime = System.currentTimeMillis();
        
        Username username = new Username(command.username());
        UserEmail email = new UserEmail(command.email());
        
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                "User with username '" + username.value() + "' already exists"
            );
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                "User with email '" + email.value() + "' already exists"
            );
        }
        
        String hashedPassword = passwordHasher.hash(command.password());
        
        User user = new User(username, email, hashedPassword);
        
        User savedUser = userRepository.save(user);
        
        long durationMs = System.currentTimeMillis() - startTime;
        eventPublisher.publish(new EntityCreatedEvent(
            EntityType.USER,
            savedUser.getId().value().toString(),
            getCurrentUsername(),
            LocalDateTime.now(),
            null,
            null,
            "User created: " + savedUser.getUsername().value(),
            durationMs
        ));
        
        return savedUser;
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}
