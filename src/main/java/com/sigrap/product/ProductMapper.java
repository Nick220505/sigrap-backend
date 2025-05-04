package com.sigrap.product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.sigrap.category.Category;
import com.sigrap.category.CategoryMapper;
import com.sigrap.common.mapping.DataToEntity;
import com.sigrap.common.mapping.EntityToInfo;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface ProductMapper {

  @EntityToInfo
  ProductInfo toInfo(Product product);

  @DataToEntity
  @Mapping(target = "category", ignore = true)
  Product toEntity(ProductData productData);

  @DataToEntity
  @Mapping(target = "category", ignore = true)
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