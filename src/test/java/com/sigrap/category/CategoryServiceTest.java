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
    Category category = new Category() {
      {
        setId(id);
        setName("Test Category");
      }
    };

    CategoryInfo categoryInfo = new CategoryInfo() {
      {
        setId(id);
        setName("Test Category");
      }
    };

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
    List<Category> categories = List.of(
        new Category() {
          {
            setId(1);
            setName("Category 1");
          }
        },
        new Category() {
          {
            setId(2);
            setName("Category 2");
          }
        });

    List<CategoryInfo> categoryInfos = List.of(
        new CategoryInfo(1, "Category 1", null, null, null),
        new CategoryInfo(2, "Category 2", null, null, null));

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
    CategoryData categoryData = new CategoryData() {
      {
        setName("New Category");
        setDescription("New Description");
      }
    };

    Category category = new Category() {
      {
        setName("New Category");
        setDescription("New Description");
      }
    };

    Category savedCategory = new Category() {
      {
        setId(1);
        setName("New Category");
        setDescription("New Description");
      }
    };

    CategoryInfo categoryInfo = new CategoryInfo() {
      {
        setId(1);
        setName("New Category");
        setDescription("New Description");
      }
    };

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
    CategoryData categoryData = new CategoryData() {
      {
        setName("Updated Name");
        setDescription("Updated Description");
      }
    };

    Category existingCategory = new Category() {
      {
        setId(id);
        setName("Old Name");
      }
    };

    Category updatedCategory = new Category() {
      {
        setId(id);
        setName("Updated Name");
        setDescription("Updated Description");
      }
    };

    CategoryInfo categoryInfo = new CategoryInfo() {
      {
        setId(id);
        setName("Updated Name");
        setDescription("Updated Description");
      }
    };

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
    CategoryData categoryData = new CategoryData() {
      {
        setName("Updated Name");
      }
    };

    when(categoryRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      categoryService.update(id, categoryData);
    });
    verify(categoryRepository, never()).save(any());
  }

  @Test
  void delete_shouldDeleteCategory_whenCategoryExists() {
    Integer id = 1;
    Category category = new Category() {
      {
        setId(id);
      }
    };

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