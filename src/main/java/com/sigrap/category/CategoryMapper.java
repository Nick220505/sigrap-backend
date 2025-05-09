package com.sigrap.category;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.sigrap.common.mapping.DataToEntity;
import com.sigrap.common.mapping.EntityToInfo;

/**
 * Mapper interface for converting between Category entity and its DTO representations.
 * Implemented automatically by MapStruct.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {
  /**
   * Converts a Category entity to its info representation.
   *
   * @param category The Category entity to convert
   * @return CategoryInfo containing the category data
   */
  @EntityToInfo
  CategoryInfo toInfo(Category category);

  /**
   * Creates a new Category entity from category data.
   *
   * @param categoryData The data to create the category from
   * @return A new Category entity
   */
  @DataToEntity
  Category toEntity(CategoryData categoryData);

  /**
   * Updates an existing Category entity with new data.
   *
   * @param categoryData The new data to update the category with
   * @param category The existing Category entity to update
   */
  @DataToEntity
  void updateEntityFromData(
    CategoryData categoryData,
    @MappingTarget Category category
  );
}
