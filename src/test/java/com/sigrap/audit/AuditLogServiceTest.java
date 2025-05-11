package com.sigrap.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

  @Mock
  private AuditLogRepository auditLogRepository;

  @Mock
  private AuditLogMapper auditLogMapper;

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private AuditLogService auditLogService;

  @Test
  void log_shouldCreateAuditLogEntry() throws Exception {
    Long userId = 1L;
    String username = "test@example.com";
    String action = "CREATE";
    String entityName = "User";
    String entityId = "1";
    Object oldValue = null;
    Object newValue = new UserObjectTest(1L, "Test User");
    String newValueJson = "{\"id\":1,\"name\":\"Test User\"}";

    when(objectMapper.writeValueAsString(newValue)).thenReturn(newValueJson);

    AuditLog savedAuditLog = AuditLog.builder()
      .id(1L)
      .userId(userId)
      .username(username)
      .action(action)
      .entityName(entityName)
      .entityId(entityId)
      .oldValue(null)
      .newValue(newValueJson)
      .timestamp(LocalDateTime.now())
      .ipAddress("127.0.0.1")
      .build();

    AuditLogInfo auditLogInfo = AuditLogInfo.builder()
      .id(1L)
      .userId(userId)
      .username(username)
      .action(action)
      .entityName(entityName)
      .entityId(entityId)
      .oldValue(null)
      .newValue(newValueJson)
      .timestamp(savedAuditLog.getTimestamp())
      .ipAddress("127.0.0.1")
      .build();

    when(auditLogRepository.save(any(AuditLog.class))).thenReturn(
      savedAuditLog
    );
    when(auditLogMapper.toInfo(savedAuditLog)).thenReturn(auditLogInfo);

    AuditLogInfo result = auditLogService.log(
      userId,
      username,
      action,
      entityName,
      entityId,
      oldValue,
      newValue
    );

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getUsername()).isEqualTo(username);
    assertThat(result.getAction()).isEqualTo(action);
    assertThat(result.getEntityName()).isEqualTo(entityName);
    assertThat(result.getEntityId()).isEqualTo(entityId);
    assertThat(result.getNewValue()).isEqualTo(newValueJson);

    verify(auditLogRepository).save(any(AuditLog.class));
    verify(auditLogMapper).toInfo(savedAuditLog);
  }

  @Test
  void findAll_shouldReturnPageOfAuditLogs() {
    Pageable pageable = Pageable.unpaged();
    AuditLog auditLog = AuditLog.builder()
      .id(1L)
      .userId(1L)
      .username("test@example.com")
      .action("CREATE")
      .entityName("User")
      .entityId("1")
      .timestamp(LocalDateTime.now())
      .build();

    List<AuditLog> auditLogs = List.of(auditLog);
    Page<AuditLog> auditLogPage = new PageImpl<>(
      auditLogs,
      pageable,
      auditLogs.size()
    );

    AuditLogInfo auditLogInfo = AuditLogInfo.builder()
      .id(1L)
      .userId(1L)
      .username("test@example.com")
      .action("CREATE")
      .entityName("User")
      .entityId("1")
      .timestamp(auditLog.getTimestamp())
      .build();

    when(auditLogRepository.findAll(pageable)).thenReturn(auditLogPage);
    when(auditLogMapper.toInfo(auditLog)).thenReturn(auditLogInfo);

    Page<AuditLogInfo> result = auditLogService.findAll(pageable);

    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
    assertThat(result.getContent().get(0).getAction()).isEqualTo("CREATE");

    verify(auditLogRepository).findAll(pageable);
    verify(auditLogMapper).toInfo(auditLog);
  }

  @Test
  void findById_shouldReturnAuditLog_whenExists() {
    Long id = 1L;
    AuditLog auditLog = AuditLog.builder()
      .id(id)
      .userId(1L)
      .username("test@example.com")
      .action("CREATE")
      .entityName("User")
      .entityId("1")
      .timestamp(LocalDateTime.now())
      .build();

    AuditLogInfo auditLogInfo = AuditLogInfo.builder()
      .id(id)
      .userId(1L)
      .username("test@example.com")
      .action("CREATE")
      .entityName("User")
      .entityId("1")
      .timestamp(auditLog.getTimestamp())
      .build();

    when(auditLogRepository.findById(id)).thenReturn(Optional.of(auditLog));
    when(auditLogMapper.toInfo(auditLog)).thenReturn(auditLogInfo);

    AuditLogInfo result = auditLogService.findById(id);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(id);
    assertThat(result.getAction()).isEqualTo("CREATE");

    verify(auditLogRepository).findById(id);
    verify(auditLogMapper).toInfo(auditLog);
  }

  @Test
  void findById_shouldThrowException_whenDoesNotExist() {
    Long id = 1L;

    when(auditLogRepository.findById(id)).thenReturn(Optional.empty());

    try {
      auditLogService.findById(id);
      assert false : "Expected exception was not thrown";
    } catch (Exception e) {
      assertThat(e).isInstanceOf(EntityNotFoundException.class);
    }

    verify(auditLogRepository).findById(id);
  }
}
