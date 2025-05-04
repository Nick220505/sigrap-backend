package com.sigrap.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sigrap.category.CategoryInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfo {

  private Integer id;
  private String name;
  private String description;
  private BigDecimal costPrice;
  private BigDecimal salePrice;
  private CategoryInfo category;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}