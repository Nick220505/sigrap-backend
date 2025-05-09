package com.sigrap.common.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mapstruct.Mapping;

/**
 * Custom MapStruct annotation for mapping DTO objects to entity objects.
 * Automatically ignores common JPA fields (id, createdAt, updatedAt) during mapping.
 *
 * <p>This annotation is used in mapper interfaces to simplify the mapping process
 * from Data Transfer Objects (DTOs) to JPA entities by automatically excluding
 * fields that should not be mapped from the source object.</p>
 *
 * <p>Example usage:
 * {@code
 * @DataToEntity
 * Product toEntity(ProductData data);
 * }</p>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@Mapping(target = "id", ignore = true)
@Mapping(target = "createdAt", ignore = true)
@Mapping(target = "updatedAt", ignore = true)
public @interface DataToEntity {
}
