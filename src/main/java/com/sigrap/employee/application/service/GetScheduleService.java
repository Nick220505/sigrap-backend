package com.sigrap.employee.application.service;

import com.sigrap.employee.application.port.in.GetScheduleUseCase;
import com.sigrap.employee.domain.model.Schedule;
import com.sigrap.employee.domain.model.ScheduleId;
import com.sigrap.employee.domain.port.ScheduleRepositoryPort;
import com.sigrap.user.domain.model.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementing the GetScheduleUseCase.
 * This service provides read operations for schedule records.
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Implements input port interface</li>
 *   <li>Uses output port (repository) for data retrieval</li>
 *   <li>Read-only operations with @Transactional(readOnly = true)</li>
 *   <li>Contains no infrastructure concerns</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
public class GetScheduleService implements GetScheduleUseCase {
    
    private final ScheduleRepositoryPort scheduleRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param scheduleRepository the repository port for schedule retrieval
     */
    public GetScheduleService(ScheduleRepositoryPort scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }
    
    /**
     * Retrieves a schedule by its identifier.
     *
     * @param id the schedule identifier
     * @return the schedule domain entity
     * @throws IllegalArgumentException if the schedule is not found
     */
    @Override
    public Schedule getById(ScheduleId id) {
        return scheduleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Schedule with ID " + id.value() + " not found"
            ));
    }
    
    /**
     * Retrieves a schedule by its identifier, returning an Optional.
     *
     * @param id the schedule identifier
     * @return an Optional containing the schedule if found, empty otherwise
     */
    @Override
    public Optional<Schedule> findById(ScheduleId id) {
        return scheduleRepository.findById(id);
    }
    
    /**
     * Retrieves all schedules.
     *
     * @return a list of all schedule domain entities
     */
    @Override
    public List<Schedule> getAll() {
        return scheduleRepository.findAll();
    }
    
    /**
     * Retrieves all schedules for a specific user.
     *
     * @param userId the user identifier
     * @return a list of schedules for the user
     */
    @Override
    public List<Schedule> getByUserId(UserId userId) {
        return scheduleRepository.findByUserId(userId);
    }
    
    /**
     * Retrieves schedules for a specific day of the week.
     *
     * @param day the day of the week
     * @return a list of schedules for the specified day
     */
    @Override
    public List<Schedule> getByDay(String day) {
        return scheduleRepository.findByDay(day);
    }
}
