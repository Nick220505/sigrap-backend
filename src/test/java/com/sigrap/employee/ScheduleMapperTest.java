package com.sigrap.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

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
class ScheduleMapperTest {

  @Mock
  private EmployeeRepository employeeRepository;

  @InjectMocks
  private ScheduleMapper scheduleMapper;

  private Schedule schedule;
  private ScheduleData scheduleData;
  private Employee employee;
  private LocalDateTime now;

  @BeforeEach
  void setUp() {
    now = LocalDateTime.now();

    employee = Employee.builder()
      .id(1L)
      .firstName("John")
      .lastName("Doe")
      .build();

    schedule = Schedule.builder()
      .id(1L)
      .employee(employee)
      .day("MONDAY")
      .startTime(now)
      .endTime(now.plusHours(8))
      .isActive(true)
      .createdAt(now)
      .updatedAt(now)
      .build();

    scheduleData = ScheduleData.builder()
      .employeeId(1L)
      .day("MONDAY")
      .startTime(now)
      .endTime(now.plusHours(8))
      .isActive(true)
      .build();
  }

  @Test
  void toInfo_ShouldMapAllFields() {
    ScheduleInfo info = scheduleMapper.toInfo(schedule);

    assertNotNull(info);
    assertEquals(schedule.getId(), info.getId());
    assertEquals(employee.getId(), info.getEmployeeId());
    assertEquals("John Doe", info.getEmployeeName());
    assertEquals(schedule.getDay(), info.getDay());
    assertEquals(schedule.getStartTime(), info.getStartTime());
    assertEquals(schedule.getEndTime(), info.getEndTime());
    assertEquals(schedule.getIsActive(), info.getIsActive());
    assertEquals(schedule.getCreatedAt(), info.getCreatedAt());
    assertEquals(schedule.getUpdatedAt(), info.getUpdatedAt());
  }

  @Test
  void toInfo_ShouldReturnNull_WhenEntityIsNull() {
    assertNull(scheduleMapper.toInfo(null));
  }

  @Test
  void toInfoList_ShouldMapAllEntities() {
    List<ScheduleInfo> infos = scheduleMapper.toInfoList(List.of(schedule));

    assertNotNull(infos);
    assertEquals(1, infos.size());

    ScheduleInfo info = infos.get(0);
    assertEquals(schedule.getId(), info.getId());
    assertEquals(employee.getId(), info.getEmployeeId());
    assertEquals("John Doe", info.getEmployeeName());
    assertEquals(schedule.getDay(), info.getDay());
    assertEquals(schedule.getStartTime(), info.getStartTime());
    assertEquals(schedule.getEndTime(), info.getEndTime());
    assertEquals(schedule.getIsActive(), info.getIsActive());
    assertEquals(schedule.getCreatedAt(), info.getCreatedAt());
    assertEquals(schedule.getUpdatedAt(), info.getUpdatedAt());
  }

  @Test
  void toInfoList_ShouldReturnEmptyList_WhenEntitiesIsNull() {
    List<ScheduleInfo> infos = scheduleMapper.toInfoList(null);

    assertNotNull(infos);
    assertEquals(0, infos.size());
  }

  @Test
  void toEntity_ShouldMapAllFields() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

    Schedule result = scheduleMapper.toEntity(scheduleData, employee);

    assertNotNull(result);
    assertEquals(employee, result.getEmployee());
    assertEquals(scheduleData.getDay(), result.getDay());
    assertEquals(scheduleData.getStartTime(), result.getStartTime());
    assertEquals(scheduleData.getEndTime(), result.getEndTime());
    assertEquals(scheduleData.getIsActive(), result.getIsActive());
  }

  @Test
  void toEntity_ShouldReturnNull_WhenDataIsNull() {
    assertNull(scheduleMapper.toEntity(null, null));
  }

  @Test
  void updateEntity_ShouldUpdateAllFields() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

    Schedule existingSchedule = Schedule.builder()
      .id(1L)
      .employee(employee)
      .day("TUESDAY")
      .startTime(now.minusDays(1))
      .endTime(now.minusDays(1).plusHours(8))
      .isActive(false)
      .build();

    scheduleMapper.updateEntity(existingSchedule, scheduleData);

    assertEquals(scheduleData.getDay(), existingSchedule.getDay());
    assertEquals(scheduleData.getStartTime(), existingSchedule.getStartTime());
    assertEquals(scheduleData.getEndTime(), existingSchedule.getEndTime());
    assertEquals(scheduleData.getIsActive(), existingSchedule.getIsActive());
  }

  @Test
  void updateEntity_ShouldNotUpdateFields_WhenDataIsNull() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

    Schedule existingSchedule = Schedule.builder()
      .id(1L)
      .employee(employee)
      .day("TUESDAY")
      .startTime(now.minusDays(1))
      .endTime(now.minusDays(1).plusHours(8))
      .isActive(false)
      .build();

    LocalDateTime originalStartTime = existingSchedule.getStartTime();
    LocalDateTime originalEndTime = existingSchedule.getEndTime();

    scheduleMapper.updateEntity(existingSchedule, null);

    assertEquals(originalStartTime, existingSchedule.getStartTime());
    assertEquals(originalEndTime, existingSchedule.getEndTime());
  }
}
