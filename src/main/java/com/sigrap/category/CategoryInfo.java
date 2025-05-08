package com.sigrap.category;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing category information")
public class CategoryInfo {

  @Schema(description = "Unique identifier of the category", example = "1")
  private Integer id;

  @Schema(description = "Name of the category", example = "Office Supplies")
  private String name;

  @Schema(description = "Description of the category", example = "Items used in office environments")
  private String description;

  @Schema(description = "Date and time when the category was created", example = "2023-01-15T10:30:00")
  private LocalDateTime createdAt;

  @Schema(description = "Date and time when the category was last updated", example = "2023-01-20T14:45:00")
  private LocalDateTime updatedAt;
}