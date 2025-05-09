package com.sigrap.category;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing category operations.
 * Handles business logic for creating, reading, updating, and deleting categories.
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

  /**
   * Repository for database operations on categories.
   * Provides CRUD functionality and custom queries for category entities.
   */
  private final CategoryRepository categoryRepository;

  /**
   * Mapper for converting between Category entities and DTOs.
   * Handles object transformation for API responses and database operations.
   */
  private final CategoryMapper categoryMapper;

  /**
   * Retrieves all categories from the database.
   *
   * @return List of all categories mapped to CategoryInfo objects
   */
  @Transactional(readOnly = true)
  public List<CategoryInfo> findAll() {
    return categoryRepository
      .findAll()
      .stream()
      .map(categoryMapper::toInfo)
      .toList();
  }

  /**
   * Finds a category by its ID.
   *
   * @param id The ID of the category to find
   * @return The found category mapped to CategoryInfo
   * @throws EntityNotFoundException if the category is not found
   */
  @Transactional(readOnly = true)
  public CategoryInfo findById(Integer id) {
    Category category = categoryRepository
      .findById(id)
      .orElseThrow(EntityNotFoundException::new);
    return categoryMapper.toInfo(category);
  }

  /**
   * Creates a new category.
   *
   * @param categoryData The data for creating the category
   * @return The created category mapped to CategoryInfo
   */
  @Transactional
  public CategoryInfo create(CategoryData categoryData) {
    Category category = categoryMapper.toEntity(categoryData);
    Category savedCategory = categoryRepository.save(category);
    return categoryMapper.toInfo(savedCategory);
  }

  /**
   * Updates an existing category.
   *
   * @param id The ID of the category to update
   * @param categoryData The new data for the category
   * @return The updated category mapped to CategoryInfo
   * @throws EntityNotFoundException if the category is not found
   */
  @Transactional
  public CategoryInfo update(Integer id, CategoryData categoryData) {
    Category category = categoryRepository
      .findById(id)
      .orElseThrow(EntityNotFoundException::new);
    categoryMapper.updateEntityFromData(categoryData, category);
    Category updatedCategory = categoryRepository.save(category);
    return categoryMapper.toInfo(updatedCategory);
  }

  /**
   * Deletes a category by its ID.
   *
   * @param id The ID of the category to delete
   * @throws EntityNotFoundException if the category is not found
   */
  @Transactional
  public void delete(Integer id) {
    Category category = categoryRepository
      .findById(id)
      .orElseThrow(EntityNotFoundException::new);
    categoryRepository.delete(category);
  }

  /**
   * Deletes multiple categories by their IDs.
   * Validates all IDs exist before performing the deletion.
   *
   * @param ids List of category IDs to delete
   * @throws EntityNotFoundException if any of the categories is not found
   */
  @Transactional
  public void deleteAllById(List<Integer> ids) {
    ids.forEach(id -> {
      if (!categoryRepository.existsById(id)) {
        throw new EntityNotFoundException(
          "Category with id " + id + " not found"
        );
      }
    });
    categoryRepository.deleteAllById(ids);
  }
}
