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
    Category category = new Category();
    category.setName("Test Category");
    category.setDescription("Test Description");

    Category savedCategory = categoryRepository.save(category);

    assertThat(savedCategory.getId()).isNotNull();
    assertThat(savedCategory.getName()).isEqualTo("Test Category");
    assertThat(savedCategory.getDescription()).isEqualTo("Test Description");
  }

  @Test
  void shouldFindCategoryById() {
    Category category = new Category();
    category.setName("Test Category");
    category.setDescription("Test Description");
    Category savedCategory = categoryRepository.save(category);

    Optional<Category> result = categoryRepository.findById(savedCategory.getId());

    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Test Category");
  }

  @Test
  void shouldFindAllCategories() {
    Category category1 = new Category();
    category1.setName("Category 1");
    category1.setDescription("Description 1");

    Category category2 = new Category();
    category2.setName("Category 2");
    category2.setDescription("Description 2");

    categoryRepository.save(category1);
    categoryRepository.save(category2);

    List<Category> categories = categoryRepository.findAll();

    assertThat(categories).hasSize(2);
    assertThat(categories).extracting(Category::getName).contains("Category 1", "Category 2");
  }

  @Test
  void shouldDeleteCategory() {
    Category category = new Category();
    category.setName("Test Category");
    Category savedCategory = categoryRepository.save(category);

    categoryRepository.deleteById(savedCategory.getId());
    Optional<Category> result = categoryRepository.findById(savedCategory.getId());

    assertThat(result).isEmpty();
  }
}