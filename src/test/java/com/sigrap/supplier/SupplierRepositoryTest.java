package com.sigrap.supplier;

import static org.assertj.core.api.Assertions.assertThat;

import com.sigrap.config.RepositoryTestConfiguration;
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
class SupplierRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private SupplierRepository supplierRepository;

  @Test
  void findById_shouldReturnSupplier_whenExists() {
    // Given
    Supplier supplier = Supplier.builder()
      .name("Test Supplier")
      .contactPerson("John Contact")
      .phone("1234567890")
      .email("supplier@example.com")
      .build();

    Supplier savedSupplier = entityManager.persistAndFlush(supplier);

    // When
    Optional<Supplier> foundSupplierOptional = supplierRepository.findById(
      savedSupplier.getId()
    );

    // Then
    assertThat(foundSupplierOptional).isPresent();
    Supplier foundSupplier = foundSupplierOptional.get();
    assertThat(foundSupplier.getId()).isEqualTo(savedSupplier.getId());
    assertThat(foundSupplier.getName()).isEqualTo("Test Supplier");
    assertThat(foundSupplier.getContactPerson()).isEqualTo("John Contact");
    assertThat(foundSupplier.getPhone()).isEqualTo("1234567890");
    assertThat(foundSupplier.getEmail()).isEqualTo("supplier@example.com");
  }

  @Test
  void findById_shouldReturnEmpty_whenNotExists() {
    // When
    Optional<Supplier> foundSupplier = supplierRepository.findById(999L);

    // Then
    assertThat(foundSupplier).isEmpty();
  }

  @Test
  void findAll_shouldReturnAllSuppliers() {
    // Given
    Supplier supplier1 = Supplier.builder()
      .name("Supplier 1")
      .email("supplier1@example.com")
      .build();

    Supplier supplier2 = Supplier.builder()
      .name("Supplier 2")
      .email("supplier2@example.com")
      .build();

    entityManager.persistAndFlush(supplier1);
    entityManager.persistAndFlush(supplier2);

    // When
    List<Supplier> suppliers = supplierRepository.findAll();

    // Then
    assertThat(suppliers).hasSize(2);
    assertThat(suppliers)
      .extracting(Supplier::getName)
      .containsExactlyInAnyOrder("Supplier 1", "Supplier 2");
  }

  @Test
  void save_shouldPersistSupplier() {
    // Given
    Supplier supplier = Supplier.builder()
      .name("New Supplier")
      .contactPerson("Jane Contact")
      .phone("9876543210")
      .email("new.supplier@example.com")
      .address("123 Supplier St")
      .website("http://supplier.com")
      .productsProvided("Office supplies")
      .averageDeliveryTime(5)
      .paymentTerms("Net 30")
      .build();

    // When
    Supplier savedSupplier = supplierRepository.save(supplier);

    // Then
    assertThat(savedSupplier.getId()).isNotNull();

    Supplier persistedSupplier = entityManager.find(
      Supplier.class,
      savedSupplier.getId()
    );
    assertThat(persistedSupplier).isNotNull();
    assertThat(persistedSupplier.getName()).isEqualTo("New Supplier");
    assertThat(persistedSupplier.getContactPerson()).isEqualTo("Jane Contact");
    assertThat(persistedSupplier.getPhone()).isEqualTo("9876543210");
    assertThat(persistedSupplier.getEmail()).isEqualTo(
      "new.supplier@example.com"
    );
    assertThat(persistedSupplier.getAddress()).isEqualTo("123 Supplier St");
    assertThat(persistedSupplier.getWebsite()).isEqualTo("http://supplier.com");
    assertThat(persistedSupplier.getProductsProvided()).isEqualTo(
      "Office supplies"
    );
    assertThat(persistedSupplier.getAverageDeliveryTime()).isEqualTo(5);
    assertThat(persistedSupplier.getPaymentTerms()).isEqualTo("Net 30");
  }

  @Test
  void delete_shouldRemoveSupplier() {
    // Given
    Supplier supplier = Supplier.builder()
      .name("Supplier to Delete")
      .email("delete.me@example.com")
      .build();

    Supplier savedSupplier = entityManager.persistAndFlush(supplier);

    // When
    supplierRepository.deleteById(savedSupplier.getId());
    entityManager.flush();

    // Then
    Supplier deletedSupplier = entityManager.find(
      Supplier.class,
      savedSupplier.getId()
    );
    assertThat(deletedSupplier).isNull();
  }
}
