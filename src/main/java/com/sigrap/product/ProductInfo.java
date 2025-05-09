package com.sigrap.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sigrap.category.CategoryInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing product information")
public class ProductInfo {

  @Schema(description = "Unique identifier of the product", example = "1")
  private Integer id;

  @Schema(description = "Name of the product", example = "Blue Pen")
  private String name;

  @Schema(
    description = "Description of the product",
    example = "High-quality blue ink pen"
  )
  private String description;

  @Schema(description = "Cost price of the product", example = "1.50")
  private BigDecimal costPrice;

  @Schema(description = "Sale price of the product", example = "2.50")
  private BigDecimal salePrice;

  @Schema(description = "Category the product belongs to")
  private CategoryInfo category;

  @Schema(
    description = "Date and time when the product was created",
    example = "2023-01-15T10:30:00"
  )
  private LocalDateTime createdAt;

  @Schema(
    description = "Date and time when the product was last updated",
    example = "2023-01-20T14:45:00"
  )
  private LocalDateTime updatedAt;
}
