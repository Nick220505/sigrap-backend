package com.sigrap.user;

import java.util.List;
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

    return UserInfo.builder()
      .id(user.getId())
      .name(user.getName())
      .email(user.getEmail())
      .phone(user.getPhone())
      .lastLogin(user.getLastLogin())
      .role(user.getRole())
      .documentId(user.getDocumentId())
      .createdAt(user.getCreatedAt())
      .updatedAt(user.getUpdatedAt())
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

    return User.builder()
      .name(userData.getName())
      .email(userData.getEmail())
      .password(passwordEncoder.encode(userData.getPassword()))
      .phone(userData.getPhone())
      .role(userData.getRole() != null ? userData.getRole() : UserRole.EMPLOYEE)
      .documentId(userData.getDocumentId())
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

    if (userData.getRole() != null) {
      user.setRole(userData.getRole());
    }

    if (userData.getDocumentId() != null) {
      user.setDocumentId(userData.getDocumentId());
    }
  }
}
