package com.sigrap.employee;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
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
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

class EmployeePerformanceControllerTest {

  private MockMvc mockMvc;
  private EmployeePerformanceService performanceService;
  private ObjectMapper objectMapper;

  @ControllerAdvice
  public static class TestExceptionHandler
    extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(
      EntityNotFoundException ex
    ) {
      return ResponseEntity.notFound().build();
    }
  }

  private EmployeePerformanceInfo testPerformance;
  private EmployeePerformanceData testData;

  @BeforeEach
  void setUp() {
    performanceService = mock(EmployeePerformanceService.class);
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    EmployeePerformanceController controller =
      new EmployeePerformanceController(performanceService);

    mockMvc = standaloneSetup(controller)
      .setControllerAdvice(new TestExceptionHandler())
      .build();

    testPerformance = EmployeePerformanceInfo.builder()
      .id(1L)
      .employeeId(1L)
      .employeeName("John Doe")
      .periodStart(LocalDateTime.now().minusDays(7))
      .periodEnd(LocalDateTime.now())
      .salesCount(10)
      .salesTotal(BigDecimal.valueOf(1000))
      .averageTransactionValue(BigDecimal.valueOf(100))
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

    testData = EmployeePerformanceData.builder()
      .employeeId(1L)
      .periodStart(LocalDateTime.now().minusDays(7))
      .periodEnd(LocalDateTime.now())
      .salesCount(10)
      .salesTotal(BigDecimal.valueOf(1000))
      .build();
  }

  @Test
  void findAll_ShouldReturnAllPerformanceRecords() throws Exception {
    when(performanceService.findAll()).thenReturn(List.of(testPerformance));

    mockMvc
      .perform(get("/api/employee-performance"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testPerformance.getId()))
      .andExpect(
        jsonPath("$[0].employeeId").value(testPerformance.getEmployeeId())
      )
      .andExpect(
        jsonPath("$[0].employeeName").value(testPerformance.getEmployeeName())
      )
      .andExpect(
        jsonPath("$[0].salesCount").value(testPerformance.getSalesCount())
      )
      .andExpect(
        jsonPath("$[0].salesTotal").value(
          testPerformance.getSalesTotal().doubleValue()
        )
      )
      .andExpect(
        jsonPath("$[0].averageTransactionValue").value(
          testPerformance.getAverageTransactionValue().doubleValue()
        )
      );
  }

  @Test
  void findById_ShouldReturnPerformanceRecord() throws Exception {
    when(performanceService.findById(anyLong())).thenReturn(testPerformance);

    mockMvc
      .perform(get("/api/employee-performance/{id}", 1L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testPerformance.getId()))
      .andExpect(
        jsonPath("$.employeeId").value(testPerformance.getEmployeeId())
      )
      .andExpect(
        jsonPath("$.employeeName").value(testPerformance.getEmployeeName())
      )
      .andExpect(
        jsonPath("$.salesCount").value(testPerformance.getSalesCount())
      )
      .andExpect(
        jsonPath("$.salesTotal").value(
          testPerformance.getSalesTotal().doubleValue()
        )
      )
      .andExpect(
        jsonPath("$.averageTransactionValue").value(
          testPerformance.getAverageTransactionValue().doubleValue()
        )
      );
  }

  @Test
  void findByEmployeeId_ShouldReturnEmployeePerformanceRecords()
    throws Exception {
    when(performanceService.findByEmployeeId(anyLong())).thenReturn(
      List.of(testPerformance)
    );

    mockMvc
      .perform(get("/api/employee-performance/employee/{employeeId}", 1L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testPerformance.getId()))
      .andExpect(
        jsonPath("$[0].employeeId").value(testPerformance.getEmployeeId())
      )
      .andExpect(
        jsonPath("$[0].employeeName").value(testPerformance.getEmployeeName())
      )
      .andExpect(
        jsonPath("$[0].salesCount").value(testPerformance.getSalesCount())
      )
      .andExpect(
        jsonPath("$[0].salesTotal").value(
          testPerformance.getSalesTotal().doubleValue()
        )
      )
      .andExpect(
        jsonPath("$[0].averageTransactionValue").value(
          testPerformance.getAverageTransactionValue().doubleValue()
        )
      );
  }

  @Test
  void create_ShouldCreateNewPerformanceRecord() throws Exception {
    when(performanceService.create(any())).thenReturn(testPerformance);

    mockMvc
      .perform(
        post("/api/employee-performance")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(testData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(testPerformance.getId()))
      .andExpect(
        jsonPath("$.employeeId").value(testPerformance.getEmployeeId())
      )
      .andExpect(
        jsonPath("$.employeeName").value(testPerformance.getEmployeeName())
      )
      .andExpect(
        jsonPath("$.salesCount").value(testPerformance.getSalesCount())
      )
      .andExpect(
        jsonPath("$.salesTotal").value(
          testPerformance.getSalesTotal().doubleValue()
        )
      )
      .andExpect(
        jsonPath("$.averageTransactionValue").value(
          testPerformance.getAverageTransactionValue().doubleValue()
        )
      );
  }

  @Test
  void update_ShouldUpdatePerformanceRecord() throws Exception {
    when(performanceService.update(anyLong(), any())).thenReturn(
      testPerformance
    );

    mockMvc
      .perform(
        put("/api/employee-performance/{id}", 1L)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(testData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testPerformance.getId()))
      .andExpect(
        jsonPath("$.employeeId").value(testPerformance.getEmployeeId())
      )
      .andExpect(
        jsonPath("$.employeeName").value(testPerformance.getEmployeeName())
      )
      .andExpect(
        jsonPath("$.salesCount").value(testPerformance.getSalesCount())
      )
      .andExpect(
        jsonPath("$.salesTotal").value(
          testPerformance.getSalesTotal().doubleValue()
        )
      )
      .andExpect(
        jsonPath("$.averageTransactionValue").value(
          testPerformance.getAverageTransactionValue().doubleValue()
        )
      );
  }

  @Test
  void delete_ShouldDeletePerformanceRecord() throws Exception {
    mockMvc
      .perform(delete("/api/employee-performance/{id}", 1L))
      .andExpect(status().isNoContent());
  }

  @Test
  void findTopPerformers_ShouldReturnTopPerformers() throws Exception {
    when(performanceService.findTopPerformers(any(), any(), any())).thenReturn(
      List.of(testPerformance)
    );

    mockMvc
      .perform(
        get("/api/employee-performance/top-performers")
          .param("startDate", LocalDateTime.now().minusDays(7).toString())
          .param("endDate", LocalDateTime.now().toString())
          .param("limit", "5")
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testPerformance.getId()))
      .andExpect(
        jsonPath("$[0].employeeId").value(testPerformance.getEmployeeId())
      )
      .andExpect(
        jsonPath("$[0].employeeName").value(testPerformance.getEmployeeName())
      )
      .andExpect(
        jsonPath("$[0].salesCount").value(testPerformance.getSalesCount())
      )
      .andExpect(
        jsonPath("$[0].salesTotal").value(
          testPerformance.getSalesTotal().doubleValue()
        )
      )
      .andExpect(
        jsonPath("$[0].averageTransactionValue").value(
          testPerformance.getAverageTransactionValue().doubleValue()
        )
      );
  }

  @Test
  void findTopPerformersBySales_ShouldReturnTopPerformers() throws Exception {
    when(
      performanceService.findTopPerformersBySales(any(), any(), any())
    ).thenReturn(List.of(testPerformance));

    mockMvc
      .perform(
        get("/api/employee-performance/top/sales")
          .param("startDate", LocalDateTime.now().minusDays(7).toString())
          .param("endDate", LocalDateTime.now().toString())
          .param("limit", "5")
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testPerformance.getId()))
      .andExpect(
        jsonPath("$[0].employeeId").value(testPerformance.getEmployeeId())
      )
      .andExpect(
        jsonPath("$[0].employeeName").value(testPerformance.getEmployeeName())
      )
      .andExpect(
        jsonPath("$[0].salesCount").value(testPerformance.getSalesCount())
      )
      .andExpect(
        jsonPath("$[0].salesTotal").value(
          testPerformance.getSalesTotal().doubleValue()
        )
      )
      .andExpect(
        jsonPath("$[0].averageTransactionValue").value(
          testPerformance.getAverageTransactionValue().doubleValue()
        )
      );
  }
}
