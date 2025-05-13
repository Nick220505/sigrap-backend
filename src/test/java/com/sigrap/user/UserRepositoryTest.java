package com.sigrap.user;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.sigrap.config.RepositoryTestConfiguration;

@DataJpaTest
@ActiveProfiles("test")
@Import(RepositoryTestConfiguration.class)
class UserRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Test
  void findByEmail_shouldReturnUser_whenEmailExists() {
    User user = User.builder()
      .name("Test User")
      .email("test@example.com")
      .password("password123")
      .build();

    entityManager.persist(user);
    entityManager.flush();

    Optional<User> foundUser = userRepository.findByEmail("test@example.com");

    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getName()).isEqualTo("Test User");
    assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
  }

  @Test
  void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
    Optional<User> foundUser = userRepository.findByEmail(
      "nonexistent@example.com"
    );
    assertThat(foundUser).isEmpty();
  }

  @Test
  void existsByEmail_shouldReturnTrue_whenEmailExists() {
    User user = User.builder()
      .name("Test User")
      .email("exists@example.com")
      .password("password123")
      .build();

    entityManager.persist(user);
    entityManager.flush();

    boolean emailExists = userRepository.existsByEmail("exists@example.com");
    assertThat(emailExists).isTrue();
  }

  @Test
  void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
    boolean emailExists = userRepository.existsByEmail(
      "nonexistent@example.com"
    );
    assertThat(emailExists).isFalse();
  }
}
