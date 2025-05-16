package com.sigrap.employee.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sigrap.employee.Employee;
import com.sigrap.user.User;
import com.sigrap.user.UserStatus;
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

  private Employee employee;
  private Schedule schedule;
  private LocalTime defaultStartTime;
  private LocalTime defaultEndTime;

  @BeforeEach
  void setUp() {
    User user = User.builder()
      .name("John Doe")
      .email("john.doe@example.com")
      .password("password123")
      .status(UserStatus.ACTIVE)
      .build();
    user = entityManager.persist(user);

    employee = Employee.builder()
      .firstName("John")
      .lastName("Doe")
      .documentId("123456")
      .email("john.doe@example.com")
      .user(user)
      .build();
    employee = entityManager.persist(employee);

    defaultStartTime = LocalTime.of(9, 0);
    defaultEndTime = LocalTime.of(17, 0);

    schedule = Schedule.builder()
      .employee(employee)
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
    assertEquals(
      schedule.getEmployee().getId(),
      found.get().getEmployee().getId()
    );
    assertEquals(schedule.getStartTime(), found.get().getStartTime());
    assertEquals(schedule.getEndTime(), found.get().getEndTime());
  }

  @Test
  void findByEmployeeId_ShouldReturnSchedules() {
    List<Schedule> schedules = scheduleRepository.findByEmployeeId(
      employee.getId()
    );

    assertNotNull(schedules);
    assertEquals(1, schedules.size());
    assertEquals(schedule.getId(), schedules.get(0).getId());
  }

  @Test
  void findByEmployeeIdAndDay_shouldReturnSchedules() {
    List<Schedule> found = scheduleRepository.findByEmployeeIdAndDay(
      employee.getId(),
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
  void findByEmployeeIdAndIsActive_shouldReturnSchedules() {
    List<Schedule> found = scheduleRepository.findByEmployeeIdAndIsActive(
      employee.getId(),
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
      .employee(employee)
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
    assertEquals(
      newSchedule.getEmployee().getId(),
      found.getEmployee().getId()
    );
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
