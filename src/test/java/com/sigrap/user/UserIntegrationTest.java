package com.sigrap.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class UserIntegrationTest {

  private MockMvc mockMvc;

  @Mock
  private UserService userService;

  @InjectMocks
  private UserController userController;

  private ObjectMapper objectMapper = new ObjectMapper();
  private UserInfo testUserInfo;

  @BeforeEach
  void setUp() {
    mockMvc = standaloneSetup(userController).build();

    testUserInfo = UserInfo.builder()
      .id(1L)
      .name("Test User")
      .email("test@example.com")
      .phone("1234567890")
      .documentId("DOC123IT")
      .role(UserRole.ADMINISTRATOR)
      .build();
  }

  @Test
  void findAll_shouldReturnAllUsers() throws Exception {
    when(userService.findAll()).thenReturn(List.of(testUserInfo));

    mockMvc
      .perform(get("/api/users"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testUserInfo.getId()))
      .andExpect(jsonPath("$[0].name").value("Test User"))
      .andExpect(jsonPath("$[0].email").value("test@example.com"))
      .andExpect(jsonPath("$[0].documentId").value("DOC123IT"));
  }

  @Test
  void findById_shouldReturnUser() throws Exception {
    when(userService.findById(1L)).thenReturn(testUserInfo);

    mockMvc
      .perform(get("/api/users/1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testUserInfo.getId()))
      .andExpect(jsonPath("$.name").value("Test User"))
      .andExpect(jsonPath("$.email").value("test@example.com"))
      .andExpect(jsonPath("$.documentId").value("DOC123IT"));
  }

  @Test
  void create_shouldCreateUser() throws Exception {
    UserData userData = UserData.builder()
      .name("New User")
      .email("newuser@example.com")
      .password("password123")
      .phone("9876543210")
      .documentId("DOC456IT")
      .role(UserRole.EMPLOYEE)
      .build();

    UserInfo newUserInfo = UserInfo.builder()
      .id(2L)
      .name(userData.getName())
      .email(userData.getEmail())
      .phone(userData.getPhone())
      .documentId(userData.getDocumentId())
      .role(userData.getRole())
      .build();

    when(userService.create(any(UserData.class))).thenReturn(newUserInfo);

    mockMvc
      .perform(
        post("/api/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(userData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.name").value("New User"))
      .andExpect(jsonPath("$.email").value("newuser@example.com"))
      .andExpect(jsonPath("$.documentId").value("DOC456IT"));
  }

  @Test
  void update_shouldUpdateUser() throws Exception {
    UserData userData = UserData.builder()
      .name("Updated User")
      .email("updated@example.com")
      .phone("5555555555")
      .documentId("DOC789IT")
      .role(UserRole.EMPLOYEE)
      .build();

    UserInfo updatedUserInfo = UserInfo.builder()
      .id(1L)
      .name(userData.getName())
      .email(userData.getEmail())
      .phone(userData.getPhone())
      .documentId(userData.getDocumentId())
      .role(userData.getRole())
      .build();

    when(userService.update(eq(1L), any(UserData.class))).thenReturn(
      updatedUserInfo
    );

    mockMvc
      .perform(
        put("/api/users/1")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(userData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1L))
      .andExpect(jsonPath("$.name").value("Updated User"))
      .andExpect(jsonPath("$.email").value("updated@example.com"))
      .andExpect(jsonPath("$.documentId").value("DOC789IT"));
  }

  @Test
  void delete_shouldDeleteUser() throws Exception {
    mockMvc.perform(delete("/api/users/1")).andExpect(status().isNoContent());
  }

  @Test
  void adminResetPassword_shouldResetUserPassword() throws Exception {
    Map<String, String> passwordResetRequest = Map.of(
      "newPassword",
      "adminReset123"
    );

    mockMvc
      .perform(
        post("/api/users/1/reset-password")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(passwordResetRequest))
      )
      .andExpect(status().isNoContent());
  }
}
