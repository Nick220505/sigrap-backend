package com.sigrap.customer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

  @Mock
  private CustomerService customerService;

  @InjectMocks
  private CustomerController customerController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    mockMvc = standaloneSetup(customerController).build();

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  void findAll_shouldReturnAllCustomers() throws Exception {
    CustomerInfo customer1 = CustomerInfo.builder()
      .id(1L)
      .fullName("Customer 1")
      .email("customer1@example.com")
      .build();

    CustomerInfo customer2 = CustomerInfo.builder()
      .id(2L)
      .fullName("Customer 2")
      .email("customer2@example.com")
      .build();

    List<CustomerInfo> customers = List.of(customer1, customer2);

    when(customerService.findAll()).thenReturn(customers);

    mockMvc
      .perform(get("/api/customers"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(1))
      .andExpect(jsonPath("$[0].fullName").value("Customer 1"))
      .andExpect(jsonPath("$[0].email").value("customer1@example.com"))
      .andExpect(jsonPath("$[1].id").value(2))
      .andExpect(jsonPath("$[1].fullName").value("Customer 2"))
      .andExpect(jsonPath("$[1].email").value("customer2@example.com"));
  }

  @Test
  void findById_shouldReturnCustomer() throws Exception {
    Long id = 1L;
    CustomerInfo customerInfo = CustomerInfo.builder()
      .id(id)
      .fullName("Test Customer")
      .email("test@example.com")
      .phoneNumber("1234567890")
      .address("123 Test St")
      .build();

    when(customerService.findById(id)).thenReturn(customerInfo);

    mockMvc
      .perform(get("/api/customers/{id}", id))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(id))
      .andExpect(jsonPath("$.fullName").value("Test Customer"))
      .andExpect(jsonPath("$.email").value("test@example.com"))
      .andExpect(jsonPath("$.phoneNumber").value("1234567890"))
      .andExpect(jsonPath("$.address").value("123 Test St"));
  }

  @Test
  void create_shouldCreateCustomer() throws Exception {
    CustomerData customerData = CustomerData.builder()
      .fullName("New Customer")
      .email("new@example.com")
      .phoneNumber("1234567890")
      .address("123 New St")
      .build();

    CustomerInfo createdCustomerInfo = CustomerInfo.builder()
      .id(1L)
      .fullName("New Customer")
      .email("new@example.com")
      .phoneNumber("1234567890")
      .address("123 New St")
      .build();

    when(customerService.create(any(CustomerData.class))).thenReturn(
      createdCustomerInfo
    );

    mockMvc
      .perform(
        post("/api/customers")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(customerData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.fullName").value("New Customer"))
      .andExpect(jsonPath("$.email").value("new@example.com"))
      .andExpect(jsonPath("$.phoneNumber").value("1234567890"))
      .andExpect(jsonPath("$.address").value("123 New St"));

    verify(customerService).create(any(CustomerData.class));
  }

  @Test
  void update_shouldUpdateCustomer() throws Exception {
    Long id = 1L;
    CustomerData customerData = CustomerData.builder()
      .fullName("Updated Customer")
      .email("updated@example.com")
      .phoneNumber("9876543210")
      .address("456 Update St")
      .build();

    CustomerInfo updatedCustomerInfo = CustomerInfo.builder()
      .id(id)
      .fullName("Updated Customer")
      .email("updated@example.com")
      .phoneNumber("9876543210")
      .address("456 Update St")
      .build();

    when(customerService.update(eq(id), any(CustomerData.class))).thenReturn(
      updatedCustomerInfo
    );

    mockMvc
      .perform(
        put("/api/customers/{id}", id)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(customerData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(id))
      .andExpect(jsonPath("$.fullName").value("Updated Customer"))
      .andExpect(jsonPath("$.email").value("updated@example.com"))
      .andExpect(jsonPath("$.phoneNumber").value("9876543210"))
      .andExpect(jsonPath("$.address").value("456 Update St"));

    verify(customerService).update(eq(id), any(CustomerData.class));
  }

  @Test
  void delete_shouldDeleteCustomer() throws Exception {
    Long id = 1L;
    doNothing().when(customerService).delete(id);

    mockMvc
      .perform(delete("/api/customers/{id}", id))
      .andExpect(status().isNoContent());

    verify(customerService).delete(id);
  }

  @Test
  void searchByName_shouldReturnMatchingCustomers() throws Exception {
    String searchTerm = "John";

    CustomerInfo customer1 = CustomerInfo.builder()
      .id(1L)
      .fullName("John Doe")
      .email("john@example.com")
      .build();

    CustomerInfo customer2 = CustomerInfo.builder()
      .id(2L)
      .fullName("Johnny Smith")
      .email("johnny@example.com")
      .build();

    List<CustomerInfo> customers = List.of(customer1, customer2);

    when(customerService.searchByName(searchTerm)).thenReturn(customers);

    mockMvc
      .perform(get("/api/customers/search").param("query", searchTerm))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(1))
      .andExpect(jsonPath("$[0].fullName").value("John Doe"))
      .andExpect(jsonPath("$[1].id").value(2))
      .andExpect(jsonPath("$[1].fullName").value("Johnny Smith"));

    verify(customerService).searchByName(searchTerm);
  }

  @Test
  void findByCreatedDateRange_shouldReturnCustomersInDateRange()
    throws Exception {
    LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(2023, 1, 31, 23, 59);

    CustomerInfo customer = CustomerInfo.builder()
      .id(1L)
      .fullName("January Customer")
      .email("jan@example.com")
      .build();

    List<CustomerInfo> customers = List.of(customer);

    when(customerService.findByCreatedDateRange(startDate, endDate)).thenReturn(
      customers
    );

    mockMvc
      .perform(
        get("/api/customers/created-between")
          .param("startDate", startDate.toString())
          .param("endDate", endDate.toString())
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(1))
      .andExpect(jsonPath("$[0].fullName").value("January Customer"))
      .andExpect(jsonPath("$[0].email").value("jan@example.com"));

    verify(customerService).findByCreatedDateRange(startDate, endDate);
  }
}
