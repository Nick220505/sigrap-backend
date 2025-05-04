package com.sigrap.product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.sigrap.category.Category;
import com.sigrap.category.CategoryMapper;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface ProductMapper {

  @Mapping(target = "category", source = "category")
  ProductInfo toInfo(Product product);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "category", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Product toEntity(ProductData productData);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "category", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntityFromData(ProductData productData, @MappingTarget Product product);

  default Category mapCategory(Integer categoryId) {
    if (categoryId == null) {
      return null;
    }
    Category category = new Category();
    category.setId(categoryId);
    return category;
  }
}