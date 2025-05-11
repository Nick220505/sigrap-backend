package com.sigrap.role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.permission.Permission;
import com.sigrap.permission.PermissionRepository;
import com.sigrap.user.User;
import com.sigrap.user.User.UserStatus;
import com.sigrap.user.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RoleIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PermissionRepository permissionRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private Role testRole;
  private Permission testPermission;
  private User testUser;

  @BeforeEach
  void setUp() {
    roleRepository.deleteAll();
    permissionRepository.deleteAll();
    userRepository.deleteAll();

    testRole = roleRepository.save(
      Role.builder().name("TEST_ROLE").description("Test role").build()
    );

    testPermission = permissionRepository.save(
      Permission.builder()
        .name("TEST_PERMISSION")
        .resource("TEST_RESOURCE")
        .action("TEST_ACTION")
        .description("Test permission")
        .build()
    );

    testUser = userRepository.save(
      User.builder()
        .name("Test User")
        .email("test@example.com")
        .password("password")
        .status(UserStatus.ACTIVE)
        .build()
    );
  }

  @AfterEach
  void tearDown() {
    testUser.getRoles().clear();
    userRepository.save(testUser);

    roleRepository.deleteAll();
    permissionRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void findAll_shouldReturnAllRoles() throws Exception {
    mockMvc
      .perform(get("/api/roles").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].id").value(testRole.getId()))
      .andExpect(jsonPath("$[0].name").value("TEST_ROLE"))
      .andExpect(jsonPath("$[0].description").value("Test role"));
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void findById_shouldReturnRole() throws Exception {
    mockMvc
      .perform(
        get("/api/roles/" + testRole.getId()).contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testRole.getId()))
      .andExpect(jsonPath("$.name").value("TEST_ROLE"))
      .andExpect(jsonPath("$.description").value("Test role"));
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void create_shouldCreateRole() throws Exception {
    RoleData roleData = RoleData.builder()
      .name("NEW_ROLE")
      .description("New role")
      .build();

    mockMvc
      .perform(
        post("/api/roles")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(roleData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.name").value("NEW_ROLE"))
      .andExpect(jsonPath("$.description").value("New role"));

    Role newRole = roleRepository
      .findByName("NEW_ROLE")
      .orElseThrow(() -> new AssertionError("Role not found"));

    assertThat(newRole.getName()).isEqualTo("NEW_ROLE");
    assertThat(newRole.getDescription()).isEqualTo("New role");
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void create_shouldCreateRoleWithPermissions() throws Exception {
    RoleData roleData = RoleData.builder()
      .name("NEW_ROLE")
      .description("New role")
      .permissionIds(Set.of(testPermission.getId()))
      .build();

    mockMvc
      .perform(
        post("/api/roles")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(roleData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.name").value("NEW_ROLE"))
      .andExpect(jsonPath("$.description").value("New role"))
      .andExpect(jsonPath("$.permissions", hasSize(1)))
      .andExpect(jsonPath("$.permissions[0].id").value(testPermission.getId()));

    Role newRole = roleRepository
      .findByName("NEW_ROLE")
      .orElseThrow(() -> new AssertionError("Role not found"));

    assertThat(newRole.getName()).isEqualTo("NEW_ROLE");
    assertThat(newRole.getDescription()).isEqualTo("New role");
    assertThat(newRole.getPermissions()).hasSize(1);
    assertThat(newRole.getPermissions().iterator().next().getId()).isEqualTo(
      testPermission.getId()
    );
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void update_shouldUpdateRole() throws Exception {
    RoleData roleData = RoleData.builder()
      .name("UPDATED_ROLE")
      .description("Updated role description")
      .build();

    mockMvc
      .perform(
        put("/api/roles/" + testRole.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(roleData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testRole.getId()))
      .andExpect(jsonPath("$.name").value("UPDATED_ROLE"))
      .andExpect(jsonPath("$.description").value("Updated role description"));

    Role updatedRole = roleRepository
      .findById(testRole.getId())
      .orElseThrow(() -> new AssertionError("Role not found"));

    assertThat(updatedRole.getName()).isEqualTo("UPDATED_ROLE");
    assertThat(updatedRole.getDescription()).isEqualTo(
      "Updated role description"
    );
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void assignToUser_shouldAssignRoleToUser() throws Exception {
    mockMvc
      .perform(
        post(
          "/api/roles/" + testRole.getId() + "/users/" + testUser.getId()
        ).contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testRole.getId()))
      .andExpect(jsonPath("$.name").value("TEST_ROLE"));

    User updatedUser = userRepository
      .findById(testUser.getId())
      .orElseThrow(() -> new AssertionError("User not found"));

    assertThat(updatedUser.getRoles()).hasSize(1);
    assertThat(updatedUser.getRoles().iterator().next().getId()).isEqualTo(
      testRole.getId()
    );
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void removeFromUser_shouldRemoveRoleFromUser() throws Exception {
    testUser.getRoles().add(testRole);
    userRepository.save(testUser);

    mockMvc
      .perform(
        delete(
          "/api/roles/" + testRole.getId() + "/users/" + testUser.getId()
        ).contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNoContent());

    User updatedUser = userRepository
      .findById(testUser.getId())
      .orElseThrow(() -> new AssertionError("User not found"));

    assertThat(updatedUser.getRoles()).isEmpty();
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void delete_shouldDeleteRole() throws Exception {
    mockMvc
      .perform(
        delete("/api/roles/" + testRole.getId()).contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isNoContent());

    assertThat(roleRepository.findById(testRole.getId())).isEmpty();
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void create_shouldReturnError_whenRoleNameExists() throws Exception {
    RoleData roleData = RoleData.builder()
      .name("TEST_ROLE")
      .description("New role description")
      .build();

    mockMvc
      .perform(
        post("/api/roles")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(roleData))
      )
      .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void delete_shouldReturnError_whenRoleAssignedToUser() throws Exception {
    testUser.getRoles().add(testRole);
    userRepository.save(testUser);

    mockMvc
      .perform(
        delete("/api/roles/" + testRole.getId()).contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isNoContent());

    assertThat(roleRepository.findById(testRole.getId())).isEmpty();
  }
}
