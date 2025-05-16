package com.sigrap.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.sigrap.config.RepositoryTestConfiguration;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

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
      .documentId("DOC123")
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
      .documentId("DOC456")
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

  @Test
  void findByDocumentId_shouldReturnUser_whenDocumentIdExists() {
    User user = User.builder()
      .name("Doc User")
      .email("docuser@example.com")
      .password("password123")
      .documentId("UNIQUE_DOC_ID_1")
      .build();

    entityManager.persist(user);
    entityManager.flush();

    Optional<User> foundUser = userRepository.findByDocumentId(
      "UNIQUE_DOC_ID_1"
    );

    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getName()).isEqualTo("Doc User");
    assertThat(foundUser.get().getDocumentId()).isEqualTo("UNIQUE_DOC_ID_1");
  }

  @Test
  void findByDocumentId_shouldReturnEmpty_whenDocumentIdDoesNotExist() {
    Optional<User> foundUser = userRepository.findByDocumentId(
      "NONEXISTENT_DOC_ID"
    );
    assertThat(foundUser).isEmpty();
  }

  @Test
  void existsByDocumentId_shouldReturnTrue_whenDocumentIdExists() {
    User user = User.builder()
      .name("Doc Exists User")
      .email("docexists@example.com")
      .password("password123")
      .documentId("UNIQUE_DOC_ID_2")
      .build();

    entityManager.persist(user);
    entityManager.flush();

    boolean docExists = userRepository.existsByDocumentId("UNIQUE_DOC_ID_2");
    assertThat(docExists).isTrue();
  }

  @Test
  void existsByDocumentId_shouldReturnFalse_whenDocumentIdDoesNotExist() {
    boolean docExists = userRepository.existsByDocumentId(
      "NONEXISTENT_DOC_ID_FOR_EXISTS"
    );
    assertThat(docExists).isFalse();
  }
}
