package com.sigrap.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Data transfer object for creating or updating a category")
public class CategoryData {

  @NotBlank(message = "Category name cannot be blank")
  @Schema(description = "Name of the category", example = "Office Supplies")
  private String name;

  @Schema(description = "Description of the category", example = "Items used in office environments")
  private String description;
}