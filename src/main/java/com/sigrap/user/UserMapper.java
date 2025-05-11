package com.sigrap.user;

import com.sigrap.role.Role;
import com.sigrap.role.RoleInfo;
import com.sigrap.role.RoleMapper;
import com.sigrap.role.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between User entities and DTOs.
 * Handles the transformation of data between different formats.
 */
@Component
@RequiredArgsConstructor
public class UserMapper {

  private final RoleRepository roleRepository;
  private final RoleMapper roleMapper;
  private final PasswordEncoder passwordEncoder;

  /**
   * Converts a User entity to a UserInfo DTO.
   *
   * @param user The User entity to convert
   * @return UserInfo containing the user data
   */
  public UserInfo toInfo(User user) {
    if (user == null) {
      return null;
    }

    Set<RoleInfo> roleInfos = user.getRoles() != null
      ? user
        .getRoles()
        .stream()
        .map(roleMapper::toInfo)
        .collect(Collectors.toSet())
      : Collections.emptySet();

    return UserInfo.builder()
      .id(user.getId())
      .name(user.getName())
      .email(user.getEmail())
      .phone(user.getPhone())
      .status(user.getStatus())
      .lastLogin(user.getLastLogin())
      .roles(roleInfos)
      .build();
  }

  /**
   * Converts a list of User entities to a list of UserInfo DTOs.
   *
   * @param users The list of User entities to convert
   * @return List of UserInfo DTOs
   */
  public List<UserInfo> toInfoList(List<User> users) {
    if (users == null) {
      return List.of();
    }

    return users.stream().map(this::toInfo).collect(Collectors.toList());
  }

  /**
   * Converts a UserData DTO to a new User entity.
   *
   * @param userData The UserData DTO to convert
   * @return User entity with data from the DTO
   */
  public User toEntity(UserData userData) {
    if (userData == null) {
      return null;
    }

    Set<Role> roles = new HashSet<>();
    if (userData.getRoleIds() != null && !userData.getRoleIds().isEmpty()) {
      roles = userData
        .getRoleIds()
        .stream()
        .map(roleId ->
          roleRepository
            .findById(roleId)
            .orElseThrow(() ->
              new EntityNotFoundException("Role not found: " + roleId)
            )
        )
        .collect(Collectors.toSet());
    }

    return User.builder()
      .name(userData.getName())
      .email(userData.getEmail())
      .password(passwordEncoder.encode(userData.getPassword()))
      .phone(userData.getPhone())
      .status(
        userData.getStatus() != null
          ? userData.getStatus()
          : User.UserStatus.ACTIVE
      )
      .roles(roles)
      .failedAttempts(0)
      .build();
  }

  /**
   * Updates an existing User entity with data from a UserData DTO.
   *
   * @param user The User entity to update
   * @param userData The UserData DTO containing the new data
   */
  public void updateEntityFromData(User user, UserData userData) {
    if (user == null || userData == null) {
      return;
    }

    if (userData.getName() != null) {
      user.setName(userData.getName());
    }

    if (userData.getEmail() != null) {
      user.setEmail(userData.getEmail());
    }

    if (userData.getPassword() != null && !userData.getPassword().isEmpty()) {
      user.setPassword(passwordEncoder.encode(userData.getPassword()));
    }

    if (userData.getPhone() != null) {
      user.setPhone(userData.getPhone());
    }

    if (userData.getStatus() != null) {
      user.setStatus(userData.getStatus());
    }
  }

  /**
   * Updates the roles of a User entity.
   *
   * @param user The User entity to update
   * @param roleIds The set of role IDs to assign
   */
  public void updateRoles(User user, Set<Long> roleIds) {
    if (user == null || roleIds == null) {
      return;
    }

    Set<Role> roles = roleIds
      .stream()
      .map(roleId ->
        roleRepository
          .findById(roleId)
          .orElseThrow(() ->
            new EntityNotFoundException("Role not found: " + roleId)
          )
      )
      .collect(Collectors.toSet());

    user.setRoles(roles);
  }
}
