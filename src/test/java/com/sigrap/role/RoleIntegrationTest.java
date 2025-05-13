package com.sigrap.role;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private Role testRole;
  private Permission testPermission;
  private User testUser;

  @BeforeEach
  void setUp() {
    // Create required tables first
    jdbcTemplate.execute(
      "CREATE TABLE IF NOT EXISTS users (" +
      "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
      "name VARCHAR(255), " +
      "email VARCHAR(255) UNIQUE, " +
      "password VARCHAR(255), " +
      "phone VARCHAR(255), " +
      "failed_attempts INTEGER, " +
      "last_login TIMESTAMP, " +
      "password_reset_token VARCHAR(255), " +
      "password_reset_expiry TIMESTAMP, " +
      "status VARCHAR(50))"
    );

    jdbcTemplate.execute(
      "CREATE TABLE IF NOT EXISTS permissions (" +
      "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
      "name VARCHAR(255) UNIQUE, " +
      "resource VARCHAR(255), " +
      "action VARCHAR(255), " +
      "description VARCHAR(255), " +
      "created_at TIMESTAMP, " +
      "updated_at TIMESTAMP)"
    );

    jdbcTemplate.execute(
      "CREATE TABLE IF NOT EXISTS roles (" +
      "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
      "name VARCHAR(255) UNIQUE, " +
      "description VARCHAR(255), " +
      "created_at TIMESTAMP, " +
      "updated_at TIMESTAMP)"
    );

    jdbcTemplate.execute(
      "CREATE TABLE IF NOT EXISTS role_permissions (" +
      "role_id BIGINT, " +
      "permission_id BIGINT, " +
      "PRIMARY KEY (role_id, permission_id))"
    );

    jdbcTemplate.execute(
      "CREATE TABLE IF NOT EXISTS user_roles (" +
      "user_id BIGINT, " +
      "role_id BIGINT, " +
      "PRIMARY KEY (user_id, role_id))"
    );

    List<SimpleGrantedAuthority> authorities = Arrays.asList(
      new SimpleGrantedAuthority("ROLE_ADMIN")
    );

    UserDetails userDetails =
      org.springframework.security.core.userdetails.User.builder()
        .username("admin@example.com")
        .password("password")
        .authorities(authorities)
        .build();

    SecurityContextHolder.getContext()
      .setAuthentication(
        new UsernamePasswordAuthenticationToken(userDetails, null, authorities)
      );

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

  @Test
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
