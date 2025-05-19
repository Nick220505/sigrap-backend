package com.sigrap.audit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

  @Mock
  private AuditLogRepository auditLogRepository;

  @InjectMocks
  private AuditLogService auditLogService;

  @Test
  void testLog() {
    // Given
    String username = "test@example.com";
    String action = "CREATE";
    String entityName = "User";

    AuditLog savedLog = AuditLog.builder()
        .id(1L)
        .username(username)
        .action(action)
        .entityName(entityName)
        .timestamp(LocalDateTime.now())
        .build();

    when(auditLogRepository.save(any(AuditLog.class))).thenReturn(savedLog);

    // When
    AuditLogInfo result = auditLogService.log(username, action, entityName);

    // Then
    assertNotNull(result);
    assertEquals(username, result.getUsername());
    assertEquals(action, result.getAction());
    assertEquals(entityName, result.getEntityName());
    assertEquals(savedLog.getId(), result.getId());
    assertNotNull(result.getTimestamp());

    verify(auditLogRepository).save(any(AuditLog.class));
  }

  @Test
  void testFindAll() {
    // Given
    List<AuditLog> logs = Arrays.asList(
        AuditLog.builder()
            .id(1L)
            .username("test1@example.com")
            .action("CREATE")
            .entityName("User")
            .timestamp(LocalDateTime.now())
            .build(),
        AuditLog.builder()
            .id(2L)
            .username("test2@example.com")
            .action("UPDATE")
            .entityName("Role")
            .timestamp(LocalDateTime.now())
            .build());

    when(auditLogRepository.findAll()).thenReturn(logs);

    // When
    List<AuditLogInfo> result = auditLogService.findAll();

    // Then
    assertEquals(logs.size(), result.size());
    for (int i = 0; i < logs.size(); i++) {
      assertEquals(logs.get(i).getId(), result.get(i).getId());
      assertEquals(logs.get(i).getUsername(), result.get(i).getUsername());
      assertEquals(logs.get(i).getAction(), result.get(i).getAction());
      assertEquals(logs.get(i).getEntityName(), result.get(i).getEntityName());
      assertEquals(logs.get(i).getTimestamp(), result.get(i).getTimestamp());
    }

    verify(auditLogRepository).findAll();
  }
}
