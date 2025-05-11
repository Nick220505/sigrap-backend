package com.sigrap.permission;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class PermissionControllerTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @Mock
  private PermissionService permissionService;

  private PermissionInfo permissionInfo;
  private PermissionData permissionData;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    mockMvc = standaloneSetup(
      new PermissionController(permissionService)
    ).build();

    permissionInfo = PermissionInfo.builder()
      .id(1L)
      .name("USER_CREATE")
      .resource("USER")
      .action("CREATE")
      .description("Create user permission")
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

    permissionData = PermissionData.builder()
      .name("USER_CREATE")
      .resource("USER")
      .action("CREATE")
      .description("Create user permission")
      .build();
  }

  @Test
  void findAll_shouldReturnAllPermissions() throws Exception {
    List<PermissionInfo> permissions = List.of(permissionInfo);

    when(permissionService.findAll()).thenReturn(permissions);

    mockMvc
      .perform(get("/api/permissions").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].id").value(1))
      .andExpect(jsonPath("$[0].name").value("USER_CREATE"))
      .andExpect(jsonPath("$[0].resource").value("USER"))
      .andExpect(jsonPath("$[0].action").value("CREATE"))
      .andExpect(jsonPath("$[0].description").value("Create user permission"));
  }

  @Test
  void findById_shouldReturnPermission() throws Exception {
    when(permissionService.findById(1L)).thenReturn(permissionInfo);

    mockMvc
      .perform(
        get("/api/permissions/1").contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.name").value("USER_CREATE"))
      .andExpect(jsonPath("$.resource").value("USER"))
      .andExpect(jsonPath("$.action").value("CREATE"))
      .andExpect(jsonPath("$.description").value("Create user permission"));
  }

  @Test
  void create_shouldCreatePermission() throws Exception {
    when(permissionService.create(any(PermissionData.class))).thenReturn(
      permissionInfo
    );

    mockMvc
      .perform(
        post("/api/permissions")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(permissionData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.name").value("USER_CREATE"))
      .andExpect(jsonPath("$.resource").value("USER"))
      .andExpect(jsonPath("$.action").value("CREATE"))
      .andExpect(jsonPath("$.description").value("Create user permission"));
  }

  @Test
  void assignToRole_shouldAssignPermissionToRole() throws Exception {
    when(permissionService.assignToRole(eq(1L), eq(1L))).thenReturn(
      permissionInfo
    );

    mockMvc
      .perform(
        post("/api/permissions/1/roles/1").contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.name").value("USER_CREATE"));
  }

  @Test
  void removeFromRole_shouldRemovePermissionFromRole() throws Exception {
    doNothing().when(permissionService).removeFromRole(eq(1L), eq(1L));

    mockMvc
      .perform(
        delete("/api/permissions/1/roles/1").contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isNoContent());
  }

  @Test
  void delete_shouldDeletePermission() throws Exception {
    doNothing().when(permissionService).delete(1L);

    mockMvc
      .perform(
        delete("/api/permissions/1").contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNoContent());
  }
}
