package com.sigrap.employee.schedule;

import com.sigrap.user.User;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between Schedule entities and DTOs.
 * Handles the conversion of data between different representations.
 *
 * <p>This class provides methods for:
 * <ul>
 *   <li>Converting entities to DTOs</li>
 *   <li>Converting DTOs to entities</li>
 *   <li>Updating entities from DTOs</li>
 *   <li>Batch conversions</li>
 * </ul></p>
 */
@Component
@RequiredArgsConstructor
public class ScheduleMapper {

  /**
   * Converts a Schedule entity to ScheduleInfo DTO.
   *
   * @param schedule The schedule entity to convert
   * @return ScheduleInfo containing the schedule's information
   */
  public ScheduleInfo toInfo(Schedule schedule) {
    if (schedule == null) {
      return null;
    }

    return ScheduleInfo.builder()
      .id(schedule.getId())
      .userId(schedule.getUser().getId())
      .userName(schedule.getUser().getName())
      .day(schedule.getDay())
      .startTime(schedule.getStartTime())
      .endTime(schedule.getEndTime())
      .isActive(schedule.getIsActive())
      .createdAt(schedule.getCreatedAt())
      .updatedAt(schedule.getUpdatedAt())
      .build();
  }

  /**
   * Converts a list of Schedule entities to a list of ScheduleInfo DTOs.
   *
   * @param schedules List of schedule entities to convert
   * @return List of ScheduleInfo DTOs
   */
  public List<ScheduleInfo> toInfoList(List<Schedule> schedules) {
    if (schedules == null) {
      return Collections.emptyList();
    }

    return schedules.stream().map(this::toInfo).toList();
  }

  /**
   * Creates a new Schedule entity from ScheduleData DTO and a User entity.
   *
   * @param data The DTO containing schedule data
   * @param user The User entity to associate with the schedule
   * @return New Schedule entity
   */
  public Schedule toEntity(ScheduleData data, User user) {
    if (data == null || user == null) {
      return null;
    }

    return Schedule.builder()
      .user(user)
      .day(data.getDay())
      .startTime(data.getStartTime())
      .endTime(data.getEndTime())
      .isActive(Optional.ofNullable(data.getIsActive()).orElse(true))
      .build();
  }

  /**
   * Updates an existing Schedule entity with data from ScheduleData DTO.
   *
   * @param entity The schedule entity to update
   * @param data The DTO containing new schedule data
   */
  public void updateEntity(Schedule entity, ScheduleData data) {
    if (data == null || entity == null) {
      return;
    }

    if (data.getDay() != null) {
      entity.setDay(data.getDay());
    }
    if (data.getStartTime() != null) {
      entity.setStartTime(data.getStartTime());
    }
    if (data.getEndTime() != null) {
      entity.setEndTime(data.getEndTime());
    }
    if (data.getIsActive() != null) {
      entity.setIsActive(data.getIsActive());
    }
  }
}
