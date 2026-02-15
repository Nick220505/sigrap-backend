package com.sigrap.user.application.port.in;

import com.sigrap.user.domain.model.User;
import java.util.List;

/**
 * Input port for retrieving users.
 * This interface defines the use case for user retrieval operations.
 */
public interface GetUserUseCase {
    
    /**
     * Retrieves a user by its identifier.
     *
     * @param id the user identifier
     * @return the user domain entity
     * @throws IllegalArgumentException if the user is not found
     */
    User getById(Long id);
    
    /**
     * Retrieves a user by username.
     *
     * @param username the username
     * @return the user domain entity
     * @throws IllegalArgumentException if the user is not found
     */
    User getByUsername(String username);
    
    /**
     * Retrieves a user by email.
     *
     * @param email the email address
     * @return the user domain entity
     * @throws IllegalArgumentException if the user is not found
     */
    User getByEmail(String email);
    
    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    List<User> getAll();
    
    /**
     * Retrieves all enabled users.
     *
     * @return a list of enabled users
     */
    List<User> getAllEnabled();
}
