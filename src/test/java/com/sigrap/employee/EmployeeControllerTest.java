package com.sigrap.employee;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.config.BaseTestConfiguration;
import com.sigrap.config.SecurityConfig;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(EmployeeController.class)
@Import({ BaseTestConfiguration.class, SecurityConfig.class })
class EmployeeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private EmployeeService employeeService;

  @Autowired
  private ObjectMapper objectMapper;

  private EmployeeInfo testEmployeeInfo;
  private EmployeeData testEmployeeData;
  private LocalDateTime hireDate;

  @BeforeEach
  void setUp() {
    hireDate = LocalDateTime.now().minusDays(30);

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
  void findAll_ShouldReturnAllEmployees() throws Exception {
    when(employeeService.findAll()).thenReturn(List.of(testEmployeeInfo));

    mockMvc
      .perform(get("/api/employees"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testEmployeeInfo.getId()))
      .andExpect(jsonPath("$[0].userId").value(testEmployeeInfo.getUserId()))
      .andExpect(
        jsonPath("$[0].firstName").value(testEmployeeInfo.getFirstName())
      )
      .andExpect(
        jsonPath("$[0].lastName").value(testEmployeeInfo.getLastName())
      )
      .andExpect(
        jsonPath("$[0].documentId").value(testEmployeeInfo.getDocumentId())
      )
      .andExpect(jsonPath("$[0].email").value(testEmployeeInfo.getEmail()))
      .andExpect(
        jsonPath("$[0].position").value(testEmployeeInfo.getPosition())
      )
      .andExpect(
        jsonPath("$[0].department").value(testEmployeeInfo.getDepartment())
      )
      .andExpect(jsonPath("$[0].hireDate").exists())
      .andExpect(
        jsonPath("$[0].status").value(Employee.EmployeeStatus.ACTIVE.name())
      );
  }

  @Test
  void findById_ShouldReturnEmployee() throws Exception {
    when(employeeService.findById(1L)).thenReturn(testEmployeeInfo);

    mockMvc
      .perform(get("/api/employees/{id}", 1L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testEmployeeInfo.getId()))
      .andExpect(jsonPath("$.userId").value(testEmployeeInfo.getUserId()))
      .andExpect(jsonPath("$.firstName").value(testEmployeeInfo.getFirstName()))
      .andExpect(jsonPath("$.lastName").value(testEmployeeInfo.getLastName()))
      .andExpect(
        jsonPath("$.documentId").value(testEmployeeInfo.getDocumentId())
      )
      .andExpect(jsonPath("$.email").value(testEmployeeInfo.getEmail()))
      .andExpect(jsonPath("$.position").value(testEmployeeInfo.getPosition()))
      .andExpect(
        jsonPath("$.department").value(testEmployeeInfo.getDepartment())
      )
      .andExpect(jsonPath("$.hireDate").exists())
      .andExpect(
        jsonPath("$.status").value(Employee.EmployeeStatus.ACTIVE.name())
      );
  }

  @Test
  void create_ShouldCreateEmployee() throws Exception {
    when(employeeService.create(any(EmployeeData.class))).thenReturn(
      testEmployeeInfo
    );

    mockMvc
      .perform(
        post("/api/employees")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(testEmployeeData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(testEmployeeInfo.getId()))
      .andExpect(jsonPath("$.userId").value(testEmployeeInfo.getUserId()))
      .andExpect(jsonPath("$.firstName").value(testEmployeeInfo.getFirstName()))
      .andExpect(jsonPath("$.lastName").value(testEmployeeInfo.getLastName()))
      .andExpect(
        jsonPath("$.documentId").value(testEmployeeInfo.getDocumentId())
      )
      .andExpect(jsonPath("$.email").value(testEmployeeInfo.getEmail()))
      .andExpect(jsonPath("$.position").value(testEmployeeInfo.getPosition()))
      .andExpect(
        jsonPath("$.department").value(testEmployeeInfo.getDepartment())
      )
      .andExpect(jsonPath("$.hireDate").exists())
      .andExpect(
        jsonPath("$.status").value(Employee.EmployeeStatus.ACTIVE.name())
      );
  }

  @Test
  void update_ShouldUpdateEmployee() throws Exception {
    when(
      employeeService.update(any(Long.class), any(EmployeeData.class))
    ).thenReturn(testEmployeeInfo);

    mockMvc
      .perform(
        put("/api/employees/{id}", 1L)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(testEmployeeData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testEmployeeInfo.getId()))
      .andExpect(jsonPath("$.userId").value(testEmployeeInfo.getUserId()))
      .andExpect(jsonPath("$.firstName").value(testEmployeeInfo.getFirstName()))
      .andExpect(jsonPath("$.lastName").value(testEmployeeInfo.getLastName()))
      .andExpect(
        jsonPath("$.documentId").value(testEmployeeInfo.getDocumentId())
      )
      .andExpect(jsonPath("$.email").value(testEmployeeInfo.getEmail()))
      .andExpect(jsonPath("$.position").value(testEmployeeInfo.getPosition()))
      .andExpect(
        jsonPath("$.department").value(testEmployeeInfo.getDepartment())
      )
      .andExpect(jsonPath("$.hireDate").exists())
      .andExpect(
        jsonPath("$.status").value(Employee.EmployeeStatus.ACTIVE.name())
      );
  }

  @Test
  void delete_ShouldDeleteEmployee() throws Exception {
    Long id = 1L;
    doNothing().when(employeeService).delete(id);

    mockMvc
      .perform(delete("/api/employees/{id}", id))
      .andExpect(status().isNoContent());

    verify(employeeService).delete(id);
  }

  @Test
  void activate_ShouldActivateEmployee() throws Exception {
    testEmployeeInfo.setStatus(Employee.EmployeeStatus.ACTIVE);
    when(employeeService.activate(1L)).thenReturn(testEmployeeInfo);

    mockMvc
      .perform(post("/api/employees/{id}/activate", 1L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testEmployeeInfo.getId()))
      .andExpect(jsonPath("$.userId").value(testEmployeeInfo.getUserId()))
      .andExpect(jsonPath("$.firstName").value(testEmployeeInfo.getFirstName()))
      .andExpect(jsonPath("$.lastName").value(testEmployeeInfo.getLastName()))
      .andExpect(
        jsonPath("$.documentId").value(testEmployeeInfo.getDocumentId())
      )
      .andExpect(jsonPath("$.email").value(testEmployeeInfo.getEmail()))
      .andExpect(jsonPath("$.position").value(testEmployeeInfo.getPosition()))
      .andExpect(
        jsonPath("$.department").value(testEmployeeInfo.getDepartment())
      )
      .andExpect(jsonPath("$.hireDate").exists())
      .andExpect(
        jsonPath("$.status").value(Employee.EmployeeStatus.ACTIVE.name())
      );
  }

  @Test
  void deactivate_ShouldDeactivateEmployee() throws Exception {
    testEmployeeInfo.setStatus(Employee.EmployeeStatus.INACTIVE);
    when(employeeService.deactivate(1L)).thenReturn(testEmployeeInfo);

    mockMvc
      .perform(post("/api/employees/{id}/deactivate", 1L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testEmployeeInfo.getId()))
      .andExpect(jsonPath("$.userId").value(testEmployeeInfo.getUserId()))
      .andExpect(jsonPath("$.firstName").value(testEmployeeInfo.getFirstName()))
      .andExpect(jsonPath("$.lastName").value(testEmployeeInfo.getLastName()))
      .andExpect(
        jsonPath("$.documentId").value(testEmployeeInfo.getDocumentId())
      )
      .andExpect(jsonPath("$.email").value(testEmployeeInfo.getEmail()))
      .andExpect(jsonPath("$.position").value(testEmployeeInfo.getPosition()))
      .andExpect(
        jsonPath("$.department").value(testEmployeeInfo.getDepartment())
      )
      .andExpect(jsonPath("$.hireDate").exists())
      .andExpect(
        jsonPath("$.status").value(Employee.EmployeeStatus.INACTIVE.name())
      );
  }

  @Test
  void terminate_ShouldTerminateEmployee() throws Exception {
    testEmployeeInfo.setStatus(Employee.EmployeeStatus.TERMINATED);
    testEmployeeInfo.setTerminationDate(LocalDateTime.now());
    when(employeeService.terminate(1L)).thenReturn(testEmployeeInfo);

    mockMvc
      .perform(post("/api/employees/{id}/terminate", 1L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testEmployeeInfo.getId()))
      .andExpect(jsonPath("$.userId").value(testEmployeeInfo.getUserId()))
      .andExpect(jsonPath("$.firstName").value(testEmployeeInfo.getFirstName()))
      .andExpect(jsonPath("$.lastName").value(testEmployeeInfo.getLastName()))
      .andExpect(
        jsonPath("$.documentId").value(testEmployeeInfo.getDocumentId())
      )
      .andExpect(jsonPath("$.email").value(testEmployeeInfo.getEmail()))
      .andExpect(jsonPath("$.position").value(testEmployeeInfo.getPosition()))
      .andExpect(
        jsonPath("$.department").value(testEmployeeInfo.getDepartment())
      )
      .andExpect(jsonPath("$.hireDate").exists())
      .andExpect(
        jsonPath("$.status").value(Employee.EmployeeStatus.TERMINATED.name())
      )
      .andExpect(jsonPath("$.terminationDate").exists());
  }

  @Test
  void findByDepartment_ShouldReturnDepartmentEmployees() throws Exception {
    when(employeeService.findByDepartment("Sales")).thenReturn(
      List.of(testEmployeeInfo)
    );

    mockMvc
      .perform(get("/api/employees/department/{department}", "Sales"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testEmployeeInfo.getId()))
      .andExpect(jsonPath("$[0].userId").value(testEmployeeInfo.getUserId()))
      .andExpect(
        jsonPath("$[0].firstName").value(testEmployeeInfo.getFirstName())
      )
      .andExpect(
        jsonPath("$[0].lastName").value(testEmployeeInfo.getLastName())
      )
      .andExpect(
        jsonPath("$[0].documentId").value(testEmployeeInfo.getDocumentId())
      )
      .andExpect(jsonPath("$[0].email").value(testEmployeeInfo.getEmail()))
      .andExpect(
        jsonPath("$[0].position").value(testEmployeeInfo.getPosition())
      )
      .andExpect(
        jsonPath("$[0].department").value(testEmployeeInfo.getDepartment())
      )
      .andExpect(jsonPath("$[0].hireDate").exists())
      .andExpect(
        jsonPath("$[0].status").value(Employee.EmployeeStatus.ACTIVE.name())
      );
  }

  @Test
  void findByStatus_ShouldReturnEmployeesWithStatus() throws Exception {
    when(
      employeeService.findByStatus(Employee.EmployeeStatus.ACTIVE)
    ).thenReturn(List.of(testEmployeeInfo));

    mockMvc
      .perform(
        get("/api/employees/status/{status}", Employee.EmployeeStatus.ACTIVE)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testEmployeeInfo.getId()))
      .andExpect(jsonPath("$[0].userId").value(testEmployeeInfo.getUserId()))
      .andExpect(
        jsonPath("$[0].firstName").value(testEmployeeInfo.getFirstName())
      )
      .andExpect(
        jsonPath("$[0].lastName").value(testEmployeeInfo.getLastName())
      )
      .andExpect(
        jsonPath("$[0].documentId").value(testEmployeeInfo.getDocumentId())
      )
      .andExpect(jsonPath("$[0].email").value(testEmployeeInfo.getEmail()))
      .andExpect(
        jsonPath("$[0].position").value(testEmployeeInfo.getPosition())
      )
      .andExpect(
        jsonPath("$[0].department").value(testEmployeeInfo.getDepartment())
      )
      .andExpect(jsonPath("$[0].hireDate").exists())
      .andExpect(
        jsonPath("$[0].status").value(Employee.EmployeeStatus.ACTIVE.name())
      );
  }
}
