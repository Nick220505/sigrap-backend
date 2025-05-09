package com.sigrap.auth;

import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import com.sigrap.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class handling authentication operations.
 * Manages user registration and authentication processes, including JWT token generation.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;
  private final UserService userService;

  /**
   * Registers a new user in the system.
   * Creates a new user account and generates a JWT token for immediate authentication.
   *
   * @param request Registration details including name, email, and password
   * @return AuthResponse containing the JWT token and user information
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
   * Validates the credentials and generates a new JWT token upon successful authentication.
   *
   * @param request Authentication credentials including email and password
   * @return AuthResponse containing the JWT token and user information
   * @throws EntityNotFoundException if the user is not found
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
