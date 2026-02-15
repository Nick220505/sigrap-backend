package com.sigrap.user.application.port.in.command;

/**
 * Command for updating an existing permission.
 * This is an immutable data carrier that represents the intent to update permission information.
 *
 * @param name the new name for the permission (optional, null means no change)
 * @param resource the new resource for the permission (optional, null means no change)
 * @param action the new action for the permission (optional, null means no change)
 */
public record UpdatePermissionCommand(
    String name,
    String resource,
    String action
) {}
