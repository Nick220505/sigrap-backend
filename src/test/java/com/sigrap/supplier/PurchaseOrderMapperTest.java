package com.sigrap.supplier;

import static org.assertj.core.api.Assertions.assertThat;

import com.sigrap.product.Product;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PurchaseOrderMapperTest {

  private PurchaseOrder purchaseOrder;
  private PurchaseOrderData purchaseOrderData;
  private Supplier supplier;
  private Product product;
  private PurchaseOrderItem purchaseOrderItem;
  private LocalDateTime now;

  @BeforeEach
  void setUp() {
    now = LocalDateTime.now();

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

    purchaseOrderItem = PurchaseOrderItem.builder()
      .id(1)
      .product(product)
      .quantity(10)
      .unitPrice(new BigDecimal("10.00"))
      .totalPrice(new BigDecimal("100.00"))
      .build();

    List<PurchaseOrderItem> items = new ArrayList<>();
    items.add(purchaseOrderItem);

    purchaseOrder = PurchaseOrder.builder()
      .id(1)
      .supplier(supplier)
      .deliveryDate(LocalDate.now().plusDays(10))
      .status(PurchaseOrderStatus.DRAFT)
      .totalAmount(new BigDecimal("100.00"))
      .items(items)
      .createdAt(now)
      .updatedAt(now)
      .build();

    purchaseOrderItem.setPurchaseOrder(purchaseOrder);

    purchaseOrderData = PurchaseOrderData.builder()
      .supplierId(1L)
      .deliveryDate(LocalDate.now().plusDays(10))
      .items(
        List.of(
          PurchaseOrderItemData.builder()
            .productId(1)
            .quantity(10)
            .unitPrice(new BigDecimal("10.00"))
            .build()
        )
      )
      .build();
  }

  @Test
  void toEntity_shouldMapDataToEntity() {
    PurchaseOrderMapperImpl mapper = new PurchaseOrderMapperImpl();
    PurchaseOrder result = mapper.toEntity(purchaseOrderData);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNull();
    assertThat(result.getStatus()).isEqualTo(PurchaseOrderStatus.DRAFT);
    assertThat(result.getDeliveryDate()).isEqualTo(
      purchaseOrderData.getDeliveryDate()
    );
    assertThat(result.getTotalAmount()).isEqualTo(BigDecimal.ZERO);
    assertThat(result.getItems()).isEmpty();
  }

  @Test
  void updateEntityFromData_shouldUpdateEntityWithData() {
    PurchaseOrderMapperImpl mapper = new PurchaseOrderMapperImpl();
    LocalDate newDeliveryDate = LocalDate.now().plusDays(15);
    PurchaseOrderData updateData = PurchaseOrderData.builder()
      .supplierId(2L)
      .deliveryDate(newDeliveryDate)
      .items(List.of())
      .build();

    mapper.updateEntityFromData(updateData, purchaseOrder);

    assertThat(purchaseOrder.getDeliveryDate()).isEqualTo(newDeliveryDate);
  }
}
