package com.sigrap.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.role.Role;
import com.sigrap.role.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PermissionIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private PermissionRepository permissionRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private Permission testPermission;
  private Role testRole;

  @BeforeEach
  void setUp() {
    permissionRepository.deleteAll();
    roleRepository.deleteAll();

    testPermission = permissionRepository.save(
      Permission.builder()
        .name("TEST_PERMISSION")
        .resource("TEST_RESOURCE")
        .action("TEST_ACTION")
        .description("Test permission")
        .build()
    );

    testRole = roleRepository.save(
      Role.builder().name("TEST_ROLE").description("Test role").build()
    );
  }

  @AfterEach
  void tearDown() {
    testRole.getPermissions().clear();
    roleRepository.save(testRole);

    permissionRepository.deleteAll();
    roleRepository.deleteAll();
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void findAll_shouldReturnAllPermissions() throws Exception {
    mockMvc
      .perform(get("/api/permissions").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].id").value(testPermission.getId()))
      .andExpect(jsonPath("$[0].name").value("TEST_PERMISSION"))
      .andExpect(jsonPath("$[0].resource").value("TEST_RESOURCE"))
      .andExpect(jsonPath("$[0].action").value("TEST_ACTION"));
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void findById_shouldReturnPermission() throws Exception {
    mockMvc
      .perform(
        get("/api/permissions/" + testPermission.getId()).contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testPermission.getId()))
      .andExpect(jsonPath("$.name").value("TEST_PERMISSION"))
      .andExpect(jsonPath("$.resource").value("TEST_RESOURCE"))
      .andExpect(jsonPath("$.action").value("TEST_ACTION"));
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void create_shouldCreatePermission() throws Exception {
    PermissionData permissionData = PermissionData.builder()
      .name("NEW_PERMISSION")
      .resource("NEW_RESOURCE")
      .action("NEW_ACTION")
      .description("New permission")
      .build();

    mockMvc
      .perform(
        post("/api/permissions")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(permissionData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.name").value("NEW_PERMISSION"))
      .andExpect(jsonPath("$.resource").value("NEW_RESOURCE"))
      .andExpect(jsonPath("$.action").value("NEW_ACTION"));

    Permission newPermission = permissionRepository
      .findByName("NEW_PERMISSION")
      .orElseThrow(() -> new AssertionError("Permission not found"));

    assertThat(newPermission.getName()).isEqualTo("NEW_PERMISSION");
    assertThat(newPermission.getResource()).isEqualTo("NEW_RESOURCE");
    assertThat(newPermission.getAction()).isEqualTo("NEW_ACTION");
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void assignToRole_shouldAssignPermissionToRole() throws Exception {
    mockMvc
      .perform(
        post(
          "/api/permissions/" +
          testPermission.getId() +
          "/roles/" +
          testRole.getId()
        ).contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testPermission.getId()))
      .andExpect(jsonPath("$.name").value("TEST_PERMISSION"));

    Role updatedRole = roleRepository
      .findById(testRole.getId())
      .orElseThrow(() -> new AssertionError("Role not found"));

    assertThat(updatedRole.getPermissions()).hasSize(1);
    assertThat(
      updatedRole.getPermissions().iterator().next().getId()
    ).isEqualTo(testPermission.getId());
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void removeFromRole_shouldRemovePermissionFromRole() throws Exception {
    testRole.getPermissions().add(testPermission);
    roleRepository.save(testRole);

    mockMvc
      .perform(
        delete(
          "/api/permissions/" +
          testPermission.getId() +
          "/roles/" +
          testRole.getId()
        ).contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNoContent());

    Role updatedRole = roleRepository
      .findById(testRole.getId())
      .orElseThrow(() -> new AssertionError("Role not found"));

    assertThat(updatedRole.getPermissions()).isEmpty();
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void delete_shouldDeletePermission() throws Exception {
    mockMvc
      .perform(
        delete("/api/permissions/" + testPermission.getId()).contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isNoContent());

    assertThat(permissionRepository.findById(testPermission.getId())).isEmpty();
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void create_shouldReturnError_whenPermissionNameExists() throws Exception {
    PermissionData permissionData = PermissionData.builder()
      .name("TEST_PERMISSION")
      .resource("NEW_RESOURCE")
      .action("NEW_ACTION")
      .description("New permission")
      .build();

    mockMvc
      .perform(
        post("/api/permissions")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(permissionData))
      )
      .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void delete_shouldReturnError_whenPermissionAssignedToRole()
    throws Exception {
    testRole.getPermissions().add(testPermission);
    roleRepository.save(testRole);

    mockMvc
      .perform(
        delete("/api/permissions/" + testPermission.getId()).contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isNoContent());

    assertThat(permissionRepository.findById(testPermission.getId())).isEmpty();
  }
}
