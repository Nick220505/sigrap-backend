package com.sigrap.employee.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sigrap.user.User;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ScheduleRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ScheduleRepository scheduleRepository;

  private User testUser;
  private Schedule schedule;
  private LocalTime defaultStartTime;
  private LocalTime defaultEndTime;

  @BeforeEach
  void setUp() {
    this.testUser = User.builder()
      .name("John Doe")
      .email("john.doe@example.com")
      .password("password123")
      .documentId("DOC_SCHEDULE_REPO_TEST" + System.currentTimeMillis())
      .build();
    this.testUser = entityManager.persist(this.testUser);

    defaultStartTime = LocalTime.of(9, 0);
    defaultEndTime = LocalTime.of(17, 0);

    schedule = Schedule.builder()
      .user(this.testUser)
      .day("MONDAY")
      .startTime(defaultStartTime)
      .endTime(defaultEndTime)
      .isActive(true)
      .build();
    schedule = scheduleRepository.save(schedule);
    entityManager.flush();
  }

  @Test
  void findAll_ShouldReturnAllSchedules() {
    List<Schedule> schedules = scheduleRepository.findAll();

    assertNotNull(schedules);
    assertEquals(1, schedules.size());
    assertEquals(schedule.getId(), schedules.get(0).getId());
  }

  @Test
  void findById_ShouldReturnSchedule() {
    Optional<Schedule> found = scheduleRepository.findById(schedule.getId());

    assertTrue(found.isPresent());
    assertEquals(schedule.getId(), found.get().getId());
    assertEquals(schedule.getUser().getId(), found.get().getUser().getId());
    assertEquals(schedule.getStartTime(), found.get().getStartTime());
    assertEquals(schedule.getEndTime(), found.get().getEndTime());
  }

  @Test
  void findByUserId_ShouldReturnSchedules() {
    List<Schedule> schedules = scheduleRepository.findByUserId(
      this.testUser.getId()
    );

    assertNotNull(schedules);
    assertEquals(1, schedules.size());
    assertEquals(schedule.getId(), schedules.get(0).getId());
  }

  @Test
  void findByUserIdAndDay_shouldReturnSchedules() {
    List<Schedule> found = scheduleRepository.findByUserIdAndDay(
      this.testUser.getId(),
      "MONDAY"
    );

    assertNotNull(found);
    assertEquals(1, found.size());
    assertEquals(schedule.getId(), found.get(0).getId());
  }

  @Test
  void findByDay_shouldReturnSchedules() {
    List<Schedule> found = scheduleRepository.findByDay("MONDAY");

    assertNotNull(found);
    assertEquals(1, found.size());
    assertEquals(schedule.getId(), found.get(0).getId());
  }

  @Test
  void findByUserIdAndIsActive_shouldReturnSchedules() {
    List<Schedule> found = scheduleRepository.findByUserIdAndIsActive(
      this.testUser.getId(),
      true
    );

    assertNotNull(found);
    assertEquals(1, found.size());
    assertEquals(schedule.getId(), found.get(0).getId());
  }

  @Test
  void findByDayAndIsActive_shouldReturnSchedules() {
    List<Schedule> found = scheduleRepository.findByDayAndIsActive(
      "MONDAY",
      true
    );

    assertNotNull(found);
    assertEquals(1, found.size());
    assertEquals(schedule.getId(), found.get(0).getId());
  }

  @Test
  void save_ShouldCreateSchedule() {
    LocalTime newStartTime = LocalTime.of(10, 0);
    LocalTime newEndTime = LocalTime.of(18, 0);

    Schedule newSchedule = Schedule.builder()
      .user(this.testUser)
      .day("TUESDAY")
      .startTime(newStartTime)
      .endTime(newEndTime)
      .isActive(true)
      .build();

    Schedule saved = scheduleRepository.save(newSchedule);
    entityManager.flush();

    assertNotNull(saved);
    assertNotNull(saved.getId());
    Schedule found = entityManager.find(Schedule.class, saved.getId());
    assertNotNull(found);
    assertEquals(newSchedule.getUser().getId(), found.getUser().getId());
    assertEquals(newSchedule.getStartTime(), found.getStartTime());
    assertEquals(newSchedule.getEndTime(), found.getEndTime());
  }

  @Test
  void delete_ShouldRemoveSchedule() {
    scheduleRepository.deleteById(schedule.getId());
    entityManager.flush();

    Optional<Schedule> found = scheduleRepository.findById(schedule.getId());
    assertTrue(found.isEmpty());
  }
}
