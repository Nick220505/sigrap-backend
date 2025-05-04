package com.sigrap.common.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mapstruct.Mapping;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@Mapping(target = "id", source = "id")
@Mapping(target = "createdAt", source = "createdAt")
@Mapping(target = "updatedAt", source = "updatedAt")
public @interface EntityToInfo {
}