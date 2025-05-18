package com.sigrap.supplier;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.sigrap.common.mapping.DataToEntity;
import com.sigrap.common.mapping.EntityToInfo;
import com.sigrap.product.Product;
import com.sigrap.product.ProductMapper;

/**
 * Mapper interface for converting between PurchaseOrderItem entity and its DTO representations.
 * Uses MapStruct for automatic implementation.
 */
@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface PurchaseOrderItemMapper {
  /**
   * Converts a PurchaseOrderItem entity to its info representation.
   *
   * @param purchaseOrderItem The PurchaseOrderItem entity to convert
   * @return PurchaseOrderItemInfo containing the purchase order item data
   */
  @EntityToInfo
  @Mapping(target = "purchaseOrderId", source = "purchaseOrder.id")
  PurchaseOrderItemInfo toInfo(PurchaseOrderItem purchaseOrderItem);

  /**
   * Converts a list of PurchaseOrderItem entities to a list of PurchaseOrderItemInfo DTOs.
   *
   * @param purchaseOrderItems The list of PurchaseOrderItem entities to convert
   * @return List of PurchaseOrderItemInfo DTOs
   */
  List<PurchaseOrderItemInfo> toInfoList(
    List<PurchaseOrderItem> purchaseOrderItems
  );

  /**
   * Creates a new PurchaseOrderItem entity from purchase order item data.
   * Product and purchase order are handled separately.
   *
   * @param purchaseOrderItemData The data to create the purchase order item from
   * @return A new PurchaseOrderItem entity
   */
  @DataToEntity
  @Mapping(target = "product", ignore = true)
  @Mapping(target = "purchaseOrder", ignore = true)
  @Mapping(target = "receivedQuantity", constant = "0")
  @Mapping(target = "totalPrice", ignore = true)
  PurchaseOrderItem toEntity(PurchaseOrderItemData purchaseOrderItemData);

  /**
   * Updates an existing PurchaseOrderItem entity with new data.
   * Product and purchase order are handled separately.
   *
   * @param purchaseOrderItemData The new data to update with
   * @param purchaseOrderItem The existing PurchaseOrderItem entity to update
   */
  @DataToEntity
  @Mapping(target = "product", ignore = true)
  @Mapping(target = "purchaseOrder", ignore = true)
  @Mapping(target = "receivedQuantity", ignore = true)
  @Mapping(target = "totalPrice", ignore = true)
  void updateEntityFromData(
    PurchaseOrderItemData purchaseOrderItemData,
    @MappingTarget PurchaseOrderItem purchaseOrderItem
  );

  /**
   * Helper method to map product ID to Product entity.
   * Used internally by MapStruct for product relationships.
   *
   * @param productId The ID of the product to map
   * @return A Product entity with the given ID, or null if ID is null
   */
  default Product mapProduct(Integer productId) {
    if (productId == null) {
      return null;
    }
    return Product.builder().id(productId).build();
  }
}
