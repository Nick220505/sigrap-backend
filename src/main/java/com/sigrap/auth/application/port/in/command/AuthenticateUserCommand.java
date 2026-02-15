package com.sigrap.auth.application.port.in.command;

/**
 * Command for authenticating a user.
 * This is an immutable data carrier that represents the user's intent to authenticate.
 *
 * @param email the email address of the user
 * @param password the plain text password
 */
public record AuthenticateUserCommand(String email, String password) {}
