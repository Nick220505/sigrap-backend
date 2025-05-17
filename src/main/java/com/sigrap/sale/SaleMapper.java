package com.sigrap.sale;

import com.sigrap.customer.Customer;
import com.sigrap.customer.CustomerMapper;
import com.sigrap.product.Product;
import com.sigrap.product.ProductMapper;
import com.sigrap.user.User;
import com.sigrap.user.UserMapper;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between Sale entities and DTOs.
 */
@Component
@RequiredArgsConstructor
public class SaleMapper {

  private final UserMapper userMapper;
  private final CustomerMapper customerMapper;
  private final ProductMapper productMapper;

  /**
   * Convert a Sale entity to a SaleInfo DTO.
   *
   * @param sale The Sale entity to convert
   * @return The corresponding SaleInfo DTO
   */
  public SaleInfo toInfo(Sale sale) {
    if (sale == null) {
      return null;
    }

    return SaleInfo.builder()
      .id(sale.getId())
      .totalAmount(sale.getTotalAmount())
      .taxAmount(sale.getTaxAmount())
      .discountAmount(sale.getDiscountAmount())
      .finalAmount(sale.getFinalAmount())
      .customer(customerMapper.toCustomerInfo(sale.getCustomer()))
      .employee(userMapper.toInfo(sale.getEmployee()))
      .items(toSaleItemInfoList(sale.getItems()))
      .createdAt(sale.getCreatedAt())
      .updatedAt(sale.getUpdatedAt())
      .build();
  }

  /**
   * Convert a list of Sale entities to a list of SaleInfo DTOs.
   *
   * @param sales The list of Sale entities to convert
   * @return The corresponding list of SaleInfo DTOs
   */
  public List<SaleInfo> toInfoList(List<Sale> sales) {
    if (sales == null) {
      return Collections.emptyList();
    }

    return sales.stream().map(this::toInfo).toList();
  }

  /**
   * Convert a SaleItem entity to a SaleItemInfo DTO.
   *
   * @param saleItem The SaleItem entity to convert
   * @return The corresponding SaleItemInfo DTO
   */
  public SaleItemInfo toSaleItemInfo(SaleItem saleItem) {
    if (saleItem == null) {
      return null;
    }

    return SaleItemInfo.builder()
      .id(saleItem.getId())
      .product(productMapper.toInfo(saleItem.getProduct()))
      .quantity(saleItem.getQuantity())
      .unitPrice(saleItem.getUnitPrice())
      .subtotal(saleItem.getSubtotal())
      .build();
  }

  /**
   * Convert a list of SaleItem entities to a list of SaleItemInfo DTOs.
   *
   * @param saleItems The list of SaleItem entities to convert
   * @return The corresponding list of SaleItemInfo DTOs
   */
  public List<SaleItemInfo> toSaleItemInfoList(List<SaleItem> saleItems) {
    if (saleItems == null) {
      return Collections.emptyList();
    }

    return saleItems.stream().map(this::toSaleItemInfo).toList();
  }

  /**
   * Convert a SaleData DTO to a Sale entity.
   * Customer and employee are set separately.
   *
   * @param saleData The SaleData DTO to convert
   * @return The corresponding Sale entity
   */
  public Sale toEntity(SaleData saleData) {
    if (saleData == null) {
      return null;
    }

    return Sale.builder()
      .totalAmount(saleData.getTotalAmount())
      .taxAmount(saleData.getTaxAmount())
      .discountAmount(
        saleData.getDiscountAmount() != null
          ? saleData.getDiscountAmount()
          : null
      )
      .finalAmount(saleData.getFinalAmount())
      .build();
  }

  /**
   * Convert a SaleItemData DTO to a SaleItem entity.
   * Sale and product are set separately.
   *
   * @param saleItemData The SaleItemData DTO to convert
   * @return The corresponding SaleItem entity
   */
  public SaleItem toSaleItemEntity(SaleItemData saleItemData) {
    if (saleItemData == null) {
      return null;
    }

    return SaleItem.builder()
      .quantity(saleItemData.getQuantity())
      .unitPrice(saleItemData.getUnitPrice())
      .subtotal(saleItemData.getSubtotal())
      .build();
  }

  /**
   * Convert a list of SaleItemData DTOs to a list of SaleItem entities.
   * Products are set separately.
   *
   * @param saleItemDataList The list of SaleItemData DTOs to convert
   * @param sale The Sale entity to associate with the SaleItem entities
   * @param getProduct Function to retrieve Product entities by ID
   * @return The corresponding list of SaleItem entities
   */
  public List<SaleItem> toSaleItemEntityList(
    List<SaleItemData> saleItemDataList,
    Sale sale,
    IntFunction<Product> getProduct
  ) {
    if (saleItemDataList == null) {
      return Collections.emptyList();
    }

    return saleItemDataList
      .stream()
      .map(itemData -> {
        SaleItem saleItem = toSaleItemEntity(itemData);
        saleItem.setSale(sale);
        saleItem.setProduct(getProduct.apply(itemData.getProductId()));
        return saleItem;
      })
      .toList();
  }

  /**
   * Update a Sale entity with data from a SaleData DTO.
   * Customer and employee are updated separately.
   *
   * @param sale The Sale entity to update
   * @param saleData The SaleData DTO containing the new data
   */
  public void updateEntityFromData(Sale sale, SaleData saleData) {
    if (sale == null || saleData == null) {
      return;
    }

    sale.setTotalAmount(saleData.getTotalAmount());
    sale.setTaxAmount(saleData.getTaxAmount());
    if (saleData.getDiscountAmount() != null) {
      sale.setDiscountAmount(saleData.getDiscountAmount());
    }
    sale.setFinalAmount(saleData.getFinalAmount());
  }

  /**
   * Set the customer and employee for a Sale entity.
   *
   * @param sale The Sale entity to update
   * @param customer The Customer entity to set
   * @param employee The User entity to set as employee
   */
  public void setCustomerAndEmployee(
    Sale sale,
    Customer customer,
    User employee
  ) {
    if (sale == null) {
      return;
    }

    sale.setCustomer(customer);
    sale.setEmployee(employee);
  }
}
