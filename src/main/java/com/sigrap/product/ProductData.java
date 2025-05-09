package com.sigrap.product;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating and updating products.
 * Contains validated product data for input operations.
 *
 * <p>This class:
 * <ul>
 *   <li>Validates product input data</li>
 *   <li>Supports create operations</li>
 *   <li>Supports update operations</li>
 *   <li>Manages pricing information</li>
 * </ul></p>
 *
 * <p>Validation Rules:
 * <ul>
 *   <li>Name must not be blank</li>
 *   <li>Name must be unique in the system</li>
 *   <li>Cost price must be zero or positive</li>
 *   <li>Sale price must be zero or positive</li>
 *   <li>Category ID must reference existing category</li>
 * </ul></p>
 *
 * <p>Usage Example:
 * <pre>
 * ProductData product = ProductData.builder()
 *     .name("Blue Pen")
 *     .description("High-quality blue ink pen")
 *     .costPrice(new BigDecimal("1.50"))
 *     .salePrice(new BigDecimal("2.50"))
 *     .categoryId(1)
 *     .build();
 * </pre></p>
 *
 * @see ProductController
 * @see ProductService
 * @see Product
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data transfer object for creating or updating a product")
public class ProductData {

  /**
   * The name of the product.
   *
   * <p>Validation:
   * <ul>
   *   <li>Must not be blank</li>
   *   <li>Must be unique across all products</li>
   *   <li>Should be descriptive and clear</li>
   * </ul></p>
   *
   * <p>Used for:
   * <ul>
   *   <li>Product identification</li>
   *   <li>Display in UI</li>
   *   <li>Search functionality</li>
   * </ul></p>
   */
  @NotBlank(message = "Product name cannot be blank")
  @Schema(description = "Name of the product", example = "Blue Pen")
  private String name;

  /**
   * The description of the product.
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
   *   <li>Product details</li>
   *   <li>Search optimization</li>
   *   <li>Customer information</li>
   * </ul></p>
   */
  @Schema(
    description = "Description of the product",
    example = "High-quality blue ink pen"
  )
  private String description;

  /**
   * The cost price of the product.
   *
   * <p>Validation:
   * <ul>
   *   <li>Must not be null</li>
   *   <li>Must be zero or positive</li>
   *   <li>Stored with 2 decimal places</li>
   * </ul></p>
   *
   * <p>Used for:
   * <ul>
   *   <li>Cost tracking</li>
   *   <li>Profit calculations</li>
   *   <li>Inventory valuation</li>
   * </ul></p>
   */
  @NotNull(message = "Cost price cannot be null")
  @PositiveOrZero(message = "Cost price must be zero or positive")
  @Schema(description = "Cost price of the product", example = "1.50")
  private BigDecimal costPrice;

  /**
   * The sale price of the product.
   *
   * <p>Validation:
   * <ul>
   *   <li>Must not be null</li>
   *   <li>Must be zero or positive</li>
   *   <li>Stored with 2 decimal places</li>
   * </ul></p>
   *
   * <p>Used for:
   * <ul>
   *   <li>Customer pricing</li>
   *   <li>Revenue calculations</li>
   *   <li>Sales reporting</li>
   * </ul></p>
   */
  @NotNull(message = "Sale price cannot be null")
  @PositiveOrZero(message = "Sale price must be zero or positive")
  @Schema(description = "Sale price of the product", example = "2.50")
  private BigDecimal salePrice;

  /**
   * The ID of the category this product belongs to.
   *
   * <p>Properties:
   * <ul>
   *   <li>References existing category</li>
   *   <li>Used for product organization</li>
   *   <li>Required for categorization</li>
   * </ul></p>
   *
   * <p>Used for:
   * <ul>
   *   <li>Product categorization</li>
   *   <li>Inventory organization</li>
   *   <li>Product filtering</li>
   * </ul></p>
   */
  @Schema(
    description = "ID of the category this product belongs to",
    example = "1"
  )
  private Integer categoryId;
}
