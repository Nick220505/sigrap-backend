package com.sigrap.supplier;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.config.BaseIntegrationTest;
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

public class SupplierIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private SupplierRepository supplierRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private Supplier testSupplier;

  @BeforeEach
  void setUp() {
    supplierRepository.deleteAll();

    testSupplier = Supplier.builder()
      .name("Test Supplier")
      .contactPerson("John Contact")
      .phone("1234567890")
      .email("supplier@example.com")
      .address("123 Supplier St")
      .website("http://supplier.com")
      .productsProvided("Office supplies")
      .averageDeliveryTime(5)
      .paymentTerms("Net 30")
      .build();

    testSupplier = supplierRepository.save(testSupplier);
  }

  @AfterEach
  void tearDown() {
    supplierRepository.deleteAll();
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void crudOperations() throws Exception {
    mockMvc
      .perform(get("/api/suppliers"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testSupplier.getId()))
      .andExpect(jsonPath("$[0].name").value(testSupplier.getName()))
      .andExpect(jsonPath("$[0].email").value(testSupplier.getEmail()));

    mockMvc
      .perform(get("/api/suppliers/{id}", testSupplier.getId()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testSupplier.getId()))
      .andExpect(jsonPath("$.name").value(testSupplier.getName()))
      .andExpect(
        jsonPath("$.contactPerson").value(testSupplier.getContactPerson())
      )
      .andExpect(jsonPath("$.email").value(testSupplier.getEmail()));

    SupplierData newSupplierData = SupplierData.builder()
      .name("New Supplier")
      .contactPerson("Jane Contact")
      .phone("9876543210")
      .email("new.supplier@example.com")
      .address("456 New St")
      .website("http://newsupplier.com")
      .productsProvided("Stationery")
      .averageDeliveryTime(3)
      .paymentTerms("Net 15")
      .build();

    MvcResult result = mockMvc
      .perform(
        post("/api/suppliers")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(newSupplierData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.name").value(newSupplierData.getName()))
      .andExpect(
        jsonPath("$.contactPerson").value(newSupplierData.getContactPerson())
      )
      .andExpect(jsonPath("$.email").value(newSupplierData.getEmail()))
      .andExpect(jsonPath("$.phone").value(newSupplierData.getPhone()))
      .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    SupplierInfo createdSupplier = objectMapper.readValue(
      responseContent,
      SupplierInfo.class
    );
    Long createdSupplierId = createdSupplier.getId();

    SupplierData updateData = SupplierData.builder()
      .name("Updated Supplier")
      .contactPerson("Updated Contact")
      .phone("5555555555")
      .email("updated@example.com")
      .address("789 Updated St")
      .website("http://updated-supplier.com")
      .productsProvided("Updated Products")
      .averageDeliveryTime(2)
      .paymentTerms("Net 60")
      .build();

    mockMvc
      .perform(
        put("/api/suppliers/{id}", createdSupplierId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(updateData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(createdSupplierId))
      .andExpect(jsonPath("$.name").value("Updated Supplier"))
      .andExpect(jsonPath("$.contactPerson").value("Updated Contact"))
      .andExpect(jsonPath("$.email").value("updated@example.com"))
      .andExpect(jsonPath("$.phone").value("5555555555"))
      .andExpect(jsonPath("$.address").value("789 Updated St"))
      .andExpect(jsonPath("$.website").value("http://updated-supplier.com"))
      .andExpect(jsonPath("$.productsProvided").value("Updated Products"))
      .andExpect(jsonPath("$.averageDeliveryTime").value(2))
      .andExpect(jsonPath("$.paymentTerms").value("Net 60"));

    mockMvc
      .perform(delete("/api/suppliers/{id}", createdSupplierId))
      .andExpect(status().isNoContent());

    mockMvc
      .perform(get("/api/suppliers/{id}", createdSupplierId))
      .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void deleteMany() throws Exception {
    Supplier supplier1 = Supplier.builder()
      .name("Supplier 1")
      .email("supplier1@example.com")
      .phone("1111111111")
      .build();

    Supplier supplier2 = Supplier.builder()
      .name("Supplier 2")
      .email("supplier2@example.com")
      .phone("2222222222")
      .build();

    supplier1 = supplierRepository.save(supplier1);
    supplier2 = supplierRepository.save(supplier2);

    List<Long> idsToDelete = Arrays.asList(
      supplier1.getId(),
      supplier2.getId()
    );

    mockMvc
      .perform(
        delete("/api/suppliers/delete-many")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(idsToDelete))
      )
      .andExpect(status().isNoContent());

    for (Long id : idsToDelete) {
      mockMvc
        .perform(get("/api/suppliers/{id}", id))
        .andExpect(status().isNotFound());
    }
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void validationConstraints() throws Exception {
    SupplierData invalidSupplierData = SupplierData.builder()
      .email("invalid@example.com")
      .build();

    mockMvc
      .perform(
        post("/api/suppliers")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidSupplierData))
      )
      .andExpect(status().isBadRequest());

    SupplierData invalidPhoneData = SupplierData.builder()
      .name("Invalid Phone Supplier")
      .contactPerson("Test Contact")
      .phone("invalid-phone")
      .email("valid@example.com")
      .build();

    mockMvc
      .perform(
        post("/api/suppliers")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidPhoneData))
      )
      .andExpect(status().isBadRequest());

    SupplierData invalidEmailData = SupplierData.builder()
      .name("Invalid Email Supplier")
      .contactPerson("Test Contact")
      .phone("9999999999")
      .email("not-an-email")
      .build();

    mockMvc
      .perform(
        post("/api/suppliers")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidEmailData))
      )
      .andExpect(status().isBadRequest());
  }
}
