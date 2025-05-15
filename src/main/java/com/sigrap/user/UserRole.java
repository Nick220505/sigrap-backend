package com.sigrap.user;

/**
 * Enum representing possible user roles in the system.
 * Each role has specific permissions and access levels.
 */
public enum UserRole {
  /**
   * Administrator with full system access.
   * Administrators can manage all aspects of the system including users, configurations,
   * and sensitive operations.
   */
  ADMINISTRATOR,

  /**
   * Regular employee with limited access.
   * Employees have access to day-to-day operations but cannot
   * change system settings or manage users.
   */
  EMPLOYEE,
}
