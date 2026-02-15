package com.sigrap.user.application.port.in.command;

/**
 * Command for creating a new role.
 * This is an immutable data carrier that represents the intent to create a role.
 *
 * @param name the name of the role to create
 * @param description the description of the role (optional)
 */
public record CreateRoleCommand(
    String name,
    String description
) {}
