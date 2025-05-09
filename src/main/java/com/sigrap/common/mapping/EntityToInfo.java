package com.sigrap.common.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mapstruct.Mapping;

/**
 * Custom MapStruct annotation for mapping entity objects to info/response DTOs.
 * Automatically maps common JPA fields (id, createdAt, updatedAt) during mapping.
 *
 * <p>This annotation is used in mapper interfaces to simplify the mapping process
 * from JPA entities to Data Transfer Objects (DTOs) by automatically including
 * common audit fields in the mapping process.</p>
 *
 * <p>Example usage:
 * {@code
 * @EntityToInfo
 * ProductInfo toInfo(Product entity);
 * }</p>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@Mapping(target = "id", source = "id")
@Mapping(target = "createdAt", source = "createdAt")
@Mapping(target = "updatedAt", source = "updatedAt")
public @interface EntityToInfo {
}
