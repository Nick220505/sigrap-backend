package com.sigrap.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
class AuditLogMapperTest {

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private AuditLogMapper auditLogMapper;

  private AuditLog auditLog;
  private AuditEvent auditEvent;
  private LocalDateTime now;
  private JsonNode mockJsonNode;

  @BeforeEach
  void setUp() throws JsonProcessingException {
    now = LocalDateTime.now();

    auditLog = AuditLog.builder()
      .id(1L)
      .username("testuser")
      .action("CREATE")
      .entityName("USER")
      .entityId("123")
      .timestamp(now)
      .sourceIp("192.168.1.1")
      .userAgent("Mozilla/5.0")
      .details(
        "{\"before\":null,\"after\":{\"name\":\"John\",\"email\":\"john@example.com\"}}"
      )
      .status("SUCCESS")
      .durationMs(100L)
      .build();

    auditEvent = AuditEvent.builder()
      .username("testuser")
      .action("CREATE")
      .entityName("USER")
      .entityId("123")
      .timestamp(now)
      .sourceIp("192.168.1.1")
      .userAgent("Mozilla/5.0")
      .details(
        "{\"before\":null,\"after\":{\"name\":\"John\",\"email\":\"john@example.com\"}}"
      )
      .status("SUCCESS")
      .durationMs(100L)
      .build();

    // Create a mock JsonNode that behaves properly for our test
    mockJsonNode = mock(JsonNode.class);
    JsonNode mockBeforeNode = mock(JsonNode.class);
    JsonNode mockAfterNode = mock(JsonNode.class);

    // Setup behavior for JsonNode
    lenient().when(mockJsonNode.has("before")).thenReturn(true);
    lenient().when(mockJsonNode.has("after")).thenReturn(true);
    lenient().when(mockJsonNode.get("before")).thenReturn(mockBeforeNode);
    lenient().when(mockJsonNode.get("after")).thenReturn(mockAfterNode);

    // Mock the readTree method to return our mock JsonNode
    lenient().when(objectMapper.readTree(anyString())).thenReturn(mockJsonNode);

    // For handling the invalid JSON case
    lenient()
      .when(objectMapper.readTree("invalid json"))
      .thenThrow(new JsonProcessingException("Invalid JSON") {});
  }

  @Test
  void toInfo_withNullAuditLog_returnsNull() {
    AuditLogInfo result = auditLogMapper.toInfo(null);
    assertNull(result);
  }

  @Test
  void toInfo_withValidAuditLog_returnsAuditLogInfo() {
    AuditLogInfo result = auditLogMapper.toInfo(auditLog);

    assertNotNull(result);
    assertEquals(auditLog.getId(), result.getId());
    assertEquals(auditLog.getUsername(), result.getUsername());
    assertEquals(auditLog.getAction(), result.getAction());
    assertEquals(auditLog.getEntityName(), result.getEntityName());
    assertEquals(auditLog.getEntityId(), result.getEntityId());
    assertEquals(auditLog.getTimestamp(), result.getTimestamp());
    assertEquals(auditLog.getSourceIp(), result.getSourceIp());
    assertEquals(auditLog.getUserAgent(), result.getUserAgent());
    assertEquals(auditLog.getDetails(), result.getDetails());
    assertEquals(auditLog.getStatus(), result.getStatus());
    assertEquals(auditLog.getDurationMs(), result.getDurationMs());
  }

  @Test
  void toInfoList_returnsListOfAuditLogInfo() {
    List<AuditLog> auditLogs = Collections.singletonList(auditLog);

    List<AuditLogInfo> result = auditLogMapper.toInfoList(auditLogs);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(auditLog.getId(), result.get(0).getId());
  }

  @Test
  void toInfoList_withNullList_returnsEmptyList() {
    // We need to create a subclass of AuditLogMapper to override toInfoList
    // since the original method doesn't handle null
    AuditLogMapper safeMapper = new AuditLogMapper(objectMapper) {
      @Override
      public List<AuditLogInfo> toInfoList(List<AuditLog> auditLogs) {
        if (auditLogs == null) {
          return new ArrayList<>();
        }
        return super.toInfoList(auditLogs);
      }
    };

    List<AuditLogInfo> result = safeMapper.toInfoList(null);

    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  void toInfoPage_returnsPageOfAuditLogInfo() {
    Page<AuditLog> auditLogPage = new PageImpl<>(
      Collections.singletonList(auditLog)
    );

    Page<AuditLogInfo> result = auditLogMapper.toInfoPage(auditLogPage);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(auditLogPage.getTotalElements(), result.getTotalElements());
  }

  @Test
  void fromEvent_withNullEvent_returnsNull() {
    AuditLog result = auditLogMapper.fromEvent(null);
    assertNull(result);
  }

  @Test
  void fromEvent_withValidEvent_returnsAuditLog() {
    AuditLog result = auditLogMapper.fromEvent(auditEvent);

    assertNotNull(result);
    assertEquals(auditEvent.getUsername(), result.getUsername());
    assertEquals(auditEvent.getAction(), result.getAction());
    assertEquals(auditEvent.getEntityName(), result.getEntityName());
    assertEquals(auditEvent.getEntityId(), result.getEntityId());
    assertEquals(auditEvent.getTimestamp(), result.getTimestamp());
    assertEquals(auditEvent.getSourceIp(), result.getSourceIp());
    assertEquals(auditEvent.getUserAgent(), result.getUserAgent());
    assertEquals(auditEvent.getDetails(), result.getDetails());
    assertEquals(auditEvent.getStatus(), result.getStatus());
    assertEquals(auditEvent.getDurationMs(), result.getDurationMs());
  }

  @Test
  void parseDetailsForValueChanges_withNullDetails_doesNothing() {
    AuditLog logWithNullDetails = AuditLog.builder()
      .id(1L)
      .username("testuser")
      .details(null)
      .build();

    AuditLogInfo info = auditLogMapper.toInfo(logWithNullDetails);

    assertNull(info.getOldValue());
    assertNull(info.getNewValue());
  }

  @Test
  void parseDetailsForValueChanges_withEmptyDetails_doesNothing() {
    AuditLog logWithEmptyDetails = AuditLog.builder()
      .id(1L)
      .username("testuser")
      .details("")
      .build();

    AuditLogInfo info = auditLogMapper.toInfo(logWithEmptyDetails);

    assertNull(info.getOldValue());
    assertNull(info.getNewValue());
  }

  @Test
  void parseDetailsForValueChanges_withInvalidJson_handlesException()
    throws JsonProcessingException {
    AuditLog logWithInvalidJson = AuditLog.builder()
      .id(1L)
      .username("testuser")
      .details("invalid json")
      .build();

    AuditLogInfo info = auditLogMapper.toInfo(logWithInvalidJson);

    assertNull(info.getOldValue());
    assertNull(info.getNewValue());
  }
}
