package com.sigrap.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserService userService;

  private User user;
  private UserInfo userInfo;
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
      .build();

    userInfo = UserInfo.builder()
      .id(1L)
      .name("John Doe")
      .email("john@example.com")
      .phone("1234567890")
      .role(UserRole.ADMINISTRATOR)
      .documentId("123456789")
      .lastLogin(now)
      .build();

    userData = new UserData();
    userData.setName("Jane Smith");
    userData.setEmail("jane@example.com");
    userData.setPassword("password123");
    userData.setPhone("0987654321");
    userData.setRole(UserRole.EMPLOYEE);
    userData.setDocumentId("987654321");
  }

  @Test
  void loadUserByUsername_withValidEmail_returnsUserDetails() {
    when(userRepository.findByEmail("john@example.com")).thenReturn(
      Optional.of(user)
    );

    UserDetails result = userService.loadUserByUsername("john@example.com");

    assertNotNull(result);
    assertEquals("john@example.com", result.getUsername());
    assertEquals("encoded_password", result.getPassword());
  }

  @Test
  void loadUserByUsername_withInvalidEmail_throwsException() {
    when(userRepository.findByEmail("invalid@example.com")).thenReturn(
      Optional.empty()
    );

    assertThrows(UsernameNotFoundException.class, () -> {
      userService.loadUserByUsername("invalid@example.com");
    });
  }

  @Test
  void findAll_returnsAllUsers() {
    List<User> users = Arrays.asList(user);
    when(userRepository.findAll()).thenReturn(users);
    when(userMapper.toInfoList(users)).thenReturn(Arrays.asList(userInfo));

    List<UserInfo> result = userService.findAll();

    assertEquals(1, result.size());
    assertEquals(userInfo, result.get(0));
  }

  @Test
  void findById_withValidId_returnsUserInfo() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userMapper.toInfo(user)).thenReturn(userInfo);

    UserInfo result = userService.findById(1L);

    assertEquals(userInfo, result);
  }

  @Test
  void findById_withInvalidId_throwsException() {
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      userService.findById(999L);
    });
  }

  @Test
  void findByEmail_withValidEmail_returnsUserInfo() {
    when(userRepository.findByEmail("john@example.com")).thenReturn(
      Optional.of(user)
    );
    when(userMapper.toInfo(user)).thenReturn(userInfo);

    UserInfo result = userService.findByEmail("john@example.com");

    assertEquals(userInfo, result);
  }

  @Test
  void findByEmail_withInvalidEmail_throwsException() {
    when(userRepository.findByEmail("invalid@example.com")).thenReturn(
      Optional.empty()
    );

    assertThrows(EntityNotFoundException.class, () -> {
      userService.findByEmail("invalid@example.com");
    });
  }

  @Test
  void create_withValidData_createsUser() {
    when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
    when(userRepository.existsByDocumentId("987654321")).thenReturn(false);
    when(userMapper.toEntity(userData)).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toInfo(user)).thenReturn(userInfo);

    UserInfo result = userService.create(userData);

    assertEquals(userInfo, result);
    verify(userRepository).save(user);
  }

  @Test
  void create_withExistingEmail_throwsException() {
    when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> {
      userService.create(userData);
    });

    verify(userRepository, never()).save(any());
  }

  @Test
  void create_withExistingDocumentId_throwsException() {
    when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
    when(userRepository.existsByDocumentId("987654321")).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> {
      userService.create(userData);
    });

    verify(userRepository, never()).save(any());
  }

  @Test
  void update_withValidIdAndData_updatesUser() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.existsByDocumentId(anyString())).thenReturn(false);
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toInfo(user)).thenReturn(userInfo);

    UserInfo result = userService.update(1L, userData);

    assertEquals(userInfo, result);
    verify(userMapper).updateEntityFromData(user, userData);
    verify(userRepository).save(user);
  }

  @Test
  void update_withInvalidId_throwsException() {
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      userService.update(999L, userData);
    });

    verify(userRepository, never()).save(any());
  }

  @Test
  void update_withExistingEmail_throwsException() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> {
      userService.update(1L, userData);
    });

    verify(userRepository, never()).save(any());
  }

  @Test
  void update_withExistingDocumentId_throwsException() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.existsByDocumentId("987654321")).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> {
      userService.update(1L, userData);
    });

    verify(userRepository, never()).save(any());
  }

  @Test
  void update_withSameEmail_updatesUser() {
    user.setEmail("jane@example.com");
    userData.setEmail("jane@example.com");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toInfo(user)).thenReturn(userInfo);

    UserInfo result = userService.update(1L, userData);

    assertEquals(userInfo, result);
    verify(userMapper).updateEntityFromData(user, userData);
    verify(userRepository).save(user);
  }

  @Test
  void update_withSameDocumentId_updatesUser() {
    user.setDocumentId("987654321");
    userData.setDocumentId("987654321");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toInfo(user)).thenReturn(userInfo);

    UserInfo result = userService.update(1L, userData);

    assertEquals(userInfo, result);
    verify(userMapper).updateEntityFromData(user, userData);
    verify(userRepository).save(user);
  }

  @Test
  void delete_withValidId_deletesUser() {
    when(userRepository.existsById(1L)).thenReturn(true);

    userService.delete(1L);

    verify(userRepository).deleteById(1L);
  }

  @Test
  void delete_withInvalidId_throwsException() {
    when(userRepository.existsById(999L)).thenReturn(false);

    assertThrows(EntityNotFoundException.class, () -> {
      userService.delete(999L);
    });

    verify(userRepository, never()).deleteById(anyLong());
  }

  @Test
  void deleteAllById_withValidIds_deletesUsers() {
    List<Long> ids = Arrays.asList(1L, 2L, 3L);
    when(userRepository.existsById(anyLong())).thenReturn(true);

    userService.deleteAllById(ids);

    verify(userRepository).deleteAllById(ids);
  }

  @Test
  void deleteAllById_withInvalidId_throwsException() {
    List<Long> ids = Arrays.asList(1L, 2L, 999L);
    when(userRepository.existsById(1L)).thenReturn(true);
    when(userRepository.existsById(2L)).thenReturn(true);
    when(userRepository.existsById(999L)).thenReturn(false);

    assertThrows(EntityNotFoundException.class, () -> {
      userService.deleteAllById(ids);
    });

    verify(userRepository, never()).deleteAllById(any());
  }

  @Test
  void updateProfile_delegatesToUpdate() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.existsByDocumentId(anyString())).thenReturn(false);
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toInfo(user)).thenReturn(userInfo);

    UserInfo result = userService.updateProfile(1L, userData);

    assertEquals(userInfo, result);
  }

  @Test
  void changePassword_withValidData_changesPassword() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toInfo(user)).thenReturn(userInfo);

    UserData passwordChangeData = new UserData();
    passwordChangeData.setPassword("new_password");

    UserInfo result = userService.changePassword(
      1L,
      "current_password",
      "new_password"
    );

    assertEquals(userInfo, result);
    verify(userRepository).save(user);
  }

  @Test
  void changePassword_withInvalidId_throwsException() {
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      userService.changePassword(999L, "current_password", "new_password");
    });
  }

  @Test
  void registerSuccessfulLogin_updatesLastLogin() {
    when(userRepository.findByEmail("john@example.com")).thenReturn(
      Optional.of(user)
    );

    userService.registerSuccessfulLogin("john@example.com");

    assertNotNull(user.getLastLogin());
    verify(userRepository).save(user);
  }

  @Test
  void registerSuccessfulLogin_withInvalidEmail_doesNothing() {
    when(userRepository.findByEmail("invalid@example.com")).thenReturn(
      Optional.empty()
    );

    userService.registerSuccessfulLogin("invalid@example.com");

    verify(userRepository, never()).save(any());
  }
}
