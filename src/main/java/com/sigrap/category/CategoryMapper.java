package com.sigrap.category;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.sigrap.common.mapping.DataToEntity;
import com.sigrap.common.mapping.EntityToInfo;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

  @EntityToInfo
  CategoryInfo toInfo(Category category);

  @DataToEntity
  Category toEntity(CategoryData categoryData);

  @DataToEntity
  void updateEntityFromData(CategoryData categoryData, @MappingTarget Category category);
}