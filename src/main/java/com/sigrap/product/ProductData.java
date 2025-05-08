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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data transfer object for creating or updating a product")
public class ProductData {

  @NotBlank(message = "Product name cannot be blank")
  @Schema(description = "Name of the product", example = "Blue Pen")
  private String name;

  @Schema(description = "Description of the product", example = "High-quality blue ink pen")
  private String description;

  @NotNull(message = "Cost price cannot be null")
  @PositiveOrZero(message = "Cost price must be zero or positive")
  @Schema(description = "Cost price of the product", example = "1.50")
  private BigDecimal costPrice;

  @NotNull(message = "Sale price cannot be null")
  @PositiveOrZero(message = "Sale price must be zero or positive")
  @Schema(description = "Sale price of the product", example = "2.50")
  private BigDecimal salePrice;

  @Schema(description = "ID of the category this product belongs to", example = "1")
  private Integer categoryId;
}