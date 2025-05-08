package com.sigrap.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  void findByEmail_shouldReturnUser_whenEmailExists() {
    User user = User.builder()
        .name("Test User")
        .email("test@example.com")
        .password("password123")
        .build();

    userRepository.save(user);

    Optional<User> foundUser = userRepository.findByEmail("test@example.com");

    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getName()).isEqualTo("Test User");
    assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
  }

  @Test
  void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
    Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

    assertThat(foundUser).isEmpty();
  }

  @Test
  void existsByEmail_shouldReturnTrue_whenEmailExists() {
    User user = User.builder()
        .name("Test User")
        .email("exists@example.com")
        .password("password123")
        .build();

    userRepository.save(user);

    boolean emailExists = userRepository.existsByEmail("exists@example.com");

    assertThat(emailExists).isTrue();
  }

  @Test
  void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
    boolean emailExists = userRepository.existsByEmail("nonexistent@example.com");

    assertThat(emailExists).isFalse();
  }
}