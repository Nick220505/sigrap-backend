package com.sigrap.sale;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.config.BaseIntegrationTest;
import com.sigrap.customer.Customer;
import com.sigrap.customer.CustomerRepository;
import com.sigrap.product.Product;
import com.sigrap.product.ProductRepository;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import com.sigrap.user.UserRole;

class SaleReturnIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private SaleReturnRepository saleReturnRepository;

  @Autowired
  private SaleReturnItemRepository saleReturnItemRepository;

  @Autowired
  private SaleRepository saleRepository;

  @Autowired
  private SaleItemRepository saleItemRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private UserRepository userRepository;

  private Sale testSale;
  private SaleReturn testSaleReturn;
  private Product testProduct1;
  private Customer testCustomer;
  private User testEmployee;

  @BeforeEach
  void setUp() {
    saleReturnItemRepository.deleteAllInBatch();
    saleReturnRepository.deleteAllInBatch();
    saleItemRepository.deleteAllInBatch();
    saleRepository.deleteAllInBatch();
    productRepository.deleteAllInBatch();
    customerRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();

    testEmployee = userRepository.save(
      User.builder()
        .name("Sales Employee ReturnTest")
        .email("sales.return.test@example.com")
        .password("secureReturnPassword")
        .role(UserRole.EMPLOYEE)
        .documentId("EMPTRETURN123")
        .build()
    );

    testCustomer = customerRepository.save(
      Customer.builder()
        .fullName("Customer ReturnTest One")
        .email("customer.return.test.one@example.com")
        .phoneNumber("5551234444")
        .address("444 Return Test Ave")
        .documentId("CUSTRETURN444")
        .build()
    );

    testProduct1 = productRepository.save(
      Product.builder()
        .name("Product ReturnTest Alpha")
        .description("Return Test Description Alpha")
        .salePrice(new BigDecimal("30.00"))
        .costPrice(new BigDecimal("15.00"))
        .stock(250)
        .minimumStockThreshold(25)
        .build()
    );

    Sale transientSale = Sale.builder()
      .customer(testCustomer)
      .employee(testEmployee)
      .totalAmount(new BigDecimal("30.00"))
      .taxAmount(new BigDecimal("3.00"))
      .discountAmount(new BigDecimal("1.50"))
      .finalAmount(new BigDecimal("31.50"))
      .build();

    SaleItem saleItem1 = SaleItem.builder()
      .product(testProduct1)
      .quantity(1)
      .unitPrice(new BigDecimal("30.00"))
      .subtotal(new BigDecimal("30.00"))
      .build();
    transientSale.addItem(saleItem1);
    testSale = saleRepository.save(transientSale);

    SaleReturn transientSaleReturn = SaleReturn.builder()
      .originalSale(testSale)
      .customer(testCustomer)
      .employee(testEmployee)
      .reason("Defective on Arrival")
      .totalReturnAmount(new BigDecimal("30.00"))
      .build();

    SaleReturnItem returnItem1 = SaleReturnItem.builder()
      .product(testProduct1)
      .quantity(1)
      .unitPrice(new BigDecimal("30.00"))
      .subtotal(new BigDecimal("30.00"))
      .build();
    transientSaleReturn.addItem(returnItem1);
    testSaleReturn = saleReturnRepository.save(transientSaleReturn);
  }

  @AfterEach
  void tearDown() {
    saleReturnItemRepository.deleteAllInBatch();
    saleReturnRepository.deleteAllInBatch();
    saleItemRepository.deleteAllInBatch();
    saleRepository.deleteAllInBatch();
    productRepository.deleteAllInBatch();
    customerRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void crudOperations() throws Exception {
    mockMvc
      .perform(get("/api/sale-returns"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].id").value(testSaleReturn.getId()))
      .andExpect(
        jsonPath(
          "$[0].totalReturnAmount",
          is(
            closeTo(testSaleReturn.getTotalReturnAmount().doubleValue(), 0.001)
          )
        )
      )
      .andExpect(jsonPath("$[0].reason").value(testSaleReturn.getReason()))
      .andExpect(jsonPath("$[0].originalSaleId").value(testSale.getId()));

    SaleReturnData newSaleReturnData = SaleReturnData.builder()
      .originalSaleId(testSale.getId())
      .customerId(testCustomer.getId())
      .employeeId(testEmployee.getId())
      .reason("Customer changed their mind")
      .totalReturnAmount(new BigDecimal("30.00"))
      .items(
        List.of(
          SaleReturnItemData.builder()
            .productId(testProduct1.getId())
            .quantity(1)
            .unitPrice(new BigDecimal("30.00"))
            .subtotal(new BigDecimal("30.00"))
            .build()
        )
      )
      .build();

    String newSaleReturnJson = objectMapper.writeValueAsString(
      newSaleReturnData
    );

    String responseContent = mockMvc
      .perform(
        post("/api/sale-returns")
          .contentType(MediaType.APPLICATION_JSON)
          .content(newSaleReturnJson)
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").exists())
      .andExpect(
        jsonPath(
          "$.totalReturnAmount",
          is(
            closeTo(
              newSaleReturnData.getTotalReturnAmount().doubleValue(),
              0.001
            )
          )
        )
      )
      .andExpect(jsonPath("$.reason").value(newSaleReturnData.getReason()))
      .andExpect(jsonPath("$.items", hasSize(1)))
      .andExpect(jsonPath("$.items[0].product.id").value(testProduct1.getId()))
      .andReturn()
      .getResponse()
      .getContentAsString();

    SaleReturnInfo createdSaleReturnInfo = objectMapper.readValue(
      responseContent,
      SaleReturnInfo.class
    );
    Integer createdId = createdSaleReturnInfo.getId();

    mockMvc
      .perform(get("/api/sale-returns/" + createdId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(createdId))
      .andExpect(jsonPath("$.reason").value(newSaleReturnData.getReason()));

    SaleReturnData updatedSaleReturnData = SaleReturnData.builder()
      .originalSaleId(testSale.getId())
      .customerId(testCustomer.getId())
      .employeeId(testEmployee.getId())
      .reason("Updated: Wrong item shipped")
      .totalReturnAmount(new BigDecimal("30.00"))
      .items(
        List.of(
          SaleReturnItemData.builder()
            .productId(testProduct1.getId())
            .quantity(1)
            .unitPrice(new BigDecimal("30.00"))
            .subtotal(new BigDecimal("30.00"))
            .build()
        )
      )
      .build();

    String updatedSaleReturnJson = objectMapper.writeValueAsString(
      updatedSaleReturnData
    );

    mockMvc
      .perform(
        put("/api/sale-returns/" + createdId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(updatedSaleReturnJson)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(createdId))
      .andExpect(
        jsonPath(
          "$.totalReturnAmount",
          is(
            closeTo(
              updatedSaleReturnData.getTotalReturnAmount().doubleValue(),
              0.001
            )
          )
        )
      )
      .andExpect(jsonPath("$.reason").value(updatedSaleReturnData.getReason()));

    mockMvc
      .perform(delete("/api/sale-returns/" + createdId))
      .andExpect(status().isNoContent());

    mockMvc
      .perform(get("/api/sale-returns/" + createdId))
      .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void findByOriginalSaleId() throws Exception {
    mockMvc
      .perform(get("/api/sale-returns/original-sale/" + testSale.getId()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].id").value(testSaleReturn.getId()))
      .andExpect(
        jsonPath(
          "$[0].totalReturnAmount",
          is(
            closeTo(testSaleReturn.getTotalReturnAmount().doubleValue(), 0.001)
          )
        )
      )
      .andExpect(jsonPath("$[0].originalSaleId").value(testSale.getId()))
      .andExpect(
        jsonPath("$[0].items[0].product.id").value(testProduct1.getId())
      )
      .andExpect(
        jsonPath(
          "$[0].items[0].subtotal",
          is(
            closeTo(
              testSaleReturn.getItems().get(0).getSubtotal().doubleValue(),
              0.001
            )
          )
        )
      );
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void createSaleReturn_invalidData_missingAllFields() throws Exception {
    SaleReturnData invalidData = SaleReturnData.builder().build();
    String invalidJson = objectMapper.writeValueAsString(invalidData);

    mockMvc
      .perform(
        post("/api/sale-returns")
          .contentType(MediaType.APPLICATION_JSON)
          .content(invalidJson)
      )
      .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void createSaleReturn_nonExistentOriginalSale() throws Exception {
    SaleReturnData data = SaleReturnData.builder()
      .originalSaleId(99999)
      .customerId(testCustomer.getId())
      .employeeId(testEmployee.getId())
      .reason("Non Existent Sale Return Test")
      .totalReturnAmount(BigDecimal.TEN)
      .items(
        List.of(
          SaleReturnItemData.builder()
            .productId(testProduct1.getId())
            .quantity(1)
            .unitPrice(BigDecimal.TEN)
            .subtotal(BigDecimal.TEN)
            .build()
        )
      )
      .build();
    String json = objectMapper.writeValueAsString(data);

    mockMvc
      .perform(
        post("/api/sale-returns")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json)
      )
      .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void createSaleReturn_nonExistentProductInItems() throws Exception {
    SaleReturnData data = SaleReturnData.builder()
      .originalSaleId(testSale.getId())
      .customerId(testCustomer.getId())
      .employeeId(testEmployee.getId())
      .reason("Non Existent Product In Return Items Test")
      .totalReturnAmount(BigDecimal.TEN)
      .items(
        List.of(
          SaleReturnItemData.builder()
            .productId(88888)
            .quantity(1)
            .unitPrice(BigDecimal.TEN)
            .subtotal(BigDecimal.TEN)
            .build()
        )
      )
      .build();
    String json = objectMapper.writeValueAsString(data);

    mockMvc
      .perform(
        post("/api/sale-returns")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json)
      )
      .andExpect(status().isNotFound());
  }
}
