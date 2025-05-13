package com.sigrap.employee;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Service class for schedule management operations.
 * Handles business logic for schedule-related functionality.
 *
 * <p>This service provides:
 * <ul>
 *   <li>Schedule CRUD operations</li>
 *   <li>Weekly schedule generation</li>
 *   <li>Schedule copying</li>
 *   <li>Schedule search functionality</li>
 * </ul></p>
 */
@Service
@RequiredArgsConstructor
public class ScheduleService {

  private final ScheduleRepository scheduleRepository;
  private final ScheduleMapper scheduleMapper;
  private final EmployeeRepository employeeRepository;

  /**
   * Retrieves all schedules.
   *
   * @return List of ScheduleInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<ScheduleInfo> findAll() {
    List<Schedule> schedules = scheduleRepository.findAll();
    return scheduleMapper.toInfoList(schedules);
  }

  /**
   * Retrieves a schedule by its ID.
   *
   * @param id The ID of the schedule to retrieve
   * @return ScheduleInfo containing the schedule's information
   * @throws EntityNotFoundException if the schedule is not found
   */
  @Transactional(readOnly = true)
  public ScheduleInfo findById(Long id) {
    Schedule schedule = scheduleRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Schedule not found: " + id)
      );
    return scheduleMapper.toInfo(schedule);
  }

  /**
   * Creates a new schedule.
   *
   * @param data The data for the new schedule
   * @return ScheduleInfo containing the created schedule's information
   * @throws EntityNotFoundException if the referenced employee is not found
   */
  @Transactional
  public ScheduleInfo create(ScheduleData data) {
    Employee employee = employeeRepository
      .findById(data.getEmployeeId())
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Employee not found: " + data.getEmployeeId()
        )
      );

    Schedule schedule = scheduleMapper.toEntity(data, employee);
    schedule = scheduleRepository.save(schedule);
    return scheduleMapper.toInfo(schedule);
  }

  /**
   * Updates an existing schedule.
   *
   * @param id The ID of the schedule to update
   * @param data The new data for the schedule
   * @return ScheduleInfo containing the updated schedule's information
   * @throws EntityNotFoundException if the schedule is not found
   */
  @Transactional
  public ScheduleInfo update(Long id, ScheduleData data) {
    Schedule schedule = scheduleRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Schedule not found: " + id)
      );

    scheduleMapper.updateEntity(schedule, data);
    schedule = scheduleRepository.save(schedule);
    return scheduleMapper.toInfo(schedule);
  }

  /**
   * Deletes a schedule.
   *
   * @param id The ID of the schedule to delete
   * @throws EntityNotFoundException if the schedule is not found
   */
  @Transactional
  public void delete(Long id) {
    if (!scheduleRepository.existsById(id)) {
      throw new EntityNotFoundException("Schedule not found: " + id);
    }
    scheduleRepository.deleteById(id);
  }

  /**
   * Finds all schedules for a specific employee.
   *
   * @param employeeId The ID of the employee
   * @return List of ScheduleInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<ScheduleInfo> findByEmployeeId(Long employeeId) {
    List<Schedule> schedules = scheduleRepository.findByEmployeeId(employeeId);
    return scheduleMapper.toInfoList(schedules);
  }

  /**
   * Finds all schedules for a specific day.
   *
   * @param day The day to search for
   * @return List of ScheduleInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<ScheduleInfo> findByDay(String day) {
    List<Schedule> schedules = scheduleRepository.findByDay(day);
    return scheduleMapper.toInfoList(schedules);
  }

  /**
   * Generates a weekly schedule for an employee.
   *
   * @param employeeId The ID of the employee
   * @param data The base schedule data to use
   * @return List of ScheduleInfo DTOs for the generated schedules
   * @throws EntityNotFoundException if the employee is not found
   */
  @Transactional
  public List<ScheduleInfo> generateWeeklySchedule(
    Long employeeId,
    ScheduleData data
  ) {
    Employee employee = employeeRepository
      .findById(employeeId)
      .orElseThrow(() ->
        new EntityNotFoundException("Employee not found: " + employeeId)
      );

    List<ScheduleInfo> weeklySchedules = new ArrayList<>();
    LocalDateTime startTime = data.getStartTime();
    LocalDateTime endTime = data.getEndTime();

    // Days of the week
    String[] daysOfWeek = {
      "MONDAY",
      "TUESDAY",
      "WEDNESDAY",
      "THURSDAY",
      "FRIDAY",
      "SATURDAY",
      "SUNDAY",
    };

    for (int i = 0; i < 7; i++) {
      ScheduleData dailyData = ScheduleData.builder()
        .employeeId(employeeId)
        .day(daysOfWeek[i])
        .startTime(startTime.plusDays(i))
        .endTime(endTime.plusDays(i))
        .isActive(data.getIsActive())
        .build();

      Schedule schedule = scheduleMapper.toEntity(dailyData, employee);
      schedule = scheduleRepository.save(schedule);
      weeklySchedules.add(scheduleMapper.toInfo(schedule));
    }

    return weeklySchedules;
  }

  /**
   * Copies schedules from the previous week.
   *
   * @param employeeId The ID of the employee
   * @return List of ScheduleInfo DTOs for the copied schedules
   * @throws EntityNotFoundException if the employee is not found
   */
  @Transactional
  public List<ScheduleInfo> copyScheduleFromPreviousWeek(Long employeeId) {
    List<Schedule> previousSchedules =
      scheduleRepository.findByEmployeeIdAndIsActive(employeeId, true);

    if (previousSchedules.isEmpty()) {
      throw new IllegalStateException("No active schedules found to copy from");
    }

    List<Schedule> newSchedules = new ArrayList<>();
    for (Schedule prevSchedule : previousSchedules) {
      Schedule newSchedule = Schedule.builder()
        .employee(prevSchedule.getEmployee())
        .day(prevSchedule.getDay())
        .startTime(prevSchedule.getStartTime())
        .endTime(prevSchedule.getEndTime())
        .isActive(true)
        .build();
      newSchedules.add(newSchedule);
    }

    List<Schedule> savedSchedules = scheduleRepository.saveAll(newSchedules);
    return scheduleMapper.toInfoList(savedSchedules);
  }

  /**
   * Finds all active schedules for a specific employee.
   *
   * @param employeeId The ID of the employee
   * @return List of ScheduleInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<ScheduleInfo> findActiveSchedulesByEmployeeId(Long employeeId) {
    List<Schedule> schedules = scheduleRepository.findByEmployeeIdAndIsActive(
      employeeId,
      true
    );
    return scheduleMapper.toInfoList(schedules);
  }

  /**
   * Finds all active schedules for a specific day.
   *
   * @param day The day to search for
   * @return List of ScheduleInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<ScheduleInfo> findActiveSchedulesByDay(String day) {
    List<Schedule> schedules = scheduleRepository.findByDayAndIsActive(
      day,
      true
    );
    return scheduleMapper.toInfoList(schedules);
  }
}
