package com.sigrap.category.infrastructure.adapter.out.persistence;

import com.sigrap.category.domain.model.Category;
import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.category.domain.model.CategoryName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between domain Category and CategoryJpaEntity.
 * This mapper handles the translation between the domain model (pure POJOs)
 * and the persistence model (JPA entities).
 * 
 * The mapper uses custom methods to convert between value objects (CategoryId, CategoryName)
 * and their primitive representations used in the JPA entity.
 */
@Mapper(componentModel = "spring")
public interface CategoryPersistenceMapper {

    /**
     * Converts a domain Category to a JPA entity.
     * Maps value objects to their primitive representations.
     *
     * @param category the domain category
     * @return the JPA entity representation
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "categoryIdToLong")
    @Mapping(target = "name", source = "name", qualifiedByName = "categoryNameToString")
    CategoryJpaEntity toJpaEntity(Category category);

    /**
     * Converts a JPA entity to a domain Category.
     * Maps primitive values to value objects.
     * Uses a custom implementation to handle the immutable domain entity.
     *
     * @param entity the JPA entity
     * @return the domain category
     */
    default Category toDomain(CategoryJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        CategoryId id = longToCategoryId(entity.getId());
        CategoryName name = stringToCategoryName(entity.getName());
        
        return new Category(
            id,
            name,
            entity.getDescription(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Converts a CategoryId value object to its Long representation.
     *
     * @param id the category ID value object
     * @return the Long value, or null if the ID is null
     */
    @Named("categoryIdToLong")
    default Long categoryIdToLong(CategoryId id) {
        return id != null ? id.value() : null;
    }

    /**
     * Converts a Long to a CategoryId value object.
     *
     * @param id the Long value
     * @return the CategoryId value object, or null if the Long is null
     */
    @Named("longToCategoryId")
    default CategoryId longToCategoryId(Long id) {
        return id != null ? new CategoryId(id) : null;
    }

    /**
     * Converts a CategoryName value object to its String representation.
     *
     * @param name the category name value object
     * @return the String value, or null if the name is null
     */
    @Named("categoryNameToString")
    default String categoryNameToString(CategoryName name) {
        return name != null ? name.value() : null;
    }

    /**
     * Converts a String to a CategoryName value object.
     *
     * @param name the String value
     * @return the CategoryName value object, or null if the String is null
     */
    @Named("stringToCategoryName")
    default CategoryName stringToCategoryName(String name) {
        return name != null ? new CategoryName(name) : null;
    }
}
