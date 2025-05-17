package com.sigrap.sale;

import com.sigrap.customer.Customer;
import com.sigrap.customer.CustomerMapper;
import com.sigrap.product.Product;
import com.sigrap.product.ProductMapper;
import com.sigrap.user.User;
import com.sigrap.user.UserMapper;
import java.util.List;
import java.util.stream.Collectors;
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
      .notes(sale.getNotes())
      .paymentMethod(sale.getPaymentMethod())
      .status(sale.getStatus())
      .customer(
        sale.getCustomer() != null
          ? customerMapper.toCustomerInfo(sale.getCustomer())
          : null
      )
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
      return null;
    }

    return sales.stream().map(this::toInfo).collect(Collectors.toList());
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
      .discount(saleItem.getDiscount())
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
      return null;
    }

    return saleItems
      .stream()
      .map(this::toSaleItemInfo)
      .collect(Collectors.toList());
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
      .notes(saleData.getNotes())
      .paymentMethod(saleData.getPaymentMethod())
      .status(
        saleData.getStatus() != null
          ? saleData.getStatus()
          : SaleStatus.COMPLETED
      )
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
      .discount(
        saleItemData.getDiscount() != null ? saleItemData.getDiscount() : null
      )
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
    java.util.function.Function<Integer, Product> getProduct
  ) {
    if (saleItemDataList == null) {
      return null;
    }

    return saleItemDataList
      .stream()
      .map(itemData -> {
        SaleItem saleItem = toSaleItemEntity(itemData);
        saleItem.setSale(sale);
        saleItem.setProduct(getProduct.apply(itemData.getProductId()));
        return saleItem;
      })
      .collect(Collectors.toList());
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
    sale.setNotes(saleData.getNotes());
    sale.setPaymentMethod(saleData.getPaymentMethod());
    if (saleData.getStatus() != null) {
      sale.setStatus(saleData.getStatus());
    }
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
