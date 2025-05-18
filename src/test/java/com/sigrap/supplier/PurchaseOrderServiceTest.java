package com.sigrap.supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sigrap.product.Product;
import com.sigrap.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

  @Mock
  private PurchaseOrderRepository purchaseOrderRepository;

  @Mock
  private PurchaseOrderItemRepository purchaseOrderItemRepository;

  @Mock
  private SupplierRepository supplierRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private PurchaseOrderMapper purchaseOrderMapper;

  @Mock
  private PurchaseOrderItemMapper purchaseOrderItemMapper;

  @InjectMocks
  private PurchaseOrderService purchaseOrderService;

  private Supplier testSupplier;
  private Product testProduct;
  private PurchaseOrder testPurchaseOrder;
  private PurchaseOrderItem testPurchaseOrderItem;
  private PurchaseOrderInfo testPurchaseOrderInfo;
  private PurchaseOrderData testPurchaseOrderData;
  private PurchaseOrderItemData testPurchaseOrderItemData;

  @BeforeEach
  void setUp() {
    testSupplier = Supplier.builder()
      .id(1L)
      .name("Test Supplier")
      .email("supplier@test.com")
      .build();

    testProduct = Product.builder()
      .id(1)
      .name("Test Product")
      .description("Test Description")
      .costPrice(new BigDecimal("10.00"))
      .build();

    testPurchaseOrderItem = PurchaseOrderItem.builder()
      .id(1)
      .product(testProduct)
      .quantity(10)
      .unitPrice(new BigDecimal("10.00"))
      .totalPrice(new BigDecimal("100.00"))
      .build();

    List<PurchaseOrderItem> items = new ArrayList<>();
    items.add(testPurchaseOrderItem);

    testPurchaseOrder = PurchaseOrder.builder()
      .id(1)
      .supplier(testSupplier)
      .deliveryDate(LocalDate.now().plusDays(10))
      .status(PurchaseOrderStatus.DRAFT)
      .totalAmount(new BigDecimal("100.00"))
      .items(items)
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

    testPurchaseOrderItem.setPurchaseOrder(testPurchaseOrder);

    SupplierInfo supplierInfo = SupplierInfo.builder()
      .id(1L)
      .name("Test Supplier")
      .email("supplier@test.com")
      .build();

    PurchaseOrderItemInfo itemInfo = PurchaseOrderItemInfo.builder()
      .id(1)
      .purchaseOrderId(1)
      .quantity(10)
      .unitPrice(new BigDecimal("10.00"))
      .totalPrice(new BigDecimal("100.00"))
      .build();

    List<PurchaseOrderItemInfo> itemInfos = new ArrayList<>();
    itemInfos.add(itemInfo);

    testPurchaseOrderInfo = PurchaseOrderInfo.builder()
      .id(1)
      .supplier(supplierInfo)
      .deliveryDate(LocalDate.now().plusDays(10))
      .status("DRAFT")
      .totalAmount(new BigDecimal("100.00"))
      .items(itemInfos)
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

    testPurchaseOrderItemData = PurchaseOrderItemData.builder()
      .productId(1)
      .quantity(10)
      .unitPrice(new BigDecimal("10.00"))
      .build();

    List<PurchaseOrderItemData> itemDataList = new ArrayList<>();
    itemDataList.add(testPurchaseOrderItemData);

    testPurchaseOrderData = PurchaseOrderData.builder()
      .supplierId(1L)
      .deliveryDate(LocalDate.now().plusDays(10))
      .items(itemDataList)
      .build();
  }

  @Test
  void findAll_shouldReturnAllPurchaseOrders() {
    when(purchaseOrderRepository.findAll()).thenReturn(
      List.of(testPurchaseOrder)
    );
    when(purchaseOrderMapper.toInfo(testPurchaseOrder)).thenReturn(
      testPurchaseOrderInfo
    );

    List<PurchaseOrderInfo> result = purchaseOrderService.findAll();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(testPurchaseOrderInfo.getId());
    verify(purchaseOrderRepository).findAll();
    verify(purchaseOrderMapper).toInfo(testPurchaseOrder);
  }

  @Test
  void findById_shouldReturnPurchaseOrder_whenExists() {
    when(purchaseOrderRepository.findById(1)).thenReturn(
      Optional.of(testPurchaseOrder)
    );
    when(purchaseOrderMapper.toInfo(testPurchaseOrder)).thenReturn(
      testPurchaseOrderInfo
    );

    PurchaseOrderInfo result = purchaseOrderService.findById(1);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(testPurchaseOrderInfo.getId());
    verify(purchaseOrderRepository).findById(1);
    verify(purchaseOrderMapper).toInfo(testPurchaseOrder);
  }

  @Test
  void findById_shouldThrowException_whenNotExists() {
    when(purchaseOrderRepository.findById(999)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> purchaseOrderService.findById(999))
      .isInstanceOf(EntityNotFoundException.class)
      .hasMessageContaining("Purchase order not found");

    verify(purchaseOrderRepository).findById(999);
  }

  @Test
  void findBySupplierId_shouldReturnPurchaseOrders() {
    when(purchaseOrderRepository.findBySupplier_Id(1)).thenReturn(
      List.of(testPurchaseOrder)
    );
    when(purchaseOrderMapper.toInfoList(List.of(testPurchaseOrder))).thenReturn(
      List.of(testPurchaseOrderInfo)
    );

    List<PurchaseOrderInfo> result = purchaseOrderService.findBySupplierId(1);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(testPurchaseOrderInfo.getId());
    verify(purchaseOrderRepository).findBySupplier_Id(1);
    verify(purchaseOrderMapper).toInfoList(List.of(testPurchaseOrder));
  }

  @Test
  void findByStatus_shouldReturnPurchaseOrders() {
    when(
      purchaseOrderRepository.findByStatus(PurchaseOrderStatus.DRAFT)
    ).thenReturn(List.of(testPurchaseOrder));
    when(purchaseOrderMapper.toInfoList(List.of(testPurchaseOrder))).thenReturn(
      List.of(testPurchaseOrderInfo)
    );

    List<PurchaseOrderInfo> result = purchaseOrderService.findByStatus(
      PurchaseOrderStatus.DRAFT
    );

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(testPurchaseOrderInfo.getId());
    verify(purchaseOrderRepository).findByStatus(PurchaseOrderStatus.DRAFT);
    verify(purchaseOrderMapper).toInfoList(List.of(testPurchaseOrder));
  }

  @Test
  void create_shouldCreatePurchaseOrder() {
    PurchaseOrder newOrder = PurchaseOrder.builder()
      .supplier(testSupplier)
      .deliveryDate(LocalDate.now().plusDays(10))
      .status(PurchaseOrderStatus.DRAFT)
      .totalAmount(BigDecimal.ZERO)
      .items(new ArrayList<>())
      .build();

    when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
    when(purchaseOrderMapper.toEntity(testPurchaseOrderData)).thenReturn(
      newOrder
    );
    when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(
      newOrder
    );
    when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
    when(
      purchaseOrderItemMapper.toEntity(any(PurchaseOrderItemData.class))
    ).thenReturn(testPurchaseOrderItem);
    when(purchaseOrderItemRepository.saveAll(any())).thenReturn(
      List.of(testPurchaseOrderItem)
    );
    when(purchaseOrderMapper.toInfo(any(PurchaseOrder.class))).thenReturn(
      testPurchaseOrderInfo
    );

    PurchaseOrderInfo result = purchaseOrderService.create(
      testPurchaseOrderData
    );

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(testPurchaseOrderInfo.getId());
    verify(supplierRepository).findById(1L);
    verify(purchaseOrderMapper).toEntity(testPurchaseOrderData);
    verify(purchaseOrderRepository, times(2)).save(any(PurchaseOrder.class));
    verify(productRepository).findById(1);
    verify(purchaseOrderItemMapper).toEntity(any(PurchaseOrderItemData.class));
    verify(purchaseOrderItemRepository).saveAll(any());
    verify(purchaseOrderMapper).toInfo(any(PurchaseOrder.class));
  }

  @Test
  void create_shouldThrowException_whenSupplierNotFound() {
    when(supplierRepository.findById(999L)).thenReturn(Optional.empty());

    PurchaseOrderData invalidData = PurchaseOrderData.builder()
      .supplierId(999L)
      .deliveryDate(LocalDate.now().plusDays(10))
      .items(List.of(testPurchaseOrderItemData))
      .build();

    assertThatThrownBy(() -> purchaseOrderService.create(invalidData))
      .isInstanceOf(EntityNotFoundException.class)
      .hasMessageContaining("Supplier not found");

    verify(supplierRepository).findById(999L);
  }

  @Test
  void update_shouldUpdatePurchaseOrder_whenInDraftStatus() {
    PurchaseOrder updatedOrder = new PurchaseOrder();
    updatedOrder.setId(1);
    updatedOrder.setSupplier(testSupplier);
    updatedOrder.setDeliveryDate(LocalDate.now().plusDays(10));
    updatedOrder.setStatus(PurchaseOrderStatus.DRAFT);
    updatedOrder.setTotalAmount(new BigDecimal("100.00"));
    updatedOrder.setItems(new ArrayList<>());

    when(purchaseOrderRepository.findById(1)).thenReturn(
      Optional.of(testPurchaseOrder)
    );
    when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
    when(purchaseOrderRepository.save(any())).thenReturn(updatedOrder);
    doNothing().when(purchaseOrderItemRepository).deleteAll(any());
    when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
    when(purchaseOrderItemMapper.toEntity(any())).thenReturn(
      testPurchaseOrderItem
    );
    when(purchaseOrderMapper.toInfo(any())).thenReturn(testPurchaseOrderInfo);

    PurchaseOrderInfo result = purchaseOrderService.update(
      1,
      testPurchaseOrderData
    );

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(testPurchaseOrderInfo.getId());

    verify(purchaseOrderRepository).findById(1);
    verify(purchaseOrderMapper).updateEntityFromData(any(), any());
    verify(purchaseOrderRepository, atLeastOnce()).save(any());
  }

  @Test
  void update_shouldThrowException_whenPurchaseOrderNotFound() {
    when(purchaseOrderRepository.findById(999)).thenReturn(Optional.empty());

    assertThatThrownBy(() ->
      purchaseOrderService.update(999, testPurchaseOrderData)
    )
      .isInstanceOf(EntityNotFoundException.class)
      .hasMessageContaining("Purchase order not found");

    verify(purchaseOrderRepository).findById(999);
  }

  @Test
  void update_shouldThrowException_whenNotInDraftStatus() {
    PurchaseOrder submittedOrder = PurchaseOrder.builder()
      .id(1)
      .supplier(testSupplier)
      .deliveryDate(LocalDate.now().plusDays(10))
      .status(PurchaseOrderStatus.SUBMITTED)
      .totalAmount(new BigDecimal("100.00"))
      .items(List.of(testPurchaseOrderItem))
      .build();

    when(purchaseOrderRepository.findById(1)).thenReturn(
      Optional.of(submittedOrder)
    );

    assertThatThrownBy(() ->
      purchaseOrderService.update(1, testPurchaseOrderData)
    )
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining(
        "Only purchase orders in DRAFT status can be updated"
      );

    verify(purchaseOrderRepository).findById(1);
  }

  @Test
  void delete_shouldDeletePurchaseOrder_whenInDraftStatus() {
    when(purchaseOrderRepository.findById(1)).thenReturn(
      Optional.of(testPurchaseOrder)
    );
    doNothing().when(purchaseOrderRepository).delete(testPurchaseOrder);

    purchaseOrderService.delete(1);

    verify(purchaseOrderRepository).findById(1);
    verify(purchaseOrderRepository).delete(testPurchaseOrder);
  }

  @Test
  void delete_shouldThrowException_whenPurchaseOrderNotFound() {
    when(purchaseOrderRepository.findById(999)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> purchaseOrderService.delete(999))
      .isInstanceOf(EntityNotFoundException.class)
      .hasMessageContaining("Purchase order not found");

    verify(purchaseOrderRepository).findById(999);
  }

  @Test
  void delete_shouldThrowException_whenNotInDraftStatus() {
    PurchaseOrder submittedOrder = PurchaseOrder.builder()
      .id(1)
      .supplier(testSupplier)
      .deliveryDate(LocalDate.now().plusDays(10))
      .status(PurchaseOrderStatus.SUBMITTED)
      .totalAmount(new BigDecimal("100.00"))
      .items(List.of(testPurchaseOrderItem))
      .build();

    when(purchaseOrderRepository.findById(1)).thenReturn(
      Optional.of(submittedOrder)
    );

    assertThatThrownBy(() -> purchaseOrderService.delete(1))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining(
        "Only purchase orders in DRAFT status can be deleted"
      );

    verify(purchaseOrderRepository).findById(1);
  }

  @Test
  void submitOrder_shouldSubmitPurchaseOrder_whenInDraftStatus() {
    when(purchaseOrderRepository.findById(1)).thenReturn(
      Optional.of(testPurchaseOrder)
    );
    when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(
      testPurchaseOrder
    );
    when(purchaseOrderMapper.toInfo(testPurchaseOrder)).thenReturn(
      testPurchaseOrderInfo
    );

    PurchaseOrderInfo result = purchaseOrderService.submitOrder(1);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(testPurchaseOrderInfo.getId());
    assertThat(testPurchaseOrder.getStatus()).isEqualTo(
      PurchaseOrderStatus.SUBMITTED
    );
    verify(purchaseOrderRepository).findById(1);
    verify(purchaseOrderRepository).save(testPurchaseOrder);
    verify(purchaseOrderMapper).toInfo(testPurchaseOrder);
  }

  @Test
  void submitOrder_shouldThrowException_whenPurchaseOrderNotFound() {
    when(purchaseOrderRepository.findById(999)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> purchaseOrderService.submitOrder(999))
      .isInstanceOf(EntityNotFoundException.class)
      .hasMessageContaining("Purchase order not found");

    verify(purchaseOrderRepository).findById(999);
  }

  @Test
  void submitOrder_shouldThrowException_whenNotInDraftStatus() {
    PurchaseOrder submittedOrder = PurchaseOrder.builder()
      .id(1)
      .supplier(testSupplier)
      .deliveryDate(LocalDate.now().plusDays(10))
      .status(PurchaseOrderStatus.SUBMITTED)
      .totalAmount(new BigDecimal("100.00"))
      .items(List.of(testPurchaseOrderItem))
      .build();

    when(purchaseOrderRepository.findById(1)).thenReturn(
      Optional.of(submittedOrder)
    );

    assertThatThrownBy(() -> purchaseOrderService.submitOrder(1))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining(
        "Only purchase orders in DRAFT status can be submitted"
      );

    verify(purchaseOrderRepository).findById(1);
  }

  @Test
  void confirmOrder_shouldConfirmPurchaseOrder_whenInSubmittedStatus() {
    PurchaseOrder submittedOrder = PurchaseOrder.builder()
      .id(1)
      .supplier(testSupplier)
      .deliveryDate(LocalDate.now().plusDays(10))
      .status(PurchaseOrderStatus.SUBMITTED)
      .totalAmount(new BigDecimal("100.00"))
      .items(List.of(testPurchaseOrderItem))
      .build();

    when(purchaseOrderRepository.findById(1)).thenReturn(
      Optional.of(submittedOrder)
    );
    when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(
      submittedOrder
    );
    when(purchaseOrderMapper.toInfo(submittedOrder)).thenReturn(
      testPurchaseOrderInfo
    );

    PurchaseOrderInfo result = purchaseOrderService.confirmOrder(1);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(testPurchaseOrderInfo.getId());
    assertThat(submittedOrder.getStatus()).isEqualTo(
      PurchaseOrderStatus.CONFIRMED
    );
    verify(purchaseOrderRepository).findById(1);
    verify(purchaseOrderRepository).save(submittedOrder);
    verify(purchaseOrderMapper).toInfo(submittedOrder);
  }
}
