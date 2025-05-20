package com.sigrap.employee.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

  @Mock
  private ScheduleRepository scheduleRepository;

  @Mock
  private ScheduleMapper scheduleMapper;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private ScheduleService scheduleService;

  private Schedule schedule;
  private ScheduleData scheduleData;
  private ScheduleInfo scheduleInfo;
  private User testUser;
  private LocalTime testStartTime;
  private LocalTime testEndTime;
  private LocalDateTime testTimestamp;

  @BeforeEach
  void setUp() {
    testStartTime = LocalTime.now().withNano(0);
    testEndTime = LocalTime.now().plusHours(8).withNano(0);
    testTimestamp = LocalDateTime.now().withNano(0);

    testUser = User.builder()
      .id(1L)
      .name("John Doe")
      .email("john.doe@example.com")
      .build();

    schedule = Schedule.builder()
      .id(1L)
      .user(testUser)
      .day("MONDAY")
      .startTime(testStartTime)
      .endTime(testEndTime)
      .isActive(true)
      .createdAt(testTimestamp)
      .updatedAt(testTimestamp)
      .build();

    scheduleData = ScheduleData.builder()
      .userId(1L)
      .day("MONDAY")
      .startTime(testStartTime)
      .endTime(testEndTime)
      .isActive(true)
      .build();

    scheduleInfo = ScheduleInfo.builder()
      .id(1L)
      .userId(1L)
      .userName("John Doe")
      .day("MONDAY")
      .startTime(testStartTime)
      .endTime(testEndTime)
      .isActive(true)
      .createdAt(testTimestamp)
      .updatedAt(testTimestamp)
      .build();
  }

  @Test
  void findAll_shouldReturnSchedules() {
    List<Schedule> schedules = List.of(schedule);
    List<ScheduleInfo> expectedInfos = List.of(scheduleInfo);

    when(scheduleRepository.findAll()).thenReturn(schedules);
    when(scheduleMapper.toInfoList(schedules)).thenReturn(expectedInfos);

    List<ScheduleInfo> result = scheduleService.findAll();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(scheduleInfo, result.get(0));
  }

  @Test
  void findById_withValidId_shouldReturnSchedule() {
    when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
    when(scheduleMapper.toInfo(schedule)).thenReturn(scheduleInfo);

    ScheduleInfo result = scheduleService.findById(1L);

    assertNotNull(result);
    assertEquals(scheduleInfo, result);
  }

  @Test
  void findById_withInvalidId_shouldThrowException() {
    when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> scheduleService.findById(1L)
    );
    assertEquals("Schedule not found: 1", exception.getMessage());
  }

  @Test
  void create_withValidData_shouldCreateSchedule() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(scheduleMapper.toEntity(scheduleData, testUser)).thenReturn(schedule);
    when(scheduleRepository.save(schedule)).thenReturn(schedule);
    when(scheduleMapper.toInfo(schedule)).thenReturn(scheduleInfo);

    ScheduleInfo result = scheduleService.create(scheduleData);

    assertNotNull(result);
    assertEquals(scheduleInfo, result);
  }

  @Test
  void create_withInvalidEmployee_shouldThrowException() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> scheduleService.create(scheduleData)
    );
    assertEquals("User not found: 1", exception.getMessage());
  }

  @Test
  void update_withValidData_shouldUpdateSchedule() {
    when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
    when(scheduleRepository.save(schedule)).thenReturn(schedule);
    when(scheduleMapper.toInfo(schedule)).thenReturn(scheduleInfo);

    ScheduleInfo result = scheduleService.update(1L, scheduleData);

    assertNotNull(result);
    assertEquals(scheduleInfo, result);
    verify(scheduleMapper).updateEntity(schedule, scheduleData);
  }

  @Test
  void update_withInvalidId_shouldThrowException() {
    when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> scheduleService.update(1L, scheduleData)
    );
    assertEquals("Schedule not found: 1", exception.getMessage());
  }

  @Test
  void delete_withValidId_shouldDeleteSchedule() {
    when(scheduleRepository.existsById(1L)).thenReturn(true);

    scheduleService.delete(1L);

    verify(scheduleRepository).deleteById(1L);
  }

  @Test
  void delete_withInvalidId_shouldThrowException() {
    when(scheduleRepository.existsById(1L)).thenReturn(false);

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> scheduleService.delete(1L)
    );
    assertEquals("Schedule not found: 1", exception.getMessage());
  }

  @Test
  void findByUserId_shouldReturnSchedules() {
    List<Schedule> schedules = List.of(schedule);
    List<ScheduleInfo> expectedInfos = List.of(scheduleInfo);

    when(scheduleRepository.findByUserId(1L)).thenReturn(schedules);
    when(scheduleMapper.toInfoList(schedules)).thenReturn(expectedInfos);

    List<ScheduleInfo> result = scheduleService.findByUserId(1L);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(scheduleInfo, result.get(0));
  }

  @Test
  void generateWeeklySchedule_shouldGenerateSchedules() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(
      scheduleMapper.toEntity(any(ScheduleData.class), any(User.class))
    ).thenReturn(schedule);
    when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);
    when(scheduleMapper.toInfo(schedule)).thenReturn(scheduleInfo);

    List<ScheduleInfo> result = scheduleService.generateWeeklySchedule(
      1L,
      scheduleData
    );

    assertNotNull(result);
    assertEquals(7, result.size());
    result.forEach(info -> {
      assertNotNull(info);
      assertEquals(scheduleInfo.getStartTime(), info.getStartTime());
      assertEquals(scheduleInfo.getEndTime(), info.getEndTime());
    });
  }

  @Test
  void generateWeeklySchedule_withInvalidEmployeeId_shouldThrowException() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> scheduleService.generateWeeklySchedule(1L, scheduleData)
    );
    assertEquals("User not found: 1", exception.getMessage());
  }

  @Test
  void copyScheduleFromPreviousWeek_withValidData_shouldCopySchedules() {
    Long userId = 1L;
    List<Schedule> existingSchedules = List.of(
      Schedule.builder()
        .id(1L)
        .user(testUser)
        .day("MONDAY")
        .startTime(testStartTime)
        .endTime(testEndTime)
        .isActive(true)
        .build(),
      Schedule.builder()
        .id(2L)
        .user(testUser)
        .day("TUESDAY")
        .startTime(testStartTime)
        .endTime(testEndTime)
        .isActive(true)
        .build()
    );

    List<ScheduleInfo> expectedInfos = List.of(
      scheduleInfo,
      ScheduleInfo.builder()
        .id(2L)
        .userId(1L)
        .userName("John Doe")
        .day("TUESDAY")
        .startTime(testStartTime)
        .endTime(testEndTime)
        .isActive(true)
        .build()
    );

    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
    when(scheduleRepository.findByUserIdAndIsActive(userId, true)).thenReturn(
      existingSchedules
    );
    when(scheduleRepository.saveAll(any())).thenReturn(existingSchedules);
    when(scheduleMapper.toInfoList(existingSchedules)).thenReturn(
      expectedInfos
    );

    List<ScheduleInfo> result = scheduleService.copyScheduleFromPreviousWeek(
      userId
    );

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(scheduleRepository).findByUserIdAndIsActive(userId, true);
    verify(scheduleRepository).saveAll(any());
  }

  @Test
  void copyScheduleFromPreviousWeek_withInvalidUserId_shouldThrowException() {
    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> scheduleService.copyScheduleFromPreviousWeek(userId)
    );
    assertEquals("User not found: 1", exception.getMessage());
  }

  @Test
  void findActiveSchedulesByUserId_shouldReturnOnlyActiveSchedules() {
    Long userId = 1L;
    List<Schedule> activeSchedules = List.of(schedule);
    List<ScheduleInfo> expectedInfos = List.of(scheduleInfo);

    when(scheduleRepository.findByUserIdAndIsActive(userId, true)).thenReturn(
      activeSchedules
    );
    when(scheduleMapper.toInfoList(activeSchedules)).thenReturn(expectedInfos);

    List<ScheduleInfo> result = scheduleService.findActiveSchedulesByUserId(
      userId
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(scheduleInfo, result.get(0));
    verify(scheduleRepository).findByUserIdAndIsActive(userId, true);
  }

  @Test
  void findActiveSchedulesByDay_shouldReturnOnlyActiveSchedules() {
    String day = "MONDAY";
    List<Schedule> activeSchedules = List.of(schedule);
    List<ScheduleInfo> expectedInfos = List.of(scheduleInfo);

    when(scheduleRepository.findByDayAndIsActive(day, true)).thenReturn(
      activeSchedules
    );
    when(scheduleMapper.toInfoList(activeSchedules)).thenReturn(expectedInfos);

    List<ScheduleInfo> result = scheduleService.findActiveSchedulesByDay(day);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(scheduleInfo, result.get(0));
    verify(scheduleRepository).findByDayAndIsActive(day, true);
  }
}
