package com.sigrap.employee.infrastructure.adapter.out.persistence;

import com.sigrap.employee.domain.model.Schedule;
import com.sigrap.employee.domain.model.ScheduleId;
import com.sigrap.user.domain.model.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between domain Schedule and ScheduleJpaEntity.
 * This mapper handles the translation between the domain model (pure POJOs)
 * and the persistence model (JPA entities).
 * 
 * The mapper uses custom methods to convert between value objects (ScheduleId, UserId)
 * and their primitive representations used in the JPA entity.
 */
@Mapper(componentModel = "spring")
public interface SchedulePersistenceMapper {

    /**
     * Converts a domain Schedule to a JPA entity.
     * Maps value objects to their primitive representations.
     *
     * @param schedule the domain schedule
     * @return the JPA entity representation
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "scheduleIdToLong")
    @Mapping(target = "userId", source = "userId", qualifiedByName = "userIdToLong")
    @Mapping(target = "isActive", source = "active")
    ScheduleJpaEntity toJpaEntity(Schedule schedule);

    /**
     * Converts a JPA entity to a domain Schedule.
     * Maps primitive values to value objects.
     * Uses a custom implementation to handle the immutable domain entity.
     *
     * @param entity the JPA entity
     * @return the domain schedule
     */
    default Schedule toDomain(ScheduleJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        ScheduleId id = longToScheduleId(entity.getId());
        UserId userId = longToUserId(entity.getUserId());
        
        return new Schedule(
            id,
            userId,
            entity.getDay(),
            entity.getStartTime(),
            entity.getEndTime(),
            entity.getType(),
            entity.isActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Converts a ScheduleId value object to its Long representation.
     *
     * @param id the schedule ID value object
     * @return the Long value, or null if the ID is null
     */
    @Named("scheduleIdToLong")
    default Long scheduleIdToLong(ScheduleId id) {
        return id != null ? id.value() : null;
    }

    /**
     * Converts a Long to a ScheduleId value object.
     *
     * @param id the Long value
     * @return the ScheduleId value object, or null if the Long is null
     */
    @Named("longToScheduleId")
    default ScheduleId longToScheduleId(Long id) {
        return id != null ? new ScheduleId(id) : null;
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
}
