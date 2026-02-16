package com.sigrap.employee.domain.port;

import com.sigrap.employee.domain.model.Schedule;
import com.sigrap.employee.domain.model.ScheduleId;
import com.sigrap.user.domain.model.UserId;
import java.util.List;
import java.util.Optional;

/**
 * Repository port interface for Schedule domain entity.
 * Defines the contract for persistence operations without exposing implementation details.
 * This interface is defined in the domain layer and implemented in the infrastructure layer.
 */
public interface ScheduleRepositoryPort {
    
    /**
     * Saves a schedule (create or update).
     *
     * @param schedule the schedule to save
     * @return the saved schedule with generated ID if new
     */
    Schedule save(Schedule schedule);
    
    /**
     * Finds a schedule by its identifier.
     *
     * @param id the schedule identifier
     * @return an Optional containing the schedule if found, empty otherwise
     */
    Optional<Schedule> findById(ScheduleId id);
    
    /**
     * Retrieves all schedules.
     *
     * @return a list of all schedules
     */
    List<Schedule> findAll();
    
    /**
     * Finds all schedules for a specific user.
     *
     * @param userId the user identifier
     * @return a list of schedules for the user
     */
    List<Schedule> findByUserId(UserId userId);
    
    /**
     * Finds schedules for a specific day of the week.
     *
     * @param day the day of the week
     * @return a list of schedules for the specified day
     */
    List<Schedule> findByDay(String day);
    
    /**
     * Deletes a schedule by its identifier.
     *
     * @param id the schedule identifier
     */
    void deleteById(ScheduleId id);
    
    /**
     * Counts the total number of schedules.
     *
     * @return the total count of schedules
     */
    long count();
    
    /**
     * Saves multiple schedules at once.
     * Useful for batch operations.
     *
     * @param schedules the list of schedules to save
     * @return the list of saved schedules
     */
    List<Schedule> saveAll(List<Schedule> schedules);
}
