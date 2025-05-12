package com.sigrap.product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.sigrap.category.Category;
import com.sigrap.category.CategoryMapper;
import com.sigrap.common.mapping.DataToEntity;
import com.sigrap.common.mapping.EntityToInfo;

/**
 * Mapper interface for converting between Product entity and its DTO representations.
 * Uses MapStruct for automatic implementation and CategoryMapper for nested mappings.
 */
@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface ProductMapper {
  /**
   * Converts a Product entity to its info representation.
   *
   * @param product The Product entity to convert
   * @return ProductInfo containing the product data
   */
  @EntityToInfo
  ProductInfo toInfo(Product product);

  /**
   * Creates a new Product entity from product data.
   * Category mapping is handled separately.
   *
   * @param productData The data to create the product from
   * @return A new Product entity
   */
  @DataToEntity
  @Mapping(target = "category", ignore = true)
  Product toEntity(ProductData productData);

  /**
   * Updates an existing Product entity with new data.
   * Category mapping is handled separately.
   *
   * @param productData The new data to update with
   * @param product The existing Product entity to update
   */
  @DataToEntity
  @Mapping(target = "category", ignore = true)
  void updateEntityFromData(
    ProductData productData,
    @MappingTarget Product product
  );

  /**
   * Helper method to map category ID to Category entity.
   * Used internally by MapStruct for category relationships.
   *
   * @param categoryId The ID of the category to map
   * @return A Category entity with the given ID, or null if ID is null
   */
  default Category mapCategory(Integer categoryId) {
    if (categoryId == null) {
      return null;
    }
    return Category.builder().id(categoryId.longValue()).build();
  }
}
