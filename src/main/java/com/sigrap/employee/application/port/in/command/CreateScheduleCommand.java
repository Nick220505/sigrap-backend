package com.sigrap.employee.application.port.in.command;

import java.time.LocalTime;

/**
 * Command for creating a new schedule.
 * This is an immutable data carrier that represents the user's intent to create a schedule.
 *
 * @param userId the user identifier
 * @param day the day of the week
 * @param startTime the start time
 * @param endTime the end time
 * @param type the schedule type (optional, defaults to "Regular")
 */
public record CreateScheduleCommand(
    Long userId,
    String day,
    LocalTime startTime,
    LocalTime endTime,
    String type
) {}
