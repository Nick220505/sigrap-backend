package com.sigrap.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
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
class EmployeeServiceTest {

  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private EmployeeMapper employeeMapper;

  @InjectMocks
  private EmployeeService employeeService;

  private Employee testEmployee;
  private EmployeeInfo testEmployeeInfo;
  private EmployeeData testEmployeeData;
  private LocalDateTime hireDate;

  @BeforeEach
  void setUp() {
    hireDate = LocalDateTime.now().minusDays(30);

    testEmployee = Employee.builder()
      .id(1L)
      .firstName("John")
      .lastName("Doe")
      .email("john.doe@example.com")
      .hireDate(hireDate)
      .status(EmployeeStatus.ACTIVE)
      .build();

    testEmployeeInfo = EmployeeInfo.builder()
      .id(1L)
      .firstName("John")
      .lastName("Doe")
      .email("john.doe@example.com")
      .hireDate(hireDate)
      .status(EmployeeStatus.ACTIVE)
      .userId(1L)
      .build();

    testEmployeeData = EmployeeData.builder()
      .firstName("Jane")
      .lastName("Smith")
      .email("jane.smith@example.com")
      .hireDate(hireDate)
      .documentId("DOC123")
      .status(EmployeeStatus.ACTIVE)
      .userId(1L)
      .build();
  }

