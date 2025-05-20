package com.sigrap.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.config.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * AOP aspect that intercepts methods annotated with @Auditable and automatically
 * generates audit logs.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

  private final AuditLogService auditLogService;
  private final SecurityUtils securityUtils;
  private final ObjectMapper objectMapper;

  private final ExpressionParser expressionParser = new SpelExpressionParser();
  private final ParameterNameDiscoverer parameterNameDiscoverer =
    new DefaultParameterNameDiscoverer();

  /**
   * Around advice that intercepts methods annotated with @Auditable.
   * Extracts metadata and generates an audit log entry.
   *
   * @param joinPoint The join point for the intercepted method
   * @param auditable The Auditable annotation on the method
   * @return The result from the method execution
   * @throws Throwable If method execution fails
   */
  @Around("@annotation(auditable)")
  public Object auditMethod(ProceedingJoinPoint joinPoint, Auditable auditable)
    throws Throwable {
    long startTime = System.currentTimeMillis();
    String username = getCurrentUsername();
    String action = auditable.action();
    String entityName = auditable.entity();

    String details = null;
    String entityId = null;
    Object methodResult = null;
    String status = "SUCCESS";

    if (!auditable.entityIdParam().isEmpty()) {
      entityId = extractEntityId(joinPoint, auditable.entityIdParam());
    }

    if (auditable.captureDetails()) {
      try {
        details = objectMapper.writeValueAsString(joinPoint.getArgs());
      } catch (JsonProcessingException e) {
        log.warn("Failed to serialize method arguments for audit log", e);
      }
    }

    AuditEvent.AuditEventBuilder eventBuilder = AuditEvent.builder()
      .username(username)
      .action(action)
      .entityName(entityName)
      .entityId(entityId)
      .timestamp(LocalDateTime.now());

    populateHttpRequestDetails(eventBuilder);

    try {
      methodResult = joinPoint.proceed();

      if (auditable.captureDetails() && methodResult != null) {
        try {
          if (details != null) {
            details =
              "{\"args\":" +
              details +
              ",\"result\":" +
              objectMapper.writeValueAsString(methodResult) +
              "}";
          } else {
            details = objectMapper.writeValueAsString(methodResult);
          }
        } catch (JsonProcessingException e) {
          log.warn("Failed to serialize method result for audit log", e);
        }
      }

      if (entityId == null && methodResult != null) {
        entityId = extractIdFromResult(methodResult);
      }

      return methodResult;
    } catch (Throwable ex) {
      status = "ERROR";
      details = "Exception: " + ex.getMessage();
      throw ex;
    } finally {
      long endTime = System.currentTimeMillis();
      long duration = endTime - startTime;

      AuditEvent auditEvent = eventBuilder
        .details(details)
        .status(status)
        .durationMs(duration)
        .entityId(entityId)
        .build();

      try {
        auditLogService.publishAuditEvent(auditEvent);
      } catch (Exception e) {
        log.error("Error publishing audit event", e);
      }
    }
  }

  /**
   * Gets the current authenticated username.
   *
   * @return The username or "system" if not authenticated
   */
  private String getCurrentUsername() {
    try {
      String username = securityUtils.getCurrentUsername();
      return username != null && !username.isEmpty() ? username : "system";
    } catch (Exception e) {
      log.debug("Could not get current username: {}", e.getMessage());
      return "system";
    }
  }

  /**
   * Extracts the entity ID from method parameters using SpEL.
   *
   * @param joinPoint The join point representing the intercepted method
   * @param entityIdParam Parameter name or SpEL expression for the entity ID
   * @return The extracted entity ID as a string
   */
  private String extractEntityId(
    ProceedingJoinPoint joinPoint,
    String entityIdParam
  ) {
    try {
      MethodSignature methodSignature =
        (MethodSignature) joinPoint.getSignature();
      String[] paramNames = parameterNameDiscoverer.getParameterNames(
        methodSignature.getMethod()
      );
      Object[] arguments = joinPoint.getArgs();

      if (paramNames == null) {
        return null;
      }

      EvaluationContext context = new StandardEvaluationContext();

      for (int i = 0; i < paramNames.length; i++) {
        context.setVariable(paramNames[i], arguments[i]);
      }

      Expression expression = expressionParser.parseExpression(
        entityIdParam.startsWith("#") ? entityIdParam : "#" + entityIdParam
      );

      Object value = expression.getValue(context);
      return value != null ? value.toString() : null;
    } catch (Exception e) {
      log.warn("Failed to extract entity ID for audit log: {}", e.getMessage());
      return null;
    }
  }

  /**
   * Populates the audit event builder with HTTP request details if available.
   *
   * @param eventBuilder The audit event builder to populate
   */
  private void populateHttpRequestDetails(
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

  /**
   * Attempts to extract an ID field from the method result.
   *
   * @param result The result object from the method execution
   * @return The ID value as a string, or null if not found
   */
  private String extractIdFromResult(Object result) {
    if (result == null) {
      return null;
    }

    try {
      try {
        java.lang.reflect.Method getIdMethod = result
          .getClass()
          .getMethod("getId");
        Object id = getIdMethod.invoke(result);
        if (id != null) {
          return id.toString();
        }
      } catch (NoSuchMethodException e) {
        try {
          java.lang.reflect.Field idField = result
            .getClass()
            .getDeclaredField("id");
          idField.setAccessible(true);
          Object id = idField.get(result);
          if (id != null) {
            return id.toString();
          }
        } catch (NoSuchFieldException ex) {}
      }

      if (result instanceof Optional<?>) {
        Optional<?> optional = (Optional<?>) result;
        if (optional.isPresent()) {
          return extractIdFromResult(optional.get());
        }
      }
    } catch (Exception e) {
      log.debug("Could not extract ID from result", e);
    }

    return null;
  }
}
