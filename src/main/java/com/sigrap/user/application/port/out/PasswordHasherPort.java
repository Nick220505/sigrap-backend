package com.sigrap.user.application.port.out;

/**
 * Output port for password hashing operations.
 * This interface abstracts the password hashing mechanism from the application layer.
 */
public interface PasswordHasherPort {
    
    /**
     * Hashes a plain text password.
     *
     * @param plainPassword the plain text password to hash
     * @return the hashed password
     */
    String hash(String plainPassword);
}
