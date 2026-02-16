package com.sigrap.user.domain.port;

import com.sigrap.user.domain.model.User;
import com.sigrap.user.domain.model.UserId;
import com.sigrap.user.domain.model.UserEmail;
import com.sigrap.user.domain.model.Username;
import java.util.List;
import java.util.Optional;

/**
 * Repository port interface for User domain entity.
 * Defines the contract for persistence operations without exposing implementation details.
 * This interface is defined in the domain layer and implemented in the infrastructure layer.
 */
public interface UserRepositoryPort {
    
    /**
     * Saves a user (create or update).
     *
     * @param user the user to save
     * @return the saved user with generated ID if new
     */
    User save(User user);
    
    /**
     * Finds a user by its identifier.
     *
     * @param id the user identifier
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findById(UserId id);
    
    /**
     * Finds a user by email address.
     *
     * @param email the user email
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(UserEmail email);
    
    /**
     * Finds a user by username.
     *
     * @param username the username
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(Username username);
    
    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    List<User> findAll();
    
    /**
     * Finds all enabled users.
     *
     * @return a list of enabled users
     */
    List<User> findAllEnabled();
    
    /**
     * Checks if a user with the given email exists.
     *
     * @param email the email to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(UserEmail email);
    
    /**
     * Checks if a user with the given username exists.
     *
     * @param username the username to check
     * @return true if a user with the username exists, false otherwise
     */
    boolean existsByUsername(Username username);
    
    /**
     * Deletes a user by its identifier.
     *
     * @param id the user identifier
     */
    void deleteById(UserId id);


    /**
     * Counts the total number of users.
     *
     * @return the total count of users
     */
    long count();

    /**
     * Saves multiple users at once.
     * Useful for batch operations.
     *
     * @param users the list of users to save
     * @return the list of saved users
     */
    List<User> saveAll(List<User> users);

}
