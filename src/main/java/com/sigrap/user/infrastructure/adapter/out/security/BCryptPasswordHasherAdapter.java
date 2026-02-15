package com.sigrap.user.infrastructure.adapter.out.security;

import com.sigrap.user.application.port.out.PasswordHasherPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * BCrypt implementation of the PasswordHasherPort.
 * This adapter uses Spring Security's BCryptPasswordEncoder to hash passwords.
 * 
 * <p>BCrypt is a strong, adaptive hashing algorithm designed for password storage.
 * It includes a salt and is computationally expensive to prevent brute-force attacks.
 * 
 * <p>This is an output adapter in hexagonal architecture terminology.
 */
@Component
public class BCryptPasswordHasherAdapter implements PasswordHasherPort {

    private final BCryptPasswordEncoder encoder;

    /**
     * Constructor that initializes the BCrypt encoder with default strength (10 rounds).
     */
    public BCryptPasswordHasherAdapter() {
        this.encoder = new BCryptPasswordEncoder();
    }

    /**
     * Hashes a plain text password using BCrypt.
     *
     * @param plainPassword the plain text password to hash
     * @return the BCrypt hashed password
     * @throws IllegalArgumentException if the password is null or empty
     */
    @Override
    public String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        return encoder.encode(plainPassword);
    }
}
