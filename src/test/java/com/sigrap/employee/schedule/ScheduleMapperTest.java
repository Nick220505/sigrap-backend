package com.sigrap.employee.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.sigrap.employee.Employee;
import com.sigrap.employee.EmployeeRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
  private LocalTime testStartTime;
  private LocalTime testEndTime;
  private LocalDateTime testTimestamp;

  @BeforeEach
  void setUp() {
    testTimestamp = LocalDateTime.now().withNano(0);
    testStartTime = LocalTime.of(9, 0);
    testEndTime = LocalTime.of(17, 0);

    employee = Employee.builder()
      .id(1L)
      .firstName("John")
      .lastName("Doe")
      .build();

    schedule = Schedule.builder()
      .id(1L)
      .employee(employee)
      .day("MONDAY")
      .startTime(testStartTime)
      .endTime(testEndTime)
      .isActive(true)
      .createdAt(testTimestamp)
      .updatedAt(testTimestamp)
      .build();

    scheduleData = ScheduleData.builder()
      .employeeId(1L)
      .day("MONDAY")
      .startTime(testStartTime)
      .endTime(testEndTime)
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
    Schedule result = scheduleMapper.toEntity(scheduleData, employee);

    assertNotNull(result);
    assertEquals(employee, result.getEmployee());
    assertEquals(scheduleData.getDay(), result.getDay());
    assertEquals(scheduleData.getStartTime(), result.getStartTime());
    assertEquals(scheduleData.getEndTime(), result.getEndTime());
    assertEquals(true, result.getIsActive());
  }

  @Test
  void toEntity_ShouldReturnNull_WhenDataIsNull() {
    assertNull(scheduleMapper.toEntity(null, null));
  }

  @Test
  void updateEntity_ShouldUpdateAllFields() {
    LocalTime newStartTime = LocalTime.of(8, 0);
    LocalTime newEndTime = LocalTime.of(16, 0);

    Schedule existingSchedule = Schedule.builder()
      .id(1L)
      .employee(employee)
      .day("TUESDAY")
      .startTime(newStartTime)
      .endTime(newEndTime)
      .isActive(false)
      .build();

    ScheduleData updateSourceData = ScheduleData.builder()
      .day("WEDNESDAY")
      .startTime(LocalTime.of(10, 0))
      .endTime(LocalTime.of(18, 0))
      .isActive(true)
      .build();

    scheduleMapper.updateEntity(existingSchedule, updateSourceData);

    assertEquals(updateSourceData.getDay(), existingSchedule.getDay());
    assertEquals(
      updateSourceData.getStartTime(),
      existingSchedule.getStartTime()
    );
    assertEquals(updateSourceData.getEndTime(), existingSchedule.getEndTime());
    assertEquals(
      updateSourceData.getIsActive(),
      existingSchedule.getIsActive()
    );
  }

  @Test
  void updateEntity_ShouldNotUpdateFields_WhenDataIsNull() {
    LocalTime originalStartTime = LocalTime.of(7, 0);
    LocalTime originalEndTime = LocalTime.of(15, 0);

    Schedule existingSchedule = Schedule.builder()
      .id(1L)
      .employee(employee)
      .day("TUESDAY")
      .startTime(originalStartTime)
      .endTime(originalEndTime)
      .isActive(false)
      .build();

    scheduleMapper.updateEntity(existingSchedule, null);

    assertEquals(originalStartTime, existingSchedule.getStartTime());
    assertEquals(originalEndTime, existingSchedule.getEndTime());
    assertEquals(false, existingSchedule.getIsActive());
  }
}
