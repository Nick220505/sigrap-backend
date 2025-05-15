package com.sigrap.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import com.sigrap.user.UserStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(username = "test@example.com", roles = { "ADMIN" })
class EmployeePerformanceIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private EmployeePerformanceRepository performanceRepository;

  @Autowired
  private UserRepository userRepository;

  private Employee testEmployee;
  private EmployeePerformance testPerformance;

  @BeforeEach
  void setUp() {
    User user = User.builder()
      .name("John Doe")
      .email("john.doe@example.com")
      .password("password123")
      .status(UserStatus.ACTIVE)
      .build();
    user = userRepository.save(user);

    testEmployee = Employee.builder()
      .firstName("John")
      .lastName("Doe")
      .documentId("123456")
      .email("john.doe@example.com")
      .position("Sales")
      .department("Sales")
      .hireDate(LocalDateTime.now())
      .status(Employee.EmployeeStatus.ACTIVE)
      .user(user)
      .build();
    testEmployee = employeeRepository.save(testEmployee);

    testPerformance = EmployeePerformance.builder()
      .employee(testEmployee)
      .periodStart(LocalDateTime.now().minusDays(1))
      .periodEnd(LocalDateTime.now())
      .rating(90)
      .salesTotal(BigDecimal.valueOf(1000))
      .salesCount(10)
      .build();
    testPerformance = performanceRepository.save(testPerformance);
  }

  @Test
  void findById_ShouldReturnPerformanceRecord() throws Exception {
    mockMvc
      .perform(get("/api/employee-performance/{id}", testPerformance.getId()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testPerformance.getId()))
      .andExpect(jsonPath("$.employeeId").value(testEmployee.getId()))
      .andExpect(jsonPath("$.rating").value(testPerformance.getRating()));
  }

  @Test
  void findByEmployeeId_ShouldReturnPerformanceRecords() throws Exception {
    mockMvc
      .perform(
        get(
          "/api/employee-performance/employee/{employeeId}",
          testEmployee.getId()
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testPerformance.getId()))
      .andExpect(jsonPath("$[0].employeeId").value(testEmployee.getId()));
  }

  @Test
  void create_ShouldCreatePerformanceRecord() throws Exception {
    EmployeePerformanceData performanceData = EmployeePerformanceData.builder()
      .employeeId(testEmployee.getId())
      .periodStart(LocalDateTime.now().minusDays(2))
      .periodEnd(LocalDateTime.now().minusDays(1))
      .rating(85)
      .salesTotal(BigDecimal.valueOf(800))
      .salesCount(8)
      .build();

    mockMvc
      .perform(
        post("/api/employee-performance")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(performanceData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.employeeId").value(testEmployee.getId()))
      .andExpect(jsonPath("$.rating").value(85));
  }

  @Test
  void update_ShouldUpdatePerformanceRecord() throws Exception {
    EmployeePerformanceData updateData = EmployeePerformanceData.builder()
      .employeeId(testEmployee.getId())
      .periodStart(testPerformance.getPeriodStart())
      .periodEnd(testPerformance.getPeriodEnd())
      .rating(95)
      .salesTotal(BigDecimal.valueOf(1200))
      .salesCount(12)
      .build();

    mockMvc
      .perform(
        put("/api/employee-performance/{id}", testPerformance.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(updateData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.rating").value(95))
      .andExpect(jsonPath("$.salesTotal").value(1200));
  }

  @Test
  void delete_ShouldDeletePerformanceRecord() throws Exception {
    mockMvc
      .perform(
        delete("/api/employee-performance/{id}", testPerformance.getId())
      )
      .andExpect(status().isNoContent());

    assertThat(
      performanceRepository.findById(testPerformance.getId())
    ).isEmpty();
  }

  @Test
  @WithMockUser
  void findTopPerformers_ShouldReturnTopPerformers() throws Exception {
    LocalDateTime startDate = LocalDateTime.now().minusDays(7);
    LocalDateTime endDate = LocalDateTime.now().plusDays(1);

    mockMvc
      .perform(
        get("/api/employee-performance/top-performers")
          .param("startDate", startDate.toString())
          .param("endDate", endDate.toString())
          .param("limit", "5")
      )
      .andExpect(status().isOk());
  }

  @Test
  void findTopPerformersBySales_ShouldReturnTopPerformers() throws Exception {
    LocalDateTime startDate = LocalDateTime.now().minusDays(7);
    LocalDateTime endDate = LocalDateTime.now().plusDays(1);

    mockMvc
      .perform(
        get("/api/employee-performance/top/sales")
          .param("startDate", startDate.toString())
          .param("endDate", endDate.toString())
          .param("limit", "5")
      )
      .andExpect(status().isOk());
  }
}
