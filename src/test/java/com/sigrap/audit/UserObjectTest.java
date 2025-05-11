package com.sigrap.audit;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Test utility class representing a simple user object.
 * Used in audit log tests to simulate user data being logged.
 */
@Data
@AllArgsConstructor
public class UserObjectTest {

  private Long id;
  private String name;
}
