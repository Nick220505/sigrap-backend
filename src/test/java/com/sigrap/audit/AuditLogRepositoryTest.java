package com.sigrap.audit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class AuditLogRepositoryTest {

  @Autowired
  private AuditLogRepository auditLogRepository;

  @Test
  void save_shouldSaveAuditLog() {
    AuditLog auditLog = AuditLog.builder()
      .userId(1L)
      .username("test@example.com")
      .action("CREATE")
      .entityName("User")
      .entityId("1")
      .newValue("{\"id\":1,\"name\":\"Test User\"}")
      .timestamp(LocalDateTime.now())
      .ipAddress("127.0.0.1")
      .build();

    AuditLog savedAuditLog = auditLogRepository.save(auditLog);

    assertThat(savedAuditLog.getId()).isNotNull();
    assertThat(savedAuditLog.getUserId()).isEqualTo(1L);
    assertThat(savedAuditLog.getUsername()).isEqualTo("test@example.com");
    assertThat(savedAuditLog.getAction()).isEqualTo("CREATE");
    assertThat(savedAuditLog.getEntityName()).isEqualTo("User");
    assertThat(savedAuditLog.getEntityId()).isEqualTo("1");
    assertThat(savedAuditLog.getNewValue()).isEqualTo(
      "{\"id\":1,\"name\":\"Test User\"}"
    );
    assertThat(savedAuditLog.getIpAddress()).isEqualTo("127.0.0.1");
  }

  @Test
  void findById_shouldReturnAuditLog_whenExists() {
    AuditLog auditLog = AuditLog.builder()
      .userId(1L)
      .username("test@example.com")
      .action("CREATE")
      .entityName("User")
      .entityId("1")
      .newValue("{\"id\":1,\"name\":\"Test User\"}")
      .timestamp(LocalDateTime.now())
      .ipAddress("127.0.0.1")
      .build();

    AuditLog savedAuditLog = auditLogRepository.save(auditLog);

    Optional<AuditLog> foundAuditLog = auditLogRepository.findById(
      savedAuditLog.getId()
    );

    assertThat(foundAuditLog).isPresent();
    assertThat(foundAuditLog.get().getUserId()).isEqualTo(1L);
    assertThat(foundAuditLog.get().getUsername()).isEqualTo("test@example.com");
    assertThat(foundAuditLog.get().getAction()).isEqualTo("CREATE");
  }

  @Test
  void findByEntityNameAndEntityId_shouldReturnAuditLogs() {
    AuditLog auditLog1 = AuditLog.builder()
      .userId(1L)
      .username("test@example.com")
      .action("CREATE")
      .entityName("User")
      .entityId("1")
      .newValue("{\"id\":1,\"name\":\"Test User\"}")
      .timestamp(LocalDateTime.now())
      .ipAddress("127.0.0.1")
      .build();

    AuditLog auditLog2 = AuditLog.builder()
      .userId(1L)
      .username("test@example.com")
      .action("UPDATE")
      .entityName("User")
      .entityId("1")
      .oldValue("{\"id\":1,\"name\":\"Test User\"}")
      .newValue("{\"id\":1,\"name\":\"Updated User\"}")
      .timestamp(LocalDateTime.now())
      .ipAddress("127.0.0.1")
      .build();

    AuditLog auditLog3 = AuditLog.builder()
      .userId(1L)
      .username("test@example.com")
      .action("CREATE")
      .entityName("Product")
      .entityId("1")
      .newValue("{\"id\":1,\"name\":\"Test Product\"}")
      .timestamp(LocalDateTime.now())
      .ipAddress("127.0.0.1")
      .build();

    auditLogRepository.saveAll(List.of(auditLog1, auditLog2, auditLog3));

    List<AuditLog> userAuditLogs =
      auditLogRepository.findByEntityNameAndEntityIdOrderByTimestampDesc(
        "User",
        "1"
      );

    assertThat(userAuditLogs).hasSize(2);
    assertThat(userAuditLogs.get(0).getEntityName()).isEqualTo("User");
    assertThat(userAuditLogs.get(0).getEntityId()).isEqualTo("1");
    assertThat(userAuditLogs.get(1).getEntityName()).isEqualTo("User");
    assertThat(userAuditLogs.get(1).getEntityId()).isEqualTo("1");
  }

  @Test
  void findByUserId_shouldReturnAuditLogs() {
    AuditLog auditLog1 = AuditLog.builder()
      .userId(1L)
      .username("user1@example.com")
      .action("CREATE")
      .entityName("User")
      .entityId("1")
      .newValue("{\"id\":1,\"name\":\"User 1\"}")
      .timestamp(LocalDateTime.now())
      .ipAddress("127.0.0.1")
      .build();

    AuditLog auditLog2 = AuditLog.builder()
      .userId(1L)
      .username("user1@example.com")
      .action("CREATE")
      .entityName("Product")
      .entityId("1")
      .newValue("{\"id\":1,\"name\":\"Test Product\"}")
      .timestamp(LocalDateTime.now())
      .ipAddress("127.0.0.1")
      .build();

    AuditLog auditLog3 = AuditLog.builder()
      .userId(2L)
      .username("user2@example.com")
      .action("CREATE")
      .entityName("User")
      .entityId("2")
      .newValue("{\"id\":2,\"name\":\"User 2\"}")
      .timestamp(LocalDateTime.now())
      .ipAddress("127.0.0.1")
      .build();

    auditLogRepository.saveAll(List.of(auditLog1, auditLog2, auditLog3));

    List<AuditLog> user1AuditLogs =
      auditLogRepository.findByUserIdOrderByTimestampDesc(1L);

    assertThat(user1AuditLogs).hasSize(2);
    assertThat(user1AuditLogs.get(0).getUserId()).isEqualTo(1L);
    assertThat(user1AuditLogs.get(0).getUsername()).isEqualTo(
      "user1@example.com"
    );
    assertThat(user1AuditLogs.get(1).getUserId()).isEqualTo(1L);
    assertThat(user1AuditLogs.get(1).getUsername()).isEqualTo(
      "user1@example.com"
    );
  }

  @Test
  void findByAction_shouldReturnAuditLogs() {
    AuditLog auditLog1 = AuditLog.builder()
      .userId(1L)
      .username("test@example.com")
      .action("CREATE")
      .entityName("User")
      .entityId("1")
      .newValue("{\"id\":1,\"name\":\"Test User\"}")
      .timestamp(LocalDateTime.now())
      .ipAddress("127.0.0.1")
      .build();

    AuditLog auditLog2 = AuditLog.builder()
      .userId(1L)
      .username("test@example.com")
      .action("UPDATE")
      .entityName("User")
      .entityId("1")
      .oldValue("{\"id\":1,\"name\":\"Test User\"}")
      .newValue("{\"id\":1,\"name\":\"Updated User\"}")
      .timestamp(LocalDateTime.now())
      .ipAddress("127.0.0.1")
      .build();

    AuditLog auditLog3 = AuditLog.builder()
      .userId(2L)
      .username("admin@example.com")
      .action("CREATE")
      .entityName("Product")
      .entityId("1")
      .newValue("{\"id\":1,\"name\":\"Test Product\"}")
      .timestamp(LocalDateTime.now())
      .ipAddress("127.0.0.1")
      .build();

    auditLogRepository.saveAll(List.of(auditLog1, auditLog2, auditLog3));

    List<AuditLog> createAuditLogs =
      auditLogRepository.findByActionOrderByTimestampDesc("CREATE");

    assertThat(createAuditLogs).hasSize(2);
    assertThat(createAuditLogs.get(0).getAction()).isEqualTo("CREATE");
    assertThat(createAuditLogs.get(1).getAction()).isEqualTo("CREATE");
  }
}
