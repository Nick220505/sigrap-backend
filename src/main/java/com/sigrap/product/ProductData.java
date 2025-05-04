package com.sigrap.product;

import java.math.BigDecimal;

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
public class ProductData {

  @NotBlank(message = "Product name cannot be blank")
  private String name;

  private String description;

  @NotNull(message = "Cost price cannot be null")
  @PositiveOrZero(message = "Cost price must be zero or positive")
  private BigDecimal costPrice;

  @NotNull(message = "Sale price cannot be null")
  @PositiveOrZero(message = "Sale price must be zero or positive")
  private BigDecimal salePrice;

  private Integer categoryId;
}