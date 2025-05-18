package com.sigrap.supplier;

import static org.assertj.core.api.Assertions.assertThat;

import com.sigrap.config.RepositoryTestConfiguration;
import com.sigrap.product.Product;
import com.sigrap.product.ProductRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(RepositoryTestConfiguration.class)
class PurchaseOrderItemRepositoryTest {

  @Autowired
  private PurchaseOrderItemRepository purchaseOrderItemRepository;

  @Autowired
  private PurchaseOrderRepository purchaseOrderRepository;

  @Autowired
  private SupplierRepository supplierRepository;

  @Autowired
  private ProductRepository productRepository;

  private PurchaseOrder savedPurchaseOrder;
  private PurchaseOrderItem savedPurchaseOrderItem;
  private Supplier savedSupplier;
  private Product savedProduct;

  @BeforeEach
  void setUp() {
    purchaseOrderItemRepository.deleteAll();
    purchaseOrderRepository.deleteAll();

    Supplier supplier = Supplier.builder()
      .name("Test Supplier")
      .email("supplier@test.com")
      .address("123 Test St")
      .build();
    savedSupplier = supplierRepository.save(supplier);

    Product product = Product.builder()
      .name("Test Product")
      .description("Test Description")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .stock(100)
      .minimumStockThreshold(10)
      .build();
    savedProduct = productRepository.save(product);

    PurchaseOrder purchaseOrder = PurchaseOrder.builder()
      .supplier(savedSupplier)
      .deliveryDate(LocalDate.now().plusDays(10))
      .status(PurchaseOrderStatus.DRAFT)
      .totalAmount(new BigDecimal("100.00"))
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();
    savedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);

    PurchaseOrderItem item = PurchaseOrderItem.builder()
      .purchaseOrder(savedPurchaseOrder)
      .product(savedProduct)
      .quantity(10)
      .unitPrice(new BigDecimal("10.00"))
      .totalPrice(new BigDecimal("100.00"))
      .build();
    savedPurchaseOrderItem = purchaseOrderItemRepository.save(item);
  }

  @Test
  void findById_shouldReturnPurchaseOrderItem_whenExists() {
    Optional<PurchaseOrderItem> foundItem =
      purchaseOrderItemRepository.findById(savedPurchaseOrderItem.getId());

    assertThat(foundItem).isPresent();
    assertThat(foundItem.get().getId()).isEqualTo(
      savedPurchaseOrderItem.getId()
    );
    assertThat(foundItem.get().getPurchaseOrder().getId()).isEqualTo(
      savedPurchaseOrder.getId()
    );
    assertThat(foundItem.get().getProduct().getId()).isEqualTo(
      savedProduct.getId()
    );
    assertThat(foundItem.get().getQuantity()).isEqualTo(10);
    assertThat(foundItem.get().getUnitPrice()).isEqualByComparingTo(
      new BigDecimal("10.00")
    );
    assertThat(foundItem.get().getTotalPrice()).isEqualByComparingTo(
      new BigDecimal("100.00")
    );
  }

  @Test
  void findById_shouldReturnEmpty_whenNotExists() {
    Optional<PurchaseOrderItem> notFoundItem =
      purchaseOrderItemRepository.findById(999);

    assertThat(notFoundItem).isEmpty();
  }

  @Test
  void findByPurchaseOrderId_shouldReturnPurchaseOrderItems() {
    List<PurchaseOrderItem> items =
      purchaseOrderItemRepository.findByPurchaseOrder_Id(
        savedPurchaseOrder.getId()
      );

    assertThat(items).hasSize(1);
    assertThat(items.get(0).getId()).isEqualTo(savedPurchaseOrderItem.getId());
    assertThat(items.get(0).getPurchaseOrder().getId()).isEqualTo(
      savedPurchaseOrder.getId()
    );
    assertThat(items.get(0).getProduct().getId()).isEqualTo(
      savedProduct.getId()
    );
  }

  @Test
  void save_shouldSavePurchaseOrderItem() {
    PurchaseOrderItem newItem = PurchaseOrderItem.builder()
      .purchaseOrder(savedPurchaseOrder)
      .product(savedProduct)
      .quantity(5)
      .unitPrice(new BigDecimal("20.00"))
      .totalPrice(new BigDecimal("100.00"))
      .build();

    PurchaseOrderItem savedItem = purchaseOrderItemRepository.save(newItem);

    assertThat(savedItem.getId()).isNotNull();
    assertThat(savedItem.getPurchaseOrder().getId()).isEqualTo(
      savedPurchaseOrder.getId()
    );
    assertThat(savedItem.getProduct().getId()).isEqualTo(savedProduct.getId());
    assertThat(savedItem.getQuantity()).isEqualTo(5);
    assertThat(savedItem.getUnitPrice()).isEqualByComparingTo(
      new BigDecimal("20.00")
    );
    assertThat(savedItem.getTotalPrice()).isEqualByComparingTo(
      new BigDecimal("100.00")
    );

    Optional<PurchaseOrderItem> retrievedItem =
      purchaseOrderItemRepository.findById(savedItem.getId());
    assertThat(retrievedItem).isPresent();
    assertThat(retrievedItem.get().getId()).isEqualTo(savedItem.getId());
  }

  @Test
  void delete_shouldDeletePurchaseOrderItem() {
    purchaseOrderItemRepository.delete(savedPurchaseOrderItem);

    Optional<PurchaseOrderItem> deletedItem =
      purchaseOrderItemRepository.findById(savedPurchaseOrderItem.getId());
    assertThat(deletedItem).isEmpty();
  }
}
