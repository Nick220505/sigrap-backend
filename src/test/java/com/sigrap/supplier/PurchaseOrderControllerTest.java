package com.sigrap.supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.exception.GlobalExceptionHandler;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderControllerTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @Mock
  private PurchaseOrderService purchaseOrderService;

  @InjectMocks
  private PurchaseOrderController purchaseOrderController;

  private PurchaseOrderInfo purchaseOrderInfo;
  private PurchaseOrderData purchaseOrderData;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();

    mockMvc = MockMvcBuilders.standaloneSetup(purchaseOrderController)
      .setControllerAdvice(new GlobalExceptionHandler())
      .setMessageConverters(new MappingJackson2HttpMessageConverter())
      .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
      .build();

    SupplierInfo supplierInfo = SupplierInfo.builder()
      .id(1L)
      .name("Supplier Test")
      .email("supplier@test.com")
      .build();

    purchaseOrderInfo = PurchaseOrderInfo.builder()
      .id(1)
      .supplier(supplierInfo)
      .deliveryDate(LocalDate.now().plusDays(10))
      .status("DRAFT")
      .totalAmount(new BigDecimal("1000.00"))
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .items(List.of())
      .build();

    purchaseOrderData = PurchaseOrderData.builder()
      .supplierId(1L)
      .deliveryDate(LocalDate.now().plusDays(10))
      .items(List.of())
      .build();
  }

  @Test
  void findAll_shouldReturnAllPurchaseOrders() throws Exception {
    when(purchaseOrderService.findAll()).thenReturn(List.of(purchaseOrderInfo));

    mockMvc
      .perform(
        get("/api/purchase-orders").contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(purchaseOrderInfo.getId()))
      .andExpect(
        jsonPath("$[0].supplier.id").value(
          purchaseOrderInfo.getSupplier().getId()
        )
      )
      .andExpect(jsonPath("$[0].status").value(purchaseOrderInfo.getStatus()));
  }

  @Test
  void findById_shouldReturnPurchaseOrder() throws Exception {
    when(purchaseOrderService.findById(1)).thenReturn(purchaseOrderInfo);

    mockMvc
      .perform(
        get("/api/purchase-orders/1").contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(purchaseOrderInfo.getId()))
      .andExpect(
        jsonPath("$.supplier.id").value(purchaseOrderInfo.getSupplier().getId())
      )
      .andExpect(jsonPath("$.status").value(purchaseOrderInfo.getStatus()));
  }

  @Test
  void findById_shouldReturnNotFound_whenNotExists() throws Exception {
    when(purchaseOrderService.findById(999)).thenThrow(
      new EntityNotFoundException("Purchase order not found")
    );

    mockMvc
      .perform(
        get("/api/purchase-orders/999").contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound());
  }

  @Test
  void findBySupplierId_shouldReturnPurchaseOrders() throws Exception {
    when(purchaseOrderService.findBySupplierId(1)).thenReturn(
      List.of(purchaseOrderInfo)
    );

    mockMvc
      .perform(
        get("/api/purchase-orders/by-supplier/1").contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(purchaseOrderInfo.getId()))
      .andExpect(
        jsonPath("$[0].supplier.id").value(
          purchaseOrderInfo.getSupplier().getId()
        )
      );
  }

  @Test
  void findByStatus_shouldReturnPurchaseOrders() throws Exception {
    when(
      purchaseOrderService.findByStatus(PurchaseOrderStatus.DRAFT)
    ).thenReturn(List.of(purchaseOrderInfo));

    mockMvc
      .perform(
        get("/api/purchase-orders/by-status/DRAFT").contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(purchaseOrderInfo.getId()))
      .andExpect(jsonPath("$[0].status").value(purchaseOrderInfo.getStatus()));
  }

  @Test
  void create_shouldCreatePurchaseOrder() throws Exception {
    when(purchaseOrderService.create(any(PurchaseOrderData.class))).thenReturn(
      purchaseOrderInfo
    );

    mockMvc
      .perform(
        post("/api/purchase-orders")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(purchaseOrderData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(purchaseOrderInfo.getId()))
      .andExpect(
        jsonPath("$.supplier.id").value(purchaseOrderInfo.getSupplier().getId())
      )
      .andExpect(jsonPath("$.status").value(purchaseOrderInfo.getStatus()));
  }

  @Test
  void update_shouldUpdatePurchaseOrder() throws Exception {
    when(
      purchaseOrderService.update(eq(1), any(PurchaseOrderData.class))
    ).thenReturn(purchaseOrderInfo);

    mockMvc
      .perform(
        put("/api/purchase-orders/1")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(purchaseOrderData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(purchaseOrderInfo.getId()))
      .andExpect(
        jsonPath("$.supplier.id").value(purchaseOrderInfo.getSupplier().getId())
      )
      .andExpect(jsonPath("$.status").value(purchaseOrderInfo.getStatus()));
  }

  @Test
  void update_shouldReturnNotFound_whenNotExists() throws Exception {
    when(
      purchaseOrderService.update(eq(999), any(PurchaseOrderData.class))
    ).thenThrow(new EntityNotFoundException("Purchase order not found"));

    mockMvc
      .perform(
        put("/api/purchase-orders/999")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(purchaseOrderData))
      )
      .andExpect(status().isNotFound());
  }

  @Test
  void delete_shouldDeletePurchaseOrder() throws Exception {
    doNothing().when(purchaseOrderService).delete(1);

    mockMvc
      .perform(
        delete("/api/purchase-orders/1").contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNoContent());
  }

  @Test
  void delete_shouldReturnNotFound_whenNotExists() throws Exception {
    doThrow(new EntityNotFoundException("Purchase order not found"))
      .when(purchaseOrderService)
      .delete(999);

    mockMvc
      .perform(
        delete("/api/purchase-orders/999").contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isNotFound());
  }

  @Test
  void submitOrder_shouldSubmitPurchaseOrder() throws Exception {
    PurchaseOrderInfo submittedOrder = PurchaseOrderInfo.builder()
      .id(purchaseOrderInfo.getId())
      .supplier(purchaseOrderInfo.getSupplier())
      .deliveryDate(purchaseOrderInfo.getDeliveryDate())
      .status("SUBMITTED")
      .totalAmount(purchaseOrderInfo.getTotalAmount())
      .createdAt(purchaseOrderInfo.getCreatedAt())
      .updatedAt(purchaseOrderInfo.getUpdatedAt())
      .items(purchaseOrderInfo.getItems())
      .build();

    when(purchaseOrderService.submitOrder(1)).thenReturn(submittedOrder);

    mockMvc
      .perform(
        patch("/api/purchase-orders/1/submit").contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(submittedOrder.getId()))
      .andExpect(jsonPath("$.status").value(submittedOrder.getStatus()));
  }

  @Test
  void confirmOrder_shouldConfirmPurchaseOrder() throws Exception {
    PurchaseOrderInfo confirmedOrder = PurchaseOrderInfo.builder()
      .id(purchaseOrderInfo.getId())
      .supplier(purchaseOrderInfo.getSupplier())
      .deliveryDate(purchaseOrderInfo.getDeliveryDate())
      .status("CONFIRMED")
      .totalAmount(purchaseOrderInfo.getTotalAmount())
      .createdAt(purchaseOrderInfo.getCreatedAt())
      .updatedAt(purchaseOrderInfo.getUpdatedAt())
      .items(purchaseOrderInfo.getItems())
      .build();

    when(purchaseOrderService.confirmOrder(1)).thenReturn(confirmedOrder);

    mockMvc
      .perform(
        patch("/api/purchase-orders/1/confirm").contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(confirmedOrder.getId()))
      .andExpect(jsonPath("$.status").value(confirmedOrder.getStatus()));
  }

  @Test
  void markAsShipped_shouldUpdatePurchaseOrderStatus() throws Exception {
    PurchaseOrderInfo shippedOrder = PurchaseOrderInfo.builder()
      .id(purchaseOrderInfo.getId())
      .supplier(purchaseOrderInfo.getSupplier())
      .deliveryDate(purchaseOrderInfo.getDeliveryDate())
      .status("SHIPPED")
      .totalAmount(purchaseOrderInfo.getTotalAmount())
      .createdAt(purchaseOrderInfo.getCreatedAt())
      .updatedAt(purchaseOrderInfo.getUpdatedAt())
      .items(purchaseOrderInfo.getItems())
      .build();

    when(purchaseOrderService.markAsShipped(1)).thenReturn(shippedOrder);

    mockMvc
      .perform(
        patch("/api/purchase-orders/1/ship").contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(shippedOrder.getId()))
      .andExpect(jsonPath("$.status").value(shippedOrder.getStatus()));
  }

  @Test
  void markAsDelivered_shouldUpdatePurchaseOrderStatus() throws Exception {
    PurchaseOrderInfo deliveredOrder = PurchaseOrderInfo.builder()
      .id(purchaseOrderInfo.getId())
      .supplier(purchaseOrderInfo.getSupplier())
      .deliveryDate(purchaseOrderInfo.getDeliveryDate())
      .status("DELIVERED")
      .totalAmount(purchaseOrderInfo.getTotalAmount())
      .createdAt(purchaseOrderInfo.getCreatedAt())
      .updatedAt(purchaseOrderInfo.getUpdatedAt())
      .items(purchaseOrderInfo.getItems())
      .build();

    when(purchaseOrderService.markAsDelivered(1)).thenReturn(deliveredOrder);

    mockMvc
      .perform(
        patch("/api/purchase-orders/1/deliver").contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(deliveredOrder.getId()))
      .andExpect(jsonPath("$.status").value(deliveredOrder.getStatus()));
  }

  @Test
  void cancelOrder_shouldCancelPurchaseOrder() throws Exception {
    PurchaseOrderInfo cancelledOrder = PurchaseOrderInfo.builder()
      .id(purchaseOrderInfo.getId())
      .supplier(purchaseOrderInfo.getSupplier())
      .deliveryDate(purchaseOrderInfo.getDeliveryDate())
      .status("CANCELLED")
      .totalAmount(purchaseOrderInfo.getTotalAmount())
      .createdAt(purchaseOrderInfo.getCreatedAt())
      .updatedAt(purchaseOrderInfo.getUpdatedAt())
      .items(purchaseOrderInfo.getItems())
      .build();

    when(purchaseOrderService.cancelOrder(1)).thenReturn(cancelledOrder);

    mockMvc
      .perform(
        patch("/api/purchase-orders/1/cancel").contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(cancelledOrder.getId()))
      .andExpect(jsonPath("$.status").value(cancelledOrder.getStatus()));
  }

  @Test
  void markAsPaid_shouldUpdatePurchaseOrderStatus() throws Exception {
    PurchaseOrderInfo paidOrder = PurchaseOrderInfo.builder()
      .id(purchaseOrderInfo.getId())
      .supplier(purchaseOrderInfo.getSupplier())
      .deliveryDate(purchaseOrderInfo.getDeliveryDate())
      .status("PAID")
      .totalAmount(purchaseOrderInfo.getTotalAmount())
      .createdAt(purchaseOrderInfo.getCreatedAt())
      .updatedAt(purchaseOrderInfo.getUpdatedAt())
      .items(purchaseOrderInfo.getItems())
      .build();

    when(purchaseOrderService.markAsPaid(1)).thenReturn(paidOrder);

    mockMvc
      .perform(
        patch("/api/purchase-orders/1/pay").contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(paidOrder.getId()))
      .andExpect(jsonPath("$.status").value(paidOrder.getStatus()));
  }
}
