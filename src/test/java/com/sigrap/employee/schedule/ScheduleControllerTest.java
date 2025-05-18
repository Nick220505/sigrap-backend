package com.sigrap.employee.schedule;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.auth.JwtUtil;
import com.sigrap.config.SecurityConfig;
import com.sigrap.user.UserService;

@WebMvcTest(controllers = ScheduleController.class)
@Import(SecurityConfig.class)
@TestPropertySource(
  properties = {
    "spring.security.user.name=testuser",
    "spring.security.user.password=testpass",
    "jwt.secret=testsecrettestsecrettestsecrettestsecrettestsecret",
    "jwt.expiration=3600000",
    "server.port=0",
  }
)
@WithMockUser(roles = "ADMINISTRATOR")
class ScheduleControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ScheduleService scheduleService;

  @MockBean
  private JwtUtil jwtUtil;

  @MockBean
  private UserService userService;

  private ScheduleInfo scheduleInfo;
  private ScheduleData scheduleData;

  @BeforeEach
  void setUp() {
    LocalTime startTime = LocalTime.of(13, 39, 13);
    LocalTime endTime = LocalTime.of(17, 39, 13);

    scheduleInfo = ScheduleInfo.builder()
      .id(1L)
      .userId(1L)
      .userName("Test User")
      .day("MONDAY")
      .startTime(startTime)
      .endTime(endTime)
      .isActive(true)
      .build();

    scheduleData = ScheduleData.builder()
      .userId(1L)
      .day("MONDAY")
      .startTime(startTime)
      .endTime(endTime)
      .isActive(true)
      .build();
  }

  @Test
  void findAll_shouldReturnSchedules() throws Exception {
    List<ScheduleInfo> schedules = List.of(scheduleInfo);
    when(scheduleService.findAll()).thenReturn(schedules);

    mockMvc
      .perform(get("/api/schedules"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].id", is(1)))
      .andExpect(jsonPath("$[0].userId", is(1)))
      .andExpect(jsonPath("$[0].userName", is("Test User")))
      .andExpect(jsonPath("$[0].day", is("MONDAY")))
      .andExpect(jsonPath("$[0].startTime", isA(Object.class)))
      .andExpect(jsonPath("$[0].endTime", isA(Object.class)))
      .andExpect(jsonPath("$[0].isActive", is(true)));
  }

  @Test
  void findById_shouldReturnSchedule() throws Exception {
    when(scheduleService.findById(1L)).thenReturn(scheduleInfo);

    mockMvc
      .perform(get("/api/schedules/1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id", is(1)))
      .andExpect(jsonPath("$.userId", is(1)))
      .andExpect(jsonPath("$.userName", is("Test User")))
      .andExpect(jsonPath("$.day", is("MONDAY")))
      .andExpect(jsonPath("$.startTime", isA(Object.class)))
      .andExpect(jsonPath("$.endTime", isA(Object.class)))
      .andExpect(jsonPath("$.isActive", is(true)));
  }

  @Test
  void findByUserId_shouldReturnSchedules() throws Exception {
    List<ScheduleInfo> schedules = List.of(scheduleInfo);
    when(scheduleService.findByUserId(1L)).thenReturn(schedules);

    mockMvc
      .perform(get("/api/schedules/user/1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].id", is(1)))
      .andExpect(jsonPath("$[0].userId", is(1)))
      .andExpect(jsonPath("$[0].userName", is("Test User")))
      .andExpect(jsonPath("$[0].day", is("MONDAY")))
      .andExpect(jsonPath("$[0].startTime", isA(Object.class)))
      .andExpect(jsonPath("$[0].endTime", isA(Object.class)))
      .andExpect(jsonPath("$[0].isActive", is(true)));
  }

  @Test
  void create_withValidData_shouldCreateSchedule() throws Exception {
    when(scheduleService.create(any(ScheduleData.class))).thenReturn(
      scheduleInfo
    );

    mockMvc
      .perform(
        post("/api/schedules")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(scheduleData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id", is(1)))
      .andExpect(jsonPath("$.userId", is(1)))
      .andExpect(jsonPath("$.userName", is("Test User")))
      .andExpect(jsonPath("$.day", is("MONDAY")))
      .andExpect(jsonPath("$.startTime", isA(Object.class)))
      .andExpect(jsonPath("$.endTime", isA(Object.class)))
      .andExpect(jsonPath("$.isActive", is(true)));
  }

  @Test
  void update_withValidData_shouldUpdateSchedule() throws Exception {
    when(scheduleService.update(anyLong(), any(ScheduleData.class))).thenReturn(
      scheduleInfo
    );

    mockMvc
      .perform(
        put("/api/schedules/1")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(scheduleData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id", is(1)))
      .andExpect(jsonPath("$.userId", is(1)))
      .andExpect(jsonPath("$.userName", is("Test User")))
      .andExpect(jsonPath("$.day", is("MONDAY")))
      .andExpect(jsonPath("$.startTime", isA(Object.class)))
      .andExpect(jsonPath("$.endTime", isA(Object.class)))
      .andExpect(jsonPath("$.isActive", is(true)));
  }

  @Test
  void delete_shouldReturnNoContent() throws Exception {
    mockMvc
      .perform(delete("/api/schedules/1"))
      .andExpect(status().isNoContent());
  }

  @Test
  void generateWeeklySchedule_shouldGenerateSchedules() throws Exception {
    List<ScheduleInfo> schedules = List.of(scheduleInfo);
    when(
      scheduleService.generateWeeklySchedule(anyLong(), any(ScheduleData.class))
    ).thenReturn(schedules);

    String requestBody =
      "{" +
      "\"userId\":1," +
      "\"startTime\":\"08:00:00\"," +
      "\"endTime\":\"17:00:00\"," +
      "\"day\":\"MONDAY\"," +
      "\"isActive\":true" +
      "}";

    mockMvc
      .perform(
        post("/api/schedules/generate-weekly/1")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody)
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].id", is(1)))
      .andExpect(jsonPath("$[0].userId", is(1)))
      .andExpect(jsonPath("$[0].userName", is("Test User")))
      .andExpect(jsonPath("$[0].day", is("MONDAY")))
      .andExpect(jsonPath("$[0].startTime", isA(Object.class)))
      .andExpect(jsonPath("$[0].endTime", isA(Object.class)))
      .andExpect(jsonPath("$[0].isActive", is(true)));
  }
}
