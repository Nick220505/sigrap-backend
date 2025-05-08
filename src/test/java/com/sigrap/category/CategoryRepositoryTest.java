package com.sigrap.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class CategoryRepositoryTest {

  @Autowired
  private CategoryRepository categoryRepository;

  @Test
  void shouldSaveCategory() {
    Category category = Category.builder()
        .name("Test Category")
        .description("Test Description")
        .build();

    Category savedCategory = categoryRepository.save(category);

    assertThat(savedCategory.getId()).isNotNull();
    assertThat(savedCategory.getName()).isEqualTo("Test Category");
    assertThat(savedCategory.getDescription()).isEqualTo("Test Description");
  }

  @Test
  void shouldFindCategoryById() {
    Category category = Category.builder()
        .name("Test Category")
        .description("Test Description")
        .build();

    Category savedCategory = categoryRepository.save(category);

    Optional<Category> foundCategory = categoryRepository.findById(savedCategory.getId());

    assertThat(foundCategory).isPresent();
    assertThat(foundCategory.get().getName()).isEqualTo("Test Category");
  }

  @Test
  void shouldFindAllCategories() {
    Category category1 = Category.builder()
        .name("Category 1")
        .description("Description 1")
        .build();

    Category category2 = Category.builder()
        .name("Category 2")
        .description("Description 2")
        .build();

    categoryRepository.save(category1);
    categoryRepository.save(category2);

    List<Category> categories = categoryRepository.findAll();

    assertThat(categories).hasSize(2);
    assertThat(categories).extracting(Category::getName).contains("Category 1", "Category 2");
  }

  @Test
  void shouldDeleteCategory() {
    Category category = Category.builder()
        .name("Test Category")
        .build();

    Category savedCategory = categoryRepository.save(category);

    categoryRepository.deleteById(savedCategory.getId());
    Optional<Category> deletedCategory = categoryRepository.findById(savedCategory.getId());

    assertThat(deletedCategory).isEmpty();
  }
}