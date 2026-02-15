package com.sigrap.user.application.port.in.command;

/**
 * Command for updating an existing user.
 * This is an immutable data carrier that represents the user's intent to update user information.
 *
 * @param email the new email address (optional, null means no change)
 * @param password the new plain text password (optional, null means no change, will be hashed before storage)
 */
public record UpdateUserCommand(
    String email,
    String password
) {}
