package com.sigrap.employee.application.port.in.command;

import java.time.LocalDateTime;

/**
 * Command for clocking in an employee.
 * This is an immutable data carrier that represents the user's intent to clock in.
 *
 * @param userId the user identifier
 * @param clockInTime the clock-in time
 */
public record ClockInCommand(
    Long userId,
    LocalDateTime clockInTime
) {}
