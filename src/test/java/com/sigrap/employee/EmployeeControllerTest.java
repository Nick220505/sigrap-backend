package com.sigrap.employee;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class EmployeeControllerTest {

  private MockMvc mockMvc;
  private EmployeeService employeeService;
  private ObjectMapper objectMapper;

  private EmployeeInfo testEmployeeInfo;
  private EmployeeData testEmployeeData;

  @BeforeEach
  void setUp() {
    employeeService = mock(EmployeeService.class);

    EmployeeController employeeController = new EmployeeController(
      employeeService
    );

    mockMvc = standaloneSetup(employeeController).build();

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    testEmployeeInfo = EmployeeInfo.builder()
      .id(1L)
      .userId(1L)
      .firstName("John")
      .lastName("Doe")
      .documentId("123456")
      .email("john.doe@example.com")
      .build();

    testEmployeeData = EmployeeData.builder()
      .userId(1L)
      .firstName("John")
      .lastName("Doe")
      .documentId("123456")
      .email("john.doe@example.com")
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
      .andExpect(jsonPath("$[0].email").value(testEmployeeInfo.getEmail()));
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
      .andExpect(jsonPath("$.email").value(testEmployeeInfo.getEmail()));
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
      .andExpect(jsonPath("$.email").value(testEmployeeInfo.getEmail()));
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
      .andExpect(jsonPath("$.email").value(testEmployeeInfo.getEmail()));
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
  void findByDocumentId_ShouldReturnEmployee() throws Exception {
    when(employeeService.findByDocumentId(anyString())).thenReturn(
      testEmployeeInfo
    );

    mockMvc
      .perform(
        get("/api/employees/document/" + testEmployeeInfo.getDocumentId())
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testEmployeeInfo.getId()))
      .andExpect(jsonPath("$.userId").value(testEmployeeInfo.getUserId()))
      .andExpect(jsonPath("$.firstName").value(testEmployeeInfo.getFirstName()))
      .andExpect(jsonPath("$.lastName").value(testEmployeeInfo.getLastName()))
      .andExpect(
        jsonPath("$.documentId").value(testEmployeeInfo.getDocumentId())
      )
      .andExpect(jsonPath("$.email").value(testEmployeeInfo.getEmail()));
  }
}
