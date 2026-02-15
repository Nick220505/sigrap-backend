package com.sigrap.category.application.port.in.command;

/**
 * Command for creating a new category.
 * This is an immutable data carrier that represents the user's intent to create a category.
 *
 * @param name the name of the category to create
 * @param description the description of the category (optional)
 */
public record CreateCategoryCommand(
    String name,
    String description
) {}
