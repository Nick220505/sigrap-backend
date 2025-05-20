package com.sigrap.employee.attendance;

import com.sigrap.audit.Auditable;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for attendance management operations.
 * Handles business logic for attendance-related functionality.
 *
 * <p>This service provides:
 * <ul>
 *   <li>Attendance CRUD operations</li>
 *   <li>Clock-in/Clock-out functionality</li>
 *   <li>Attendance reporting</li>
 *   <li>Attendance search functionality</li>
 * </ul></p>
 */
@Service
@RequiredArgsConstructor
public class AttendanceService {

  private final AttendanceRepository attendanceRepository;
  private final AttendanceMapper attendanceMapper;
  private final UserRepository userRepository;

  private static final int LATE_THRESHOLD_MINUTES = 5;
  private static final int EARLY_DEPARTURE_THRESHOLD_MINUTES = 30;

  private static final LocalTime STANDARD_START_TIME = LocalTime.of(8, 0);
  private static final LocalTime STANDARD_END_TIME = LocalTime.of(17, 0);

  /**
   * Retrieves all attendance records.
   *
   * @return List of AttendanceInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<AttendanceInfo> findAll() {
    List<Attendance> attendances = attendanceRepository.findAll();
    return attendanceMapper.toInfoList(attendances);
  }

  /**
   * Retrieves an attendance record by its ID.
   *
   * @param id The ID of the attendance record to retrieve
   * @return AttendanceInfo containing the attendance's information
   * @throws EntityNotFoundException if the attendance record is not found
   */
  @Transactional(readOnly = true)
  public AttendanceInfo findById(Long id) {
    Attendance attendance = attendanceRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Attendance not found: " + id)
      );
    return attendanceMapper.toInfo(attendance);
  }

  /**
   * Records a clock-in for a user.
   *
   * @param userId The ID of the user
   * @param timestamp The clock-in timestamp, or null to use current time in Bogotá, Colombia
   * @return AttendanceInfo containing the created attendance record
   * @throws EntityNotFoundException if the user is not found
   * @throws IllegalStateException if the user already has an active attendance record
   */
  @Transactional
  @Auditable(
    action = "REGISTRAR_ENTRADA",
    entity = "ASISTENCIA",
    entityIdParam = "userId",
    captureDetails = true
  )
  public AttendanceInfo clockIn(Long userId, LocalDateTime timestamp) {
    User user = userRepository
      .findById(userId)
      .orElseThrow(() ->
        new EntityNotFoundException("User not found: " + userId)
      );

    if (timestamp == null) {
      timestamp = LocalDateTime.now(ZoneId.of("America/Bogota"));
    }

    LocalDateTime today = timestamp.toLocalDate().atStartOfDay();
    if (attendanceRepository.findByUserIdAndDate(userId, today).isPresent()) {
      throw new IllegalStateException(
        "User already has an attendance record for today"
      );
    }

    AttendanceStatus status = determineClockInStatus(timestamp.toLocalTime());

    AttendanceData attendanceData = AttendanceData.builder()
      .userId(user.getId())
      .date(today)
      .clockInTime(timestamp)
      .status(status)
      .build();

    Attendance attendance = attendanceMapper.toEntity(attendanceData);
    Attendance savedAttendance = attendanceRepository.save(attendance);
    return attendanceMapper.toInfo(savedAttendance);
  }

  /**
   * Records a clock-out for a user.
   *
   * @param attendanceId The ID of the attendance record
   * @param timestamp The clock-out timestamp, or null to use current time in Bogotá, Colombia
   * @return AttendanceInfo containing the updated attendance record
   * @throws EntityNotFoundException if the attendance record is not found
   */
  @Transactional
  @Auditable(
    action = "REGISTRAR_SALIDA",
    entity = "ASISTENCIA",
    entityIdParam = "attendanceId",
    captureDetails = true
  )
  public AttendanceInfo clockOut(Long attendanceId, LocalDateTime timestamp) {
    Attendance attendance = attendanceRepository
      .findById(attendanceId)
      .orElseThrow(() ->
        new EntityNotFoundException("Attendance not found: " + attendanceId)
      );

    if (attendance.getClockOutTime() != null) {
      throw new IllegalStateException("User has already clocked out");
    }

    if (timestamp == null) {
      timestamp = LocalDateTime.now(ZoneId.of("America/Bogota"));
    }

    attendance.setClockOutTime(timestamp);

    Duration duration = Duration.between(
      attendance.getClockInTime(),
      attendance.getClockOutTime()
    );
    attendance.setTotalHours(duration.toMinutes() / 60.0);

    if (attendance.getStatus() != AttendanceStatus.ON_LEAVE) {
      AttendanceStatus clockOutStatus = determineClockOutStatus(
        timestamp.toLocalTime()
      );

      if (
        attendance.getStatus() != AttendanceStatus.LATE ||
        clockOutStatus != AttendanceStatus.EARLY_DEPARTURE
      ) {
        attendance.setStatus(clockOutStatus);
      }
    }

    Attendance savedAttendance = attendanceRepository.save(attendance);
    return attendanceMapper.toInfo(savedAttendance);
  }

  /**
   * Determines the attendance status based on clock-in time.
   *
   * @param clockInTime The time when the employee clocked in
   * @return LATE if more than threshold minutes late, PRESENT otherwise
   */
  private AttendanceStatus determineClockInStatus(LocalTime clockInTime) {
    if (
      clockInTime.isAfter(
        STANDARD_START_TIME.plusMinutes(LATE_THRESHOLD_MINUTES)
      )
    ) {
      return AttendanceStatus.LATE;
    }
    return AttendanceStatus.PRESENT;
  }

  /**
   * Determines the attendance status based on clock-out time.
   *
   * @param clockOutTime The time when the employee clocked out
   * @return EARLY_DEPARTURE if leaving before standard end time - threshold, PRESENT otherwise
   */
  private AttendanceStatus determineClockOutStatus(LocalTime clockOutTime) {
    if (
      clockOutTime.isBefore(
        STANDARD_END_TIME.minusMinutes(EARLY_DEPARTURE_THRESHOLD_MINUTES)
      )
    ) {
      return AttendanceStatus.EARLY_DEPARTURE;
    }
    return AttendanceStatus.PRESENT;
  }

  /**
   * Finds all attendance records for a specific user.
   *
   * @param userId The ID of the user
   * @return List of AttendanceInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<AttendanceInfo> findByUserId(Long userId) {
    List<Attendance> attendances = attendanceRepository.findByUserId(userId);
    return attendanceMapper.toInfoList(attendances);
  }

  /**
   * Finds all attendance records for a specific date.
   *
   * @param date The date to search for
   * @return List of AttendanceInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<AttendanceInfo> findByDate(LocalDate date) {
    LocalDateTime startOfDay = date.atStartOfDay();
    LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

    List<Attendance> attendances = attendanceRepository.findByDateBetween(
      startOfDay,
      endOfDay
    );
    return attendanceMapper.toInfoList(attendances);
  }

  /**
   * Generates an attendance report for a specific user between two dates.
   *
   * @param userId The ID of the user
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of AttendanceInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<AttendanceInfo> generateAttendanceReport(
    Long userId,
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    List<Attendance> attendances =
      attendanceRepository.findByUserIdAndDateBetween(
        userId,
        startDate,
        endDate
      );
    return attendanceMapper.toInfoList(attendances);
  }

  /**
   * Updates an attendance record's status.
   *
   * @param id The ID of the attendance record
   * @param status The new status
   * @return AttendanceInfo containing the updated attendance record
   * @throws EntityNotFoundException if the attendance record is not found
   */
  @Transactional
  @Auditable(
    action = "ACTUALIZAR",
    entity = "ASISTENCIA",
    entityIdParam = "id",
    captureDetails = true
  )
  public AttendanceInfo updateStatus(Long id, AttendanceStatus status) {
    Attendance attendance = attendanceRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Attendance not found: " + id)
      );

    attendance.setStatus(status);

    Attendance savedAttendance = attendanceRepository.save(attendance);
    return attendanceMapper.toInfo(savedAttendance);
  }

  /**
   * Finds all attendance records with a specific status.
   *
   * @param status The status to search for
   * @return List of AttendanceInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<AttendanceInfo> findByStatus(AttendanceStatus status) {
    List<Attendance> attendances = attendanceRepository.findByStatus(status);
    return attendanceMapper.toInfoList(attendances);
  }

  /**
   * Finds all attendance records between two dates.
   *
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of AttendanceInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<AttendanceInfo> findByDateRange(
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    List<Attendance> attendances = attendanceRepository.findByDateBetween(
      startDate,
      endDate
    );
    return attendanceMapper.toInfoList(attendances);
  }
}
