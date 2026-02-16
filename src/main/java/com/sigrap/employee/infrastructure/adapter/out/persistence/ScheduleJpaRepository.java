package com.sigrap.employee.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository interface for ScheduleJpaEntity.
 * Provides database operations for schedule persistence in the hexagonal architecture.
 * This is an output adapter component that will be used by SchedulePersistenceAdapter.
 */
@Repository
public interface ScheduleJpaRepository extends JpaRepository<ScheduleJpaEntity, Long> {
    
    /**
     * Finds all schedules for a specific user.
     *
     * @param userId the user identifier
     * @return a list of schedules for the user
     */
    List<ScheduleJpaEntity> findByUserId(Long userId);
    
    /**
     * Finds schedules for a specific day of the week.
     *
     * @param day the day of the week
     * @return a list of schedules for the specified day
     */
    List<ScheduleJpaEntity> findByDay(String day);
}
