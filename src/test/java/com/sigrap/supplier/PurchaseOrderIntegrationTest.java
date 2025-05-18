package com.sigrap.supplier;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.config.BaseIntegrationTest;
import com.sigrap.product.Product;
import com.sigrap.product.ProductRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class PurchaseOrderIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private PurchaseOrderRepository purchaseOrderRepository;

  @Autowired
  private SupplierRepository supplierRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private Supplier testSupplier;
  private Product testProduct;
  private PurchaseOrder testPurchaseOrder;

  @BeforeEach
  void setUp() {
    purchaseOrderRepository.deleteAll();
    supplierRepository.deleteAll();
    productRepository.deleteAll();

    testSupplier = Supplier.builder()
      .name("Test Supplier")
      .contactPerson("John Contact")
      .phone("1234567890")
      .email("supplier@example.com")
      .build();
    testSupplier = supplierRepository.save(testSupplier);

    testProduct = Product.builder()
      .name("Test Product")
      .description("Test Product Description")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .stock(100)
      .minimumStockThreshold(10)
      .build();
    testProduct = productRepository.save(testProduct);

    testPurchaseOrder = PurchaseOrder.builder()
      .supplier(testSupplier)
      .deliveryDate(LocalDate.now().plusDays(7))
      .status(PurchaseOrderStatus.DRAFT)
      .totalAmount(new BigDecimal("100.00"))
      .build();
    testPurchaseOrder = purchaseOrderRepository.save(testPurchaseOrder);
  }

  @AfterEach
  void tearDown() {
    purchaseOrderRepository.deleteAll();
    supplierRepository.deleteAll();
    productRepository.deleteAll();
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void crudOperations() throws Exception {
    mockMvc
      .perform(get("/api/purchase-orders"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testPurchaseOrder.getId()))
      .andExpect(
        jsonPath("$[0].status").value(testPurchaseOrder.getStatus().toString())
      )
      .andExpect(
        jsonPath("$[0].totalAmount").value(
          testPurchaseOrder.getTotalAmount().doubleValue()
        )
      )
      .andExpect(jsonPath("$[0].supplier.id").value(testSupplier.getId()));

    mockMvc
      .perform(get("/api/purchase-orders/{id}", testPurchaseOrder.getId()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testPurchaseOrder.getId()))
      .andExpect(
        jsonPath("$.status").value(testPurchaseOrder.getStatus().toString())
      )
      .andExpect(
        jsonPath("$.totalAmount").value(
          testPurchaseOrder.getTotalAmount().doubleValue()
        )
      )
      .andExpect(jsonPath("$.supplier.id").value(testSupplier.getId()));

    PurchaseOrderData newOrderData = PurchaseOrderData.builder()
      .supplierId(testSupplier.getId())
      .deliveryDate(LocalDate.now().plusDays(14))
      .build();

    MvcResult result = mockMvc
      .perform(
        post("/api/purchase-orders")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(newOrderData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.supplier.id").value(testSupplier.getId()))
      .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    PurchaseOrderInfo createdOrder = objectMapper.readValue(
      responseContent,
      PurchaseOrderInfo.class
    );
    Integer createdOrderId = createdOrder.getId();

    PurchaseOrderData updateData = PurchaseOrderData.builder()
      .supplierId(testSupplier.getId())
      .deliveryDate(LocalDate.now().plusDays(10))
      .build();

    mockMvc
      .perform(
        put("/api/purchase-orders/{id}", createdOrderId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(updateData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(createdOrderId))
      .andExpect(jsonPath("$.supplier.id").value(testSupplier.getId()));

    mockMvc
      .perform(delete("/api/purchase-orders/{id}", createdOrderId))
      .andExpect(status().isNoContent());

    mockMvc
      .perform(get("/api/purchase-orders/{id}", createdOrderId))
      .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void findBySupplier() throws Exception {
    PurchaseOrder secondOrder = PurchaseOrder.builder()
      .supplier(testSupplier)
      .deliveryDate(LocalDate.now().plusDays(5))
      .status(PurchaseOrderStatus.CONFIRMED)
      .totalAmount(new BigDecimal("200.00"))
      .build();

    secondOrder = purchaseOrderRepository.save(secondOrder);

    mockMvc
      .perform(
        get("/api/purchase-orders/supplier/{supplierId}", testSupplier.getId())
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].supplier.id").value(testSupplier.getId()))
      .andExpect(jsonPath("$[1].supplier.id").value(testSupplier.getId()));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void findByStatus() throws Exception {
    PurchaseOrder confirmedOrder = PurchaseOrder.builder()
      .supplier(testSupplier)
      .deliveryDate(LocalDate.now().plusDays(5))
      .status(PurchaseOrderStatus.CONFIRMED)
      .totalAmount(new BigDecimal("200.00"))
      .build();

    PurchaseOrder deliveredOrder = PurchaseOrder.builder()
      .supplier(testSupplier)
      .deliveryDate(LocalDate.now().minusDays(2))
      .status(PurchaseOrderStatus.DELIVERED)
      .totalAmount(new BigDecimal("300.00"))
      .build();

    confirmedOrder = purchaseOrderRepository.save(confirmedOrder);
    deliveredOrder = purchaseOrderRepository.save(deliveredOrder);

    mockMvc
      .perform(get("/api/purchase-orders/status/{status}", "CONFIRMED"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$[0].status").value("CONFIRMED"));

    mockMvc
      .perform(get("/api/purchase-orders/status/{status}", "DELIVERED"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$[0].status").value("DELIVERED"));

    mockMvc
      .perform(get("/api/purchase-orders/status/{status}", "DRAFT"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$[0].status").value("DRAFT"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void validationConstraints() throws Exception {
    PurchaseOrderData invalidOrderData = PurchaseOrderData.builder().build();

    mockMvc
      .perform(
        post("/api/purchase-orders")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidOrderData))
      )
      .andExpect(status().isBadRequest());

    PurchaseOrderData invalidSupplierData = PurchaseOrderData.builder()
      .supplierId(999L)
      .deliveryDate(LocalDate.now().plusDays(10))
      .build();

    mockMvc
      .perform(
        post("/api/purchase-orders")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidSupplierData))
      )
      .andExpect(status().isNotFound());
  }
}
