package com.sigrap.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
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
  private EmployeeRepository employeeRepository;

  @InjectMocks
  private ScheduleService scheduleService;

  private Schedule schedule;
  private ScheduleData scheduleData;
  private ScheduleInfo scheduleInfo;
  private Employee employee;

  @BeforeEach
  void setUp() {
    employee = Employee.builder()
      .id(1L)
      .firstName("John")
      .lastName("Doe")
      .email("john.doe@example.com")
      .build();

    schedule = Schedule.builder()
      .id(1L)
      .employee(employee)
      .startTime(LocalDateTime.now())
      .endTime(LocalDateTime.now().plusHours(8))
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

    scheduleData = ScheduleData.builder()
      .employeeId(1L)
      .startTime(LocalDateTime.now())
      .endTime(LocalDateTime.now().plusHours(8))
      .build();

    scheduleInfo = ScheduleInfo.builder()
      .id(1L)
      .employeeId(1L)
      .employeeName("John Doe")
      .startTime(LocalDateTime.now())
      .endTime(LocalDateTime.now().plusHours(8))
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
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
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
    when(scheduleMapper.toEntity(scheduleData, employee)).thenReturn(schedule);
    when(scheduleRepository.save(schedule)).thenReturn(schedule);
    when(scheduleMapper.toInfo(schedule)).thenReturn(scheduleInfo);

    ScheduleInfo result = scheduleService.create(scheduleData);

    assertNotNull(result);
    assertEquals(scheduleInfo, result);
  }

  @Test
  void create_withInvalidEmployee_shouldThrowException() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> scheduleService.create(scheduleData)
    );
    assertEquals("Employee not found: 1", exception.getMessage());
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
    verify(scheduleRepository).save(schedule);
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
  void findByEmployeeId_shouldReturnSchedules() {
    List<Schedule> schedules = List.of(schedule);
    List<ScheduleInfo> expectedInfos = List.of(scheduleInfo);

    when(scheduleRepository.findByEmployeeId(1L)).thenReturn(schedules);
    when(scheduleMapper.toInfoList(schedules)).thenReturn(expectedInfos);

    List<ScheduleInfo> result = scheduleService.findByEmployeeId(1L);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(scheduleInfo, result.get(0));
  }

  @Test
  void generateWeeklySchedule_shouldGenerateSchedules() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
    when(scheduleMapper.toEntity(any(), any())).thenReturn(schedule);
    when(scheduleRepository.save(any())).thenReturn(schedule);
    when(scheduleMapper.toInfo(schedule)).thenReturn(scheduleInfo);

    List<ScheduleInfo> result = scheduleService.generateWeeklySchedule(
      1L,
      scheduleData
    );

    assertNotNull(result);
    assertEquals(7, result.size());
    result.forEach(info -> assertEquals(scheduleInfo, info));
  }

  @Test
  void generateWeeklySchedule_withInvalidEmployeeId_shouldThrowException() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> scheduleService.generateWeeklySchedule(1L, scheduleData)
    );
    assertEquals("Employee not found: 1", exception.getMessage());
  }
}
