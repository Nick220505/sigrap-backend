package com.sigrap.employee.infrastructure.adapter.out.persistence;

import com.sigrap.employee.domain.model.Attendance;
import com.sigrap.employee.domain.model.AttendanceId;
import com.sigrap.employee.domain.model.AttendanceStatus;
import com.sigrap.employee.infrastructure.adapter.out.persistence.AttendanceJpaEntity.AttendanceStatusJpa;
import com.sigrap.user.domain.model.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between domain Attendance and AttendanceJpaEntity.
 * This mapper handles the translation between the domain model (pure POJOs)
 * and the persistence model (JPA entities).
 * 
 * The mapper uses custom methods to convert between value objects (AttendanceId, UserId)
 * and their primitive representations used in the JPA entity.
 */
@Mapper(componentModel = "spring")
public interface AttendancePersistenceMapper {

    /**
     * Converts a domain Attendance to a JPA entity.
     * Maps value objects to their primitive representations.
     *
     * @param attendance the domain attendance
     * @return the JPA entity representation
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "attendanceIdToLong")
    @Mapping(target = "userId", source = "userId", qualifiedByName = "userIdToLong")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToJpa")
    AttendanceJpaEntity toJpaEntity(Attendance attendance);

    /**
     * Converts a JPA entity to a domain Attendance.
     * Maps primitive values to value objects.
     * Uses a custom implementation to handle the immutable domain entity.
     *
     * @param entity the JPA entity
     * @return the domain attendance
     */
    default Attendance toDomain(AttendanceJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        AttendanceId id = longToAttendanceId(entity.getId());
        UserId userId = longToUserId(entity.getUserId());
        AttendanceStatus status = jpaToStatus(entity.getStatus());
        
        return new Attendance(
            id,
            userId,
            entity.getDate(),
            entity.getClockInTime(),
            entity.getClockOutTime(),
            entity.getTotalHours(),
            status,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Converts an AttendanceId value object to its Long representation.
     *
     * @param id the attendance ID value object
     * @return the Long value, or null if the ID is null
     */
    @Named("attendanceIdToLong")
    default Long attendanceIdToLong(AttendanceId id) {
        return id != null ? id.value() : null;
    }

    /**
     * Converts a Long to an AttendanceId value object.
     *
     * @param id the Long value
     * @return the AttendanceId value object, or null if the Long is null
     */
    @Named("longToAttendanceId")
    default AttendanceId longToAttendanceId(Long id) {
        return id != null ? new AttendanceId(id) : null;
    }

    /**
     * Converts a UserId value object to its Long representation.
     *
     * @param userId the user ID value object
     * @return the Long value, or null if the ID is null
     */
    @Named("userIdToLong")
    default Long userIdToLong(UserId userId) {
        return userId != null ? userId.value() : null;
    }

    /**
     * Converts a Long to a UserId value object.
     *
     * @param userId the Long value
     * @return the UserId value object, or null if the Long is null
     */
    @Named("longToUserId")
    default UserId longToUserId(Long userId) {
        return userId != null ? new UserId(userId) : null;
    }

    /**
     * Converts domain AttendanceStatus to JPA AttendanceStatusJpa.
     *
     * @param status the domain status
     * @return the JPA status, or null if the status is null
     */
    @Named("statusToJpa")
    default AttendanceStatusJpa statusToJpa(AttendanceStatus status) {
        if (status == null) {
            return null;
        }
        return AttendanceStatusJpa.valueOf(status.name());
    }

    /**
     * Converts JPA AttendanceStatusJpa to domain AttendanceStatus.
     *
     * @param status the JPA status
     * @return the domain status, or null if the status is null
     */
    @Named("jpaToStatus")
    default AttendanceStatus jpaToStatus(AttendanceStatusJpa status) {
        if (status == null) {
            return null;
        }
        return AttendanceStatus.valueOf(status.name());
    }
}
