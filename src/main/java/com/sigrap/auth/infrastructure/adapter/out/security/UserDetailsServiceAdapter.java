package com.sigrap.auth.infrastructure.adapter.out.security;

import com.sigrap.user.application.port.in.GetUserUseCase;
import com.sigrap.user.domain.model.Permission;
import com.sigrap.user.domain.model.Role;
import com.sigrap.user.domain.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Adapter that implements Spring Security's UserDetailsService.
 * This adapter bridges the hexagonal architecture with Spring Security's authentication mechanism.
 * 
 * <p>This is an output adapter in hexagonal architecture terminology,
 * adapting our domain User model to Spring Security's UserDetails interface.
 */
@Component
public class UserDetailsServiceAdapter implements UserDetailsService {

    private final GetUserUseCase getUserUseCase;

    /**
     * Constructor for dependency injection.
     *
     * @param getUserUseCase the use case for retrieving users
     */
    public UserDetailsServiceAdapter(GetUserUseCase getUserUseCase) {
        this.getUserUseCase = getUserUseCase;
    }

    /**
     * Loads a user by their email address (used as username in Spring Security).
     * 
     * <p>This method is called by Spring Security during authentication.
     * It retrieves the user from our domain through the GetUserUseCase
     * and converts it to Spring Security's UserDetails format.
     *
     * @param email the email address (username) to load
     * @return UserDetails object for Spring Security
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            User user = getUserUseCase.getByEmail(email);
            return buildUserDetails(user);
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("User not found with email: " + email, e);
        }
    }

    /**
     * Converts a domain User to Spring Security's UserDetails.
     *
     * @param user the domain user
     * @return UserDetails for Spring Security
     */
    private UserDetails buildUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail().value())
            .password(user.getHashedPassword())
            .authorities(getAuthorities(user))
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!user.isEnabled())
            .build();
    }

    /**
     * Extracts authorities (roles and permissions) from the domain user.
     * 
     * <p>Converts domain roles and permissions to Spring Security's GrantedAuthority format.
     * Roles are prefixed with "ROLE_" and permissions are used as-is.
     *
     * @param user the domain user
     * @return collection of granted authorities
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        // Add roles with ROLE_ prefix
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().value()));
            
            // Add permissions from each role
            for (Permission permission : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getName().value()));
            }
        }
        
        return authorities;
    }
}
