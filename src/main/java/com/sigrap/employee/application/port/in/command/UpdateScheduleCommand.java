package com.sigrap.employee.application.port.in.command;

import java.time.LocalTime;

/**
 * Command for updating an existing schedule.
 * This is an immutable data carrier that represents the user's intent to update a schedule.
 *
 * @param day the day of the week (optional)
 * @param startTime the start time (optional)
 * @param endTime the end time (optional)
 * @param type the schedule type (optional)
 * @param isActive whether the schedule is active (optional)
 */
public record UpdateScheduleCommand(
    String day,
    LocalTime startTime,
    LocalTime endTime,
    String type,
    Boolean isActive
) {}
