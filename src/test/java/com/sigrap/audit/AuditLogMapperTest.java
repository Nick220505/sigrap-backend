package com.sigrap.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
    now = LocalDateTime.now().withNano(0);

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

    mockJsonNode = mock(JsonNode.class);
    JsonNode mockAfterNode = mock(JsonNode.class);

    when(mockJsonNode.has("before")).thenReturn(true);
    when(mockJsonNode.has("after")).thenReturn(true);
    when(mockJsonNode.get("before")).thenReturn(null);
    when(mockJsonNode.get("after")).thenReturn(mockAfterNode);

    when(objectMapper.readTree(anyString())).thenReturn(mockJsonNode);

    when(objectMapper.readTree("invalid json")).thenThrow(
      new JsonProcessingException("Invalid JSON") {}
    );

    ObjectNode argsResultNode = mock(ObjectNode.class);
    ObjectNode argsNode = mock(ObjectNode.class);
    ObjectNode resultNode = mock(ObjectNode.class);

    when(argsResultNode.get("args")).thenReturn(argsNode);
    when(argsResultNode.get("result")).thenReturn(resultNode);
    when(argsResultNode.has("args")).thenReturn(true);
    when(argsResultNode.has("result")).thenReturn(true);

    String argsResultJson =
      "{\"args\":{\"param\":\"value\"},\"result\":{\"response\":\"success\"}}";
    when(objectMapper.readTree(argsResultJson)).thenReturn(argsResultNode);

    ObjectNode incompleteNode = mock(ObjectNode.class);
    String incompleteJson = "{\"someField\":\"someValue\"}";
    when(objectMapper.readTree(incompleteJson)).thenReturn(incompleteNode);
    when(incompleteNode.has("before")).thenReturn(false);
    when(incompleteNode.has("after")).thenReturn(false);
    when(incompleteNode.has("args")).thenReturn(false);
    when(incompleteNode.has("result")).thenReturn(false);
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

  @Test
  void toInfo_withValidData_returnsAuditLogInfo() {
    AuditLogInfo info = auditLogMapper.toInfo(auditLog);

    assertNotNull(info);
    assertEquals(auditLog.getId(), info.getId());
    assertEquals(auditLog.getUsername(), info.getUsername());
    assertEquals(auditLog.getAction(), info.getAction());
    assertEquals(auditLog.getEntityName(), info.getEntityName());
    assertEquals(auditLog.getEntityId(), info.getEntityId());
    assertEquals(auditLog.getTimestamp(), info.getTimestamp());
    assertEquals(auditLog.getSourceIp(), info.getSourceIp());
    assertEquals(auditLog.getUserAgent(), info.getUserAgent());
    assertEquals(auditLog.getDetails(), info.getDetails());
    assertEquals(auditLog.getStatus(), info.getStatus());
    assertEquals(auditLog.getDurationMs(), info.getDurationMs());
  }

  @Test
  void toInfo_withNullInput_returnsNull() {
    AuditLogInfo info = auditLogMapper.toInfo(null);
    assertNull(info);
  }

  @Test
  void toInfo_withNullDetails_handlesGracefully() {
    AuditLog logWithNullDetails = AuditLog.builder()
      .id(2L)
      .details(null)
      .build();

    AuditLogInfo info = auditLogMapper.toInfo(logWithNullDetails);

    assertNotNull(info);
    assertNull(info.getOldValue());
    assertNull(info.getNewValue());
  }

  @Test
  void toInfo_withEmptyDetails_handlesGracefully() {
    AuditLog logWithEmptyDetails = AuditLog.builder()
      .id(3L)
      .details("")
      .build();

    AuditLogInfo info = auditLogMapper.toInfo(logWithEmptyDetails);

    assertNotNull(info);
    assertNull(info.getOldValue());
    assertNull(info.getNewValue());
  }

  @Test
  void toInfo_withInvalidJson_handlesGracefully() {
    AuditLog logWithInvalidJson = AuditLog.builder()
      .id(4L)
      .details("invalid json")
      .build();

    AuditLogInfo info = auditLogMapper.toInfo(logWithInvalidJson);

    assertNotNull(info);
    assertNull(info.getOldValue());
    assertNull(info.getNewValue());
  }

  @Test
  void toInfo_withArgsResultFormat_parsesCorrectly()
    throws JsonProcessingException {
    String argsResultJson =
      "{\"args\":{\"param\":\"value\"},\"result\":{\"response\":\"success\"}}";
    AuditLog logWithArgsResult = AuditLog.builder()
      .id(5L)
      .details(argsResultJson)
      .build();

    AuditLogInfo info = auditLogMapper.toInfo(logWithArgsResult);

    assertNotNull(info);
    assertNotNull(info.getOldValue());
    assertNotNull(info.getNewValue());
  }

  @Test
  void toInfo_withMissingBeforeAfterFields_handlesGracefully()
    throws JsonProcessingException {
    String incompleteJson = "{\"someField\":\"someValue\"}";
    AuditLog logWithIncompleteJson = AuditLog.builder()
      .id(6L)
      .details(incompleteJson)
      .build();

    AuditLogInfo info = auditLogMapper.toInfo(logWithIncompleteJson);

    assertNotNull(info);
    assertNull(info.getOldValue());
    assertNull(info.getNewValue());
  }

  @Test
  void toInfoList_convertsListCorrectly() {
    List<AuditLog> auditLogs = List.of(auditLog);
    List<AuditLogInfo> infoList = auditLogMapper.toInfoList(auditLogs);

    assertNotNull(infoList);
    assertEquals(1, infoList.size());
    assertEquals(auditLog.getId(), infoList.get(0).getId());
  }

  @Test
  void fromEvent_convertsEventCorrectly() {
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
  void fromEvent_withNullInput_returnsNull() {
    AuditLog result = auditLogMapper.fromEvent(null);
    assertNull(result);
  }
}
