package com.sigrap.audit;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import com.sigrap.user.UserRole;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuditLogControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AuditLogService auditLogService;

  @MockBean
  private UserRepository userRepository;

  private List<AuditLogInfo> auditLogs;
  private LocalDateTime now;

  @BeforeEach
  void setUp() {
    setupMockAuthentication();
    now = LocalDateTime.now();
    auditLogs = List.of(
      AuditLogInfo.builder()
        .id(1L)
        .username("admin")
        .action("CREATE")
        .entityName("USER")
        .entityId("1")
        .timestamp(now)
        .sourceIp("127.0.0.1")
        .userAgent("Mozilla/5.0")
        .details("{\"before\":null,\"after\":{\"name\":\"John\"}}")
        .status("SUCCESS")
        .durationMs(100L)
        .build(),
      AuditLogInfo.builder()
        .id(2L)
        .username("admin")
        .action("UPDATE")
        .entityName("USER")
        .entityId("1")
        .timestamp(now.plusHours(1))
        .sourceIp("127.0.0.1")
        .userAgent("Mozilla/5.0")
        .details(
          "{\"before\":{\"name\":\"John\"},\"after\":{\"name\":\"John Doe\"}}"
        )
        .status("SUCCESS")
        .durationMs(150L)
        .build()
    );
    User adminUser = User.builder()
      .id(1L)
      .name("Admin User")
      .email("admin@example.com")
      .password("encoded_password")
      .role(UserRole.ADMINISTRATOR)
      .build();
    when(userRepository.findByEmail("admin@example.com")).thenReturn(
      Optional.of(adminUser)
    );
  }

  private void setupMockAuthentication() {
    var authorities = Collections.singletonList(
      new SimpleGrantedAuthority("ROLE_ADMINISTRATOR")
    );
    var authentication = new UsernamePasswordAuthenticationToken(
      "admin@example.com",
      null,
      authorities
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Test
  void getAll_returnsAuditLogs() throws Exception {
    Page<AuditLogInfo> page = new PageImpl<>(auditLogs);
    when(auditLogService.findAll(any(Pageable.class))).thenReturn(page);

    mockMvc
      .perform(get("/api/audit").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content", hasSize(2)))
      .andExpect(jsonPath("$.content[0].id", is(1)))
      .andExpect(jsonPath("$.content[0].username", is("admin")))
      .andExpect(jsonPath("$.content[0].action", is("CREATE")))
      .andExpect(jsonPath("$.content[0].entityName", is("USER")))
      .andExpect(jsonPath("$.content[1].id", is(2)))
      .andExpect(jsonPath("$.content[1].action", is("UPDATE")));
  }

  @Test
  void getById_returnsAuditLog() throws Exception {
    when(auditLogService.findById(1L)).thenReturn(auditLogs.get(0));

    mockMvc
      .perform(get("/api/audit/1").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id", is(1)))
      .andExpect(jsonPath("$.username", is("admin")))
      .andExpect(jsonPath("$.action", is("CREATE")))
      .andExpect(jsonPath("$.entityName", is("USER")));
  }

  @Test
  void getByUsername_returnsAuditLogs() throws Exception {
    Page<AuditLogInfo> page = new PageImpl<>(auditLogs);
    when(
      auditLogService.findByUsername(eq("admin"), any(Pageable.class))
    ).thenReturn(page);

    mockMvc
      .perform(
        get("/api/audit/by-user")
          .param("username", "admin")
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content", hasSize(2)))
      .andExpect(jsonPath("$.content[0].username", is("admin")));
  }

  @Test
  void getByAction_returnsAuditLogs() throws Exception {
    Page<AuditLogInfo> page = new PageImpl<>(List.of(auditLogs.get(0)));
    when(
      auditLogService.findByAction(eq("CREATE"), any(Pageable.class))
    ).thenReturn(page);

    mockMvc
      .perform(
        get("/api/audit/by-action")
          .param("action", "CREATE")
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content", hasSize(1)))
      .andExpect(jsonPath("$.content[0].action", is("CREATE")));
  }

  @Test
  void getByEntityName_returnsAuditLogs() throws Exception {
    Page<AuditLogInfo> page = new PageImpl<>(auditLogs);
    when(
      auditLogService.findByEntityName(eq("USER"), any(Pageable.class))
    ).thenReturn(page);

    mockMvc
      .perform(
        get("/api/audit/by-entity")
          .param("entityName", "USER")
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content", hasSize(2)))
      .andExpect(jsonPath("$.content[0].entityName", is("USER")));
  }

  @Test
  void getByEntityId_returnsAuditLogs() throws Exception {
    Page<AuditLogInfo> page = new PageImpl<>(auditLogs);
    when(
      auditLogService.findByEntityId(eq("1"), any(Pageable.class))
    ).thenReturn(page);

    mockMvc
      .perform(
        get("/api/audit/by-entity-id")
          .param("entityId", "1")
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content", hasSize(2)))
      .andExpect(jsonPath("$.content[0].entityId", is("1")));
  }

  @Test
  void getByEntityNameAndId_returnsAuditLogs() throws Exception {
    Page<AuditLogInfo> page = new PageImpl<>(auditLogs);
    when(
      auditLogService.findByEntityNameAndEntityId(
        eq("USER"),
        eq("1"),
        any(Pageable.class)
      )
    ).thenReturn(page);

    mockMvc
      .perform(
        get("/api/audit/by-entity-and-id")
          .param("entityName", "USER")
          .param("entityId", "1")
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content", hasSize(2)))
      .andExpect(jsonPath("$.content[0].entityName", is("USER")))
      .andExpect(jsonPath("$.content[0].entityId", is("1")));
  }

  @Test
  void getByDateRange_returnsAuditLogs() throws Exception {
    Page<AuditLogInfo> page = new PageImpl<>(auditLogs);

    String startTime = now
      .minusHours(1)
      .format(DateTimeFormatter.ISO_DATE_TIME);
    String endTime = now.plusHours(2).format(DateTimeFormatter.ISO_DATE_TIME);

    when(
      auditLogService.findByDateRange(
        any(LocalDateTime.class),
        any(LocalDateTime.class),
        any(Pageable.class)
      )
    ).thenReturn(page);

    mockMvc
      .perform(
        get("/api/audit/by-date-range")
          .param("startDate", startTime)
          .param("endDate", endTime)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content", hasSize(2)));
  }

  @Test
  void getBySourceIp_returnsAuditLogs() throws Exception {
    Page<AuditLogInfo> page = new PageImpl<>(auditLogs);
    when(
      auditLogService.findBySourceIp(eq("127.0.0.1"), any(Pageable.class))
    ).thenReturn(page);

    mockMvc
      .perform(
        get("/api/audit/by-ip")
          .param("sourceIp", "127.0.0.1")
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content", hasSize(2)))
      .andExpect(jsonPath("$.content[0].sourceIp", is("127.0.0.1")));
  }

  @Test
  void getErrors_returnsAuditLogs() throws Exception {
    Page<AuditLogInfo> page = new PageImpl<>(auditLogs);
    when(auditLogService.findErrors(any(Pageable.class))).thenReturn(page);

    mockMvc
      .perform(get("/api/audit/errors").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content", hasSize(2)));
  }

  @Test
  void getByEntityAndDate_returnsAuditLogs() throws Exception {
    Page<AuditLogInfo> page = new PageImpl<>(auditLogs);

    String startTime = now
      .minusHours(1)
      .format(DateTimeFormatter.ISO_DATE_TIME);
    String endTime = now.plusHours(2).format(DateTimeFormatter.ISO_DATE_TIME);

    when(
      auditLogService.findByEntityNameAndTimestampBetween(
        eq("USER"),
        any(LocalDateTime.class),
        any(LocalDateTime.class),
        any(Pageable.class)
      )
    ).thenReturn(page);

    mockMvc
      .perform(
        get("/api/audit/by-entity-and-date")
          .param("entityName", "USER")
          .param("startDate", startTime)
          .param("endDate", endTime)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content", hasSize(2)))
      .andExpect(jsonPath("$.content[0].entityName", is("USER")));
  }
}
