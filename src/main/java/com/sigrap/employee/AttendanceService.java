package com.sigrap.employee;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

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
  private final EmployeeRepository employeeRepository;

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
   * Records a clock-in for an employee.
   *
   * @param employeeId The ID of the employee
   * @param timestamp The clock-in timestamp
   * @param notes Optional notes about the clock-in
   * @return AttendanceInfo containing the created attendance record
   * @throws EntityNotFoundException if the employee is not found
   * @throws IllegalStateException if the employee already has an active attendance record
   */
  @Transactional
  public AttendanceInfo clockIn(
    Long employeeId,
    LocalDateTime timestamp,
    String notes
  ) {
    Employee employee = employeeRepository
      .findById(employeeId)
      .orElseThrow(() ->
        new EntityNotFoundException("Employee not found: " + employeeId)
      );

    LocalDateTime today = timestamp.toLocalDate().atStartOfDay();
    if (
      attendanceRepository
        .findByEmployeeIdAndDate(employeeId, today)
        .isPresent()
    ) {
      throw new IllegalStateException(
        "Employee already has an attendance record for today"
      );
    }

    AttendanceData attendanceData = AttendanceData.builder()
      .employeeId(employee.getId())
      .date(today)
      .clockInTime(timestamp)
      .status(Attendance.AttendanceStatus.PRESENT)
      .notes(notes)
      .build();

    Attendance attendance = attendanceMapper.toEntity(attendanceData);
    Attendance savedAttendance = attendanceRepository.save(attendance);
    return attendanceMapper.toInfo(savedAttendance);
  }

  /**
   * Records a clock-out for an employee.
   *
   * @param attendanceId The ID of the attendance record
   * @param timestamp The clock-out timestamp
   * @param notes Optional notes about the clock-out
   * @return AttendanceInfo containing the updated attendance record
   * @throws EntityNotFoundException if the attendance record is not found
   */
  @Transactional
  public AttendanceInfo clockOut(
    Long attendanceId,
    LocalDateTime timestamp,
    String notes
  ) {
    Attendance attendance = attendanceRepository
      .findById(attendanceId)
      .orElseThrow(() ->
        new EntityNotFoundException("Attendance not found: " + attendanceId)
      );

    if (attendance.getClockOutTime() != null) {
      throw new IllegalStateException("Employee has already clocked out");
    }

    attendance.setClockOutTime(timestamp);
    if (notes != null) {
      attendance.setNotes(
        attendance.getNotes() != null
          ? attendance.getNotes() + "; " + notes
          : notes
      );
    }

    Duration duration = Duration.between(
      attendance.getClockInTime(),
      attendance.getClockOutTime()
    );
    attendance.setTotalHours(duration.toMinutes() / 60.0);

    Attendance savedAttendance = attendanceRepository.save(attendance);
    return attendanceMapper.toInfo(savedAttendance);
  }

  /**
   * Finds all attendance records for a specific employee.
   *
   * @param employeeId The ID of the employee
   * @return List of AttendanceInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<AttendanceInfo> findByEmployeeId(Long employeeId) {
    List<Attendance> attendances = attendanceRepository.findByEmployeeId(
      employeeId
    );
    return attendanceMapper.toInfoList(attendances);
  }

  /**
   * Finds all attendance records for a specific date.
   *
   * @param date The date to search for
   * @return List of AttendanceInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<AttendanceInfo> findByDate(LocalDateTime date) {
    List<Attendance> attendances = attendanceRepository.findByDate(date);
    return attendanceMapper.toInfoList(attendances);
  }

  /**
   * Generates an attendance report for a specific employee between two dates.
   *
   * @param employeeId The ID of the employee
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of AttendanceInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<AttendanceInfo> generateAttendanceReport(
    Long employeeId,
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    List<Attendance> attendances =
      attendanceRepository.findByEmployeeIdAndDateBetween(
        employeeId,
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
   * @param notes Optional notes about the status change
   * @return AttendanceInfo containing the updated attendance record
   * @throws EntityNotFoundException if the attendance record is not found
   */
  @Transactional
  public AttendanceInfo updateStatus(
    Long id,
    Attendance.AttendanceStatus status,
    String notes
  ) {
    Attendance attendance = attendanceRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Attendance not found: " + id)
      );

    attendance.setStatus(status);
    if (notes != null) {
      attendance.setNotes(
        attendance.getNotes() != null
          ? attendance.getNotes() + "; " + notes
          : notes
      );
    }

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
  public List<AttendanceInfo> findByStatus(Attendance.AttendanceStatus status) {
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
