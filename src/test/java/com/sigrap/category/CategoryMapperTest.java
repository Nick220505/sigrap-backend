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
    Category category = new Category();
    category.setId(1);
    category.setName("Test Category");
    category.setDescription("Test Description");

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
    CategoryData categoryData = new CategoryData();
    categoryData.setName("Test Category");
    categoryData.setDescription("Test Description");

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
    Category category = new Category();
    category.setId(1);
    category.setName("Old Name");
    category.setDescription("Old Description");

    CategoryData categoryData = new CategoryData();
    categoryData.setName("New Name");
    categoryData.setDescription("New Description");

    categoryMapper.updateEntityFromData(categoryData, category);

    assertThat(category.getId()).isEqualTo(1);
    assertThat(category.getName()).isEqualTo("New Name");
    assertThat(category.getDescription()).isEqualTo("New Description");
  }

  @Test
  void updateEntityFromData_shouldDoNothing_whenCategoryDataIsNull() {
    Category category = new Category();
    category.setId(1);
    category.setName("Original Name");
    category.setDescription("Original Description");

    categoryMapper.updateEntityFromData(null, category);

    assertThat(category.getId()).isEqualTo(1);
    assertThat(category.getName()).isEqualTo("Original Name");
    assertThat(category.getDescription()).isEqualTo("Original Description");
  }

  @Test
  void updateEntityFromData_shouldSetNullValues_whenDataFieldsAreNull() {
    Category category = new Category();
    category.setId(1);
    category.setName("Original Name");
    category.setDescription("Original Description");

    CategoryData categoryData = new CategoryData();
    categoryData.setName("New Name");

    categoryMapper.updateEntityFromData(categoryData, category);

    assertThat(category.getId()).isEqualTo(1);
    assertThat(category.getName()).isEqualTo("New Name");
    assertThat(category.getDescription()).isNull();
  }
}