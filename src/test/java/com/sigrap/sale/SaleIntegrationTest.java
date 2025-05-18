package com.sigrap.sale;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.config.BaseIntegrationTest;
import com.sigrap.customer.Customer;
import com.sigrap.customer.CustomerRepository;
import com.sigrap.product.Product;
import com.sigrap.product.ProductRepository;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class SaleIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private SaleRepository saleRepository;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private User testEmployee;
  private Customer testCustomer;
  private List<Product> testProducts;
  private Sale testSale;

  @BeforeEach
  void setUp() {
    saleRepository.deleteAll();
    userRepository.deleteAll();
    customerRepository.deleteAll();
    productRepository.deleteAll();

    testEmployee = User.builder()
      .name("Sales Employee")
      .email("sales@example.com")
      .password("password123")
      .documentId("SALES123")
      .build();
    testEmployee = userRepository.save(testEmployee);

    testCustomer = Customer.builder()
      .fullName("Test Customer")
      .email("customer@example.com")
      .phoneNumber("5551234567")
      .documentId("CUST456")
      .address("123 Customer St")
      .build();
    testCustomer = customerRepository.save(testCustomer);

    Product product1 = Product.builder()
      .name("Product 1")
      .description("Description 1")
      .costPrice(new BigDecimal("5.00"))
      .salePrice(new BigDecimal("10.00"))
      .stock(100)
      .minimumStockThreshold(10)
      .build();

    Product product2 = Product.builder()
      .name("Product 2")
      .description("Description 2")
      .costPrice(new BigDecimal("15.00"))
      .salePrice(new BigDecimal("25.00"))
      .stock(50)
      .minimumStockThreshold(5)
      .build();

    testProducts = productRepository.saveAll(Arrays.asList(product1, product2));

    SaleItem item1 = SaleItem.builder()
      .product(testProducts.get(0))
      .quantity(2)
      .unitPrice(new BigDecimal("10.00"))
      .subtotal(new BigDecimal("20.00"))
      .build();

    Sale sale = Sale.builder()
      .customer(testCustomer)
      .employee(testEmployee)
      .totalAmount(new BigDecimal("20.00"))
      .taxAmount(new BigDecimal("3.80"))
      .discountAmount(new BigDecimal("0.00"))
      .finalAmount(new BigDecimal("23.80"))
      .items(new ArrayList<>())
      .build();

    sale = saleRepository.save(sale);
    item1.setSale(sale);
    sale.addItem(item1);

    testSale = saleRepository.save(sale);
  }

  @AfterEach
  void tearDown() {
    saleRepository.deleteAll();
    userRepository.deleteAll();
    customerRepository.deleteAll();
    productRepository.deleteAll();
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void crudOperations() throws Exception {
    mockMvc
      .perform(get("/api/sales"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testSale.getId()))
      .andExpect(
        jsonPath("$[0].totalAmount").value(
          is(closeTo(testSale.getTotalAmount().doubleValue(), 0.001))
        )
      )
      .andExpect(
        jsonPath("$[0].taxAmount").value(
          is(closeTo(testSale.getTaxAmount().doubleValue(), 0.001))
        )
      )
      .andExpect(
        jsonPath("$[0].discountAmount").value(
          is(closeTo(testSale.getDiscountAmount().doubleValue(), 0.001))
        )
      )
      .andExpect(
        jsonPath("$[0].finalAmount").value(
          is(closeTo(testSale.getFinalAmount().doubleValue(), 0.001))
        )
      )
      .andExpect(jsonPath("$[0].customer.id").value(testCustomer.getId()))
      .andExpect(
        jsonPath("$[0].customer.fullName").value(testCustomer.getFullName())
      )
      .andExpect(jsonPath("$[0].employee.id").value(testEmployee.getId()))
      .andExpect(jsonPath("$[0].employee.name").value(testEmployee.getName()));

    mockMvc
      .perform(get("/api/sales/{id}", testSale.getId()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testSale.getId()))
      .andExpect(
        jsonPath("$.totalAmount").value(
          is(closeTo(testSale.getTotalAmount().doubleValue(), 0.001))
        )
      )
      .andExpect(
        jsonPath("$.taxAmount").value(
          is(closeTo(testSale.getTaxAmount().doubleValue(), 0.001))
        )
      )
      .andExpect(
        jsonPath("$.discountAmount").value(
          is(closeTo(testSale.getDiscountAmount().doubleValue(), 0.001))
        )
      )
      .andExpect(
        jsonPath("$.finalAmount").value(
          is(closeTo(testSale.getFinalAmount().doubleValue(), 0.001))
        )
      );

    List<SaleItemData> itemDataList = new ArrayList<>();
    itemDataList.add(
      SaleItemData.builder()
        .productId(testProducts.get(0).getId())
        .quantity(1)
        .unitPrice(new BigDecimal("10.00"))
        .subtotal(new BigDecimal("10.00"))
        .build()
    );

    itemDataList.add(
      SaleItemData.builder()
        .productId(testProducts.get(1).getId())
        .quantity(2)
        .unitPrice(new BigDecimal("25.00"))
        .subtotal(new BigDecimal("50.00"))
        .build()
    );

    SaleData newSaleData = SaleData.builder()
      .customerId(testCustomer.getId())
      .employeeId(testEmployee.getId())
      .totalAmount(new BigDecimal("60.00"))
      .taxAmount(new BigDecimal("11.40"))
      .discountAmount(new BigDecimal("5.00"))
      .finalAmount(new BigDecimal("66.40"))
      .items(itemDataList)
      .build();

    MvcResult result = mockMvc
      .perform(
        post("/api/sales")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(newSaleData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.totalAmount").value(is(closeTo(60.00, 0.001))))
      .andExpect(jsonPath("$.taxAmount").value(is(closeTo(11.40, 0.001))))
      .andExpect(jsonPath("$.discountAmount").value(is(closeTo(5.00, 0.001))))
      .andExpect(jsonPath("$.finalAmount").value(is(closeTo(66.40, 0.001))))
      .andExpect(jsonPath("$.customer.id").value(testCustomer.getId()))
      .andExpect(jsonPath("$.items").isArray())
      .andExpect(jsonPath("$.items.length()").value(2))
      .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    SaleInfo createdSale = objectMapper.readValue(
      responseContent,
      SaleInfo.class
    );

    SaleData updateData = SaleData.builder()
      .customerId(testCustomer.getId())
      .employeeId(testEmployee.getId())
      .totalAmount(new BigDecimal("60.00"))
      .taxAmount(new BigDecimal("11.40"))
      .discountAmount(new BigDecimal("10.00"))
      .finalAmount(new BigDecimal("61.40"))
      .items(itemDataList)
      .build();

    mockMvc
      .perform(
        put("/api/sales/{id}", createdSale.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(updateData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(createdSale.getId()))
      .andExpect(jsonPath("$.totalAmount").value(is(closeTo(60.00, 0.001))))
      .andExpect(jsonPath("$.taxAmount").value(is(closeTo(11.40, 0.001))))
      .andExpect(jsonPath("$.discountAmount").value(is(closeTo(10.00, 0.001))))
      .andExpect(jsonPath("$.finalAmount").value(is(closeTo(61.40, 0.001))));

    mockMvc
      .perform(delete("/api/sales/{id}", createdSale.getId()))
      .andExpect(status().isNoContent());

    mockMvc
      .perform(get("/api/sales/{id}", createdSale.getId()))
      .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void findByDateRange() throws Exception {
    LocalDate today = LocalDate.now();
    String startDateStr = today.minusDays(1).format(DateTimeFormatter.ISO_DATE);
    String endDateStr = today.plusDays(1).format(DateTimeFormatter.ISO_DATE);

    mockMvc
      .perform(
        get("/api/sales/by-date-range")
          .param("startDate", startDateStr)
          .param("endDate", endDateStr)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$[0].id").value(testSale.getId()));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void findByCustomerId() throws Exception {
    mockMvc
      .perform(get("/api/sales/customer/{customerId}", testCustomer.getId()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$[0].id").value(testSale.getId()))
      .andExpect(jsonPath("$[0].customer.id").value(testCustomer.getId()));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void findByEmployeeId() throws Exception {
    mockMvc
      .perform(get("/api/sales/employee/{employeeId}", testEmployee.getId()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$[0].id").value(testSale.getId()))
      .andExpect(jsonPath("$[0].employee.id").value(testEmployee.getId()));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void deleteMultipleSales() throws Exception {
    SaleItem item = SaleItem.builder()
      .product(testProducts.get(1))
      .quantity(1)
      .unitPrice(new BigDecimal("25.00"))
      .subtotal(new BigDecimal("25.00"))
      .build();

    Sale sale2 = Sale.builder()
      .customer(testCustomer)
      .employee(testEmployee)
      .totalAmount(new BigDecimal("25.00"))
      .taxAmount(new BigDecimal("4.75"))
      .discountAmount(new BigDecimal("0.00"))
      .finalAmount(new BigDecimal("29.75"))
      .items(new ArrayList<>())
      .build();

    sale2 = saleRepository.save(sale2);
    item.setSale(sale2);
    sale2.addItem(item);
    sale2 = saleRepository.save(sale2);

    List<Integer> idsToDelete = Arrays.asList(testSale.getId(), sale2.getId());

    mockMvc
      .perform(
        delete("/api/sales/delete-many")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(idsToDelete))
      )
      .andExpect(status().isNoContent());

    mockMvc
      .perform(get("/api/sales/{id}", testSale.getId()))
      .andExpect(status().isNotFound());

    mockMvc
      .perform(get("/api/sales/{id}", sale2.getId()))
      .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void validationConstraints() throws Exception {
    SaleData invalidSaleData = SaleData.builder().build();

    mockMvc
      .perform(
        post("/api/sales")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidSaleData))
      )
      .andExpect(status().isBadRequest());

    List<SaleItemData> validItems = new ArrayList<>();
    validItems.add(
      SaleItemData.builder()
        .productId(testProducts.get(0).getId())
        .quantity(1)
        .unitPrice(new BigDecimal("10.00"))
        .subtotal(new BigDecimal("10.00"))
        .build()
    );

    SaleData invalidCustomerData = SaleData.builder()
      .customerId(999L)
      .employeeId(testEmployee.getId())
      .totalAmount(new BigDecimal("10.00"))
      .taxAmount(new BigDecimal("1.90"))
      .discountAmount(new BigDecimal("0.00"))
      .finalAmount(new BigDecimal("11.90"))
      .items(validItems)
      .build();

    mockMvc
      .perform(
        post("/api/sales")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidCustomerData))
      )
      .andExpect(status().isNotFound());

    SaleData invalidEmployeeData = SaleData.builder()
      .customerId(testCustomer.getId())
      .employeeId(999L)
      .totalAmount(new BigDecimal("10.00"))
      .taxAmount(new BigDecimal("1.90"))
      .discountAmount(new BigDecimal("0.00"))
      .finalAmount(new BigDecimal("11.90"))
      .items(validItems)
      .build();

    mockMvc
      .perform(
        post("/api/sales")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidEmployeeData))
      )
      .andExpect(status().isNotFound());

    SaleData negativeTotalData = SaleData.builder()
      .customerId(testCustomer.getId())
      .employeeId(testEmployee.getId())
      .totalAmount(new BigDecimal("-10.00"))
      .taxAmount(new BigDecimal("1.90"))
      .discountAmount(new BigDecimal("0.00"))
      .finalAmount(new BigDecimal("11.90"))
      .items(validItems)
      .build();

    mockMvc
      .perform(
        post("/api/sales")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(negativeTotalData))
      )
      .andExpect(status().isBadRequest());
  }
}
