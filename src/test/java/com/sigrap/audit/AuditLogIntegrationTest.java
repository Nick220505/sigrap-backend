package com.sigrap.audit;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuditLogIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AuditLogRepository auditLogRepository;

  @Test
  @WithMockUser(roles = "ADMIN")
  void testGetAllAuditLogs() throws Exception {
    // Given
    AuditLog log1 = auditLogRepository.save(AuditLog.builder()
        .username("test1@example.com")
        .action("CREATE")
        .entityName("User")
        .timestamp(LocalDateTime.now())
        .build());

    AuditLog log2 = auditLogRepository.save(AuditLog.builder()
        .username("test2@example.com")
        .action("UPDATE")
        .entityName("Role")
        .timestamp(LocalDateTime.now())
        .build());

    // When/Then
    mockMvc.perform(get("/api/audit-logs")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
        .andExpect(jsonPath("$[*].id").exists())
        .andExpect(jsonPath("$[*].username").exists())
        .andExpect(jsonPath("$[*].action").exists())
        .andExpect(jsonPath("$[*].entityName").exists())
        .andExpect(jsonPath("$[*].timestamp").exists())
        .andExpect(jsonPath("$[*].userId").doesNotExist())
        .andExpect(jsonPath("$[*].entityId").doesNotExist())
        .andExpect(jsonPath("$[*].oldValue").doesNotExist())
        .andExpect(jsonPath("$[*].newValue").doesNotExist())
        .andExpect(jsonPath("$[*].ipAddress").doesNotExist());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void testCreateAuditLog() throws Exception {
    // Given/When/Then
    mockMvc.perform(post("/api/audit-logs")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"test@example.com\",\"action\":\"CREATE\",\"entityName\":\"User\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.username").value("test@example.com"))
        .andExpect(jsonPath("$.action").value("CREATE"))
        .andExpect(jsonPath("$.entityName").value("User"))
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.userId").doesNotExist())
        .andExpect(jsonPath("$.entityId").doesNotExist())
        .andExpect(jsonPath("$.oldValue").doesNotExist())
        .andExpect(jsonPath("$.newValue").doesNotExist())
        .andExpect(jsonPath("$.ipAddress").doesNotExist());
  }
}
