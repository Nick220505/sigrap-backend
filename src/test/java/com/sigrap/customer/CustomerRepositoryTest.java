package com.sigrap.customer;

import static org.assertj.core.api.Assertions.assertThat;

import com.sigrap.config.RepositoryTestConfiguration;
import java.time.LocalDateTime;
import java.util.List;
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
class CustomerRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private CustomerRepository customerRepository;

  @Test
  void shouldSaveCustomer() {
    Customer customer = Customer.builder()
      .fullName("Test Customer")
      .email("test@example.com")
      .phoneNumber("1234567890")
      .address("123 Test St")
      .build();

    Customer savedCustomer = customerRepository.save(customer);

    assertThat(savedCustomer.getId()).isNotNull();
    assertThat(savedCustomer.getFullName()).isEqualTo("Test Customer");
    assertThat(savedCustomer.getEmail()).isEqualTo("test@example.com");
    assertThat(savedCustomer.getPhoneNumber()).isEqualTo("1234567890");
    assertThat(savedCustomer.getAddress()).isEqualTo("123 Test St");
    assertThat(savedCustomer.getCreatedAt()).isNotNull();
  }

  @Test
  void shouldFindCustomerById() {
    Customer customer = Customer.builder()
      .fullName("Find Customer")
      .email("find@example.com")
      .phoneNumber("1234567890")
      .address("123 Find St")
      .build();

    Customer savedCustomer = entityManager.persistAndFlush(customer);

    Optional<Customer> foundCustomer = customerRepository.findById(
      savedCustomer.getId()
    );

    assertThat(foundCustomer).isPresent();
    assertThat(foundCustomer.get().getFullName()).isEqualTo("Find Customer");
    assertThat(foundCustomer.get().getEmail()).isEqualTo("find@example.com");
  }

  @Test
  void shouldFindCustomerByEmail() {
    Customer customer = Customer.builder()
      .fullName("Email Customer")
      .email("email@example.com")
      .phoneNumber("1234567890")
      .build();

    entityManager.persistAndFlush(customer);

    Optional<Customer> foundCustomer = customerRepository.findByEmail(
      "email@example.com"
    );

    assertThat(foundCustomer).isPresent();
    assertThat(foundCustomer.get().getFullName()).isEqualTo("Email Customer");
  }

  @Test
  void shouldNotFindCustomerByNonExistentEmail() {
    Optional<Customer> foundCustomer = customerRepository.findByEmail(
      "nonexistent@example.com"
    );

    assertThat(foundCustomer).isEmpty();
  }

  @Test
  void existsByEmail_shouldReturnTrue_whenEmailExists() {
    Customer customer = Customer.builder()
      .fullName("Exists Customer")
      .email("exists@example.com")
      .phoneNumber("1234567890")
      .build();

    entityManager.persistAndFlush(customer);

    boolean exists = customerRepository.existsByEmail("exists@example.com");

    assertThat(exists).isTrue();
  }

  @Test
  void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
    boolean exists = customerRepository.existsByEmail(
      "nonexistent@example.com"
    );

    assertThat(exists).isFalse();
  }

  @Test
  void shouldFindCustomersByNameContaining() {
    Customer customer1 = Customer.builder()
      .fullName("John Doe")
      .email("john@example.com")
      .phoneNumber("1234567890")
      .build();

    Customer customer2 = Customer.builder()
      .fullName("Jane Doe")
      .email("jane@example.com")
      .phoneNumber("0987654321")
      .build();

    Customer customer3 = Customer.builder()
      .fullName("Alice Smith")
      .email("alice@example.com")
      .phoneNumber("1122334455")
      .build();

    entityManager.persistAndFlush(customer1);
    entityManager.persistAndFlush(customer2);
    entityManager.persistAndFlush(customer3);

    List<Customer> foundCustomers =
      customerRepository.findByFullNameContainingIgnoreCase("doe");

    assertThat(foundCustomers).hasSize(2);
    assertThat(foundCustomers)
      .extracting(Customer::getFullName)
      .containsExactlyInAnyOrder("John Doe", "Jane Doe");
  }

  @Test
  void shouldFindCustomersByCreatedDateRange() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime yesterday = now.minusDays(1);
    LocalDateTime twoDaysAgo = now.minusDays(2);
    LocalDateTime threeDaysAgo = now.minusDays(3);

    Customer customer1 = entityManager.persistAndFlush(
      Customer.builder()
        .fullName("Recent Customer")
        .email("recent@example.com")
        .phoneNumber("1234567890")
        .build()
    );

    entityManager
      .getEntityManager()
      .createNativeQuery(
        "UPDATE customers SET created_at = :date WHERE id = :id"
      )
      .setParameter("date", yesterday)
      .setParameter("id", customer1.getId())
      .executeUpdate();

    Customer customer2 = entityManager.persistAndFlush(
      Customer.builder()
        .fullName("Old Customer")
        .email("old@example.com")
        .phoneNumber("0987654321")
        .build()
    );

    entityManager
      .getEntityManager()
      .createNativeQuery(
        "UPDATE customers SET created_at = :date WHERE id = :id"
      )
      .setParameter("date", threeDaysAgo)
      .setParameter("id", customer2.getId())
      .executeUpdate();

    entityManager.clear();

    List<Customer> foundCustomers = customerRepository.findByCreatedAtBetween(
      twoDaysAgo,
      now
    );

    assertThat(foundCustomers).hasSize(1);
    assertThat(foundCustomers.get(0).getEmail()).isEqualTo(
      "recent@example.com"
    );
  }
}
