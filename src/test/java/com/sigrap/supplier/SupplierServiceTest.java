package com.sigrap.supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

  @Mock
  private SupplierRepository supplierRepository;

  @Mock
  private SupplierMapper supplierMapper;

  @InjectMocks
  private SupplierService supplierService;

  @Test
  void findAll_shouldReturnAllSuppliers() {
    Supplier supplier1 = Supplier.builder().id(1L).name("Supplier 1").build();

    Supplier supplier2 = Supplier.builder().id(2L).name("Supplier 2").build();

    List<Supplier> suppliers = List.of(supplier1, supplier2);

    SupplierInfo supplierInfo1 = SupplierInfo.builder()
      .id(1L)
      .name("Supplier 1")
      .build();

    SupplierInfo supplierInfo2 = SupplierInfo.builder()
      .id(2L)
      .name("Supplier 2")
      .build();

    when(supplierRepository.findAll()).thenReturn(suppliers);
    when(supplierMapper.toInfo(supplier1)).thenReturn(supplierInfo1);
    when(supplierMapper.toInfo(supplier2)).thenReturn(supplierInfo2);

    List<SupplierInfo> result = supplierService.findAll();

    assertThat(result).hasSize(2);
    assertThat(result).containsExactly(supplierInfo1, supplierInfo2);
  }

  @Test
  void findById_shouldReturnSupplier_whenExists() {
    Long id = 1L;

    Supplier supplier = Supplier.builder()
      .id(id)
      .name("Test Supplier")
      .contactPerson("John Contact")
      .phone("1234567890")
      .email("supplier@example.com")
      .build();

    SupplierInfo expectedSupplierInfo = SupplierInfo.builder()
      .id(id)
      .name("Test Supplier")
      .contactPerson("John Contact")
      .phone("1234567890")
      .email("supplier@example.com")
      .build();

    when(supplierRepository.findById(id)).thenReturn(Optional.of(supplier));
    when(supplierMapper.toInfo(supplier)).thenReturn(expectedSupplierInfo);

    SupplierInfo result = supplierService.findById(id);

    assertThat(result).isEqualTo(expectedSupplierInfo);
  }

  @Test
  void findById_shouldThrowEntityNotFoundException_whenNotExists() {
    Long id = 1L;
    when(supplierRepository.findById(id)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> supplierService.findById(id)
    );

    assertThat(exception.getMessage()).isEqualTo(
      "Supplier not found with id: " + id
    );
  }

  @Test
  void create_shouldCreateSupplier() {
    SupplierData supplierData = SupplierData.builder()
      .name("New Supplier")
      .contactPerson("Jane Contact")
      .phone("9876543210")
      .email("new.supplier@example.com")
      .build();

    Supplier supplierEntity = Supplier.builder()
      .name("New Supplier")
      .contactPerson("Jane Contact")
      .phone("9876543210")
      .email("new.supplier@example.com")
      .build();

    Supplier savedSupplier = Supplier.builder()
      .id(1L)
      .name("New Supplier")
      .contactPerson("Jane Contact")
      .phone("9876543210")
      .email("new.supplier@example.com")
      .build();

    SupplierInfo expectedSupplierInfo = SupplierInfo.builder()
      .id(1L)
      .name("New Supplier")
      .contactPerson("Jane Contact")
      .phone("9876543210")
      .email("new.supplier@example.com")
      .build();

    when(supplierMapper.toEntity(supplierData)).thenReturn(supplierEntity);
    when(supplierRepository.save(supplierEntity)).thenReturn(savedSupplier);
    when(supplierMapper.toInfo(savedSupplier)).thenReturn(expectedSupplierInfo);

    SupplierInfo result = supplierService.create(supplierData);

    assertThat(result).isEqualTo(expectedSupplierInfo);
    verify(supplierRepository).save(supplierEntity);
  }

  @Test
  void update_shouldUpdateSupplier_whenExists() {
    Long id = 1L;

    SupplierData supplierData = SupplierData.builder()
      .name("Updated Supplier")
      .contactPerson("Updated Contact")
      .phone("5555555555")
      .email("updated@example.com")
      .build();

    Supplier existingSupplier = Supplier.builder()
      .id(id)
      .name("Original Supplier")
      .contactPerson("Original Contact")
      .phone("1234567890")
      .email("original@example.com")
      .build();

    Supplier updatedSupplier = Supplier.builder()
      .id(id)
      .name("Updated Supplier")
      .contactPerson("Updated Contact")
      .phone("5555555555")
      .email("updated@example.com")
      .build();

    SupplierInfo expectedSupplierInfo = SupplierInfo.builder()
      .id(id)
      .name("Updated Supplier")
      .contactPerson("Updated Contact")
      .phone("5555555555")
      .email("updated@example.com")
      .build();

    when(supplierRepository.findById(id)).thenReturn(
      Optional.of(existingSupplier)
    );
    doNothing()
      .when(supplierMapper)
      .updateEntityFromData(supplierData, existingSupplier);
    when(supplierRepository.save(existingSupplier)).thenReturn(updatedSupplier);
    when(supplierMapper.toInfo(updatedSupplier)).thenReturn(
      expectedSupplierInfo
    );

    SupplierInfo result = supplierService.update(id, supplierData);

    assertThat(result).isEqualTo(expectedSupplierInfo);
    verify(supplierMapper).updateEntityFromData(supplierData, existingSupplier);
    verify(supplierRepository).save(existingSupplier);
  }

  @Test
  void update_shouldThrowEntityNotFoundException_whenNotExists() {
    Long id = 1L;
    SupplierData supplierData = SupplierData.builder()
      .name("Updated Supplier")
      .build();

    when(supplierRepository.findById(id)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> supplierService.update(id, supplierData)
    );

    assertThat(exception.getMessage()).isEqualTo(
      "Supplier not found with id: " + id
    );
    verify(supplierRepository, never()).save(any());
  }

  @Test
  void delete_shouldDeleteSupplier_whenExists() {
    Long id = 1L;

    Supplier supplier = Supplier.builder()
      .id(id)
      .name("Supplier to Delete")
      .build();

    when(supplierRepository.findById(id)).thenReturn(Optional.of(supplier));

    supplierService.delete(id);

    verify(supplierRepository).delete(supplier);
  }

  @Test
  void delete_shouldThrowEntityNotFoundException_whenNotExists() {
    Long id = 1L;
    when(supplierRepository.findById(id)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> supplierService.delete(id)
    );

    assertThat(exception.getMessage()).isEqualTo(
      "Supplier not found with id: " + id
    );
    verify(supplierRepository, never()).delete(any());
  }

  @Test
  void deleteAllById_shouldDeleteAllSuppliers_whenAllExist() {
    List<Long> ids = Arrays.asList(1L, 2L);

    when(supplierRepository.existsById(1L)).thenReturn(true);
    when(supplierRepository.existsById(2L)).thenReturn(true);

    supplierService.deleteAllById(ids);

    verify(supplierRepository).deleteAllById(ids);
  }

  @Test
  void deleteAllById_shouldThrowEntityNotFoundException_whenOneNotExists() {
    List<Long> ids = Arrays.asList(1L, 2L);

    when(supplierRepository.existsById(1L)).thenReturn(true);
    when(supplierRepository.existsById(2L)).thenReturn(false);

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> supplierService.deleteAllById(ids)
    );

    assertThat(exception.getMessage()).isEqualTo(
      "Supplier with id 2 not found"
    );
    verify(supplierRepository, never()).deleteAllById(any());
  }
}
