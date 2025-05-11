package com.sigrap.role;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
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
import com.sigrap.permission.PermissionInfo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @Mock
  private RoleService roleService;

  private RoleInfo roleInfo;
  private RoleData roleData;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    mockMvc = standaloneSetup(new RoleController(roleService)).build();

    PermissionInfo permission = PermissionInfo.builder()
      .id(1L)
      .name("USER_CREATE")
      .resource("USER")
      .action("CREATE")
      .build();

    roleInfo = RoleInfo.builder()
      .id(1L)
      .name("ADMIN")
      .description("Administrator role")
      .permissions(Set.of(permission))
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

    roleData = RoleData.builder()
      .name("ADMIN")
      .description("Administrator role")
      .permissionIds(Set.of(1L))
      .build();
  }

  @Test
  void findAll_shouldReturnAllRoles() throws Exception {
    List<RoleInfo> roles = List.of(roleInfo);

    when(roleService.findAll()).thenReturn(roles);

    mockMvc
      .perform(get("/api/roles").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].id").value(1))
      .andExpect(jsonPath("$[0].name").value("ADMIN"))
      .andExpect(jsonPath("$[0].description").value("Administrator role"))
      .andExpect(jsonPath("$[0].permissions", hasSize(1)))
      .andExpect(jsonPath("$[0].permissions[0].name").value("USER_CREATE"));
  }

  @Test
  void findById_shouldReturnRole() throws Exception {
    when(roleService.findById(1L)).thenReturn(roleInfo);

    mockMvc
      .perform(get("/api/roles/1").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.name").value("ADMIN"))
      .andExpect(jsonPath("$.description").value("Administrator role"))
      .andExpect(jsonPath("$.permissions", hasSize(1)))
      .andExpect(jsonPath("$.permissions[0].name").value("USER_CREATE"));
  }

  @Test
  void create_shouldCreateRole() throws Exception {
    when(roleService.create(any(RoleData.class))).thenReturn(roleInfo);

    mockMvc
      .perform(
        post("/api/roles")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(roleData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.name").value("ADMIN"))
      .andExpect(jsonPath("$.description").value("Administrator role"))
      .andExpect(jsonPath("$.permissions", hasSize(1)))
      .andExpect(jsonPath("$.permissions[0].name").value("USER_CREATE"));
  }

  @Test
  void update_shouldUpdateRole() throws Exception {
    when(roleService.update(eq(1L), any(RoleData.class))).thenReturn(roleInfo);

    mockMvc
      .perform(
        put("/api/roles/1")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(roleData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.name").value("ADMIN"))
      .andExpect(jsonPath("$.description").value("Administrator role"));
  }

  @Test
  void assignToUser_shouldAssignRoleToUser() throws Exception {
    when(roleService.assignToUser(eq(1L), eq(1L))).thenReturn(roleInfo);

    mockMvc
      .perform(
        post("/api/roles/1/users/1").contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.name").value("ADMIN"));
  }

  @Test
  void removeFromUser_shouldRemoveRoleFromUser() throws Exception {
    doNothing().when(roleService).removeFromUser(eq(1L), eq(1L));

    mockMvc
      .perform(
        delete("/api/roles/1/users/1").contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNoContent());
  }

  @Test
  void delete_shouldDeleteRole() throws Exception {
    doNothing().when(roleService).delete(1L);

    mockMvc
      .perform(delete("/api/roles/1").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());
  }
}
