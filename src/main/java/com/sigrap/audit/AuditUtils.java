package com.sigrap.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.config.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Utility class providing helper methods for audit logging.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditUtils {

  private final ObjectMapper objectMapper;
  private final SecurityUtils securityUtils;

  /**
   * Gets the current authenticated username.
   *
   * @return The username or "anonymous" if not authenticated
   */
  public String getCurrentUsername() {
    try {
      return securityUtils.getCurrentUsername();
    } catch (Exception e) {
      return "anonymous";
    }
  }

  /**
   * Creates an AuditEvent builder pre-populated with current username and HTTP request details.
   *
   * @param action The action being performed
   * @param entityName The entity type being affected
   * @return A pre-configured AuditEvent builder
   */
  public AuditEvent.AuditEventBuilder createAuditEventBuilder(
    String action,
    String entityName
  ) {
    AuditEvent.AuditEventBuilder builder = AuditEvent.builder()
      .username(getCurrentUsername())
      .action(action)
      .entityName(entityName);

    populateHttpRequestDetails(builder);
    return builder;
  }

  /**
   * Converts an object to JSON string.
   *
   * @param object The object to convert
   * @return JSON string representation, or null if conversion fails
   */
  public String toJsonString(Object object) {
    if (object == null) {
      return null;
    }

    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.warn("Failed to convert object to JSON string", e);
      return null;
    }
  }

  /**
   * Creates a JSON diff string comparing before and after states.
   *
   * @param before The object's state before the action
   * @param after The object's state after the action
   * @return JSON string containing both states
   */
  public String createDiffString(Object before, Object after) {
    try {
      Map<String, Object> diff = Map.of("before", before, "after", after);
      return objectMapper.writeValueAsString(diff);
    } catch (JsonProcessingException e) {
      log.warn("Failed to create diff string", e);
      return null;
    }
  }

  /**
   * Populates the audit event builder with HTTP request details if available.
   *
   * @param eventBuilder The audit event builder to populate
   */
  public void populateHttpRequestDetails(
    AuditEvent.AuditEventBuilder eventBuilder
  ) {
    try {
      ServletRequestAttributes requestAttributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if (requestAttributes != null) {
        HttpServletRequest request = requestAttributes.getRequest();
        eventBuilder.sourceIp(request.getRemoteAddr());
        eventBuilder.userAgent(request.getHeader("User-Agent"));
      }
    } catch (Exception e) {
      log.debug("Could not access HTTP request details", e);
    }
  }
}
