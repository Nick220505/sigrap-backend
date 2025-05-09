package com.sigrap.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

/**
 * REST controller for managing product categories.
 * Provides endpoints for CRUD operations on categories.
 *
 * <p>This controller manages:
 * <ul>
 *   <li>Category creation and validation</li>
 *   <li>Category retrieval (single and bulk)</li>
 *   <li>Category updates</li>
 *   <li>Category deletion (single and bulk)</li>
 * </ul></p>
 *
 * <p>Categories are fundamental to the product organization system:
 * <ul>
 *   <li>Each product must belong to a category</li>
 *   <li>Categories help organize and filter products</li>
 *   <li>Categories support inventory management</li>
 * </ul></p>
 *
 * <p>Usage Examples:
 * <pre>
 * // Create category
 * POST /api/categories
 * {
 *   "name": "Office Supplies",
 *   "description": "General office supplies and stationery"
 * }
 *
 * // Update category
 * PUT /api/categories/1
 * {
 *   "name": "School Supplies",
 *   "description": "Educational materials and supplies"
 * }
 * </pre></p>
 *
 * @see CategoryService
 * @see Category
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(
  name = "Category Management",
  description = "Operations for managing categories"
)
public class CategoryController {

  private final CategoryService categoryService;

  /**
   * Retrieves all categories in the system.
   *
   * <p>This endpoint provides a complete list of all available categories,
   * which can be used for:
   * <ul>
   *   <li>Populating category selection dropdowns</li>
   *   <li>Category management interfaces</li>
   *   <li>Product categorization</li>
   * </ul></p>
   *
   * @return List of all categories with their details
   */
  @Operation(
    summary = "Get all categories",
    description = "Retrieves a list of all categories"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Categories retrieved successfully"
  )
  @GetMapping
  public List<CategoryInfo> findAll() {
    return categoryService.findAll();
  }

  /**
   * Retrieves a specific category by its ID.
   *
   * <p>This endpoint is useful for:
   * <ul>
   *   <li>Viewing category details</li>
   *   <li>Pre-populating edit forms</li>
   *   <li>Verifying category existence</li>
   * </ul></p>
   *
   * @param id The unique identifier of the category
   * @return The category information
   * @throws EntityNotFoundException if category not found
   */
  @Operation(
    summary = "Get category by ID",
    description = "Retrieves a category by its ID"
  )
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "Category found"),
      @ApiResponse(
        responseCode = "404",
        description = "Category not found",
        content = @Content
      ),
    }
  )
  @GetMapping("/{id}")
  public CategoryInfo findById(
    @Parameter(
      description = "ID of the category to retrieve"
    ) @PathVariable Integer id
  ) {
    return categoryService.findById(id);
  }

  /**
   * Creates a new category.
   *
   * <p>This endpoint:
   * <ul>
   *   <li>Validates category data</li>
   *   <li>Creates new category record</li>
   *   <li>Returns the created category</li>
   * </ul></p>
   *
   * <p>Required fields:
   * <ul>
   *   <li>name - Category name (must be unique)</li>
   *   <li>description - Category description</li>
   * </ul></p>
   *
   * @param categoryData The category data to create
   * @return The created category information
   * @throws IllegalArgumentException if category name already exists
   */
  @Operation(
    summary = "Create a new category",
    description = "Creates a new category"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "201",
        description = "Category created successfully"
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Invalid input data",
        content = @Content
      ),
    }
  )
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CategoryInfo create(
    @Parameter(
      description = "Category data to create",
      required = true
    ) @Valid @RequestBody CategoryData categoryData
  ) {
    return categoryService.create(categoryData);
  }

  /**
   * Updates an existing category.
   *
   * <p>This endpoint:
   * <ul>
   *   <li>Validates update data</li>
   *   <li>Updates category record</li>
   *   <li>Returns updated category</li>
   * </ul></p>
   *
   * <p>Updatable fields:
   * <ul>
   *   <li>name - New category name (must be unique)</li>
   *   <li>description - New category description</li>
   * </ul></p>
   *
   * @param id The ID of the category to update
   * @param categoryData The updated category data
   * @return The updated category information
   * @throws EntityNotFoundException if category not found
   * @throws IllegalArgumentException if new name conflicts with existing category
   */
  @Operation(
    summary = "Update a category",
    description = "Updates an existing category by its ID"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Category updated successfully"
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Invalid input data",
        content = @Content
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Category not found",
        content = @Content
      ),
    }
  )
  @PutMapping("/{id}")
  public CategoryInfo update(
    @Parameter(
      description = "ID of the category to update"
    ) @PathVariable Integer id,
    @Parameter(
      description = "Updated category data",
      required = true
    ) @Valid @RequestBody CategoryData categoryData
  ) {
    return categoryService.update(id, categoryData);
  }

  /**
   * Deletes a category by its ID.
   *
   * <p>This endpoint:
   * <ul>
   *   <li>Verifies category existence</li>
   *   <li>Checks for associated products</li>
   *   <li>Removes the category</li>
   * </ul></p>
   *
   * <p>Note: Categories with associated products cannot be deleted
   * until all products are reassigned or deleted.</p>
   *
   * @param id The ID of the category to delete
   * @throws EntityNotFoundException if category not found
   * @throws IllegalStateException if category has associated products
   */
  @Operation(
    summary = "Delete a category",
    description = "Deletes a category by its ID"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "204",
        description = "Category deleted successfully"
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Category not found",
        content = @Content
      ),
    }
  )
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
    @Parameter(
      description = "ID of the category to delete"
    ) @PathVariable Integer id
  ) {
    categoryService.delete(id);
  }

  /**
   * Deletes multiple categories by their IDs.
   *
   * <p>This endpoint:
   * <ul>
   *   <li>Validates all category IDs</li>
   *   <li>Checks for associated products</li>
   *   <li>Performs bulk deletion</li>
   * </ul></p>
   *
   * <p>Note: The operation will fail if any category:
   * <ul>
   *   <li>Does not exist</li>
   *   <li>Has associated products</li>
   * </ul></p>
   *
   * @param ids List of category IDs to delete
   * @throws EntityNotFoundException if any category not found
   * @throws IllegalStateException if any category has associated products
   */
  @Operation(
    summary = "Delete multiple categories",
    description = "Deletes multiple categories by their IDs"
  )
  @ApiResponse(
    responseCode = "204",
    description = "Categories deleted successfully"
  )
  @DeleteMapping("/delete-many")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAllById(
    @Parameter(
      description = "List of category IDs to delete",
      required = true
    ) @RequestBody List<Integer> ids
  ) {
    categoryService.deleteAllById(ids);
  }
}
