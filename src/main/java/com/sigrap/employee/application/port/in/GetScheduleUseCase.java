package com.sigrap.employee.application.port.in;

import com.sigrap.employee.domain.model.Schedule;
import com.sigrap.employee.domain.model.ScheduleId;
import com.sigrap.user.domain.model.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Input port for retrieving schedules.
 * This interface defines the use cases for schedule retrieval operations.
 */
public interface GetScheduleUseCase {
    
    /**
     * Retrieves a schedule by its identifier.
     *
     * @param id the schedule identifier
     * @return the schedule domain entity
     * @throws IllegalArgumentException if the schedule is not found
     */
    Schedule getById(ScheduleId id);
    
    /**
     * Retrieves a schedule by its identifier, returning an Optional.
     *
     * @param id the schedule identifier
     * @return an Optional containing the schedule if found, empty otherwise
     */
    Optional<Schedule> findById(ScheduleId id);
    
    /**
     * Retrieves all schedules.
     *
     * @return a list of all schedule domain entities
     */
    List<Schedule> getAll();
    
    /**
     * Retrieves all schedules for a specific user.
     *
     * @param userId the user identifier
     * @return a list of schedules for the user
     */
    List<Schedule> getByUserId(UserId userId);
    
    /**
     * Retrieves schedules for a specific day of the week.
     *
     * @param day the day of the week
     * @return a list of schedules for the specified day
     */
    List<Schedule> getByDay(String day);
}
