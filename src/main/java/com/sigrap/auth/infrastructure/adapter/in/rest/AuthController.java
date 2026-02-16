package com.sigrap.auth.infrastructure.adapter.in.rest;

import com.sigrap.auth.application.port.in.AuthenticateUserUseCase;
import com.sigrap.auth.application.port.in.RegisterUserUseCase;
import com.sigrap.auth.application.port.in.ValidateTokenUseCase;
import com.sigrap.auth.application.port.in.command.AuthenticateUserCommand;
import com.sigrap.auth.application.port.in.command.RegisterUserCommand;
import com.sigrap.auth.domain.model.AuthenticationResult;
import com.sigrap.auth.domain.model.Email;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 * Handles HTTP requests for user authentication and registration at /api/v2/auth endpoints.
 * 
 * <p>This is an input adapter in hexagonal architecture terminology.
 * It translates HTTP requests to use case calls and domain objects to HTTP responses.
 */
@RestController
@RequestMapping("/api/v2/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and authorization")
public class AuthController {

  private final AuthenticateUserUseCase authenticateUserUseCase;
  private final RegisterUserUseCase registerUserUseCase;
  private final ValidateTokenUseCase validateTokenUseCase;
  private final AuthResponseMapper responseMapper;

  /**
   * Constructor for dependency injection.
   *
   * @param authenticateUserUseCase use case for user authentication
   * @param registerUserUseCase use case for user registration
   * @param validateTokenUseCase use case for token validation
   * @param responseMapper mapper for converting domain objects to DTOs
   */
  public AuthController(
    AuthenticateUserUseCase authenticateUserUseCase,
    RegisterUserUseCase registerUserUseCase,
    ValidateTokenUseCase validateTokenUseCase,
    AuthResponseMapper responseMapper
  ) {
    this.authenticateUserUseCase = authenticateUserUseCase;
    this.registerUserUseCase = registerUserUseCase;
    this.validateTokenUseCase = validateTokenUseCase;
    this.responseMapper = responseMapper;
  }

  /**
   * Authenticates a user with email and password.
   *
   * @param request the authentication request containing credentials
   * @return the authentication response with JWT token
   */
  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Authenticate user",
    description = "Authenticates a user with email and password credentials. " +
                  "Returns a JWT token that must be included in the Authorization header " +
                  "for subsequent API requests."
  )
  @ApiResponse(
    responseCode = "200",
    description = "Authentication successful - JWT token returned",
    content = @Content(schema = @Schema(implementation = AuthResponse.class))
  )
  @ApiResponse(
    responseCode = "400",
    description = "Invalid request - validation errors or malformed credentials"
  )
  @ApiResponse(
    responseCode = "401",
    description = "Authentication failed - invalid email or password"
  )
  public AuthResponse login(@Valid @RequestBody AuthRequest request) {
    AuthenticateUserCommand command = new AuthenticateUserCommand(
      request.email(),
      request.password()
    );
    
    AuthenticationResult result = authenticateUserUseCase.authenticate(command);
    return responseMapper.toResponse(result);
  }

  /**
   * Registers a new user.
   *
   * @param request the registration request containing user data
   * @return the authentication response with JWT token
   */
  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
    summary = "Register new user",
    description = "Creates a new user account with the provided name, email, and password. " +
                  "The email must be unique. Upon successful registration, returns a JWT token " +
                  "that can be used immediately for authentication."
  )
  @ApiResponse(
    responseCode = "201",
    description = "User registered successfully - JWT token returned",
    content = @Content(schema = @Schema(implementation = AuthResponse.class))
  )
  @ApiResponse(
    responseCode = "400",
    description = "Invalid request - validation errors or email already exists"
  )
  public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
    RegisterUserCommand command = new RegisterUserCommand(
      request.name(),
      request.email(),
      request.password()
    );
    
    AuthenticationResult result = registerUserUseCase.register(command);
    return responseMapper.toResponse(result);
  }

  /**
   * Validates a JWT token.
   *
   * @param token the JWT token to validate (from Authorization header)
   * @return a simple response indicating the token is valid
   */
  @GetMapping("/validate")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Validate JWT token",
    description = "Validates a JWT token and returns the associated user email. " +
                  "The token should be provided in the Authorization header with 'Bearer ' prefix."
  )
  @ApiResponse(
    responseCode = "200",
    description = "Token is valid - returns user email",
    content = @Content(schema = @Schema(implementation = TokenValidationResponse.class))
  )
  @ApiResponse(
    responseCode = "401",
    description = "Token is invalid or expired"
  )
  public TokenValidationResponse validateToken(
    @RequestHeader("Authorization") String token
  ) {
    // Remove "Bearer " prefix if present
    String tokenValue = token.startsWith("Bearer ") 
      ? token.substring(7) 
      : token;
    
    Email email = validateTokenUseCase.validateToken(tokenValue);
    return new TokenValidationResponse(true, email.value());
  }

  /**
   * Simple response for token validation.
   */
  public record TokenValidationResponse(boolean valid, String email) {}
}
