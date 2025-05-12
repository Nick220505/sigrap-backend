package com.sigrap.employee;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.sigrap.user.User;
import com.sigrap.user.User.UserStatus;

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
      .position("Sales")
      .department("Sales")
      .hireDate(LocalDateTime.now())
      .status(Employee.EmployeeStatus.ACTIVE)
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
    assertEquals(testEmployee.getPosition(), found.getPosition());
    assertEquals(testEmployee.getDepartment(), found.getDepartment());
    assertEquals(testEmployee.getHireDate(), found.getHireDate());
    assertEquals(testEmployee.getStatus(), found.getStatus());
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
    assertEquals(testEmployee.getPosition(), found.getPosition());
    assertEquals(testEmployee.getDepartment(), found.getDepartment());
    assertEquals(testEmployee.getHireDate(), found.getHireDate());
    assertEquals(testEmployee.getStatus(), found.getStatus());
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
    assertEquals(testEmployee.getPosition(), found.getPosition());
    assertEquals(testEmployee.getDepartment(), found.getDepartment());
    assertEquals(testEmployee.getHireDate(), found.getHireDate());
    assertEquals(testEmployee.getStatus(), found.getStatus());
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
    assertEquals(testEmployee.getPosition(), found.getPosition());
    assertEquals(testEmployee.getDepartment(), found.getDepartment());
    assertEquals(testEmployee.getHireDate(), found.getHireDate());
    assertEquals(testEmployee.getStatus(), found.getStatus());
  }

  @Test
  void findByDepartment_ShouldReturnDepartmentEmployees() {
    List<Employee> found = employeeRepository.findByDepartment(
      testEmployee.getDepartment()
    );

    assertNotNull(found);
    assertEquals(1, found.size());
    assertEquals(testEmployee.getId(), found.get(0).getId());
    assertEquals(testEmployee.getFirstName(), found.get(0).getFirstName());
    assertEquals(testEmployee.getLastName(), found.get(0).getLastName());
    assertEquals(testEmployee.getDocumentId(), found.get(0).getDocumentId());
    assertEquals(testEmployee.getEmail(), found.get(0).getEmail());
    assertEquals(testEmployee.getPosition(), found.get(0).getPosition());
    assertEquals(testEmployee.getDepartment(), found.get(0).getDepartment());
    assertEquals(testEmployee.getHireDate(), found.get(0).getHireDate());
    assertEquals(testEmployee.getStatus(), found.get(0).getStatus());
  }

  @Test
  void findByStatus_ShouldReturnEmployeesWithStatus() {
    List<Employee> found = employeeRepository.findByStatus(
      testEmployee.getStatus()
    );

    assertNotNull(found);
    assertEquals(1, found.size());
    assertEquals(testEmployee.getId(), found.get(0).getId());
    assertEquals(testEmployee.getFirstName(), found.get(0).getFirstName());
    assertEquals(testEmployee.getLastName(), found.get(0).getLastName());
    assertEquals(testEmployee.getDocumentId(), found.get(0).getDocumentId());
    assertEquals(testEmployee.getEmail(), found.get(0).getEmail());
    assertEquals(testEmployee.getPosition(), found.get(0).getPosition());
    assertEquals(testEmployee.getDepartment(), found.get(0).getDepartment());
    assertEquals(testEmployee.getHireDate(), found.get(0).getHireDate());
    assertEquals(testEmployee.getStatus(), found.get(0).getStatus());
  }
}
