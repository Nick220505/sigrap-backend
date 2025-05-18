package com.sigrap.supplier;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WithMockUser(username = "admin", roles = { "ADMIN" })
class PurchaseOrderIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private SupplierRepository supplierRepository;

  @Autowired
  private PurchaseOrderRepository purchaseOrderRepository;

  @Autowired
  private PurchaseOrderItemRepository purchaseOrderItemRepository;

  @Autowired
  private ProductRepository productRepository;

  private Supplier testSupplier;
  private Product testProduct;
  private Integer purchaseOrderId;

  @BeforeEach
  void setUp() {
    purchaseOrderItemRepository.deleteAll();
    purchaseOrderRepository.deleteAll();

    Supplier supplier = Supplier.builder()
      .name("Test Supplier")
      .email("supplier@test.com")
      .address("123 Test St")
      .build();
    testSupplier = supplierRepository.save(supplier);

    Product product = Product.builder()
      .name("Test Product")
      .description("Test Description")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .stock(100)
      .minimumStockThreshold(10)
      .build();
    testProduct = productRepository.save(product);
  }

  @Test
  void crudOperations() throws Exception {
    PurchaseOrderItemData itemData = PurchaseOrderItemData.builder()
      .productId(testProduct.getId())
      .quantity(10)
      .unitPrice(new BigDecimal("10.00"))
      .build();

    List<PurchaseOrderItemData> items = new ArrayList<>();
    items.add(itemData);

    LocalDate deliveryDate = LocalDate.now().plusDays(10);
    PurchaseOrderData createData = PurchaseOrderData.builder()
      .supplierId(testSupplier.getId())
      .deliveryDate(deliveryDate)
      .items(items)
      .build();

    MvcResult createResult = mockMvc
      .perform(
        post("/api/purchase-orders")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(createData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").exists())
      .andExpect(jsonPath("$.supplier.id").value(testSupplier.getId()))
      .andExpect(jsonPath("$.status").value("DRAFT"))
      .andExpect(jsonPath("$.items", hasSize(1)))
      .andExpect(jsonPath("$.totalAmount").value(100.0))
      .andReturn();

    String content = createResult.getResponse().getContentAsString();
    PurchaseOrderInfo createdOrder = objectMapper.readValue(
      content,
      PurchaseOrderInfo.class
    );
    purchaseOrderId = createdOrder.getId();

    mockMvc
      .perform(get("/api/purchase-orders/{id}", purchaseOrderId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(purchaseOrderId))
      .andExpect(jsonPath("$.supplier.id").value(testSupplier.getId()))
      .andExpect(jsonPath("$.status").value("DRAFT"));

    mockMvc
      .perform(get("/api/purchase-orders"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].id").value(purchaseOrderId));

    mockMvc
      .perform(
        get("/api/purchase-orders/by-supplier/{id}", testSupplier.getId())
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].id").value(purchaseOrderId));

    mockMvc
      .perform(get("/api/purchase-orders/by-status/{status}", "DRAFT"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].id").value(purchaseOrderId));

    LocalDate newDeliveryDate = LocalDate.now().plusDays(15);
    PurchaseOrderItemData newItemData = PurchaseOrderItemData.builder()
      .productId(testProduct.getId())
      .quantity(20)
      .unitPrice(new BigDecimal("10.00"))
      .build();

    List<PurchaseOrderItemData> newItems = new ArrayList<>();
    newItems.add(newItemData);

    PurchaseOrderData updateData = PurchaseOrderData.builder()
      .supplierId(testSupplier.getId())
      .deliveryDate(newDeliveryDate)
      .items(newItems)
      .build();

    mockMvc
      .perform(
        put("/api/purchase-orders/{id}", purchaseOrderId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(updateData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(purchaseOrderId))
      .andExpect(jsonPath("$.deliveryDate").value(newDeliveryDate.toString()))
      .andExpect(jsonPath("$.totalAmount").value(200.0));

    mockMvc
      .perform(
        patch("/api/purchase-orders/{id}/submit", purchaseOrderId).contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(purchaseOrderId))
      .andExpect(jsonPath("$.status").value("SUBMITTED"));

    mockMvc
      .perform(
        put("/api/purchase-orders/{id}", purchaseOrderId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(updateData))
      )
      .andExpect(status().isInternalServerError());

    mockMvc
      .perform(
        patch("/api/purchase-orders/{id}/confirm", purchaseOrderId).contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(purchaseOrderId))
      .andExpect(jsonPath("$.status").value("CONFIRMED"));

    mockMvc
      .perform(
        patch("/api/purchase-orders/{id}/ship", purchaseOrderId).contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(purchaseOrderId))
      .andExpect(jsonPath("$.status").value("SHIPPED"));

    mockMvc
      .perform(
        patch("/api/purchase-orders/{id}/deliver", purchaseOrderId).contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(purchaseOrderId))
      .andExpect(jsonPath("$.status").value("DELIVERED"));

    mockMvc
      .perform(
        patch("/api/purchase-orders/{id}/pay", purchaseOrderId).contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(purchaseOrderId))
      .andExpect(jsonPath("$.status").value("PAID"));

    MvcResult newOrderResult = mockMvc
      .perform(
        post("/api/purchase-orders")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(createData))
      )
      .andExpect(status().isCreated())
      .andReturn();

    String newContent = newOrderResult.getResponse().getContentAsString();
    PurchaseOrderInfo newOrder = objectMapper.readValue(
      newContent,
      PurchaseOrderInfo.class
    );
    Integer newOrderId = newOrder.getId();

    mockMvc
      .perform(delete("/api/purchase-orders/{id}", newOrderId))
      .andExpect(status().isNoContent());

    mockMvc
      .perform(get("/api/purchase-orders/{id}", newOrderId))
      .andExpect(status().isNotFound());
  }

  @Test
  void invalidOperations() throws Exception {
    PurchaseOrderItemData itemData = PurchaseOrderItemData.builder()
      .productId(testProduct.getId())
      .quantity(10)
      .unitPrice(new BigDecimal("10.00"))
      .build();

    List<PurchaseOrderItemData> items = new ArrayList<>();
    items.add(itemData);

    LocalDate deliveryDate = LocalDate.now().plusDays(10);
    PurchaseOrderData createData = PurchaseOrderData.builder()
      .supplierId(testSupplier.getId())
      .deliveryDate(deliveryDate)
      .items(items)
      .build();

    MvcResult createResult = mockMvc
      .perform(
        post("/api/purchase-orders")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(createData))
      )
      .andExpect(status().isCreated())
      .andReturn();

    String content = createResult.getResponse().getContentAsString();
    PurchaseOrderInfo createdOrder = objectMapper.readValue(
      content,
      PurchaseOrderInfo.class
    );
    purchaseOrderId = createdOrder.getId();

    PurchaseOrderData invalidSupplierData = PurchaseOrderData.builder()
      .supplierId(999L)
      .deliveryDate(deliveryDate)
      .items(items)
      .build();

    mockMvc
      .perform(
        post("/api/purchase-orders")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidSupplierData))
      )
      .andExpect(status().isNotFound());

    PurchaseOrderItemData invalidItemData = PurchaseOrderItemData.builder()
      .productId(999)
      .quantity(10)
      .unitPrice(new BigDecimal("10.00"))
      .build();

    List<PurchaseOrderItemData> invalidItems = new ArrayList<>();
    invalidItems.add(invalidItemData);

    PurchaseOrderData invalidProductData = PurchaseOrderData.builder()
      .supplierId(testSupplier.getId())
      .deliveryDate(deliveryDate)
      .items(invalidItems)
      .build();

    mockMvc
      .perform(
        post("/api/purchase-orders")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidProductData))
      )
      .andExpect(status().isNotFound());

    mockMvc
      .perform(
        patch("/api/purchase-orders/{id}/cancel", purchaseOrderId).contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value("CANCELLED"));

    mockMvc
      .perform(
        patch("/api/purchase-orders/{id}/submit", purchaseOrderId).contentType(
          MediaType.APPLICATION_JSON
        )
      )
      .andExpect(status().isInternalServerError());
  }
}
