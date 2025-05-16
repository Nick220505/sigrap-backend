package com.sigrap.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sigrap.user.User;
import com.sigrap.user.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class EmployeeIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private EmployeeRepository employeeRepository;

  private Employee testEmployee;
  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
      .name("Test User")
      .email("test@example.com")
      .password("password123")
      .status(UserStatus.ACTIVE)
      .build();
    testUser = entityManager.persist(testUser);

    testEmployee = Employee.builder()
      .firstName("John")
      .lastName("Doe")
      .documentId("123456")
      .email("john.doe@example.com")
      .user(testUser)
      .build();
    testEmployee = entityManager.persist(testEmployee);
    entityManager.flush();
  }

  @Test
  void findById_ShouldReturnEmployee() {
    Employee found = employeeRepository
      .findById(testEmployee.getId())
      .orElse(null);

    assertNotNull(found);
    assertEquals(testEmployee.getId(), found.getId());
    assertEquals(testEmployee.getFirstName(), found.getFirstName());
    assertEquals(testEmployee.getLastName(), found.getLastName());
    assertEquals(testEmployee.getDocumentId(), found.getDocumentId());
    assertEquals(testEmployee.getEmail(), found.getEmail());
  }

  @Test
  void findByEmail_ShouldReturnEmployee() {
    Employee found = employeeRepository
      .findByEmail(testEmployee.getEmail())
      .orElse(null);

    assertNotNull(found);
    assertEquals(testEmployee.getId(), found.getId());
    assertEquals(testEmployee.getFirstName(), found.getFirstName());
    assertEquals(testEmployee.getLastName(), found.getLastName());
    assertEquals(testEmployee.getDocumentId(), found.getDocumentId());
    assertEquals(testEmployee.getEmail(), found.getEmail());
  }

  @Test
  void findByDocumentId_ShouldReturnEmployee() {
    Employee found = employeeRepository
      .findByDocumentId(testEmployee.getDocumentId())
      .orElse(null);

    assertNotNull(found);
    assertEquals(testEmployee.getId(), found.getId());
    assertEquals(testEmployee.getFirstName(), found.getFirstName());
    assertEquals(testEmployee.getLastName(), found.getLastName());
    assertEquals(testEmployee.getDocumentId(), found.getDocumentId());
    assertEquals(testEmployee.getEmail(), found.getEmail());
  }

  @Test
  void findByUserId_ShouldReturnEmployee() {
    Employee found = employeeRepository
      .findByUserId(testUser.getId())
      .orElse(null);

    assertNotNull(found);
    assertEquals(testEmployee.getId(), found.getId());
    assertEquals(testEmployee.getFirstName(), found.getFirstName());
    assertEquals(testEmployee.getLastName(), found.getLastName());
    assertEquals(testEmployee.getDocumentId(), found.getDocumentId());
    assertEquals(testEmployee.getEmail(), found.getEmail());
  }
}
