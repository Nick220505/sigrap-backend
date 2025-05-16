package com.sigrap.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sigrap.config.RepositoryTestConfiguration;
import com.sigrap.user.User;
import com.sigrap.user.UserStatus;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

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
}
