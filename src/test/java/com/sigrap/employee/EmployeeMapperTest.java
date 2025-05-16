package com.sigrap.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import com.sigrap.user.UserStatus;
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
class EmployeeMapperTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private EmployeeMapper employeeMapper;

  private User testUser;
  private Employee testEmployee;
  private EmployeeInfo testEmployeeInfo;
  private EmployeeData testEmployeeData;

  @BeforeEach
  void setUp() {
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
      .email("john.doe@example.com")
      .user(testUser)
      .build();

    testEmployeeInfo = EmployeeInfo.builder()
      .id(1L)
      .userId(1L)
      .firstName("John")
      .lastName("Doe")
      .email("john.doe@example.com")
      .build();

    testEmployeeData = EmployeeData.builder()
      .userId(1L)
      .firstName("John")
      .lastName("Doe")
      .email("john.doe@example.com")
      .build();
  }

  @Test
  void toInfo_ShouldMapEmployeeToEmployeeInfo() {
    Employee employee = Employee.builder()
      .id(1L)
      .firstName("John")
      .lastName("Doe")
      .documentId("DOC123")
      .email("john.doe@example.com")
      .user(testUser)
      .build();

    EmployeeInfo employeeInfo = employeeMapper.toInfo(employee);

    assertNotNull(employeeInfo);
    assertEquals(1L, employeeInfo.getId());
    assertEquals(testUser.getId(), employeeInfo.getUserId());
    assertEquals("John", employeeInfo.getFirstName());
    assertEquals("Doe", employeeInfo.getLastName());
    assertEquals("DOC123", employeeInfo.getDocumentId());
    assertEquals("john.doe@example.com", employeeInfo.getEmail());
  }

  @Test
  void toInfo_ShouldReturnNull_WhenEntityIsNull() {
    EmployeeInfo result = employeeMapper.toInfo(null);

    assertEquals(null, result);
  }

  @Test
  void toInfoList_ShouldMapEmployeeListToEmployeeInfoList() {
    Employee employee = Employee.builder()
      .id(1L)
      .firstName("John")
      .lastName("Doe")
      .documentId("DOC123")
      .email("john.doe@example.com")
      .user(testUser)
      .build();
    List<Employee> employees = List.of(employee);

    List<EmployeeInfo> employeeInfos = employeeMapper.toInfoList(employees);

    assertNotNull(employeeInfos);
    assertEquals(1, employeeInfos.size());
    EmployeeInfo employeeInfo = employeeInfos.get(0);
    assertEquals(1L, employeeInfo.getId());
    assertEquals(testUser.getId(), employeeInfo.getUserId());
    assertEquals("John", employeeInfo.getFirstName());
    assertEquals("Doe", employeeInfo.getLastName());
    assertEquals("DOC123", employeeInfo.getDocumentId());
    assertEquals("john.doe@example.com", employeeInfo.getEmail());
  }

  @Test
  void toInfoList_ShouldReturnEmptyList_WhenEntitiesIsNull() {
    List<EmployeeInfo> result = employeeMapper.toInfoList(null);

    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  void toEntity_ShouldMapEmployeeDataToEmployee() {
    when(userRepository.findById(20L)).thenReturn(
      Optional.of(User.builder().id(20L).build())
    );
    EmployeeData employeeData = EmployeeData.builder()
      .firstName("Jane")
      .lastName("Smith")
      .documentId("DOC456")
      .email("jane.smith@example.com")
      .userId(20L)
      .build();

    Employee employee = employeeMapper.toEntity(employeeData);

    assertNotNull(employee);
    assertNull(employee.getId());
    assertNotNull(employee.getUser());
    assertEquals(20L, employee.getUser().getId());
    assertEquals("Jane", employee.getFirstName());
    assertEquals("Smith", employee.getLastName());
    assertEquals("DOC456", employee.getDocumentId());
    assertEquals("jane.smith@example.com", employee.getEmail());
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
  void updateEntityFromData_ShouldUpdateExistingEmployee() {
    Employee existingEmployee = Employee.builder()
      .id(1L)
      .firstName("Initial First")
      .lastName("Initial Last")
      .documentId("DOC789")
      .email("initial@example.com")
      .user(testUser)
      .build();

    EmployeeData updateData = EmployeeData.builder()
      .firstName("Updated First")
      .lastName("Updated Last")
      .documentId("DOCNEW")
      .email("updated@example.com")
      .userId(1L)
      .build();
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

    employeeMapper.updateEntityFromData(existingEmployee, updateData);

    assertEquals(1L, existingEmployee.getId());
    assertEquals(testUser.getId(), existingEmployee.getUser().getId());
    assertEquals("Updated First", existingEmployee.getFirstName());
    assertEquals("Updated Last", existingEmployee.getLastName());
    assertEquals("DOCNEW", existingEmployee.getDocumentId());
    assertEquals("updated@example.com", existingEmployee.getEmail());
  }

  @Test
  void updateEntityFromData_WithNullFieldsInData_ShouldNotUpdateThoseFieldsInEntity() {
    Employee existingEmployee = Employee.builder()
      .id(1L)
      .firstName("Initial First")
      .lastName("Initial Last")
      .documentId("DOC789")
      .email("initial@example.com")
      .user(testUser)
      .build();

    EmployeeData updateDataWithNulls = EmployeeData.builder()
      .firstName("Updated First")
      .lastName(null)
      .documentId(null)
      .email(null)
      .userId(null)
      .build();

    employeeMapper.updateEntityFromData(existingEmployee, updateDataWithNulls);

    assertEquals(1L, existingEmployee.getId());
    assertEquals(testUser.getId(), existingEmployee.getUser().getId());
    assertEquals("Updated First", existingEmployee.getFirstName());
    assertEquals("Initial Last", existingEmployee.getLastName());
    assertEquals("DOC789", existingEmployee.getDocumentId());
    assertEquals("initial@example.com", existingEmployee.getEmail());
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
