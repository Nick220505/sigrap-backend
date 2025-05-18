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
class PurchaseOrderRepositoryTest {

  @Autowired
  private PurchaseOrderRepository purchaseOrderRepository;

  @Autowired
  private PurchaseOrderItemRepository purchaseOrderItemRepository;

  @Autowired
  private SupplierRepository supplierRepository;

  @Autowired
  private ProductRepository productRepository;

  private PurchaseOrder savedPurchaseOrder;
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
    purchaseOrderItemRepository.save(item);
  }

  @Test
  void findById_shouldReturnPurchaseOrder_whenExists() {
    Optional<PurchaseOrder> foundPurchaseOrder =
      purchaseOrderRepository.findById(savedPurchaseOrder.getId());

    assertThat(foundPurchaseOrder).isPresent();
    assertThat(foundPurchaseOrder.get().getId()).isEqualTo(
      savedPurchaseOrder.getId()
    );
    assertThat(foundPurchaseOrder.get().getSupplier().getId()).isEqualTo(
      savedSupplier.getId()
    );
    assertThat(foundPurchaseOrder.get().getStatus()).isEqualTo(
      PurchaseOrderStatus.DRAFT
    );
  }

  @Test
  void findById_shouldReturnEmpty_whenNotExists() {
    Optional<PurchaseOrder> notFoundPurchaseOrder =
      purchaseOrderRepository.findById(999);

    assertThat(notFoundPurchaseOrder).isEmpty();
  }

  @Test
  void findBySupplier_Id_shouldReturnPurchaseOrders() {
    List<PurchaseOrder> purchaseOrders =
      purchaseOrderRepository.findBySupplier_Id(
        savedSupplier.getId().intValue()
      );

    assertThat(purchaseOrders).hasSize(1);
    assertThat(purchaseOrders.get(0).getId()).isEqualTo(
      savedPurchaseOrder.getId()
    );
    assertThat(purchaseOrders.get(0).getSupplier().getId()).isEqualTo(
      savedSupplier.getId()
    );
  }

  @Test
  void findByStatus_shouldReturnPurchaseOrders() {
    List<PurchaseOrder> draftOrders = purchaseOrderRepository.findByStatus(
      PurchaseOrderStatus.DRAFT
    );

    assertThat(draftOrders).hasSize(1);
    assertThat(draftOrders.get(0).getId()).isEqualTo(
      savedPurchaseOrder.getId()
    );
    assertThat(draftOrders.get(0).getStatus()).isEqualTo(
      PurchaseOrderStatus.DRAFT
    );

    List<PurchaseOrder> submittedOrders = purchaseOrderRepository.findByStatus(
      PurchaseOrderStatus.SUBMITTED
    );
    assertThat(submittedOrders).isEmpty();
  }

  @Test
  void save_shouldSavePurchaseOrder() {
    PurchaseOrder newPurchaseOrder = PurchaseOrder.builder()
      .supplier(savedSupplier)
      .deliveryDate(LocalDate.now().plusDays(20))
      .status(PurchaseOrderStatus.DRAFT)
      .totalAmount(new BigDecimal("200.00"))
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

    PurchaseOrder savedNewPurchaseOrder = purchaseOrderRepository.save(
      newPurchaseOrder
    );

    assertThat(savedNewPurchaseOrder.getId()).isNotNull();
    assertThat(savedNewPurchaseOrder.getSupplier().getId()).isEqualTo(
      savedSupplier.getId()
    );
    assertThat(savedNewPurchaseOrder.getStatus()).isEqualTo(
      PurchaseOrderStatus.DRAFT
    );
    assertThat(savedNewPurchaseOrder.getTotalAmount()).isEqualByComparingTo(
      new BigDecimal("200.00")
    );

    Optional<PurchaseOrder> retrievedPurchaseOrder =
      purchaseOrderRepository.findById(savedNewPurchaseOrder.getId());
    assertThat(retrievedPurchaseOrder).isPresent();
    assertThat(retrievedPurchaseOrder.get().getId()).isEqualTo(
      savedNewPurchaseOrder.getId()
    );
  }

  @Test
  void delete_shouldDeletePurchaseOrder() {
    purchaseOrderItemRepository.deleteAll();
    purchaseOrderRepository.delete(savedPurchaseOrder);

    Optional<PurchaseOrder> deletedPurchaseOrder =
      purchaseOrderRepository.findById(savedPurchaseOrder.getId());
    assertThat(deletedPurchaseOrder).isEmpty();
  }
}
