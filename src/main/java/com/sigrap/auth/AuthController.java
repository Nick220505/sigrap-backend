package com.sigrap.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "Authentication operations for user registration and login")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "Register a new user", description = "Creates a new user account and returns an authentication token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User successfully registered", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
      @ApiResponse(responseCode = "409", description = "Email already exists", content = @Content)
  })
  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthResponse register(
      @Parameter(description = "User registration data", required = true) @Valid @RequestBody RegisterRequest request) {
    return authService.register(request);
  }

  @Operation(summary = "Authenticate user", description = "Validates user credentials and returns an authentication token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authentication successful", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
      @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
  })
  @PostMapping("/login")
  public AuthResponse login(
      @Parameter(description = "User login credentials", required = true) @Valid @RequestBody AuthRequest request) {
    return authService.authenticate(request);
  }
}