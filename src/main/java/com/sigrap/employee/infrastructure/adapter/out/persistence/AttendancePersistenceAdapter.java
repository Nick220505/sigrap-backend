package com.sigrap.employee.infrastructure.adapter.out.persistence;

import com.sigrap.employee.domain.model.Attendance;
import com.sigrap.employee.domain.model.AttendanceId;
import com.sigrap.employee.domain.port.AttendanceRepositoryPort;
import com.sigrap.user.domain.model.UserId;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementation for Attendance repository port.
 * This adapter bridges the domain layer with the JPA persistence infrastructure.
 * 
 * <p>Responsibilities:
 * <ul>
 *   <li>Implements the AttendanceRepositoryPort interface from the domain layer</li>
 *   <li>Translates between domain entities and JPA entities using the mapper</li>
 *   <li>Delegates actual persistence operations to the JPA repository</li>
 *   <li>Handles the impedance mismatch between domain and persistence models</li>
 * </ul>
 * 
 * <p>This is an output adapter in hexagonal architecture terminology.
 */
@Component
public class AttendancePersistenceAdapter implements AttendanceRepositoryPort {

    private final AttendanceJpaRepository jpaRepository;
    private final AttendancePersistenceMapper mapper;

    /**
     * Constructor for dependency injection.
     *
     * @param jpaRepository the Spring Data JPA repository
     * @param mapper the MapStruct mapper for entity conversion
     */
    public AttendancePersistenceAdapter(
            AttendanceJpaRepository jpaRepository,
            AttendancePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * Saves an attendance record to the database.
     * Converts the domain entity to a JPA entity, persists it, and converts back.
     *
     * @param attendance the domain attendance to save
     * @return the saved attendance with generated ID if it was new
     */
    @Override
    public Attendance save(Attendance attendance) {
        AttendanceJpaEntity entity = mapper.toJpaEntity(attendance);
        AttendanceJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    /**
     * Finds an attendance record by its identifier.
     *
     * @param id the attendance identifier
     * @return an Optional containing the domain attendance if found, empty otherwise
     */
    @Override
    public Optional<Attendance> findById(AttendanceId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    /**
     * Retrieves all attendance records from the database.
     *
     * @return a list of all domain attendance records
     */
    @Override
    public List<Attendance> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Finds all attendance records for a specific user.
     *
     * @param userId the user identifier
     * @return a list of attendance records for the user
     */
    @Override
    public List<Attendance> findByUserId(UserId userId) {
        return jpaRepository.findByUserId(userId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Finds attendance records within a date range.
     *
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a list of attendance records within the date range
     */
    @Override
    public List<Attendance> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaRepository.findByDateBetween(startDate, endDate).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Deletes an attendance record by its identifier.
     *
     * @param id the attendance identifier
     */
    @Override
    public void deleteById(AttendanceId id) {
        jpaRepository.deleteById(id.value());
    }

    /**
     * Counts the total number of attendance records.
     *
     * @return the total count of attendance records
     */
    @Override
    public long count() {
        return jpaRepository.count();
    }

    /**
     * Saves multiple attendance records at once.
     *
     * @param attendances the list of attendances to save
     * @return the list of saved attendances
     */
    @Override
    public List<Attendance> saveAll(List<Attendance> attendances) {
        List<AttendanceJpaEntity> entities = attendances.stream()
                .map(mapper::toJpaEntity)
                .toList();
        List<AttendanceJpaEntity> savedEntities = jpaRepository.saveAll(entities);
        return savedEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }
}
