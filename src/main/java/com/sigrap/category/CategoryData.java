package com.sigrap.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating and updating categories.
 * Contains validated category data for input operations.
 *
 * <p>This class:
 * <ul>
 *   <li>Validates category input data</li>
 *   <li>Supports create operations</li>
 *   <li>Supports update operations</li>
 * </ul></p>
 *
 * <p>Validation Rules:
 * <ul>
 *   <li>Name must not be blank</li>
 *   <li>Name must be unique in the system</li>
 *   <li>Description is optional</li>
 * </ul></p>
 *
 * <p>Usage Example:
 * <pre>
 * CategoryData category = CategoryData.builder()
 *     .name("Office Supplies")
 *     .description("General office supplies and stationery")
 *     .build();
 * </pre></p>
 *
 * @see CategoryController
 * @see CategoryService
 * @see Category
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
  description = "Data transfer object for creating or updating a category"
)
public class CategoryData {

  /**
   * The name of the category.
   *
   * <p>Validation:
   * <ul>
   *   <li>Must not be blank</li>
   *   <li>Must be unique across all categories</li>
   *   <li>Should be descriptive and clear</li>
   * </ul></p>
   *
   * <p>Used for:
   * <ul>
   *   <li>Category identification</li>
   *   <li>Display in UI</li>
   *   <li>Product categorization</li>
   * </ul></p>
   */
  @NotBlank(message = "Category name cannot be blank")
  @Schema(description = "Name of the category", example = "Office Supplies")
  private String name;

  /**
   * The description of the category.
   *
   * <p>Properties:
   * <ul>
   *   <li>Optional field</li>
   *   <li>Can contain detailed information</li>
   *   <li>Used for additional context</li>
   * </ul></p>
   *
   * <p>Used for:
   * <ul>
   *   <li>Providing category details</li>
   *   <li>Help text in UI</li>
   *   <li>Search optimization</li>
   * </ul></p>
   */
  @Schema(
    description = "Description of the category",
    example = "Items used in office environments"
  )
  private String description;
}
