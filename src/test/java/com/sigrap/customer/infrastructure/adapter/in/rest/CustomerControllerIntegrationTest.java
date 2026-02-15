package com.sigrap.customer.infrastructure.adapter.in.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.config.BaseIntegrationTest;
import com.sigrap.customer.domain.model.Customer;
import com.sigrap.customer.domain.model.CustomerEmail;
import com.sigrap.customer.domain.model.CustomerId;
import com.sigrap.customer.domain.model.CustomerName;
import com.sigrap.customer.domain.model.CustomerPhone;
import com.sigrap.customer.domain.port.CustomerRepositoryPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Integration test for the new hexagonal architecture CustomerController.
 * Tests the /api/v2/customers endpoints.
 */
public class CustomerControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepositoryPort customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        // Create a test customer using domain model
        testCustomer = new Customer(
            new CustomerName("John Doe"),
            "DOC123",
            new CustomerEmail("john.doe@example.com"),
            new CustomerPhone("5551234567"),
            "123 Main St, Anytown"
        );
        testCustomer = customerRepository.save(testCustomer);
    }

    @AfterEach
    void tearDown() {
        // Clean up test data
        if (testCustomer != null && testCustomer.getId() != null) {
            try {
                customerRepository.deleteById(testCustomer.getId());
            } catch (Exception e) {
                // Ignore if already deleted
            }
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllCustomers() throws Exception {
        mockMvc.perform(get("/api/v2/customers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(testCustomer.getId().value()))
            .andExpect(jsonPath("$[0].fullName").value("John Doe"))
            .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetCustomerById() throws Exception {
        mockMvc.perform(get("/api/v2/customers/{id}", testCustomer.getId().value()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testCustomer.getId().value()))
            .andExpect(jsonPath("$.fullName").value("John Doe"))
            .andExpect(jsonPath("$.email").value("john.doe@example.com"))
            .andExpect(jsonPath("$.phoneNumber").value("5551234567"))
            .andExpect(jsonPath("$.documentId").value("DOC123"))
            .andExpect(jsonPath("$.address").value("123 Main St, Anytown"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateCustomer() throws Exception {
        CustomerRequest request = new CustomerRequest(
            "Jane Smith",
            "DOC456",
            "jane.smith@example.com",
            "5559876543",
            "456 Oak St, Other Town"
        );

        MvcResult result = mockMvc.perform(post("/api/v2/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.fullName").value("Jane Smith"))
            .andExpect(jsonPath("$.email").value("jane.smith@example.com"))
            .andExpect(jsonPath("$.phoneNumber").value("5559876543"))
            .andExpect(jsonPath("$.documentId").value("DOC456"))
            .andExpect(jsonPath("$.address").value("456 Oak St, Other Town"))
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        CustomerResponse createdCustomer = objectMapper.readValue(responseContent, CustomerResponse.class);

        // Clean up
        customerRepository.deleteById(new CustomerId(createdCustomer.id()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateCustomer() throws Exception {
        CustomerRequest updateRequest = new CustomerRequest(
            "John Doe Updated",
            "DOC123-UPDATED",
            "john.doe@example.com",
            "5551234567",
            "789 Pine St, New Town"
        );

        mockMvc.perform(put("/api/v2/customers/{id}", testCustomer.getId().value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testCustomer.getId().value()))
            .andExpect(jsonPath("$.fullName").value("John Doe Updated"))
            .andExpect(jsonPath("$.documentId").value("DOC123-UPDATED"))
            .andExpect(jsonPath("$.address").value("789 Pine St, New Town"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteCustomer() throws Exception {
        // Create a customer to delete
        Customer customerToDelete = new Customer(
            new CustomerName("Delete Me"),
            "DOC999",
            new CustomerEmail("delete.me@example.com"),
            new CustomerPhone("5559999999"),
            "999 Delete St"
        );
        customerToDelete = customerRepository.save(customerToDelete);

        mockMvc.perform(delete("/api/v2/customers/{id}", customerToDelete.getId().value()))
            .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/v2/customers/{id}", customerToDelete.getId().value()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForInvalidEmail() throws Exception {
        CustomerRequest invalidRequest = new CustomerRequest(
            "Invalid Email User",
            "DOC789",
            "not-an-email",
            "5551234567",
            "123 Test St"
        );

        mockMvc.perform(post("/api/v2/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForMissingRequiredFields() throws Exception {
        CustomerRequest invalidRequest = new CustomerRequest(
            null,  // Missing fullName
            "DOC789",
            null,  // Missing email
            "5551234567",
            "123 Test St"
        );

        mockMvc.perform(post("/api/v2/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundForNonExistentCustomer() throws Exception {
        mockMvc.perform(get("/api/v2/customers/{id}", 99999L))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForBlankFullName() throws Exception {
        CustomerRequest request = new CustomerRequest(
            "",  // Blank fullName
            "DOC789",
            "test@example.com",
            "5551234567",
            "123 Test St"
        );

        mockMvc.perform(post("/api/v2/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForFullNameTooLong() throws Exception {
        String longName = "A".repeat(256); // Exceeds 255 character limit
        CustomerRequest request = new CustomerRequest(
            longName,
            "DOC789",
            "test@example.com",
            "5551234567",
            "123 Test St"
        );

        mockMvc.perform(post("/api/v2/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForEmailTooLong() throws Exception {
        String longEmail = "a".repeat(250) + "@example.com"; // Exceeds 255 character limit
        CustomerRequest request = new CustomerRequest(
            "Valid Name",
            "DOC789",
            longEmail,
            "5551234567",
            "123 Test St"
        );

        mockMvc.perform(post("/api/v2/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForDuplicateEmail() throws Exception {
        CustomerRequest request = new CustomerRequest(
            "Another Customer",
            "DOC999",
            "john.doe@example.com",  // Same email as testCustomer
            "5559999999",
            "999 Test St"
        );

        mockMvc.perform(post("/api/v2/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateCustomerWithMinimalFields() throws Exception {
        CustomerRequest request = new CustomerRequest(
            "Minimal Customer",
            null,  // Optional
            "minimal@example.com",
            null,  // Optional
            null   // Optional
        );

        MvcResult result = mockMvc.perform(post("/api/v2/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.fullName").value("Minimal Customer"))
            .andExpect(jsonPath("$.email").value("minimal@example.com"))
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        CustomerResponse createdCustomer = objectMapper.readValue(responseContent, CustomerResponse.class);

        // Clean up
        customerRepository.deleteById(new CustomerId(createdCustomer.id()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateCustomerWithDifferentEmail() throws Exception {
        CustomerRequest updateRequest = new CustomerRequest(
            "John Doe",
            "DOC123",
            "newemail@example.com",  // Different email
            "5551234567",
            "123 Main St, Anytown"
        );

        mockMvc.perform(put("/api/v2/customers/{id}", testCustomer.getId().value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("newemail@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenUpdatingWithDuplicateEmail() throws Exception {
        // Create another customer
        Customer anotherCustomer = new Customer(
            new CustomerName("Another Customer"),
            "DOC456",
            new CustomerEmail("another@example.com"),
            new CustomerPhone("5559999999"),
            "456 Another St"
        );
        anotherCustomer = customerRepository.save(anotherCustomer);

        // Try to update testCustomer with anotherCustomer's email
        CustomerRequest updateRequest = new CustomerRequest(
            "John Doe",
            "DOC123",
            "another@example.com",  // Duplicate email
            "5551234567",
            "123 Main St, Anytown"
        );

        mockMvc.perform(put("/api/v2/customers/{id}", testCustomer.getId().value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isBadRequest());

        // Clean up
        customerRepository.deleteById(anotherCustomer.getId());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowUpdateWithSameEmail() throws Exception {
        CustomerRequest updateRequest = new CustomerRequest(
            "John Doe Updated",
            "DOC123",
            "john.doe@example.com",  // Same email
            "5551234567",
            "123 Main St, Anytown"
        );

        mockMvc.perform(put("/api/v2/customers/{id}", testCustomer.getId().value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fullName").value("John Doe Updated"))
            .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenUpdatingNonExistentCustomer() throws Exception {
        CustomerRequest updateRequest = new CustomerRequest(
            "Non Existent",
            "DOC999",
            "nonexistent@example.com",
            "5559999999",
            "999 Test St"
        );

        mockMvc.perform(put("/api/v2/customers/{id}", 99999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenDeletingNonExistentCustomer() throws Exception {
        mockMvc.perform(delete("/api/v2/customers/{id}", 99999L))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldBatchDeleteMultipleCustomers() throws Exception {
        // Create additional customers
        Customer customer2 = customerRepository.save(new Customer(
            new CustomerName("Customer 2"),
            "DOC2",
            new CustomerEmail("customer2@example.com"),
            new CustomerPhone("5552222222"),
            "222 Test St"
        ));
        Customer customer3 = customerRepository.save(new Customer(
            new CustomerName("Customer 3"),
            "DOC3",
            new CustomerEmail("customer3@example.com"),
            new CustomerPhone("5553333333"),
            "333 Test St"
        ));

        java.util.List<Long> ids = java.util.List.of(
            customer2.getId().value(),
            customer3.getId().value()
        );

        mockMvc.perform(delete("/api/v2/customers/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
            .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/v2/customers/{id}", customer2.getId().value()))
            .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/v2/customers/{id}", customer3.getId().value()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnEmptyArrayWhenNoCustomers() throws Exception {
        // Clean up all customers
        customerRepository.deleteById(testCustomer.getId());

        mockMvc.perform(get("/api/v2/customers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForPhoneNumberTooLong() throws Exception {
        String longPhone = "5".repeat(21); // Exceeds 20 character limit
        CustomerRequest request = new CustomerRequest(
            "Valid Name",
            "DOC789",
            "test@example.com",
            longPhone,
            "123 Test St"
        );

        mockMvc.perform(post("/api/v2/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForAddressTooLong() throws Exception {
        String longAddress = "A".repeat(501); // Exceeds 500 character limit
        CustomerRequest request = new CustomerRequest(
            "Valid Name",
            "DOC789",
            "test@example.com",
            "5551234567",
            longAddress
        );

        mockMvc.perform(post("/api/v2/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForDocumentIdTooLong() throws Exception {
        String longDocId = "D".repeat(51); // Exceeds 50 character limit
        CustomerRequest request = new CustomerRequest(
            "Valid Name",
            longDocId,
            "test@example.com",
            "5551234567",
            "123 Test St"
        );

        mockMvc.perform(post("/api/v2/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldHandleCustomerWithSpecialCharacters() throws Exception {
        CustomerRequest request = new CustomerRequest(
            "José María O'Brien-Smith",
            "DOC-SPECIAL",
            "jose.maria@example.com",
            "+1 (555) 123-4567",
            "123 Main St, Apt #5, São Paulo"
        );

        MvcResult result = mockMvc.perform(post("/api/v2/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.fullName").value("José María O'Brien-Smith"))
            .andExpect(jsonPath("$.phoneNumber").value("+1 (555) 123-4567"))
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        CustomerResponse createdCustomer = objectMapper.readValue(responseContent, CustomerResponse.class);

        // Clean up
        customerRepository.deleteById(new CustomerId(createdCustomer.id()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForInvalidIdInUpdate() throws Exception {
        CustomerRequest updateRequest = new CustomerRequest(
            "Valid Name",
            "DOC789",
            "test@example.com",
            "5551234567",
            "123 Test St"
        );

        mockMvc.perform(put("/api/v2/customers/{id}", 0L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForInvalidIdInDelete() throws Exception {
        mockMvc.perform(delete("/api/v2/customers/{id}", 0L))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForInvalidIdInGet() throws Exception {
        mockMvc.perform(get("/api/v2/customers/{id}", -1L))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldBatchDeleteWithEmptyList() throws Exception {
        java.util.List<Long> emptyIds = java.util.List.of();

        mockMvc.perform(delete("/api/v2/customers/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyIds)))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenBatchDeletingNonExistentCustomers() throws Exception {
        java.util.List<Long> nonExistentIds = java.util.List.of(99998L, 99999L);

        mockMvc.perform(delete("/api/v2/customers/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentIds)))
            .andExpect(status().isNotFound());
    }
}
