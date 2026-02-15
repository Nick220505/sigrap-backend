package com.sigrap.audit.infrastructure.adapter;

import com.sigrap.audit.application.port.in.CreateAuditLogUseCase;
import com.sigrap.audit.domain.event.*;
import com.sigrap.audit.domain.model.*;
import com.sigrap.audit.domain.port.AuditLogRepositoryPort;
import com.sigrap.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * End-to-end integration tests for the Audit module.
 * Tests the complete flow from domain event publishing to audit log creation.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import(TestSecurityConfig.class)
class AuditIntegrationTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private AuditLogRepositoryPort auditLogRepository;

    @Autowired
    private CreateAuditLogUseCase createAuditLogUseCase;

    @Test
    void whenEntityCreatedEventPublished_thenAuditLogShouldBeCreated() {
        // Given
        EntityCreatedEvent event = new EntityCreatedEvent(
            EntityType.CATEGORY,
            "123",
            "testuser",
            LocalDateTime.now(),
            "192.168.1.1",
            "Mozilla/5.0",
            "Created category",
            100L
        );

        // When
        eventPublisher.publishEvent(event);

        // Then - wait for async event processing
        await().atMost(5, SECONDS).untilAsserted(() -> {
            List<AuditLog> logs = auditLogRepository.findByEntity(EntityType.CATEGORY, "123");
            assertThat(logs).isNotEmpty();
            
            AuditLog log = logs.get(0);
            assertThat(log.getUsername()).isEqualTo("testuser");
            assertThat(log.getAction()).isEqualTo(AuditAction.CREATE);
            assertThat(log.getEntityType()).isEqualTo(EntityType.CATEGORY);
            assertThat(log.getEntityId()).isEqualTo("123");
        });
    }

    @Test
    void whenEntityUpdatedEventPublished_thenAuditLogShouldBeCreated() {
        // Given
        EntityUpdatedEvent event = new EntityUpdatedEvent(
            EntityType.PRODUCT,
            "456",
            "editor",
            LocalDateTime.now(),
            "192.168.1.2",
            "Chrome/91.0",
            "Updated product name",
            150L
        );

        // When
        eventPublisher.publishEvent(event);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            List<AuditLog> logs = auditLogRepository.findByEntity(EntityType.PRODUCT, "456");
            assertThat(logs).isNotEmpty();
            
            AuditLog log = logs.get(0);
            assertThat(log.getUsername()).isEqualTo("editor");
            assertThat(log.getAction()).isEqualTo(AuditAction.UPDATE);
        });
    }

    @Test
    void whenEntityDeletedEventPublished_thenAuditLogShouldBeCreated() {
        // Given
        EntityDeletedEvent event = new EntityDeletedEvent(
            EntityType.CUSTOMER,
            "789",
            "admin",
            LocalDateTime.now(),
            "192.168.1.3",
            "Firefox/89.0",
            "Deleted customer",
            50L
        );

        // When
        eventPublisher.publishEvent(event);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            List<AuditLog> logs = auditLogRepository.findByEntity(EntityType.CUSTOMER, "789");
            assertThat(logs).isNotEmpty();
            
            AuditLog log = logs.get(0);
            assertThat(log.getUsername()).isEqualTo("admin");
            assertThat(log.getAction()).isEqualTo(AuditAction.DELETE);
        });
    }

    @Test
    void whenMultipleEventsPublished_thenAllAuditLogsShouldBeCreated() {
        // Given
        EntityCreatedEvent createEvent = new EntityCreatedEvent(
            EntityType.SALE,
            "100",
            "testuser",
            LocalDateTime.now(),
            "192.168.1.1",
            "Mozilla/5.0",
            null,
            null
        );
        
        EntityUpdatedEvent updateEvent = new EntityUpdatedEvent(
            EntityType.SALE,
            "100",
            "testuser",
            LocalDateTime.now(),
            "192.168.1.1",
            "Mozilla/5.0",
            "Updated sale",
            null
        );

        // When
        eventPublisher.publishEvent(createEvent);
        eventPublisher.publishEvent(updateEvent);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            List<AuditLog> logs = auditLogRepository.findByEntity(EntityType.SALE, "100");
            assertThat(logs).hasSize(2);
            
            assertThat(logs).extracting(AuditLog::getAction)
                .containsExactlyInAnyOrder(AuditAction.CREATE, AuditAction.UPDATE);
        });
    }

    @Test
    void whenEventsPublished_thenCanQueryByUsername() {
        // Given
        EntityCreatedEvent event1 = new EntityCreatedEvent(
            EntityType.CATEGORY,
            "1",
            "queryuser",
            LocalDateTime.now(),
            null,
            null,
            null,
            null
        );
        
        EntityCreatedEvent event2 = new EntityCreatedEvent(
            EntityType.PRODUCT,
            "2",
            "queryuser",
            LocalDateTime.now(),
            null,
            null,
            null,
            null
        );

        // When
        eventPublisher.publishEvent(event1);
        eventPublisher.publishEvent(event2);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            List<AuditLog> logs = auditLogRepository.findByUsername("queryuser");
            assertThat(logs).hasSizeGreaterThanOrEqualTo(2);
            assertThat(logs).allMatch(log -> log.getUsername().equals("queryuser"));
        });
    }

    @Test
    void whenEventsPublished_thenCanQueryByTimeRange() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(1);
        
        EntityCreatedEvent event = new EntityCreatedEvent(
            EntityType.SUPPLIER,
            "500",
            "timeuser",
            LocalDateTime.now(),
            null,
            null,
            null,
            null
        );

        // When
        eventPublisher.publishEvent(event);

        // Then
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(1);
        
        await().atMost(5, SECONDS).untilAsserted(() -> {
            List<AuditLog> logs = auditLogRepository.findByTimestampBetween(startTime, endTime);
            assertThat(logs).isNotEmpty();
            assertThat(logs).anyMatch(log -> 
                log.getEntityType().equals(EntityType.SUPPLIER) && 
                log.getEntityId().equals("500")
            );
        });
    }

    @Test
    void whenCreateAuditLogUseCaseInvoked_thenAuditLogShouldBeCreated() {
        // Given
        EntityViewedEvent event = new EntityViewedEvent(
            EntityType.PRODUCT,
            "999",
            "viewer",
            LocalDateTime.now(),
            "192.168.1.10",
            "Safari/14.0",
            null
        );

        // When
        AuditLog result = createAuditLogUseCase.createFromEvent(event);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUsername()).isEqualTo("viewer");
        assertThat(result.getAction()).isEqualTo(AuditAction.VIEW);
        assertThat(result.getEntityType()).isEqualTo(EntityType.PRODUCT);
        assertThat(result.getEntityId()).isEqualTo("999");
    }
}
