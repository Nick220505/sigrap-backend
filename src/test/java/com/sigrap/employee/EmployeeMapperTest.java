package com.sigrap.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.sigrap.user.User;
import com.sigrap.user.User.UserStatus;
import com.sigrap.user.UserRepository;
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
class EmployeeMapperTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private EmployeeMapper employeeMapper;

  private User testUser;
  private Employee testEmployee;
  private EmployeeInfo testEmployeeInfo;
  private EmployeeData testEmployeeData;
  private LocalDateTime hireDate;

  @BeforeEach
  void setUp() {
    hireDate = LocalDateTime.now().minusDays(30);

    testUser = User.builder()
      .id(1L)
      .name("John Doe")
      .email("john.doe@example.com")
      .password("password123")
      .status(UserStatus.ACTIVE)
      .build();

    testEmployee = Employee.builder()
      .id(1L)
      .firstName("John")
      .lastName("Doe")
      .documentId("123456")
      .email("john.doe@example.com")
      .position("Sales")
      .department("Sales")
      .hireDate(hireDate)
      .status(Employee.EmployeeStatus.ACTIVE)
      .user(testUser)
      .build();

    testEmployeeInfo = EmployeeInfo.builder()
      .id(1L)
      .userId(1L)
      .firstName("John")
      .lastName("Doe")
      .documentId("123456")
      .email("john.doe@example.com")
      .position("Sales")
      .department("Sales")
      .hireDate(hireDate)
      .status(Employee.EmployeeStatus.ACTIVE)
      .build();

    testEmployeeData = EmployeeData.builder()
      .userId(1L)
      .firstName("John")
      .lastName("Doe")
      .documentId("123456")
      .email("john.doe@example.com")
      .position("Sales")
      .department("Sales")
      .hireDate(hireDate)
      .build();
  }

  @Test
  void toInfo_ShouldMapEntityToInfo() {
    EmployeeInfo result = employeeMapper.toInfo(testEmployee);

    assertNotNull(result);
    assertEquals(testEmployee.getId(), result.getId());
    assertEquals(testEmployee.getUser().getId(), result.getUserId());
    assertEquals(testEmployee.getFirstName(), result.getFirstName());
    assertEquals(testEmployee.getLastName(), result.getLastName());
    assertEquals(testEmployee.getDocumentId(), result.getDocumentId());
    assertEquals(testEmployee.getEmail(), result.getEmail());
    assertEquals(testEmployee.getPosition(), result.getPosition());
    assertEquals(testEmployee.getDepartment(), result.getDepartment());
    assertEquals(testEmployee.getHireDate(), result.getHireDate());
    assertEquals(testEmployee.getStatus(), result.getStatus());
  }

  @Test
  void toInfo_ShouldReturnNull_WhenEntityIsNull() {
    EmployeeInfo result = employeeMapper.toInfo(null);

    assertEquals(null, result);
  }

  @Test
  void toInfoList_ShouldMapEntitiesToInfos() {
    List<EmployeeInfo> result = employeeMapper.toInfoList(
      List.of(testEmployee)
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testEmployee.getId(), result.get(0).getId());
    assertEquals(testEmployee.getUser().getId(), result.get(0).getUserId());
    assertEquals(testEmployee.getFirstName(), result.get(0).getFirstName());
    assertEquals(testEmployee.getLastName(), result.get(0).getLastName());
    assertEquals(testEmployee.getDocumentId(), result.get(0).getDocumentId());
    assertEquals(testEmployee.getEmail(), result.get(0).getEmail());
    assertEquals(testEmployee.getPosition(), result.get(0).getPosition());
    assertEquals(testEmployee.getDepartment(), result.get(0).getDepartment());
    assertEquals(testEmployee.getHireDate(), result.get(0).getHireDate());
    assertEquals(testEmployee.getStatus(), result.get(0).getStatus());
  }

  @Test
  void toInfoList_ShouldReturnEmptyList_WhenEntitiesIsNull() {
    List<EmployeeInfo> result = employeeMapper.toInfoList(null);

    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  void toEntity_ShouldMapDataToEntity() {
    when(userRepository.findById(testEmployeeData.getUserId())).thenReturn(
      Optional.of(testUser)
    );

    Employee result = employeeMapper.toEntity(testEmployeeData);

    assertNotNull(result);
    assertEquals(testUser, result.getUser());
    assertEquals(testEmployeeData.getFirstName(), result.getFirstName());
    assertEquals(testEmployeeData.getLastName(), result.getLastName());
    assertEquals(testEmployeeData.getDocumentId(), result.getDocumentId());
    assertEquals(testEmployeeData.getEmail(), result.getEmail());
    assertEquals(testEmployeeData.getPosition(), result.getPosition());
    assertEquals(testEmployeeData.getDepartment(), result.getDepartment());
    assertEquals(testEmployeeData.getHireDate(), result.getHireDate());
    assertEquals(Employee.EmployeeStatus.ACTIVE, result.getStatus());
  }

  @Test
  void toEntity_ShouldThrowEntityNotFoundException_WhenUserNotFound() {
    when(userRepository.findById(testEmployeeData.getUserId())).thenReturn(
      Optional.empty()
    );

    assertThrows(EntityNotFoundException.class, () ->
      employeeMapper.toEntity(testEmployeeData)
    );
  }

  @Test
  void toEntity_ShouldReturnNull_WhenDataIsNull() {
    Employee result = employeeMapper.toEntity(null);

    assertEquals(null, result);
  }

  @Test
  void updateEntityFromData_ShouldUpdateEntityWithData() {
    Employee existingEmployee = Employee.builder()
      .id(1L)
      .firstName("Old First")
      .lastName("Old Last")
      .documentId("000000")
      .email("old@example.com")
      .position("Old Position")
      .department("Old Department")
      .hireDate(LocalDateTime.now().minusDays(1))
      .status(Employee.EmployeeStatus.ACTIVE)
      .user(testUser)
      .build();

    employeeMapper.updateEntityFromData(existingEmployee, testEmployeeData);

    assertEquals(
      testEmployeeData.getFirstName(),
      existingEmployee.getFirstName()
    );
    assertEquals(
      testEmployeeData.getLastName(),
      existingEmployee.getLastName()
    );
    assertEquals(
      testEmployeeData.getDocumentId(),
      existingEmployee.getDocumentId()
    );
    assertEquals(testEmployeeData.getEmail(), existingEmployee.getEmail());
    assertEquals(
      testEmployeeData.getPosition(),
      existingEmployee.getPosition()
    );
    assertEquals(
      testEmployeeData.getDepartment(),
      existingEmployee.getDepartment()
    );
    assertEquals(
      testEmployeeData.getHireDate(),
      existingEmployee.getHireDate()
    );
  }

  @Test
  void updateEntityFromData_ShouldNotUpdateUser_WhenDataIsNull() {
    Employee existingEmployee = Employee.builder()
      .id(1L)
      .firstName("Old First")
      .lastName("Old Last")
      .documentId("000000")
      .email("old@example.com")
      .position("Old Position")
      .department("Old Department")
      .hireDate(LocalDateTime.now().minusDays(1))
      .status(Employee.EmployeeStatus.ACTIVE)
      .user(testUser)
      .build();

    employeeMapper.updateEntityFromData(existingEmployee, null);

    assertEquals("Old First", existingEmployee.getFirstName());
    assertEquals("Old Last", existingEmployee.getLastName());
    assertEquals("000000", existingEmployee.getDocumentId());
    assertEquals("old@example.com", existingEmployee.getEmail());
    assertEquals("Old Position", existingEmployee.getPosition());
    assertEquals("Old Department", existingEmployee.getDepartment());
    assertEquals(testUser, existingEmployee.getUser());
  }

  @Test
  void getFullName_ShouldReturnConcatenatedName() {
    String result = employeeMapper.getFullName(testEmployee);

    assertEquals("John Doe", result);
  }

  @Test
  void getFullName_ShouldReturnNull_WhenEmployeeIsNull() {
    String result = employeeMapper.getFullName(null);

    assertEquals(null, result);
  }
}
