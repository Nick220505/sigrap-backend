package com.sigrap.notification;

import com.sigrap.common.mapping.DataToEntity;
import com.sigrap.common.mapping.EntityToInfo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for converting between NotificationPreference entity and its DTO representations.
 * Uses MapStruct for automatic implementation.
 */
@Mapper(componentModel = "spring")
public interface NotificationPreferenceMapper {
  /**
   * Converts a NotificationPreference entity to its info representation.
   *
   * @param preference The NotificationPreference entity to convert
   * @return NotificationPreferenceInfo containing the preference data
   */
  @EntityToInfo
  NotificationPreferenceInfo toInfo(NotificationPreference preference);

  /**
   * Creates a new NotificationPreference entity from preference data.
   *
   * @param preferenceData The data to create the preference from
   * @return A new NotificationPreference entity
   */
  @DataToEntity
  NotificationPreference toEntity(NotificationPreferenceData preferenceData);

  /**
   * Updates an existing NotificationPreference entity with new data.
   *
   * @param preferenceData The new data to update with
   * @param preference The existing NotificationPreference entity to update
   */
  @DataToEntity
  void updateEntityFromData(
    NotificationPreferenceData preferenceData,
    @MappingTarget NotificationPreference preference
  );
}
