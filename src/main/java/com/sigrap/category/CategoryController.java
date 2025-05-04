package com.sigrap.category;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category Management", description = "Operations for managing categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all categories", description = "Retrieves a list of all categories")
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    @GetMapping
    public List<CategoryInfo> findAll() {
        return categoryService.findAll();
    }

    @Operation(summary = "Get category by ID", description = "Retrieves a category by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    @GetMapping("/{id}")
    public CategoryInfo findById(
            @Parameter(description = "ID of the category to retrieve") @PathVariable Integer id) {
        return categoryService.findById(id);
    }

    @Operation(summary = "Create a new category", description = "Creates a new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryInfo create(
            @Parameter(description = "Category data to create", required = true) @Valid @RequestBody CategoryData categoryData) {
        return categoryService.create(categoryData);
    }

    @Operation(summary = "Update a category", description = "Updates an existing category by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    @PutMapping("/{id}")
    public CategoryInfo update(
            @Parameter(description = "ID of the category to update") @PathVariable Integer id,
            @Parameter(description = "Updated category data", required = true) @Valid @RequestBody CategoryData categoryData) {
        return categoryService.update(id, categoryData);
    }

    @Operation(summary = "Delete a category", description = "Deletes a category by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "ID of the category to delete") @PathVariable Integer id) {
        categoryService.delete(id);
    }

    @Operation(summary = "Delete multiple categories", description = "Deletes multiple categories by their IDs")
    @ApiResponse(responseCode = "204", description = "Categories deleted successfully")
    @DeleteMapping("/delete-many")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllById(
            @Parameter(description = "List of category IDs to delete", required = true) @RequestBody List<Integer> ids) {
        categoryService.deleteAllById(ids);
    }
}