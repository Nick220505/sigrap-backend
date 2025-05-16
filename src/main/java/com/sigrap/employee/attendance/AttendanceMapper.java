package com.sigrap.employee.attendance;

import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between Attendance entities and DTOs.
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
public class AttendanceMapper {

  private final UserRepository userRepository;

  /**
   * Converts an Attendance entity to AttendanceInfo DTO.
   *
   * @param attendance The attendance entity to convert
   * @return AttendanceInfo containing the attendance's information
   */
  public AttendanceInfo toInfo(Attendance attendance) {
    if (attendance == null) {
      return null;
    }

    return AttendanceInfo.builder()
      .id(attendance.getId())
      .userId(attendance.getUser().getId())
      .userName(attendance.getUser().getName())
      .date(attendance.getDate())
      .clockInTime(attendance.getClockInTime())
      .clockOutTime(attendance.getClockOutTime())
      .totalHours(attendance.getTotalHours())
      .status(attendance.getStatus())
      .notes(attendance.getNotes())
      .createdAt(attendance.getCreatedAt())
      .updatedAt(attendance.getUpdatedAt())
      .build();
  }

  /**
   * Converts a list of Attendance entities to a list of AttendanceInfo DTOs.
   *
   * @param attendances List of attendance entities to convert
   * @return List of AttendanceInfo DTOs
   */
  public List<AttendanceInfo> toInfoList(List<Attendance> attendances) {
    if (attendances == null || attendances.isEmpty()) {
      return Collections.emptyList();
    }
    return attendances.stream().map(this::toInfo).toList();
  }

  /**
   * Creates a new Attendance entity from AttendanceData DTO.
   *
   * @param attendanceData The DTO containing attendance data
   * @return New Attendance entity
   * @throws EntityNotFoundException if referenced user is not found
   */
  public Attendance toEntity(AttendanceData attendanceData) {
    if (attendanceData == null) {
      return null;
    }

    User user = userRepository
      .findById(attendanceData.getUserId())
      .orElseThrow(() ->
        new EntityNotFoundException(
          "User not found: " + attendanceData.getUserId()
        )
      );

    Attendance attendance = Attendance.builder()
      .user(user)
      .date(attendanceData.getDate())
      .clockInTime(attendanceData.getClockInTime())
      .clockOutTime(attendanceData.getClockOutTime())
      .status(attendanceData.getStatus())
      .notes(attendanceData.getNotes())
      .build();

    calculateTotalHours(attendance);
    return attendance;
  }

  /**
   * Updates an existing Attendance entity with data from AttendanceData DTO.
   *
   * @param attendance The attendance entity to update
   * @param attendanceData The DTO containing new attendance data
   * @throws EntityNotFoundException if referenced user is not found
   */
  public void updateEntityFromData(
    Attendance attendance,
    AttendanceData attendanceData
  ) {
    if (attendanceData == null) {
      return;
    }

    if (
      attendanceData.getUserId() != null &&
      !attendanceData.getUserId().equals(attendance.getUser().getId())
    ) {
      User user = userRepository
        .findById(attendanceData.getUserId())
        .orElseThrow(() ->
          new EntityNotFoundException(
            "User not found: " + attendanceData.getUserId()
          )
        );
      attendance.setUser(user);
    }

    if (attendanceData.getDate() != null) {
      attendance.setDate(attendanceData.getDate());
    }
    if (attendanceData.getClockInTime() != null) {
      attendance.setClockInTime(attendanceData.getClockInTime());
    }
    if (attendanceData.getClockOutTime() != null) {
      attendance.setClockOutTime(attendanceData.getClockOutTime());
    }
    if (attendanceData.getStatus() != null) {
      attendance.setStatus(attendanceData.getStatus());
    }
    if (attendanceData.getNotes() != null) {
      attendance.setNotes(attendanceData.getNotes());
    }

    calculateTotalHours(attendance);
  }

  /**
   * Calculates total hours worked from clock-in and clock-out times.
   *
   * @param attendance The attendance entity to calculate hours for
   */
  private void calculateTotalHours(Attendance attendance) {
    if (
      attendance.getClockInTime() != null &&
      attendance.getClockOutTime() != null
    ) {
      Duration duration = Duration.between(
        attendance.getClockInTime(),
        attendance.getClockOutTime()
      );
      attendance.setTotalHours(duration.toMinutes() / 60.0);
    }
  }
}
