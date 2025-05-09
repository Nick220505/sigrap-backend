package com.sigrap.user;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  @Test
  void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
    User user = User.builder()
      .id(1L)
      .name("Test User")
      .email("test@example.com")
      .password("encodedPassword")
      .build();

    when(userRepository.findByEmail("test@example.com")).thenReturn(
      Optional.of(user)
    );

    UserDetails userDetails = userService.loadUserByUsername(
      "test@example.com"
    );

    assertThat(userDetails).isNotNull();
    assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
    assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
    assertThat(userDetails.getAuthorities()).isEmpty();
  }

  @Test
  void loadUserByUsername_shouldThrowException_whenUserDoesNotExist() {
    when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(
      Optional.empty()
    );

    assertThrows(UsernameNotFoundException.class, () -> {
      userService.loadUserByUsername("nonexistent@example.com");
    });
  }
}
