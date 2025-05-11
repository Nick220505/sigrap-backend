package com.sigrap.user;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.role.RoleInfo;
import com.sigrap.user.User.UserStatus;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @Mock
  private UserService userService;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private Authentication authentication;

  private UserInfo userInfo;
  private UserData userData;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    mockMvc = standaloneSetup(new UserController(userService)).build();

    RoleInfo role = RoleInfo.builder()
      .id(1L)
      .name("ADMIN")
      .description("Administrator role")
      .build();

    userInfo = UserInfo.builder()
      .id(1L)
      .name("Test User")
      .email("test@example.com")
      .phone("1234567890")
      .status(UserStatus.ACTIVE)
      .roles(Set.of(role))
      .build();

    userData = UserData.builder()
      .name("Test User")
      .email("test@example.com")
      .password("password123")
      .phone("1234567890")
      .status(UserStatus.ACTIVE)
      .roleIds(Set.of(1L))
      .build();

    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  @WithMockUser(username = "admin@example.com", roles = { "ADMIN" })
  void findAll_shouldReturnAllUsers() throws Exception {
    List<UserInfo> users = List.of(userInfo);

    when(userService.findAll()).thenReturn(users);

    mockMvc
      .perform(get("/api/users").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].id").value(1))
      .andExpect(jsonPath("$[0].name").value("Test User"))
      .andExpect(jsonPath("$[0].email").value("test@example.com"))
      .andExpect(jsonPath("$[0].roles", hasSize(1)))
      .andExpect(jsonPath("$[0].roles[0].name").value("ADMIN"));
  }

  @Test
  @WithMockUser(username = "admin@example.com", roles = { "ADMIN" })
  void findById_shouldReturnUser() throws Exception {
    when(userService.findById(1L)).thenReturn(userInfo);

    mockMvc
      .perform(get("/api/users/1").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.name").value("Test User"))
      .andExpect(jsonPath("$.email").value("test@example.com"))
      .andExpect(jsonPath("$.roles", hasSize(1)))
      .andExpect(jsonPath("$.roles[0].name").value("ADMIN"));
  }

  @Test
  @WithMockUser(username = "admin@example.com", roles = { "ADMIN" })
  void create_shouldCreateUser() throws Exception {
    when(userService.create(any(UserData.class))).thenReturn(userInfo);

    mockMvc
      .perform(
        post("/api/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(userData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.name").value("Test User"))
      .andExpect(jsonPath("$.email").value("test@example.com"))
      .andExpect(jsonPath("$.roles", hasSize(1)))
      .andExpect(jsonPath("$.roles[0].name").value("ADMIN"));
  }

  @Test
  @WithMockUser(username = "admin@example.com", roles = { "ADMIN" })
  void update_shouldUpdateUser() throws Exception {
    when(userService.update(eq(1L), any(UserData.class))).thenReturn(userInfo);

    mockMvc
      .perform(
        put("/api/users/1")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(userData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.name").value("Test User"))
      .andExpect(jsonPath("$.email").value("test@example.com"));
  }

  @Test
  @WithMockUser(username = "test@example.com", roles = { "USER" })
  void getProfile_shouldReturnCurrentUser() throws Exception {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("test@example.com");
    when(userService.findByEmail("test@example.com")).thenReturn(userInfo);

    mockMvc
      .perform(get("/api/users/me").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.name").value("Test User"))
      .andExpect(jsonPath("$.email").value("test@example.com"));
  }

  @Test
  @WithMockUser(username = "test@example.com", roles = { "USER" })
  void updateProfile_shouldUpdateCurrentUser() throws Exception {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("test@example.com");
    when(userService.findByEmail("test@example.com")).thenReturn(userInfo);
    when(userService.updateProfile(eq(1L), any(UserData.class))).thenReturn(
      userInfo
    );

    mockMvc
      .perform(
        put("/api/users/me")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(userData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.name").value("Test User"))
      .andExpect(jsonPath("$.email").value("test@example.com"));
  }

  @Test
  @WithMockUser(username = "test@example.com", roles = { "USER" })
  void changePassword_shouldChangeCurrentUserPassword() throws Exception {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("test@example.com");
    when(userService.findByEmail("test@example.com")).thenReturn(userInfo);
    when(
      userService.changePassword(eq(1L), eq("oldPassword"), eq("newPassword"))
    ).thenReturn(userInfo);

    PasswordChangeRequest passwordChange = new PasswordChangeRequest(
      "oldPassword",
      "newPassword"
    );

    mockMvc
      .perform(
        post("/api/users/me/change-password")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(passwordChange))
      )
      .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(username = "admin@example.com", roles = { "ADMIN" })
  void resetPassword_shouldResetUserPassword() throws Exception {
    when(userService.resetPassword(anyString(), anyString())).thenReturn(
      userInfo
    );

    PasswordResetRequest passwordReset = new PasswordResetRequest(
      "test@example.com"
    );

    mockMvc
      .perform(
        post("/api/users/reset-password")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(passwordReset))
      )
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "admin@example.com", roles = { "ADMIN" })
  void lockAccount_shouldLockUserAccount() throws Exception {
    when(userService.lockAccount(1L)).thenReturn(userInfo);

    mockMvc
      .perform(put("/api/users/1/lock").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "admin@example.com", roles = { "USER" })
  void unlockAccount_shouldUnlockUserAccount() throws Exception {
    when(userService.unlockAccount(1L)).thenReturn(userInfo);

    mockMvc
      .perform(
        put("/api/users/1/unlock").contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "admin@example.com", roles = { "ADMIN" })
  void delete_shouldDeleteUser() throws Exception {
    doNothing().when(userService).delete(1L);

    mockMvc
      .perform(delete("/api/users/1").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());
  }
}
