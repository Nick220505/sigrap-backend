package com.sigrap.employee.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.sigrap.user.User;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleMapperTest {

  @InjectMocks
  private ScheduleMapper scheduleMapper;

  private Schedule schedule;
  private ScheduleData scheduleData;
  private User testUser;
  private LocalTime testStartTime;
  private LocalTime testEndTime;
  private LocalDateTime testTimestamp;

  @BeforeEach
  void setUp() {
    testTimestamp = LocalDateTime.now().withNano(0);
    testStartTime = LocalTime.of(9, 0);
    testEndTime = LocalTime.of(17, 0);

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
  }

  @Test
  void toInfo_ShouldMapAllFields() {
    ScheduleInfo info = scheduleMapper.toInfo(schedule);

    assertNotNull(info);
    assertEquals(schedule.getId(), info.getId());
    assertEquals(testUser.getId(), info.getUserId());
    assertEquals(testUser.getName(), info.getUserName());
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
    assertEquals(testUser.getId(), info.getUserId());
    assertEquals(testUser.getName(), info.getUserName());
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
    Schedule result = scheduleMapper.toEntity(scheduleData, testUser);

    assertNotNull(result);
    assertEquals(testUser, result.getUser());
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
      .user(testUser)
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
      .user(testUser)
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
