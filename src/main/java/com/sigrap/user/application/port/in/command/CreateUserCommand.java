package com.sigrap.user.application.port.in.command;

/**
 * Command for creating a new user.
 * This is an immutable data carrier that represents the user's intent to create a user account.
 *
 * @param username the username for the new user
 * @param email the email address for the new user
 * @param password the plain text password (will be hashed before storage)
 */
public record CreateUserCommand(
    String username,
    String email,
    String password
) {}
