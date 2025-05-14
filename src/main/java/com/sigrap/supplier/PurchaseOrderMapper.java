package com.sigrap.supplier;

import com.sigrap.common.mapping.DataToEntity;
import com.sigrap.common.mapping.EntityToInfo;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for converting between PurchaseOrder entity and its DTO representations.
 * Uses MapStruct for automatic implementation.
 */
@Mapper(
  componentModel = "spring",
  uses = {
    PurchaseOrderItemMapper.class,
    SupplierMapper.class,
    PurchaseOrderTrackingEventMapper.class,
  }
)
public interface PurchaseOrderMapper {
  /**
   * Converts a PurchaseOrder entity to its info representation.
   *
   * @param purchaseOrder The PurchaseOrder entity to convert
   * @return PurchaseOrderInfo containing the purchase order data
   */
  @EntityToInfo
  @Mapping(
    target = "status",
    expression = "java(purchaseOrder.getStatus().name())"
  )
  PurchaseOrderInfo toInfo(PurchaseOrder purchaseOrder);

  /**
   * Converts a list of PurchaseOrder entities to a list of PurchaseOrderInfo DTOs.
   *
   * @param purchaseOrders The list of PurchaseOrder entities to convert
   * @return List of PurchaseOrderInfo DTOs
   */
  List<PurchaseOrderInfo> toInfoList(List<PurchaseOrder> purchaseOrders);

  /**
   * Creates a new PurchaseOrder entity from purchase order data.
   * Supplier and items are handled separately.
   *
   * @param purchaseOrderData The data to create the purchase order from
   * @return A new PurchaseOrder entity
   */
  @DataToEntity
  @Mapping(target = "supplier", ignore = true)
  @Mapping(target = "items", ignore = true)
  @Mapping(target = "orderNumber", ignore = true)
  @Mapping(target = "actualDeliveryDate", ignore = true)
  @Mapping(target = "totalAmount", ignore = true)
  @Mapping(target = "status", constant = "DRAFT")
  PurchaseOrder toEntity(PurchaseOrderData purchaseOrderData);

  /**
   * Updates an existing PurchaseOrder entity with new data.
   * Supplier and items are handled separately.
   *
   * @param purchaseOrderData The new data to update with
   * @param purchaseOrder The existing PurchaseOrder entity to update
   */
  @DataToEntity
  @Mapping(target = "supplier", ignore = true)
  @Mapping(target = "items", ignore = true)
  @Mapping(target = "trackingEvents", ignore = true)
  @Mapping(target = "orderNumber", ignore = true)
  @Mapping(target = "actualDeliveryDate", ignore = true)
  @Mapping(target = "shipDate", ignore = true)
  @Mapping(target = "totalAmount", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "removeItem", ignore = true)
  void updateEntityFromData(
    PurchaseOrderData purchaseOrderData,
    @MappingTarget PurchaseOrder purchaseOrder
  );
}
