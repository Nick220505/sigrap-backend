package com.sigrap.user;

import static org.assertj.core.api.Assertions.assertThat;
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

    var result = userRepository.findByEmail("test@example.com");

    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Test User");
    assertThat(result.get().getEmail()).isEqualTo("test@example.com");
  }

  @Test
  void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
    var result = userRepository.findByEmail("nonexistent@example.com");

    assertThat(result).isEmpty();
  }

  @Test
  void existsByEmail_shouldReturnTrue_whenEmailExists() {
    User user = User.builder()
        .name("Test User")
        .email("exists@example.com")
        .password("password123")
        .build();

    userRepository.save(user);

    boolean result = userRepository.existsByEmail("exists@example.com");

    assertThat(result).isTrue();
  }

  @Test
  void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
    boolean result = userRepository.existsByEmail("nonexistent@example.com");

    assertThat(result).isFalse();
  }
}