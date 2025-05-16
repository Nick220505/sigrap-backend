package com.sigrap.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.sigrap.config.RepositoryTestConfiguration;
import com.sigrap.user.User;
import com.sigrap.user.UserStatus;

@DataJpaTest
@ActiveProfiles("test")
@Import(RepositoryTestConfiguration.class)
class EmployeeRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private EmployeeRepository employeeRepository;

  private User testUser;
  private Employee testEmployee;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
      .name("John Doe")
      .email("john.doe@example.com")
      .password("password123")
      .status(UserStatus.ACTIVE)
      .build();
    entityManager.persist(testUser);

    testEmployee = Employee.builder()
      .firstName("John")
      .lastName("Doe")
      .documentId("123456")
      .email("john.doe@example.com")
      .hireDate(LocalDateTime.now())
      .status(EmployeeStatus.ACTIVE)
      .user(testUser)
      .build();
    entityManager.persist(testEmployee);
    entityManager.flush();
  }

  @Test
  void findByUserId_ShouldReturnEmployee() {
    Optional<Employee> result = employeeRepository.findByUserId(
      testUser.getId()
    );

    assertTrue(result.isPresent());
    assertEquals(testEmployee.getId(), result.get().getId());
  }

  @Test
  void findByEmail_ShouldReturnEmployee() {
    Optional<Employee> result = employeeRepository.findByEmail(
      testEmployee.getEmail()
    );

    assertTrue(result.isPresent());
    assertEquals(testEmployee.getId(), result.get().getId());
  }

  @Test
  void findByDocumentId_ShouldReturnEmployee() {
    Optional<Employee> result = employeeRepository.findByDocumentId(
      testEmployee.getDocumentId()
    );

    assertTrue(result.isPresent());
    assertEquals(testEmployee.getId(), result.get().getId());
  }

  @Test
  void findByStatus_ShouldReturnEmployees() {
    List<Employee> result = employeeRepository.findByStatus(
      testEmployee.getStatus()
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testEmployee.getId(), result.get(0).getId());
  }

  @Test
  void findByHireDateBetween_ShouldReturnEmployees() {
    LocalDateTime startDate = testEmployee.getHireDate().minusDays(1);
    LocalDateTime endDate = testEmployee.getHireDate().plusDays(1);

    List<Employee> result = employeeRepository.findByHireDateBetween(
      startDate,
      endDate
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testEmployee.getId(), result.get(0).getId());
  }
}
