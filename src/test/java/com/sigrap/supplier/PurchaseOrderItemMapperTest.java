package com.sigrap.supplier;

import static org.assertj.core.api.Assertions.assertThat;

import com.sigrap.product.Product;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PurchaseOrderItemMapperTest {

  private PurchaseOrderItem purchaseOrderItem;
  private PurchaseOrderItemData purchaseOrderItemData;
  private Product product;
  private PurchaseOrder purchaseOrder;
  private Supplier supplier;

  @BeforeEach
  void setUp() {
    supplier = Supplier.builder()
      .id(1L)
      .name("Test Supplier")
      .email("supplier@test.com")
      .build();

    product = Product.builder()
      .id(1)
      .name("Test Product")
      .description("Test Description")
      .costPrice(new BigDecimal("10.00"))
      .build();

    purchaseOrder = PurchaseOrder.builder()
      .id(1)
      .supplier(supplier)
      .deliveryDate(LocalDate.now().plusDays(10))
      .status(PurchaseOrderStatus.DRAFT)
      .totalAmount(new BigDecimal("100.00"))
      .items(new ArrayList<>())
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

    purchaseOrderItem = PurchaseOrderItem.builder()
      .id(1)
      .purchaseOrder(purchaseOrder)
      .product(product)
      .quantity(10)
      .unitPrice(new BigDecimal("10.00"))
      .totalPrice(new BigDecimal("100.00"))
      .build();

    purchaseOrderItemData = PurchaseOrderItemData.builder()
      .productId(1)
      .quantity(10)
      .unitPrice(new BigDecimal("10.00"))
      .build();
  }

  @Test
  void toEntity_shouldCreateEntityWithExpectedValues() {
    BigDecimal quantity = new BigDecimal(purchaseOrderItemData.getQuantity());
    BigDecimal totalPrice = quantity.multiply(
      purchaseOrderItemData.getUnitPrice()
    );

    PurchaseOrderItem item = PurchaseOrderItem.builder()
      .product(product)
      .quantity(purchaseOrderItemData.getQuantity())
      .unitPrice(purchaseOrderItemData.getUnitPrice())
      .totalPrice(totalPrice)
      .build();

    assertThat(item.getQuantity()).isEqualTo(
      purchaseOrderItemData.getQuantity()
    );
    assertThat(item.getUnitPrice()).isEqualTo(
      purchaseOrderItemData.getUnitPrice()
    );
    assertThat(item.getTotalPrice()).isEqualByComparingTo(
      new BigDecimal("100.00")
    );
  }
}
