package com.sigrap.supplier;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupplierMapperTest {

  private SupplierMapper supplierMapper;

  private Supplier supplier;
  private SupplierInfo supplierInfo;
  private SupplierData supplierData;

  @BeforeEach
  void setUp() {
    supplierMapper = new SupplierMapperImpl();

    supplier = Supplier.builder()
      .id(1L)
      .name("Test Supplier")
      .email("supplier@test.com")
      .address("123 Test St")
      .build();

    supplierInfo = SupplierInfo.builder()
      .id(1L)
      .name("Test Supplier")
      .email("supplier@test.com")
      .address("123 Test St")
      .build();

    supplierData = SupplierData.builder()
      .name("New Supplier")
      .email("new@test.com")
      .address("456 New St")
      .build();
  }

  @Test
  void toInfo_shouldMapEntityToInfo() {
    SupplierInfo result = supplierMapper.toInfo(supplier);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(supplier.getId());
    assertThat(result.getName()).isEqualTo(supplier.getName());
    assertThat(result.getEmail()).isEqualTo(supplier.getEmail());
    assertThat(result.getAddress()).isEqualTo(supplier.getAddress());
  }

  @Test
  void toEntity_shouldMapDataToEntity() {
    Supplier result = supplierMapper.toEntity(supplierData);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNull();
    assertThat(result.getName()).isEqualTo(supplierData.getName());
    assertThat(result.getEmail()).isEqualTo(supplierData.getEmail());
    assertThat(result.getAddress()).isEqualTo(supplierData.getAddress());
  }

  @Test
  void updateEntityFromData_shouldUpdateEntityWithData() {
    supplierMapper.updateEntityFromData(supplierData, supplier);

    assertThat(supplier.getId()).isEqualTo(1L);
    assertThat(supplier.getName()).isEqualTo(supplierData.getName());
    assertThat(supplier.getEmail()).isEqualTo(supplierData.getEmail());
    assertThat(supplier.getAddress()).isEqualTo(supplierData.getAddress());
  }
}
