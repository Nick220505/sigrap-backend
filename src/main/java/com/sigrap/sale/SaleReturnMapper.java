package com.sigrap.sale;

import com.sigrap.customer.CustomerMapper;
import com.sigrap.product.Product;
import com.sigrap.product.ProductMapper;
import com.sigrap.user.UserMapper;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between SaleReturn entities and DTOs.
 */
@Component
@RequiredArgsConstructor
public class SaleReturnMapper {

  private final UserMapper userMapper;
  private final CustomerMapper customerMapper;
  private final ProductMapper productMapper;

  /**
   * Convert a SaleReturn entity to a SaleReturnInfo DTO.
   *
   * @param saleReturn The SaleReturn entity to convert
   * @return The corresponding SaleReturnInfo DTO
   */
  public SaleReturnInfo toInfo(SaleReturn saleReturn) {
    if (saleReturn == null) {
      return null;
    }

    return SaleReturnInfo.builder()
      .id(saleReturn.getId())
      .originalSaleId(
        saleReturn.getOriginalSale() != null
          ? saleReturn.getOriginalSale().getId()
          : null
      )
      .totalReturnAmount(saleReturn.getTotalReturnAmount())
      .customer(customerMapper.toCustomerInfo(saleReturn.getCustomer()))
      .employee(userMapper.toInfo(saleReturn.getEmployee()))
      .items(toSaleReturnItemInfoList(saleReturn.getItems()))
      .reason(saleReturn.getReason())
      .createdAt(saleReturn.getCreatedAt())
      .updatedAt(saleReturn.getUpdatedAt())
      .build();
  }

  /**
   * Convert a list of SaleReturn entities to a list of SaleReturnInfo DTOs.
   *
   * @param saleReturns The list of SaleReturn entities to convert
   * @return The corresponding list of SaleReturnInfo DTOs
   */
  public List<SaleReturnInfo> toInfoList(List<SaleReturn> saleReturns) {
    if (saleReturns == null) {
      return Collections.emptyList();
    }
    return saleReturns.stream().map(this::toInfo).toList();
  }

  /**
   * Convert a SaleReturnItem entity to a SaleReturnItemInfo DTO.
   *
   * @param saleReturnItem The SaleReturnItem entity to convert
   * @return The corresponding SaleReturnItemInfo DTO
   */
  public SaleReturnItemInfo toSaleReturnItemInfo(
    SaleReturnItem saleReturnItem
  ) {
    if (saleReturnItem == null) {
      return null;
    }
    return SaleReturnItemInfo.builder()
      .id(saleReturnItem.getId())
      .product(productMapper.toInfo(saleReturnItem.getProduct()))
      .quantity(saleReturnItem.getQuantity())
      .unitPrice(saleReturnItem.getUnitPrice())
      .subtotal(saleReturnItem.getSubtotal())
      .build();
  }

  /**
   * Convert a list of SaleReturnItem entities to a list of SaleReturnItemInfo DTOs.
   *
   * @param saleReturnItems The list of SaleReturnItem entities to convert
   * @return The corresponding list of SaleReturnItemInfo DTOs
   */
  public List<SaleReturnItemInfo> toSaleReturnItemInfoList(
    List<SaleReturnItem> saleReturnItems
  ) {
    if (saleReturnItems == null) {
      return Collections.emptyList();
    }
    return saleReturnItems.stream().map(this::toSaleReturnItemInfo).toList();
  }

  /**
   * Convert a SaleReturnData DTO to a SaleReturn entity.
   * OriginalSale, Customer, and Employee are set separately.
   *
   * @param saleReturnData The SaleReturnData DTO to convert
   * @return The corresponding SaleReturn entity
   */
  public SaleReturn toEntity(SaleReturnData saleReturnData) {
    if (saleReturnData == null) {
      return null;
    }
    return SaleReturn.builder()
      .totalReturnAmount(saleReturnData.getTotalReturnAmount())
      .reason(saleReturnData.getReason())
      .build();
  }

  /**
   * Convert a SaleReturnItemData DTO to a SaleReturnItem entity.
   * Product is set separately.
   *
   * @param saleReturnItemData The SaleReturnItemData DTO to convert
   * @return The corresponding SaleReturnItem entity
   */
  public SaleReturnItem toSaleReturnItemEntity(
    SaleReturnItemData saleReturnItemData
  ) {
    if (saleReturnItemData == null) {
      return null;
    }
    return SaleReturnItem.builder()
      .quantity(saleReturnItemData.getQuantity())
      .unitPrice(saleReturnItemData.getUnitPrice())
      .subtotal(saleReturnItemData.getSubtotal())
      .build();
  }

  /**
   * Convert a list of SaleReturnItemData DTOs to a list of SaleReturnItem entities.
   * Products are set separately.
   *
   * @param saleReturnItemDataList The list of SaleReturnItemData DTOs to convert
   * @param saleReturn The SaleReturn entity to associate with the SaleReturnItem entities
   * @param getProduct Function to retrieve Product entities by ID
   * @return The corresponding list of SaleReturnItem entities
   */
  public List<SaleReturnItem> toSaleReturnItemEntityList(
    List<SaleReturnItemData> saleReturnItemDataList,
    SaleReturn saleReturn,
    IntFunction<Product> getProduct
  ) {
    if (saleReturnItemDataList == null) {
      return Collections.emptyList();
    }
    return saleReturnItemDataList
      .stream()
      .map(itemData -> {
        SaleReturnItem saleReturnItem = toSaleReturnItemEntity(itemData);
        saleReturnItem.setSaleReturn(saleReturn);
        saleReturnItem.setProduct(getProduct.apply(itemData.getProductId()));
        return saleReturnItem;
      })
      .toList();
  }

  /**
   * Update a SaleReturn entity with data from a SaleReturnData DTO.
   * OriginalSale, Customer, and Employee are updated separately.
   *
   * @param saleReturn The SaleReturn entity to update
   * @param saleReturnData The SaleReturnData DTO containing the new data
   */
  public void updateEntityFromData(
    SaleReturn saleReturn,
    SaleReturnData saleReturnData
  ) {
    if (saleReturn == null || saleReturnData == null) {
      return;
    }
    saleReturn.setTotalReturnAmount(saleReturnData.getTotalReturnAmount());
    saleReturn.setReason(saleReturnData.getReason());
  }
}
