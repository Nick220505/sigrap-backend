package com.sigrap.employee;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

class EmployeeControllerTest {

  private MockMvc mockMvc;
  private EmployeeService employeeService;
  private ObjectMapper objectMapper;

  private EmployeeInfo testEmployeeInfo;
  private EmployeeData testEmployeeData;
  private LocalDateTime hireDate;

  @BeforeEach
  void setUp() {
    // Setup mock service
    employeeService = mock(EmployeeService.class);

    // Setup controller with mocked service
    EmployeeController employeeController = new EmployeeController(
      employeeService
    );

    // Setup MockMvc
    mockMvc = standaloneSetup(employeeController).build();

    // Configure ObjectMapper for handling LocalDateTime
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

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
      .perform(put("/api/employees/{id}/activate", 1L))
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
      .perform(put("/api/employees/{id}/deactivate", 1L))
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
    when(employeeService.terminate(1L)).thenReturn(testEmployeeInfo);

    mockMvc
      .perform(put("/api/employees/{id}/terminate", 1L))
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
      );
  }

  @Test
  void findByDepartment_ShouldReturnDepartmentEmployees() throws Exception {
    String department = "Sales";
    when(employeeService.findByDepartment(department)).thenReturn(
      List.of(testEmployeeInfo)
    );

    mockMvc
      .perform(get("/api/employees/department/{department}", department))
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
    Employee.EmployeeStatus status = Employee.EmployeeStatus.ACTIVE;
    when(employeeService.findByStatus(status)).thenReturn(
      List.of(testEmployeeInfo)
    );

    mockMvc
      .perform(get("/api/employees/status/{status}", status))
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
