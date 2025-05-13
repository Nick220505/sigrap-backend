package com.sigrap.supplier;

import com.sigrap.common.mapping.DataToEntity;
import com.sigrap.common.mapping.EntityToInfo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for converting between Supplier entity and its DTO representations.
 * Implemented automatically by MapStruct.
 */
@Mapper(componentModel = "spring")
public interface SupplierMapper {
  /**
   * Converts a Supplier entity to its info representation.
   *
   * @param supplier The Supplier entity to convert
   * @return SupplierInfo containing the supplier data
   */
  @EntityToInfo
  SupplierInfo toInfo(Supplier supplier);

  /**
   * Creates a new Supplier entity from supplier data.
   *
   * @param supplierData The data to create the supplier from
   * @return A new Supplier entity
   */
  @DataToEntity
  Supplier toEntity(SupplierData supplierData);

  /**
   * Updates an existing Supplier entity with new data.
   *
   * @param supplierData The new data to update the supplier with
   * @param supplier The existing Supplier entity to update
   */
  @DataToEntity
  void updateEntityFromData(
    SupplierData supplierData,
    @MappingTarget Supplier supplier
  );
}
