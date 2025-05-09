package com.sigrap.auth;

import com.sigrap.config.SecurityConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling authentication operations.
 * Provides endpoints for user registration and login.
 *
 * <p>This controller manages:
 * <ul>
 *   <li>User registration with validation</li>
 *   <li>User authentication and JWT token generation</li>
 *   <li>Cross-origin resource sharing configuration</li>
 * </ul></p>
 *
 * <p>Authentication Flow:
 * <ol>
 *   <li>Client submits registration or login request</li>
 *   <li>Request data is validated</li>
 *   <li>User is authenticated or created</li>
 *   <li>JWT token is generated and returned</li>
 * </ol></p>
 *
 * <p>Security Features:
 * <ul>
 *   <li>Password validation and encryption</li>
 *   <li>Email uniqueness verification</li>
 *   <li>JWT-based authentication</li>
 *   <li>CORS security configuration</li>
 * </ul></p>
 *
 * <p>Usage Examples:
 * <pre>
 * // Registration
 * POST /api/auth/register
 * {
 *   "name": "John Doe",
 *   "email": "john@example.com",
 *   "password": "SecurePass123!"
 * }
 *
 * // Login
 * POST /api/auth/login
 * {
 *   "email": "john@example.com",
 *   "password": "SecurePass123!"
 * }
 * </pre></p>
 *
 * @see AuthService
 * @see JwtUtil
 * @see SecurityConfig
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(
  name = "Authentication",
  description = "Authentication operations for user registration and login"
)
public class AuthController {

  private final AuthService authService;

  /**
   * Registers a new user in the system.
   *
   * <p>This endpoint:
   * <ul>
   *   <li>Validates registration data</li>
   *   <li>Checks email uniqueness</li>
   *   <li>Creates new user account</li>
   *   <li>Generates authentication token</li>
   * </ul></p>
   *
   * <p>The password must meet security requirements:
   * <ul>
   *   <li>Minimum 8 characters</li>
   *   <li>At least one uppercase letter</li>
   *   <li>At least one lowercase letter</li>
   *   <li>At least one number</li>
   *   <li>At least one special character</li>
   * </ul></p>
   *
   * @param request Registration data including name, email, and password
   * @return AuthResponse containing JWT token and user details
   * @throws IllegalArgumentException if email already exists
   */
  @Operation(
    summary = "Register a new user",
    description = "Creates a new user account and returns an authentication token"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "201",
        description = "User successfully registered",
        content = @Content(
          schema = @Schema(implementation = AuthResponse.class)
        )
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Invalid input data",
        content = @Content
      ),
      @ApiResponse(
        responseCode = "409",
        description = "Email already exists",
        content = @Content
      ),
    }
  )
  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthResponse register(
    @Parameter(
      description = "User registration data",
      required = true
    ) @Valid @RequestBody RegisterRequest request
  ) {
    return authService.register(request);
  }

  /**
   * Authenticates a user and provides a JWT token.
   *
   * <p>This endpoint:
   * <ul>
   *   <li>Validates login credentials</li>
   *   <li>Authenticates user</li>
   *   <li>Generates new JWT token</li>
   * </ul></p>
   *
   * <p>The token can be used for subsequent authenticated requests by
   * including it in the Authorization header:
   * {@code Authorization: Bearer <token>}</p>
   *
   * @param request Login credentials including email and password
   * @return AuthResponse containing JWT token and user details
   * @throws EntityNotFoundException if user not found
   * @throws BadCredentialsException if credentials are invalid
   */
  @Operation(
    summary = "Authenticate user",
    description = "Validates user credentials and returns an authentication token"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Authentication successful",
        content = @Content(
          schema = @Schema(implementation = AuthResponse.class)
        )
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Invalid input data",
        content = @Content
      ),
      @ApiResponse(
        responseCode = "401",
        description = "Invalid credentials",
        content = @Content
      ),
    }
  )
  @PostMapping("/login")
  public AuthResponse login(
    @Parameter(
      description = "User login credentials",
      required = true
    ) @Valid @RequestBody AuthRequest request
  ) {
    return authService.authenticate(request);
  }
}
