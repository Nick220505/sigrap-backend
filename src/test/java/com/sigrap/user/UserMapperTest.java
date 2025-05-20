package com.sigrap.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserMapper userMapper;

  private User user;
  private UserData userData;
  private LocalDateTime now;

  @BeforeEach
  void setUp() {
    now = LocalDateTime.now();

    user = User.builder()
      .id(1L)
      .name("John Doe")
      .email("john@example.com")
      .password("encoded_password")
      .phone("1234567890")
      .role(UserRole.ADMINISTRATOR)
      .documentId("123456789")
      .lastLogin(now)
      .createdAt(now)
      .updatedAt(now)
      .build();

    userData = new UserData();
    userData.setName("Jane Smith");
    userData.setEmail("jane@example.com");
    userData.setPassword("password123");
    userData.setPhone("0987654321");
    userData.setRole(UserRole.EMPLOYEE);
    userData.setDocumentId("987654321");

    lenient()
      .when(passwordEncoder.encode(anyString()))
      .thenReturn("encoded_password");
  }

  @Test
  void toInfo_withNullUser_returnsNull() {
    UserInfo result = userMapper.toInfo(null);

    assertNull(result);
  }

  @Test
  void toInfo_withValidUser_returnsCorrectUserInfo() {
    UserInfo result = userMapper.toInfo(user);

    assertNotNull(result);
    assertEquals(user.getId(), result.getId());
    assertEquals(user.getName(), result.getName());
    assertEquals(user.getEmail(), result.getEmail());
    assertEquals(user.getPhone(), result.getPhone());
    assertEquals(user.getRole(), result.getRole());
    assertEquals(user.getDocumentId(), result.getDocumentId());
    assertEquals(user.getLastLogin(), result.getLastLogin());
    assertEquals(user.getCreatedAt(), result.getCreatedAt());
    assertEquals(user.getUpdatedAt(), result.getUpdatedAt());
    // Password should not be in UserInfo
  }

  @Test
  void toInfoList_withNullList_returnsEmptyList() {
    List<UserInfo> result = userMapper.toInfoList(null);

    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  void toInfoList_withEmptyList_returnsEmptyList() {
    List<UserInfo> result = userMapper.toInfoList(Collections.emptyList());

    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  void toInfoList_withUserList_returnsCorrectUserInfoList() {
    List<User> users = Arrays.asList(user, user);

    List<UserInfo> result = userMapper.toInfoList(users);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(user.getId(), result.get(0).getId());
    assertEquals(user.getName(), result.get(0).getName());
    assertEquals(user.getEmail(), result.get(0).getEmail());
  }

  @Test
  void toEntity_withNullUserData_returnsNull() {
    User result = userMapper.toEntity(null);

    assertNull(result);
  }

  @Test
  void toEntity_withUserData_returnsCorrectUser() {
    User result = userMapper.toEntity(userData);

    assertNotNull(result);
    assertEquals(userData.getName(), result.getName());
    assertEquals(userData.getEmail(), result.getEmail());
    assertEquals("encoded_password", result.getPassword()); // Password should be encoded
    assertEquals(userData.getPhone(), result.getPhone());
    assertEquals(userData.getRole(), result.getRole());
    assertEquals(userData.getDocumentId(), result.getDocumentId());
    verify(passwordEncoder).encode(userData.getPassword());
  }

  @Test
  void toEntity_withNullRole_setsDefaultRole() {
    userData.setRole(null);

    User result = userMapper.toEntity(userData);

    assertNotNull(result);
    assertEquals(UserRole.EMPLOYEE, result.getRole()); // Default role should be EMPLOYEE
  }

  @Test
  void updateEntityFromData_withNullUser_doesNothing() {
    userMapper.updateEntityFromData(null, userData);
    // No exception should be thrown
  }

  @Test
  void updateEntityFromData_withNullUserData_doesNothing() {
    User userToUpdate = new User();
    userMapper.updateEntityFromData(userToUpdate, null);
    // User should remain unchanged
  }

  @Test
  void updateEntityFromData_withValidInputs_updatesUserCorrectly() {
    User userToUpdate = User.builder()
      .id(1L)
      .name("Old Name")
      .email("old@example.com")
      .password("old_password")
      .phone("1111111111")
      .role(UserRole.EMPLOYEE)
      .documentId("11111111")
      .build();

    userMapper.updateEntityFromData(userToUpdate, userData);

    assertEquals(userData.getName(), userToUpdate.getName());
    assertEquals(userData.getEmail(), userToUpdate.getEmail());
    assertEquals("encoded_password", userToUpdate.getPassword());
    assertEquals(userData.getPhone(), userToUpdate.getPhone());
    assertEquals(userData.getRole(), userToUpdate.getRole());
    assertEquals(userData.getDocumentId(), userToUpdate.getDocumentId());
    verify(passwordEncoder).encode(userData.getPassword());
  }

  @Test
  void updateEntityFromData_withNullFields_preservesExistingValues() {
    User userToUpdate = User.builder()
      .id(1L)
      .name("Old Name")
      .email("old@example.com")
      .password("old_password")
      .phone("1111111111")
      .role(UserRole.EMPLOYEE)
      .documentId("11111111")
      .build();

    UserData partialData = new UserData();
    partialData.setName("New Name");
    // All other fields are null

    userMapper.updateEntityFromData(userToUpdate, partialData);

    assertEquals("New Name", userToUpdate.getName()); // Only name should be updated
    assertEquals("old@example.com", userToUpdate.getEmail());
    assertEquals("old_password", userToUpdate.getPassword());
    assertEquals("1111111111", userToUpdate.getPhone());
    assertEquals(UserRole.EMPLOYEE, userToUpdate.getRole());
    assertEquals("11111111", userToUpdate.getDocumentId());
  }

  @Test
  void updateEntityFromData_withEmptyPassword_preservesExistingPassword() {
    User userToUpdate = User.builder()
      .id(1L)
      .name("Old Name")
      .password("old_password")
      .build();

    UserData dataWithEmptyPassword = new UserData();
    dataWithEmptyPassword.setName("New Name");
    dataWithEmptyPassword.setPassword(""); // Empty password

    userMapper.updateEntityFromData(userToUpdate, dataWithEmptyPassword);

    assertEquals("New Name", userToUpdate.getName());
    assertEquals("old_password", userToUpdate.getPassword()); // Password should remain unchanged
  }
}
