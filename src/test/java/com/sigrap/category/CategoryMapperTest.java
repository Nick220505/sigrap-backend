package com.sigrap.category;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CategoryMapperTest {

  @Autowired
  private CategoryMapper categoryMapper;

  @Test
  void toInfo_shouldMapEntityToInfo() {
    Category category = Category.builder()
        .id(1)
        .name("Test Category")
        .description("Test Description")
        .build();

    CategoryInfo categoryInfo = categoryMapper.toInfo(category);

    assertThat(categoryInfo).isNotNull();
    assertThat(categoryInfo.getId()).isEqualTo(1);
    assertThat(categoryInfo.getName()).isEqualTo("Test Category");
    assertThat(categoryInfo.getDescription()).isEqualTo("Test Description");
  }

  @Test
  void toInfo_shouldReturnNull_whenCategoryIsNull() {
    CategoryInfo categoryInfo = categoryMapper.toInfo(null);
    assertThat(categoryInfo).isNull();
  }

  @Test
  void toEntity_shouldMapDataToEntity() {
    CategoryData categoryData = CategoryData.builder()
        .name("Test Category")
        .description("Test Description")
        .build();

    Category category = categoryMapper.toEntity(categoryData);

    assertThat(category).isNotNull();
    assertThat(category.getId()).isNull();
    assertThat(category.getName()).isEqualTo("Test Category");
    assertThat(category.getDescription()).isEqualTo("Test Description");
  }

  @Test
  void toEntity_shouldReturnNull_whenCategoryDataIsNull() {
    Category category = categoryMapper.toEntity(null);
    assertThat(category).isNull();
  }

  @Test
  void updateEntityFromData_shouldUpdateEntityWithDataValues() {
    Category category = Category.builder()
        .id(1)
        .name("Old Name")
        .description("Old Description")
        .build();

    CategoryData categoryData = CategoryData.builder()
        .name("New Name")
        .description("New Description")
        .build();

    categoryMapper.updateEntityFromData(categoryData, category);

    assertThat(category.getId()).isEqualTo(1);
    assertThat(category.getName()).isEqualTo("New Name");
    assertThat(category.getDescription()).isEqualTo("New Description");
  }

  @Test
  void updateEntityFromData_shouldDoNothing_whenCategoryDataIsNull() {
    Category category = Category.builder()
        .id(1)
        .name("Original Name")
        .description("Original Description")
        .build();

    categoryMapper.updateEntityFromData(null, category);

    assertThat(category.getId()).isEqualTo(1);
    assertThat(category.getName()).isEqualTo("Original Name");
    assertThat(category.getDescription()).isEqualTo("Original Description");
  }

  @Test
  void updateEntityFromData_shouldSetNullValues_whenDataFieldsAreNull() {
    Category category = Category.builder()
        .id(1)
        .name("Original Name")
        .description("Original Description")
        .build();

    CategoryData categoryData = CategoryData.builder()
        .name("New Name")
        .build();

    categoryMapper.updateEntityFromData(categoryData, category);

    assertThat(category.getId()).isEqualTo(1);
    assertThat(category.getName()).isEqualTo("New Name");
    assertThat(category.getDescription()).isNull();
  }
}