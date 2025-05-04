package com.sigrap.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

  @NotBlank(message = "Email cannot be empty")
  @Email(message = "Invalid email format")
  private String email;

  @NotBlank(message = "Password cannot be empty")
  private String password;
}