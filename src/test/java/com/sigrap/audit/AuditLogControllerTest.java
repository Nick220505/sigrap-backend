package com.sigrap.audit;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests for AuditLogController class.
 */
@ExtendWith(MockitoExtension.class)
class AuditLogControllerTest {

  private MockMvc mockMvc;

  @Mock
  private AuditLogService auditLogService;

  @InjectMocks
  private AuditLogController auditLogController;

  @BeforeEach
  void setUp() {
    mockMvc = standaloneSetup(auditLogController)
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .build();
  }

  @Test
  void findById_shouldReturnAuditLog() throws Exception {
    AuditLogInfo auditLogInfo = createAuditLogInfo();

    when(auditLogService.findById(1L)).thenReturn(auditLogInfo);

    mockMvc
        .perform(get("/api/audit-logs/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  /**
   * Creates a sample AuditLogInfo for testing.
   */
  private AuditLogInfo createAuditLogInfo() {
    return AuditLogInfo.builder()
        .id(1L)
        .username("test@example.com")
        .action("CREATE")
        .entityName("User")
        .timestamp(LocalDateTime.of(2025, 5, 1, 10, 0))
        .build();
  }
}
