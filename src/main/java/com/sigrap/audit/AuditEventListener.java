package com.sigrap.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Event listener for handling audit events asynchronously.
 * Separates audit logging from business logic execution flow.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventListener {

  private final AuditLogService auditLogService;

  /**
   * Asynchronously handles audit events by persisting them to the database.
   * Uses a new transaction to prevent failures from affecting the original transaction.
   *
   * @param event The audit event to handle
   */
  @Async
  @EventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleAuditEvent(AuditEvent event) {
    if (event == null) {
      log.warn("Received null audit event");
      return;
    }

    if (event.getUsername() == null || event.getUsername().isEmpty()) {
      event.setUsername("system");
    }

    try {
      auditLogService.handleAuditEvent(event);
    } catch (Exception e) {
      log.error("Error handling audit event: {}", e.getMessage(), e);
    }
  }
}
