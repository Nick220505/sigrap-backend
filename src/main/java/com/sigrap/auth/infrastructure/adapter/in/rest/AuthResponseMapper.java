package com.sigrap.auth.infrastructure.adapter.in.rest;

import com.sigrap.auth.domain.model.AuthenticationResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting AuthenticationResult to AuthResponse.
 * This mapper handles the translation between domain objects and REST DTOs.
 */
@Mapper(componentModel = "spring")
public interface AuthResponseMapper {

  /**
   * Converts an AuthenticationResult domain object to an AuthResponse DTO.
   *
   * @param result the authentication result from the domain
   * @return the REST response DTO
   */
  @Mapping(target = "token", source = "token.value")
  @Mapping(target = "expiresAt", source = "token.expiresAt")
  @Mapping(target = "email", source = "email.value")
  @Mapping(target = "authenticatedAt", source = "lastLogin")
  AuthResponse toResponse(AuthenticationResult result);
}
