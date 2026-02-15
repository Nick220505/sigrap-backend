package com.sigrap.user.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for UserJpaEntity.
 * Provides CRUD operations and custom query methods for user persistence.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    
    /**
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found
     */
    Optional<UserJpaEntity> findByUsername(String username);
    
    /**
     * Finds a user by email.
     *
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    Optional<UserJpaEntity> findByEmail(String email);
    
    /**
     * Checks if a user with the given username exists.
     *
     * @param username the username to check
     * @return true if a user with this username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Checks if a user with the given email exists.
     *
     * @param email the email to check
     * @return true if a user with this email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Finds all enabled users.
     *
     * @return a list of enabled users
     */
    List<UserJpaEntity> findByEnabledTrue();
}
