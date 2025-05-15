package com.sigrap.user;

/**
 * Enum representing possible user account statuses.
 * The status determines whether a user can log in and access the system.
 */
public enum UserStatus {
  /**
   * User account is active and can be used for login.
   * Active accounts have normal access to the system according to their role.
   */
  ACTIVE,

  /**
   * User account is locked due to security concerns or policy violations.
   * Locked accounts cannot be used for login but still exist in the system.
   */
  LOCKED,

  /**
   * User account is inactive and cannot be used for login.
   * Inactive accounts are typically for users who are temporarily not using the system.
   */
  INACTIVE,
}
