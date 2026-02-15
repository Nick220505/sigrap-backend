package com.sigrap.category.application.port.in.command;

/**
 * Command for updating an existing category.
 * This is an immutable data carrier that represents the user's intent to update a category.
 *
 * @param name the new name of the category
 * @param description the new description of the category (optional)
 */
public record UpdateCategoryCommand(
    String name,
    String description
) {}
