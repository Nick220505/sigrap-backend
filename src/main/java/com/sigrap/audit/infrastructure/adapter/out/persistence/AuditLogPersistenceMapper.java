package com.sigrap.audit.infrastructure.adapter.out.persistence;

import com.sigrap.audit.domain.model.AuditLog;
import com.sigrap.audit.domain.model.AuditLogId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between domain AuditLog and JPA entity.
 * 
 * <p>This mapper handles the translation between the domain model and the
 * persistence model, including conversion of value objects like {@link AuditLogId}.</p>
 */
@Mapper(componentModel = "spring")
public interface AuditLogPersistenceMapper {
    
    /**
     * Converts a domain AuditLog to a JPA entity.
     * 
     * @param auditLog The domain audit log
     * @return The JPA entity
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "auditLogIdToLong")
    @Mapping(target = "entityType", source = "entityType", qualifiedByName = "entityTypeToString")
    AuditLogJpaEntity toJpaEntity(AuditLog auditLog);
    
    /**
     * Converts a JPA entity to a domain AuditLog.
     * Uses a default method to properly construct the domain entity with all required fields.
     * 
     * @param entity The JPA entity
     * @return The domain audit log
     */
    default AuditLog toDomain(AuditLogJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        AuditLogId id = longToAuditLogId(entity.getId());
        
        return new AuditLog(
            id,
            entity.getUsername(),
            entity.getAction(),
            stringToEntityType(entity.getEntityType()),
            entity.getEntityId(),
            entity.getTimestamp(),
            entity.getSourceIp(),
            entity.getUserAgent(),
            entity.getDetails(),
            entity.getStatus(),
            entity.getDurationMs()
        );
    }
    
    /**
     * Converts an AuditLogId to a Long for JPA persistence.
     * 
     * @param id The audit log ID
     * @return The Long value, or null if id is null
     */
    @Named("auditLogIdToLong")
    default Long auditLogIdToLong(AuditLogId id) {
        return id != null ? id.value() : null;
    }
    
    /**
     * Converts a Long to an AuditLogId.
     * 
     * @param id The Long value
     * @return The AuditLogId, or null if id is null
     */
    @Named("longToAuditLogId")
    default AuditLogId longToAuditLogId(Long id) {
        return id != null ? new AuditLogId(id) : null;
    }
    
    /**
     * Converts an EntityType to a String for JPA persistence.
     * 
     * @param entityType The entity type
     * @return The String value, or null if entityType is null
     */
    @Named("entityTypeToString")
    default String entityTypeToString(com.sigrap.audit.domain.model.EntityType entityType) {
        return entityType != null ? entityType.value() : null;
    }
    
    /**
     * Converts a String to an EntityType.
     * 
     * @param value The String value
     * @return The EntityType, or null if value is null
     */
    @Named("stringToEntityType")
    default com.sigrap.audit.domain.model.EntityType stringToEntityType(String value) {
        return value != null ? new com.sigrap.audit.domain.model.EntityType(value) : null;
    }
}
