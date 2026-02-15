package com.sigrap.user.application.port.in.command;

/**
 * Command for creating a new permission.
 * This is an immutable data carrier that represents the intent to create a permission.
 *
 * @param name the name of the permission to create
 * @param resource the resource this permission applies to
 * @param action the action this permission allows
 */
public record CreatePermissionCommand(
    String name,
    String resource,
    String action
) {}