  @Test
  void findAll_ShouldReturnAllEmployees() {
    when(employeeRepository.findAll()).thenReturn(List.of(testEmployee));
    when(employeeMapper.toInfoList(List.of(testEmployee))).thenReturn(
      List.of(testEmployeeInfo)
    );

    List<EmployeeInfo> result = employeeService.findAll();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testEmployeeInfo.getId(), result.get(0).getId());
    assertEquals(testEmployeeInfo.getUserId(), result.get(0).getUserId());
    assertEquals(testEmployeeInfo.getFirstName(), result.get(0).getFirstName());
    assertEquals(testEmployeeInfo.getLastName(), result.get(0).getLastName());
    assertEquals(
      testEmployeeInfo.getDocumentId(),
      result.get(0).getDocumentId()
    );
    assertEquals(testEmployeeInfo.getEmail(), result.get(0).getEmail());
    assertEquals(testEmployeeInfo.getHireDate(), result.get(0).getHireDate());
    assertEquals(testEmployeeInfo.getStatus(), result.get(0).getStatus());
  }

  @Test
  void findById_ShouldReturnEmployee() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    when(employeeMapper.toInfo(testEmployee)).thenReturn(testEmployeeInfo);

    EmployeeInfo result = employeeService.findById(1L);

    assertNotNull(result);
    assertEquals(testEmployeeInfo.getId(), result.getId());
    assertEquals(testEmployeeInfo.getUserId(), result.getUserId());
    assertEquals(testEmployeeInfo.getFirstName(), result.getFirstName());
    assertEquals(testEmployeeInfo.getLastName(), result.getLastName());
    assertEquals(testEmployeeInfo.getDocumentId(), result.getDocumentId());
    assertEquals(testEmployeeInfo.getEmail(), result.getEmail());
    assertEquals(testEmployeeInfo.getHireDate(), result.getHireDate());
    assertEquals(testEmployeeInfo.getStatus(), result.getStatus());
  }

  @Test
  void findById_ShouldThrowEntityNotFoundException() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      employeeService.findById(1L)
    );
  }

  @Test
  void create_ShouldCreateNewEmployee() {
    when(
      employeeRepository.existsByDocumentId(testEmployeeData.getDocumentId())
    ).thenReturn(false);
    when(
      employeeRepository.existsByEmail(testEmployeeData.getEmail())
    ).thenReturn(false);
    when(employeeMapper.toEntity(testEmployeeData)).thenReturn(testEmployee);
    when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
    when(employeeMapper.toInfo(testEmployee)).thenReturn(testEmployeeInfo);

    EmployeeInfo result = employeeService.create(testEmployeeData);

    assertNotNull(result);
    assertEquals(testEmployeeInfo.getId(), result.getId());
    assertEquals(testEmployeeInfo.getUserId(), result.getUserId());
    assertEquals(testEmployeeInfo.getFirstName(), result.getFirstName());
    assertEquals(testEmployeeInfo.getLastName(), result.getLastName());
    assertEquals(testEmployeeInfo.getDocumentId(), result.getDocumentId());
    assertEquals(testEmployeeInfo.getEmail(), result.getEmail());
    assertEquals(testEmployeeInfo.getHireDate(), result.getHireDate());
    assertEquals(testEmployeeInfo.getStatus(), result.getStatus());

    verify(employeeRepository).save(testEmployee);
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenDocumentIdExists() {
    when(
      employeeRepository.existsByDocumentId(testEmployeeData.getDocumentId())
    ).thenReturn(true);

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> employeeService.create(testEmployeeData)
    );
    assertEquals(
      "Employee with this document ID already exists",
      exception.getMessage()
    );
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenEmailExists() {
    when(
      employeeRepository.existsByDocumentId(testEmployeeData.getDocumentId())
    ).thenReturn(false);
    when(
      employeeRepository.existsByEmail(testEmployeeData.getEmail())
    ).thenReturn(true);

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> employeeService.create(testEmployeeData)
    );
    assertEquals(
      "Employee with this email already exists",
      exception.getMessage()
    );
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenUserIdIsNull() {
    testEmployeeData = EmployeeData.builder()
      .firstName("John")
      .lastName("Doe")
      .documentId("123456")
      .email("john.doe@example.com")
      .hireDate(hireDate)
      .build();

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> employeeService.create(testEmployeeData)
    );
    assertEquals("User ID is required", exception.getMessage());
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenFirstNameIsNull() {
    testEmployeeData = EmployeeData.builder()
      .userId(1L)
      .lastName("Doe")
      .documentId("123456")
      .email("john.doe@example.com")
      .hireDate(hireDate)
      .build();

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> employeeService.create(testEmployeeData)
    );
    assertEquals("First name is required", exception.getMessage());
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenLastNameIsNull() {
    testEmployeeData = EmployeeData.builder()
      .userId(1L)
      .firstName("John")
      .documentId("123456")
      .email("john.doe@example.com")
      .hireDate(hireDate)
      .build();

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> employeeService.create(testEmployeeData)
    );
    assertEquals("Last name is required", exception.getMessage());
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenDocumentIdIsNull() {
    testEmployeeData = EmployeeData.builder()
      .userId(1L)
      .firstName("John")
      .lastName("Doe")
      .email("john.doe@example.com")
      .hireDate(hireDate)
      .build();

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> employeeService.create(testEmployeeData)
    );
    assertEquals("Document ID is required", exception.getMessage());
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenEmailIsNull() {
    testEmployeeData = EmployeeData.builder()
      .userId(1L)
      .firstName("John")
      .lastName("Doe")
      .documentId("123456")
      .hireDate(hireDate)
      .build();

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> employeeService.create(testEmployeeData)
    );
    assertEquals("Email is required", exception.getMessage());
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenHireDateIsNull() {
    testEmployeeData = EmployeeData.builder()
      .userId(1L)
      .firstName("John")
      .lastName("Doe")
      .documentId("123456")
      .email("john.doe@example.com")
      .build();

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> employeeService.create(testEmployeeData)
    );
    assertEquals("Hire date is required", exception.getMessage());
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenHireDateIsInFuture() {
    testEmployeeData = EmployeeData.builder()
      .userId(1L)
      .firstName("John")
      .lastName("Doe")
      .documentId("123456")
      .email("john.doe@example.com")
      .hireDate(LocalDateTime.now().plusDays(1))
      .build();

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> employeeService.create(testEmployeeData)
    );
    assertEquals("Hire date cannot be in the future", exception.getMessage());
  }

  @Test
  void update_ShouldUpdateEmployee() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
    when(employeeMapper.toInfo(testEmployee)).thenReturn(testEmployeeInfo);

    EmployeeInfo result = employeeService.update(1L, testEmployeeData);

    assertNotNull(result);
    assertEquals(testEmployeeInfo.getId(), result.getId());
    assertEquals(testEmployeeInfo.getUserId(), result.getUserId());
    assertEquals(testEmployeeInfo.getFirstName(), result.getFirstName());
    assertEquals(testEmployeeInfo.getLastName(), result.getLastName());
    assertEquals(testEmployeeInfo.getDocumentId(), result.getDocumentId());
    assertEquals(testEmployeeInfo.getEmail(), result.getEmail());
    assertEquals(testEmployeeInfo.getHireDate(), result.getHireDate());
    assertEquals(testEmployeeInfo.getStatus(), result.getStatus());

    verify(employeeRepository).save(testEmployee);
  }

  @Test
  void update_ShouldThrowEntityNotFoundException() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      employeeService.update(1L, testEmployeeData)
    );
  }

  @Test
  void activate_ShouldActivateEmployee() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
    when(employeeMapper.toInfo(testEmployee)).thenReturn(testEmployeeInfo);

    EmployeeInfo result = employeeService.activate(1L);

    assertNotNull(result);
    assertEquals(EmployeeStatus.ACTIVE, result.getStatus());

    verify(employeeRepository).save(testEmployee);
  }

  @Test
  void activate_ShouldThrowIllegalStateException_WhenEmployeeIsTerminated() {
    testEmployee.setStatus(EmployeeStatus.TERMINATED);
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

    IllegalStateException exception = assertThrows(
      IllegalStateException.class,
      () -> employeeService.activate(1L)
    );
    assertEquals(
      "Cannot activate a terminated employee",
      exception.getMessage()
    );
  }

  @Test
  void deactivate_ShouldDeactivateEmployee() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    testEmployeeInfo.setStatus(EmployeeStatus.INACTIVE);
    when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
    when(employeeMapper.toInfo(testEmployee)).thenReturn(testEmployeeInfo);

    EmployeeInfo result = employeeService.deactivate(1L);

    assertNotNull(result);
    assertEquals(EmployeeStatus.INACTIVE, result.getStatus());

    verify(employeeRepository).save(testEmployee);
  }

  @Test
  void deactivate_ShouldThrowIllegalStateException_WhenEmployeeIsTerminated() {
    testEmployee.setStatus(EmployeeStatus.TERMINATED);
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

    IllegalStateException exception = assertThrows(
      IllegalStateException.class,
      () -> employeeService.deactivate(1L)
    );
    assertEquals(
      "Cannot deactivate a terminated employee",
      exception.getMessage()
    );
  }

  @Test
  void terminate_ShouldTerminateEmployee() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    testEmployeeInfo.setStatus(EmployeeStatus.TERMINATED);
    testEmployeeInfo.setTerminationDate(LocalDateTime.now());
    when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
    when(employeeMapper.toInfo(testEmployee)).thenReturn(testEmployeeInfo);

    EmployeeInfo result = employeeService.terminate(1L);

    assertNotNull(result);
    assertEquals(EmployeeStatus.TERMINATED, result.getStatus());
    assertNotNull(result.getTerminationDate());

    verify(employeeRepository).save(testEmployee);
  }

  @Test
  void findByStatus_ShouldReturnEmployeesWithStatus() {
    when(employeeRepository.findByStatus(EmployeeStatus.ACTIVE)).thenReturn(
      List.of(testEmployee)
    );
    when(employeeMapper.toInfoList(List.of(testEmployee))).thenReturn(
      List.of(testEmployeeInfo)
    );

    List<EmployeeInfo> result = employeeService.findByStatus(
      EmployeeStatus.ACTIVE
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testEmployeeInfo.getId(), result.get(0).getId());
    assertEquals(testEmployeeInfo.getUserId(), result.get(0).getUserId());
    assertEquals(testEmployeeInfo.getFirstName(), result.get(0).getFirstName());
    assertEquals(testEmployeeInfo.getLastName(), result.get(0).getLastName());
    assertEquals(
      testEmployeeInfo.getDocumentId(),
      result.get(0).getDocumentId()
    );
    assertEquals(testEmployeeInfo.getEmail(), result.get(0).getEmail());
    assertEquals(testEmployeeInfo.getHireDate(), result.get(0).getHireDate());
    assertEquals(testEmployeeInfo.getStatus(), result.get(0).getStatus());
  }
}
