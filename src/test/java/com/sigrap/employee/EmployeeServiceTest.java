package com.sigrap.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
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

  @BeforeEach
  void setUp() {
    testEmployee = Employee.builder()
      .id(1L)
      .firstName("John")
      .lastName("Doe")
      .email("john.doe@example.com")
      .build();

    testEmployeeInfo = EmployeeInfo.builder()
      .id(1L)
      .firstName("John")
      .lastName("Doe")
      .email("john.doe@example.com")
      .userId(1L)
      .build();

    testEmployeeData = EmployeeData.builder()
      .firstName("Jane")
      .lastName("Smith")
      .email("jane.smith@example.com")
      .documentId("DOC123")
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
      .build();

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> employeeService.create(testEmployeeData)
    );
    assertEquals("Email is required", exception.getMessage());
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

    verify(employeeRepository).save(testEmployee);
  }

  @Test
  void update_ShouldThrowEntityNotFoundException() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      employeeService.update(1L, testEmployeeData)
    );
  }
}
