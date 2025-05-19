package com.sigrap.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class AuditLogRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private AuditLogRepository auditLogRepository;

  @Test
  void testSaveAndFindAll() {
    // Given
    AuditLog log1 = AuditLog.builder()
        .username("test1@example.com")
        .action("CREATE")
        .entityName("User")
        .timestamp(LocalDateTime.now())
        .build();

    AuditLog log2 = AuditLog.builder()
        .username("test2@example.com")
        .action("UPDATE")
        .entityName("Role")
        .timestamp(LocalDateTime.now())
        .build();

    entityManager.persist(log1);
    entityManager.persist(log2);
    entityManager.flush();

    // When
    List<AuditLog> found = auditLogRepository.findAll();

    // Then
    assertEquals(2, found.size());
    assertTrue(found.stream().anyMatch(log -> log.getUsername().equals(log1.getUsername()) &&
        log.getAction().equals(log1.getAction()) &&
        log.getEntityName().equals(log1.getEntityName())));
    assertTrue(found.stream().anyMatch(log -> log.getUsername().equals(log2.getUsername()) &&
        log.getAction().equals(log2.getAction()) &&
        log.getEntityName().equals(log2.getEntityName())));
  }

  @Test
  void testSaveAndFindById() {
    // Given
    AuditLog log = AuditLog.builder()
        .username("test@example.com")
        .action("CREATE")
        .entityName("User")
        .timestamp(LocalDateTime.now())
        .build();

    AuditLog saved = entityManager.persist(log);
    entityManager.flush();

    // When
    AuditLog found = auditLogRepository.findById(saved.getId()).orElse(null);

    // Then
    assertNotNull(found);
    assertEquals(saved.getId(), found.getId());
    assertEquals(saved.getUsername(), found.getUsername());
    assertEquals(saved.getAction(), found.getAction());
    assertEquals(saved.getEntityName(), found.getEntityName());
    assertEquals(saved.getTimestamp(), found.getTimestamp());
  }
}
