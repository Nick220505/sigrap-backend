package com.sigrap.category;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryInfo {

  private Integer id;
  private String name;
  private String description;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}