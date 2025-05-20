package com.sigrap.employee.schedule;

import com.sigrap.audit.Auditable;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  private final UserRepository userRepository;

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
   * @throws EntityNotFoundException if the referenced user is not found
   */
  @Transactional
  @Auditable(action = "CREAR", entity = "HORARIO", captureDetails = true)
  public ScheduleInfo create(ScheduleData data) {
    User user = userRepository
      .findById(data.getUserId())
      .orElseThrow(() ->
        new EntityNotFoundException("User not found: " + data.getUserId())
      );
    Schedule schedule = scheduleMapper.toEntity(data, user);
    schedule = scheduleRepository.save(schedule);
    return scheduleMapper.toInfo(schedule);
  }

  /**
   * Updates an existing schedule.
   *
   * @param id The ID of the schedule to update
   * @param data The new data for the schedule
   * @return ScheduleInfo containing the updated schedule's information
   * @throws EntityNotFoundException if the schedule or user is not found
   */
  @Transactional
  @Auditable(
    action = "ACTUALIZAR",
    entity = "HORARIO",
    entityIdParam = "id",
    captureDetails = true
  )
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
  @Auditable(action = "ELIMINAR", entity = "HORARIO", entityIdParam = "id")
  public void delete(Long id) {
    if (!scheduleRepository.existsById(id)) {
      throw new EntityNotFoundException("Schedule not found: " + id);
    }
    scheduleRepository.deleteById(id);
  }

  /**
   * Finds all schedules for a specific user.
   *
   * @param userId The ID of the user
   * @return List of ScheduleInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<ScheduleInfo> findByUserId(Long userId) {
    List<Schedule> schedules = scheduleRepository.findByUserId(userId);
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
   * Generates a weekly schedule for a user.
   * The input ScheduleData's startTime and endTime (LocalTime) will be used for all days.
   *
   * @param userId The ID of the user
   * @param data The base schedule data to use (containing LocalTime for start/end and optionally isActive)
   * @return List of ScheduleInfo DTOs for the generated schedules
   * @throws EntityNotFoundException if the user is not found
   */
  @Transactional
  @Auditable(action = "CREAR", entity = "HORARIO", captureDetails = true)
  public List<ScheduleInfo> generateWeeklySchedule(
    Long userId,
    ScheduleData data
  ) {
    User user = userRepository
      .findById(userId)
      .orElseThrow(() ->
        new EntityNotFoundException("User not found: " + userId)
      );

    List<ScheduleInfo> weeklySchedules = new ArrayList<>();
    LocalTime startTime = data.getStartTime();
    LocalTime endTime = data.getEndTime();
    boolean isActiveForWeek = Optional.ofNullable(data.getIsActive()).orElse(
      true
    );

    String[] daysOfWeek = {
      "MONDAY",
      "TUESDAY",
      "WEDNESDAY",
      "THURSDAY",
      "FRIDAY",
      "SATURDAY",
      "SUNDAY",
    };

    for (String day : daysOfWeek) {
      ScheduleData dailyData = ScheduleData.builder()
        .userId(userId)
        .day(day)
        .startTime(startTime)
        .endTime(endTime)
        .isActive(isActiveForWeek)
        .build();

      Schedule schedule = scheduleMapper.toEntity(dailyData, user);
      schedule = scheduleRepository.save(schedule);
      weeklySchedules.add(scheduleMapper.toInfo(schedule));
    }

    return weeklySchedules;
  }

  /**
   * Copies schedules from the previous week for a user.
   *
   * @param userId The ID of the user
   * @return List of ScheduleInfo DTOs for the copied schedules
   * @throws EntityNotFoundException if the user is not found
   */
  @Transactional
  @Auditable(action = "CREAR", entity = "HORARIO", captureDetails = true)
  public List<ScheduleInfo> copyScheduleFromPreviousWeek(Long userId) {
    User user = userRepository
      .findById(userId)
      .orElseThrow(() ->
        new EntityNotFoundException("User not found: " + userId)
      );

    List<Schedule> previousSchedules =
      scheduleRepository.findByUserIdAndIsActive(userId, true);

    if (previousSchedules.isEmpty()) {
      throw new IllegalStateException(
        "No active schedules found to copy from for user: " + userId
      );
    }

    List<Schedule> newSchedules = new ArrayList<>();
    for (Schedule prevSchedule : previousSchedules) {
      ScheduleData copiedData = ScheduleData.builder()
        .userId(user.getId())
        .day(prevSchedule.getDay())
        .startTime(prevSchedule.getStartTime())
        .endTime(prevSchedule.getEndTime())
        .isActive(prevSchedule.getIsActive())
        .build();
      Schedule newSchedule = scheduleMapper.toEntity(copiedData, user);
      newSchedules.add(newSchedule);
    }

    List<Schedule> savedSchedules = scheduleRepository.saveAll(newSchedules);
    return scheduleMapper.toInfoList(savedSchedules);
  }

  /**
   * Finds all active schedules for a specific user.
   *
   * @param userId The ID of the user
   * @return List of ScheduleInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<ScheduleInfo> findActiveSchedulesByUserId(Long userId) {
    List<Schedule> schedules = scheduleRepository.findByUserIdAndIsActive(
      userId,
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
