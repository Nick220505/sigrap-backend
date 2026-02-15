package com.sigrap.auth.application.port.in.command;

/**
 * Command for registering a new user.
 * This is an immutable data carrier that represents the user's intent to register.
 *
 * @param name the full name of the user
 * @param email the email address for the new user
 * @param password the plain text password (will be encoded before storage)
 */
public record RegisterUserCommand(String name, String email, String password) {}
