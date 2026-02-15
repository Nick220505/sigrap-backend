package com.sigrap.audit.infrastructure.adapter.out.persistence;

import com.sigrap.audit.domain.model.AuditAction;
import com.sigrap.audit.domain.model.AuditStatus;
import com.sigrap.audit.domain.model.EntityType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * JPA entity for audit log persistence.
 * 
 * <p>This entity is separate from the domain {@link com.sigrap.audit.domain.model.AuditLog}
 * to keep the domain layer free of JPA annotations and framework dependencies.</p>
 * 
 * <p>Mapping between this entity and the domain model is handled by
 * {@link AuditLogPersistenceMapper}.</p>
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_username", columnList = "username"),
    @Index(name = "idx_audit_entity", columnList = "entity_type, entity_id"),
    @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
    @Index(name = "idx_audit_action", columnList = "action")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String username;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AuditAction action;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 50)
    private EntityType entityType;
    
    @Column(name = "entity_id", length = 100)
    private String entityId;
    
    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime timestamp;
    
    @Column(name = "source_ip", length = 45)
    private String sourceIp;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuditStatus status;
    
    @Column(name = "duration_ms")
    private Long durationMs;
}
