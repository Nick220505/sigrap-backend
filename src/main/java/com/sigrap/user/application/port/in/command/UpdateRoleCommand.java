package com.sigrap.user.application.port.in.command;

/**
 * Command for updating an existing role.
 * This is an immutable data carrier that represents the intent to update role information.
 *
 * @param name the new name for the role (optional, null means no change)
 * @param description the new description for the role (optional, null means no change)
 */
public record UpdateRoleCommand(
    String name,
    String description
) {}
