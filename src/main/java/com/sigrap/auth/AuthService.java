package com.sigrap.auth;

import com.sigrap.config.SecurityConfig;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import com.sigrap.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class handling authentication and user registration operations.
 *
 * <p>This service provides two main functionalities:
 * <ul>
 *   <li>User Registration - Creates new user accounts with encrypted passwords</li>
 *   <li>User Authentication - Validates credentials and generates JWT tokens</li>
 * </ul></p>
 *
 * <p>Authentication Flow:
 * <ol>
 *   <li>User submits credentials (email/password)</li>
 *   <li>Credentials are validated against stored user data</li>
 *   <li>On successful validation, a JWT token is generated</li>
 *   <li>Token is returned along with basic user information</li>
 * </ol></p>
 *
 * <p>Security Measures:
 * <ul>
 *   <li>Passwords are encrypted using BCrypt before storage</li>
 *   <li>JWT tokens are signed and have configurable expiration</li>
 *   <li>Email uniqueness is enforced at the database level</li>
 *   <li>Failed authentication attempts return generic error messages</li>
 * </ul></p>
 *
 * <p>Usage Example:
 * <pre>
 * // Registration
 * RegisterRequest registerRequest = new RegisterRequest("John Doe", "john@example.com", "password123");
 * AuthResponse registerResponse = authService.register(registerRequest);
 *
 * // Authentication
 * AuthRequest authRequest = new AuthRequest("john@example.com", "password123");
 * AuthResponse authResponse = authService.authenticate(authRequest);
 * </pre></p>
 *
 * @see JwtUtil
 * @see UserService
 * @see SecurityConfig
 */
@Service
@RequiredArgsConstructor
public class AuthService {

  /**
   * Repository for accessing user data in the database.
   * Used for checking email existence and retrieving user information.
   */
  private final UserRepository userRepository;

  /**
   * Encoder for securely hashing user passwords before storage.
   * Ensures passwords are never stored in plain text.
   */
  private final PasswordEncoder passwordEncoder;

  /**
   * Utility for generating and validating JWT tokens.
   * Creates authentication tokens for authenticated users.
   */
  private final JwtUtil jwtUtil;

  /**
   * Spring Security manager for validating user credentials.
   * Handles the core authentication logic.
   */
  private final AuthenticationManager authenticationManager;

  /**
   * Service for loading user details for JWT token generation.
   * Provides user information in the format required by Spring Security.
   */
  private final UserService userService;

  /**
   * Registers a new user in the system.
   *
   * <p>Registration Process:
   * <ol>
   *   <li>Validates email uniqueness</li>
   *   <li>Encrypts the password</li>
   *   <li>Creates and persists the user</li>
   *   <li>Generates a JWT token</li>
   *   <li>Returns token with user details</li>
   * </ol></p>
   *
   * @param request Registration details including name, email, and password
   * @return AuthResponse containing JWT token and user information
   * @throws IllegalArgumentException if the email is already registered
   */
  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("Email already exists");
    }

    User user = User.builder()
      .name(request.getName())
      .email(request.getEmail())
      .password(passwordEncoder.encode(request.getPassword()))
      .build();

    userRepository.save(user);

    UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
    String jwt = jwtUtil.generateToken(userDetails);

    return AuthResponse.builder()
      .token(jwt)
      .email(user.getEmail())
      .name(user.getName())
      .build();
  }

  /**
   * Authenticates a user with their credentials.
   *
   * <p>Authentication Process:
   * <ol>
   *   <li>Validates credentials using Spring Security</li>
   *   <li>Retrieves user details</li>
   *   <li>Generates a new JWT token</li>
   *   <li>Returns token with user details</li>
   * </ol></p>
   *
   * <p>Security Note: This method uses Spring Security's authentication manager
   * to validate credentials, ensuring proper password comparison and security measures.</p>
   *
   * @param request Authentication credentials including email and password
   * @return AuthResponse containing JWT token and user information
   * @throws EntityNotFoundException if the user is not found
   * @throws BadCredentialsException if the credentials are invalid
   */
  public AuthResponse authenticate(AuthRequest request) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        request.getEmail(),
        request.getPassword()
      )
    );

    User user = userRepository
      .findByEmail(request.getEmail())
      .orElseThrow(() -> new EntityNotFoundException("User not found"));

    UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
    String jwt = jwtUtil.generateToken(userDetails);

    return AuthResponse.builder()
      .token(jwt)
      .email(user.getEmail())
      .name(user.getName())
      .build();
  }
}
