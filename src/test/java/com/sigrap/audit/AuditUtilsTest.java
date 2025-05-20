package com.sigrap.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.config.SecurityUtils;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class AuditUtilsTest {

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private SecurityUtils securityUtils;

  @InjectMocks
  private AuditUtils auditUtils;

  @BeforeEach
  void setUp() {}

  @Test
  void getCurrentTimestamp_ShouldReturnCurrentTimeInColombiaBogotaTimezone() {
    LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Bogota"));
    LocalDateTime result = AuditUtils.getCurrentTimestamp();

    assertNotNull(result);

    long secondsDifference = ChronoUnit.SECONDS.between(now, result);
    assertTrue(
      Math.abs(secondsDifference) < 5,
      "Time difference should be less than 5 seconds"
    );

    assertEquals(
      now.getHour(),
      result.getHour(),
      "Hour should match Bogota timezone"
    );
  }

  @Test
  void getCurrentUsername_ShouldReturnUsernameFromSecurityUtils() {
    when(securityUtils.getCurrentUsername()).thenReturn("testUser");

    String username = auditUtils.getCurrentUsername();

    assertEquals("testUser", username);
  }

  @Test
  void getCurrentUsername_ShouldReturnAnonymousWhenExceptionIsThrown() {
    when(securityUtils.getCurrentUsername()).thenThrow(
      new RuntimeException("User not authenticated")
    );

    String username = auditUtils.getCurrentUsername();

    assertEquals("anonymous", username);
  }

  @Test
  void createAuditEventBuilder_ShouldReturnBuilderWithCorrectValues() {
    when(securityUtils.getCurrentUsername()).thenReturn("testUser");

    AuditEvent.AuditEventBuilder builder = auditUtils.createAuditEventBuilder(
      "CREATE",
      "User"
    );

    assertNotNull(builder);

    AuditEvent event = builder.build();
    assertEquals("testUser", event.getUsername());
    assertEquals("CREATE", event.getAction());
    assertEquals("User", event.getEntityName());
    assertNotNull(event.getTimestamp());
  }

  @Test
  void toJsonString_ShouldReturnJsonStringWhenObjectProvided()
    throws JsonProcessingException {
    TestObject testObject = new TestObject("test", 123);
    when(objectMapper.writeValueAsString(testObject)).thenReturn(
      "{\"name\":\"test\",\"value\":123}"
    );

    String json = auditUtils.toJsonString(testObject);

    assertEquals("{\"name\":\"test\",\"value\":123}", json);
  }

  @Test
  void toJsonString_ShouldReturnNullWhenObjectIsNull() {
    String json = auditUtils.toJsonString(null);

    assertNull(json);
  }

  @Test
  void toJsonString_ShouldReturnNullWhenJsonProcessingExceptionIsThrown()
    throws JsonProcessingException {
    TestObject testObject = new TestObject("test", 123);
    when(objectMapper.writeValueAsString(testObject)).thenThrow(
      new JsonProcessingException("Error processing JSON") {}
    );

    String json = auditUtils.toJsonString(testObject);

    assertNull(json);
  }

  @Test
  void createDiffString_ShouldReturnJsonStringWithBeforeAndAfterStates()
    throws JsonProcessingException {
    TestObject before = new TestObject("before", 100);
    TestObject after = new TestObject("after", 200);

    when(objectMapper.writeValueAsString(any())).thenReturn(
      "{\"before\":{\"name\":\"before\",\"value\":100},\"after\":{\"name\":\"after\",\"value\":200}}"
    );

    String diffString = auditUtils.createDiffString(before, after);

    assertEquals(
      "{\"before\":{\"name\":\"before\",\"value\":100},\"after\":{\"name\":\"after\",\"value\":200}}",
      diffString
    );
  }

  @Test
  void createDiffString_ShouldReturnNullWhenJsonProcessingExceptionIsThrown()
    throws JsonProcessingException {
    TestObject before = new TestObject("before", 100);
    TestObject after = new TestObject("after", 200);

    when(objectMapper.writeValueAsString(any())).thenThrow(
      new JsonProcessingException("Error processing JSON") {}
    );

    String diffString = auditUtils.createDiffString(before, after);

    assertNull(diffString);
  }

  @Test
  void populateHttpRequestDetails_ShouldAddRequestDetailsWhenAvailable() {
    ServletRequestAttributes requestAttributes = mock(
      ServletRequestAttributes.class
    );
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(requestAttributes.getRequest()).thenReturn(request);
    when(request.getRemoteAddr()).thenReturn("192.168.1.1");
    when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");

    try (
      MockedStatic<RequestContextHolder> requestContextHolderMock =
        Mockito.mockStatic(RequestContextHolder.class)
    ) {
      requestContextHolderMock
        .when(RequestContextHolder::getRequestAttributes)
        .thenReturn(requestAttributes);

      AuditEvent.AuditEventBuilder builder = AuditEvent.builder();
      auditUtils.populateHttpRequestDetails(builder);

      AuditEvent event = builder.build();
      assertEquals("192.168.1.1", event.getSourceIp());
      assertEquals("Mozilla/5.0", event.getUserAgent());
    }
  }

  @Test
  void populateHttpRequestDetails_ShouldHandleNullRequestAttributes() {
    try (
      MockedStatic<RequestContextHolder> requestContextHolderMock =
        Mockito.mockStatic(RequestContextHolder.class)
    ) {
      requestContextHolderMock
        .when(RequestContextHolder::getRequestAttributes)
        .thenReturn(null);

      AuditEvent.AuditEventBuilder builder = AuditEvent.builder();
      auditUtils.populateHttpRequestDetails(builder);

      AuditEvent event = builder.build();
      assertNull(event.getSourceIp());
      assertNull(event.getUserAgent());
    }
  }

  @Test
  void populateHttpRequestDetails_ShouldHandleExceptions() {
    ServletRequestAttributes requestAttributes = mock(
      ServletRequestAttributes.class
    );
    when(requestAttributes.getRequest()).thenThrow(
      new RuntimeException("Test exception")
    );

    try (
      MockedStatic<RequestContextHolder> requestContextHolderMock =
        Mockito.mockStatic(RequestContextHolder.class)
    ) {
      requestContextHolderMock
        .when(RequestContextHolder::getRequestAttributes)
        .thenReturn(requestAttributes);

      AuditEvent.AuditEventBuilder builder = AuditEvent.builder();
      auditUtils.populateHttpRequestDetails(builder);

      AuditEvent event = builder.build();
      assertNull(event.getSourceIp());
      assertNull(event.getUserAgent());
    }
  }

  static class TestObject {

    private final String name;
    private final int value;

    public TestObject(String name, int value) {
      this.name = name;
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public int getValue() {
      return value;
    }
  }
}
