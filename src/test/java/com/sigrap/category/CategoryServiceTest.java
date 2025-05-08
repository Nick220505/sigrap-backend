package com.sigrap.category;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private CategoryMapper categoryMapper;

  @InjectMocks
  private CategoryService categoryService;

  @Test
  void findById_shouldReturnCategoryInfo_whenCategoryExists() {
    Integer id = 1;
    Category category = new Category();
    category.setId(id);
    category.setName("Test Category");

    CategoryInfo categoryInfo = new CategoryInfo();
    categoryInfo.setId(id);
    categoryInfo.setName("Test Category");

    when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
    when(categoryMapper.toInfo(category)).thenReturn(categoryInfo);

    CategoryInfo foundCategoryInfo = categoryService.findById(id);

    assertThat(foundCategoryInfo).isNotNull();
    assertThat(foundCategoryInfo.getId()).isEqualTo(id);
    assertThat(foundCategoryInfo.getName()).isEqualTo("Test Category");
  }

  @Test
  void findById_shouldThrowException_whenCategoryDoesNotExist() {
    Integer id = 1;
    when(categoryRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      categoryService.findById(id);
    });
  }

  @Test
  void findAll_shouldReturnAllCategoryInfos() {
    Category category1 = new Category();
    category1.setId(1);
    category1.setName("Category 1");

    Category category2 = new Category();
    category2.setId(2);
    category2.setName("Category 2");

    List<Category> categories = List.of(category1, category2);

    CategoryInfo categoryInfo1 = new CategoryInfo();
    categoryInfo1.setId(1);
    categoryInfo1.setName("Category 1");

    CategoryInfo categoryInfo2 = new CategoryInfo();
    categoryInfo2.setId(2);
    categoryInfo2.setName("Category 2");

    List<CategoryInfo> categoryInfos = List.of(categoryInfo1, categoryInfo2);

    when(categoryRepository.findAll()).thenReturn(categories);
    when(categoryMapper.toInfo(categories.get(0))).thenReturn(categoryInfos.get(0));
    when(categoryMapper.toInfo(categories.get(1))).thenReturn(categoryInfos.get(1));

    List<CategoryInfo> allCategoryInfos = categoryService.findAll();

    assertThat(allCategoryInfos).hasSize(2);
    assertThat(allCategoryInfos.get(0).getName()).isEqualTo("Category 1");
    assertThat(allCategoryInfos.get(1).getName()).isEqualTo("Category 2");
  }

  @Test
  void create_shouldCreateCategory() {
    CategoryData categoryData = new CategoryData();
    categoryData.setName("New Category");
    categoryData.setDescription("New Description");

    Category category = new Category();
    category.setName("New Category");
    category.setDescription("New Description");

    Category savedCategory = new Category();
    savedCategory.setId(1);
    savedCategory.setName("New Category");
    savedCategory.setDescription("New Description");

    CategoryInfo categoryInfo = new CategoryInfo();
    categoryInfo.setId(1);
    categoryInfo.setName("New Category");
    categoryInfo.setDescription("New Description");

    when(categoryMapper.toEntity(categoryData)).thenReturn(category);
    when(categoryRepository.save(category)).thenReturn(savedCategory);
    when(categoryMapper.toInfo(savedCategory)).thenReturn(categoryInfo);

    CategoryInfo createdCategoryInfo = categoryService.create(categoryData);

    assertThat(createdCategoryInfo).isNotNull();
    assertThat(createdCategoryInfo.getId()).isEqualTo(1);
    assertThat(createdCategoryInfo.getName()).isEqualTo("New Category");
    assertThat(createdCategoryInfo.getDescription()).isEqualTo("New Description");
    verify(categoryRepository).save(category);
  }

  @Test
  void update_shouldUpdateCategory_whenCategoryExists() {
    Integer id = 1;
    CategoryData categoryData = new CategoryData();
    categoryData.setName("Updated Name");
    categoryData.setDescription("Updated Description");

    Category existingCategory = new Category();
    existingCategory.setId(id);
    existingCategory.setName("Old Name");

    Category updatedCategory = new Category();
    updatedCategory.setId(id);
    updatedCategory.setName("Updated Name");
    updatedCategory.setDescription("Updated Description");

    CategoryInfo categoryInfo = new CategoryInfo();
    categoryInfo.setId(id);
    categoryInfo.setName("Updated Name");
    categoryInfo.setDescription("Updated Description");

    when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
    doNothing().when(categoryMapper).updateEntityFromData(categoryData, existingCategory);
    when(categoryRepository.save(existingCategory)).thenReturn(updatedCategory);
    when(categoryMapper.toInfo(updatedCategory)).thenReturn(categoryInfo);

    CategoryInfo updatedCategoryInfo = categoryService.update(id, categoryData);

    assertThat(updatedCategoryInfo).isNotNull();
    assertThat(updatedCategoryInfo.getId()).isEqualTo(id);
    assertThat(updatedCategoryInfo.getName()).isEqualTo("Updated Name");
    verify(categoryRepository).save(existingCategory);
  }

  @Test
  void update_shouldThrowException_whenCategoryDoesNotExist() {
    Integer id = 1;
    CategoryData categoryData = new CategoryData();
    categoryData.setName("Updated Name");

    when(categoryRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      categoryService.update(id, categoryData);
    });
    verify(categoryRepository, never()).save(any());
  }

  @Test
  void delete_shouldDeleteCategory_whenCategoryExists() {
    Integer id = 1;
    Category category = new Category();
    category.setId(id);

    when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

    categoryService.delete(id);

    verify(categoryRepository).delete(category);
  }

  @Test
  void delete_shouldThrowException_whenCategoryDoesNotExist() {
    Integer id = 1;
    when(categoryRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      categoryService.delete(id);
    });
    verify(categoryRepository, never()).delete(any());
  }

  @Test
  void deleteAllById_shouldDeleteAllCategories_whenAllExist() {
    List<Integer> ids = List.of(1, 2);

    when(categoryRepository.existsById(1)).thenReturn(true);
    when(categoryRepository.existsById(2)).thenReturn(true);
    doNothing().when(categoryRepository).deleteAllById(ids);

    categoryService.deleteAllById(ids);

    verify(categoryRepository).deleteAllById(ids);
  }

  @Test
  void deleteAllById_shouldThrowException_whenAnyCategoryDoesNotExist() {
    List<Integer> ids = List.of(1, 2);

    when(categoryRepository.existsById(1)).thenReturn(true);
    when(categoryRepository.existsById(2)).thenReturn(false);

    assertThrows(EntityNotFoundException.class, () -> {
      categoryService.deleteAllById(ids);
    });
    verify(categoryRepository, never()).deleteAllById(any());
  }
}