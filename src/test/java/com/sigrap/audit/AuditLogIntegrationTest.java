package com.sigrap.audit;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
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
class AuditLogIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AuditLogRepository auditLogRepository;

  @Autowired
  private UserRepository userRepository;

  private AuditLog createAuditLog;
  private AuditLog updateAuditLog;
  private User testUser;

  @BeforeEach
  void setUp() {
    auditLogRepository.deleteAll();
    userRepository.deleteAll();

    testUser = userRepository.save(
      User.builder()
        .name("Test User")
        .email("test@example.com")
        .password("password")
        .documentId("AUDIT_USER_DOC_123")
        .build()
    );

    createAuditLog = auditLogRepository.save(
      AuditLog.builder()
        .userId(testUser.getId())
        .username(testUser.getEmail())
        .action("CREATE")
        .entityName("User")
        .entityId(testUser.getId().toString())
        .oldValue(null)
        .newValue(
          "{\"id\":" +
          testUser.getId() +
          ",\"name\":\"Test User\",\"email\":\"test@example.com\"}"
        )
        .timestamp(LocalDateTime.now())
        .ipAddress("127.0.0.1")
        .build()
    );

    updateAuditLog = auditLogRepository.save(
      AuditLog.builder()
        .userId(testUser.getId())
        .username(testUser.getEmail())
        .action("UPDATE")
        .entityName("User")
        .entityId(testUser.getId().toString())
        .oldValue(
          "{\"id\":" +
          testUser.getId() +
          ",\"name\":\"Test User\",\"email\":\"test@example.com\"}"
        )
        .newValue(
          "{\"id\":" +
          testUser.getId() +
          ",\"name\":\"Updated User\",\"email\":\"test@example.com\"}"
        )
        .timestamp(LocalDateTime.now())
        .ipAddress("127.0.0.1")
        .build()
    );
  }

  @AfterEach
  void tearDown() {
    auditLogRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void findAll_shouldReturnAllAuditLogs() throws Exception {
    mockMvc
      .perform(get("/api/audit-logs").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(2)))
      .andExpect(jsonPath("$[0].id").isNumber())
      .andExpect(jsonPath("$[0].userId").value(testUser.getId()))
      .andExpect(jsonPath("$[0].username").value(testUser.getEmail()));
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void findById_shouldReturnAuditLog() throws Exception {
    mockMvc
      .perform(
        get("/api/audit-logs/" + createAuditLog.getId()).contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(createAuditLog.getId()))
      .andExpect(jsonPath("$.userId").value(testUser.getId()))
      .andExpect(jsonPath("$.username").value(testUser.getEmail()))
      .andExpect(jsonPath("$.action").value("CREATE"))
      .andExpect(jsonPath("$.entityName").value("User"))
      .andExpect(jsonPath("$.entityId").value(testUser.getId().toString()));
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void findByEntityNameAndEntityId_shouldReturnAuditLogs() throws Exception {
    mockMvc
      .perform(
        get("/api/audit-logs/entities/User/" + testUser.getId()).contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(2)))
      .andExpect(jsonPath("$[0].entityName").value("User"))
      .andExpect(jsonPath("$[0].entityId").value(testUser.getId().toString()))
      .andExpect(jsonPath("$[1].entityName").value("User"))
      .andExpect(jsonPath("$[1].entityId").value(testUser.getId().toString()));
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void findByUserId_shouldReturnAuditLogs() throws Exception {
    mockMvc
      .perform(
        get("/api/audit-logs/users/" + testUser.getId()).contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(2)))
      .andExpect(jsonPath("$[0].userId").value(testUser.getId()))
      .andExpect(jsonPath("$[0].username").value(testUser.getEmail()))
      .andExpect(jsonPath("$[1].userId").value(testUser.getId()))
      .andExpect(jsonPath("$[1].username").value(testUser.getEmail()));
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void findByAction_shouldReturnAuditLogs() throws Exception {
    mockMvc
      .perform(
        get("/api/audit-logs/actions/CREATE").contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].id").value(createAuditLog.getId()))
      .andExpect(jsonPath("$[0].action").value("CREATE"));

    mockMvc
      .perform(
        get("/api/audit-logs/actions/UPDATE").contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].id").value(updateAuditLog.getId()))
      .andExpect(jsonPath("$[0].action").value("UPDATE"));
  }
}
