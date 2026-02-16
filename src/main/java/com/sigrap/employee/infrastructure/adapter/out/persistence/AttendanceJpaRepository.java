package com.sigrap.employee.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository interface for AttendanceJpaEntity.
 * Provides database operations for attendance persistence in the hexagonal architecture.
 * This is an output adapter component that will be used by AttendancePersistenceAdapter.
 */
@Repository
public interface AttendanceJpaRepository extends JpaRepository<AttendanceJpaEntity, Long> {
    
    /**
     * Finds all attendance records for a specific user.
     *
     * @param userId the user identifier
     * @return a list of attendance records for the user
     */
    List<AttendanceJpaEntity> findByUserId(Long userId);
    
    /**
     * Finds attendance records within a date range.
     *
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a list of attendance records within the date range
     */
    List<AttendanceJpaEntity> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
