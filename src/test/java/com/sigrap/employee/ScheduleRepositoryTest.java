package com.sigrap.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sigrap.user.User;
import com.sigrap.user.UserStatus;
import java.time.LocalDateTime;
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

  @Autowired
  private EmployeeRepository employeeRepository;

  private Employee employee;
  private Schedule schedule;

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
      .position("Sales")
      .department("Sales")
      .hireDate(LocalDateTime.now())
      .status(Employee.EmployeeStatus.ACTIVE)
      .user(user)
      .build();
    employee = employeeRepository.save(employee);

    schedule = Schedule.builder()
      .employee(employee)
      .day("MONDAY")
      .startTime(LocalDateTime.now())
      .endTime(LocalDateTime.now().plusHours(8))
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
    Schedule newSchedule = Schedule.builder()
      .employee(employee)
      .day("MONDAY")
      .startTime(LocalDateTime.now().plusDays(1))
      .endTime(LocalDateTime.now().plusDays(1).plusHours(8))
      .isActive(true)
      .build();

    Schedule saved = scheduleRepository.save(newSchedule);

    assertNotNull(saved);
    assertNotNull(saved.getId());
    assertEquals(
      newSchedule.getEmployee().getId(),
      saved.getEmployee().getId()
    );
    assertEquals(newSchedule.getStartTime(), saved.getStartTime());
    assertEquals(newSchedule.getEndTime(), saved.getEndTime());
  }

  @Test
  void delete_ShouldRemoveSchedule() {
    scheduleRepository.deleteById(schedule.getId());

    Optional<Schedule> found = scheduleRepository.findById(schedule.getId());
    assertTrue(found.isEmpty());
  }
}
