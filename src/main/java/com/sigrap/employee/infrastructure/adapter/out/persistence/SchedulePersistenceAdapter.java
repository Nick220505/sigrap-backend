package com.sigrap.employee.infrastructure.adapter.out.persistence;

import com.sigrap.employee.domain.model.Schedule;
import com.sigrap.employee.domain.model.ScheduleId;
import com.sigrap.employee.domain.port.ScheduleRepositoryPort;
import com.sigrap.user.domain.model.UserId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementation for Schedule repository port.
 * This adapter bridges the domain layer with the JPA persistence infrastructure.
 * 
 * <p>Responsibilities:
 * <ul>
 *   <li>Implements the ScheduleRepositoryPort interface from the domain layer</li>
 *   <li>Translates between domain entities and JPA entities using the mapper</li>
 *   <li>Delegates actual persistence operations to the JPA repository</li>
 *   <li>Handles the impedance mismatch between domain and persistence models</li>
 * </ul>
 * 
 * <p>This is an output adapter in hexagonal architecture terminology.
 */
@Component
public class SchedulePersistenceAdapter implements ScheduleRepositoryPort {

    private final ScheduleJpaRepository jpaRepository;
    private final SchedulePersistenceMapper mapper;

    /**
     * Constructor for dependency injection.
     *
     * @param jpaRepository the Spring Data JPA repository
     * @param mapper the MapStruct mapper for entity conversion
     */
    public SchedulePersistenceAdapter(
            ScheduleJpaRepository jpaRepository,
            SchedulePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * Saves a schedule to the database.
     * Converts the domain entity to a JPA entity, persists it, and converts back.
     *
     * @param schedule the domain schedule to save
     * @return the saved schedule with generated ID if it was new
     */
    @Override
    public Schedule save(Schedule schedule) {
        ScheduleJpaEntity entity = mapper.toJpaEntity(schedule);
        ScheduleJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    /**
     * Finds a schedule by its identifier.
     *
     * @param id the schedule identifier
     * @return an Optional containing the domain schedule if found, empty otherwise
     */
    @Override
    public Optional<Schedule> findById(ScheduleId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    /**
     * Retrieves all schedules from the database.
     *
     * @return a list of all domain schedules
     */
    @Override
    public List<Schedule> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Finds all schedules for a specific user.
     *
     * @param userId the user identifier
     * @return a list of schedules for the user
     */
    @Override
    public List<Schedule> findByUserId(UserId userId) {
        return jpaRepository.findByUserId(userId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Finds schedules for a specific day of the week.
     *
     * @param day the day of the week
     * @return a list of schedules for the specified day
     */
    @Override
    public List<Schedule> findByDay(String day) {
        return jpaRepository.findByDay(day).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Deletes a schedule by its identifier.
     *
     * @param id the schedule identifier
     */
    @Override
    public void deleteById(ScheduleId id) {
        jpaRepository.deleteById(id.value());
    }

    /**
     * Counts the total number of schedules.
     *
     * @return the total count of schedules
     */
    @Override
    public long count() {
        return jpaRepository.count();
    }

    /**
     * Saves multiple schedules at once.
     *
     * @param schedules the list of schedules to save
     * @return the list of saved schedules
     */
    @Override
    public List<Schedule> saveAll(List<Schedule> schedules) {
        List<ScheduleJpaEntity> entities = schedules.stream()
                .map(mapper::toJpaEntity)
                .toList();
        List<ScheduleJpaEntity> savedEntities = jpaRepository.saveAll(entities);
        return savedEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }
}
