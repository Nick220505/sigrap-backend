package com.sigrap.customer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.config.BaseIntegrationTest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class CustomerIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private Customer testCustomer;

  @BeforeEach
  void setUp() {
    customerRepository.deleteAll();

    testCustomer = Customer.builder()
      .fullName("John Doe")
      .email("john.doe@example.com")
      .phoneNumber("5551234567")
      .documentId("DOC123")
      .address("123 Main St, Anytown")
      .build();
    testCustomer = customerRepository.save(testCustomer);
  }

  @AfterEach
  void tearDown() {
    customerRepository.deleteAll();
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void crudOperations() throws Exception {
    mockMvc
      .perform(get("/api/customers"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testCustomer.getId()))
      .andExpect(jsonPath("$[0].fullName").value(testCustomer.getFullName()))
      .andExpect(jsonPath("$[0].email").value(testCustomer.getEmail()));

    mockMvc
      .perform(get("/api/customers/{id}", testCustomer.getId()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testCustomer.getId()))
      .andExpect(jsonPath("$.fullName").value(testCustomer.getFullName()))
      .andExpect(jsonPath("$.email").value(testCustomer.getEmail()));

    CustomerData newCustomerData = CustomerData.builder()
      .fullName("Jane Smith")
      .email("jane.smith@example.com")
      .phoneNumber("5559876543")
      .documentId("DOC456")
      .address("456 Oak St, Other Town")
      .build();

    MvcResult result = mockMvc
      .perform(
        post("/api/customers")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(newCustomerData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.fullName").value("Jane Smith"))
      .andExpect(jsonPath("$.email").value("jane.smith@example.com"))
      .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    CustomerInfo createdCustomer = objectMapper.readValue(
      responseContent,
      CustomerInfo.class
    );

    CustomerData updateData = CustomerData.builder()
      .fullName("Jane Smith-Johnson")
      .email("jane.smith@example.com")
      .phoneNumber("5559876543")
      .documentId("DOC456")
      .address("789 Pine St, New Town")
      .build();

    mockMvc
      .perform(
        put("/api/customers/{id}", createdCustomer.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(updateData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(createdCustomer.getId()))
      .andExpect(jsonPath("$.fullName").value("Jane Smith-Johnson"))
      .andExpect(jsonPath("$.address").value("789 Pine St, New Town"));

    mockMvc
      .perform(delete("/api/customers/{id}", createdCustomer.getId()))
      .andExpect(status().isNoContent());

    mockMvc
      .perform(get("/api/customers/{id}", createdCustomer.getId()))
      .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void searchByName() throws Exception {
    Customer customer1 = Customer.builder()
      .fullName("Alice Johnson")
      .email("alice@example.com")
      .phoneNumber("5551111111")
      .documentId("DOC111")
      .build();

    Customer customer2 = Customer.builder()
      .fullName("Bob Anderson")
      .email("bob@example.com")
      .phoneNumber("5552222222")
      .documentId("DOC222")
      .build();

    customerRepository.saveAll(Arrays.asList(customer1, customer2));

    mockMvc
      .perform(get("/api/customers/search").param("query", "Johnson"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$[0].fullName").value("Alice Johnson"));

    mockMvc
      .perform(get("/api/customers/search").param("query", "Bob"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$[0].fullName").value("Bob Anderson"));

    mockMvc
      .perform(get("/api/customers/search").param("query", "NonExistentName"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void findByCreatedDateRange() throws Exception {
    LocalDateTime startDate = LocalDateTime.now().minusDays(1);
    LocalDateTime endDate = LocalDateTime.now().plusDays(1);

    String startDateStr = startDate.format(DateTimeFormatter.ISO_DATE_TIME);
    String endDateStr = endDate.format(DateTimeFormatter.ISO_DATE_TIME);

    mockMvc
      .perform(
        get("/api/customers/created-between")
          .param("startDate", startDateStr)
          .param("endDate", endDateStr)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$[0].id").value(testCustomer.getId()));

    LocalDateTime pastStartDate = LocalDateTime.now().minusYears(2);
    LocalDateTime pastEndDate = LocalDateTime.now().minusYears(1);

    String pastStartDateStr = pastStartDate.format(
      DateTimeFormatter.ISO_DATE_TIME
    );
    String pastEndDateStr = pastEndDate.format(DateTimeFormatter.ISO_DATE_TIME);

    mockMvc
      .perform(
        get("/api/customers/created-between")
          .param("startDate", pastStartDateStr)
          .param("endDate", pastEndDateStr)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void validationConstraints() throws Exception {
    CustomerData invalidCustomerData = CustomerData.builder().build();

    mockMvc
      .perform(
        post("/api/customers")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidCustomerData))
      )
      .andExpect(status().isBadRequest());

    CustomerData invalidEmailCustomer = CustomerData.builder()
      .fullName("Invalid Email User")
      .email("not-an-email")
      .phoneNumber("5551234567")
      .build();

    mockMvc
      .perform(
        post("/api/customers")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidEmailCustomer))
      )
      .andExpect(status().isBadRequest());

    CustomerData duplicateEmailCustomer = CustomerData.builder()
      .fullName("Duplicate Email User")
      .email(testCustomer.getEmail())
      .phoneNumber("5551234567")
      .build();

    mockMvc
      .perform(
        post("/api/customers")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(duplicateEmailCustomer))
      )
      .andExpect(status().isBadRequest());
  }
}
