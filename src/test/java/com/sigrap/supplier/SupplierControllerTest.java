package com.sigrap.supplier;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class SupplierControllerTest {

  @Mock
  private SupplierService supplierService;

  @InjectMocks
  private SupplierController supplierController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    mockMvc = standaloneSetup(supplierController).build();

    objectMapper = new ObjectMapper();
  }

  @Test
  void findAll_shouldReturnAllSuppliers() throws Exception {
    SupplierInfo supplier1 = SupplierInfo.builder()
      .id(1L)
      .name("Supplier 1")
      .email("supplier1@example.com")
      .build();

    SupplierInfo supplier2 = SupplierInfo.builder()
      .id(2L)
      .name("Supplier 2")
      .email("supplier2@example.com")
      .build();

    List<SupplierInfo> suppliers = List.of(supplier1, supplier2);

    when(supplierService.findAll()).thenReturn(suppliers);

    mockMvc
      .perform(get("/api/suppliers"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].id").value(1))
      .andExpect(jsonPath("$[0].name").value("Supplier 1"))
      .andExpect(jsonPath("$[0].email").value("supplier1@example.com"))
      .andExpect(jsonPath("$[1].id").value(2))
      .andExpect(jsonPath("$[1].name").value("Supplier 2"))
      .andExpect(jsonPath("$[1].email").value("supplier2@example.com"));
  }

  @Test
  void findById_shouldReturnSupplier_whenExists() throws Exception {
    Long id = 1L;

    SupplierInfo supplierInfo = SupplierInfo.builder()
      .id(id)
      .name("Test Supplier")
      .contactPerson("John Contact")
      .phone("1234567890")
      .email("supplier@example.com")
      .build();

    when(supplierService.findById(id)).thenReturn(supplierInfo);

    mockMvc
      .perform(get("/api/suppliers/{id}", id))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(id))
      .andExpect(jsonPath("$.name").value("Test Supplier"))
      .andExpect(jsonPath("$.contactPerson").value("John Contact"))
      .andExpect(jsonPath("$.phone").value("1234567890"))
      .andExpect(jsonPath("$.email").value("supplier@example.com"));
  }

  @Test
  void create_shouldCreateSupplier() throws Exception {
    SupplierData supplierData = SupplierData.builder()
      .name("New Supplier")
      .contactPerson("Jane Contact")
      .phone("9876543210")
      .email("new.supplier@example.com")
      .build();

    SupplierInfo createdSupplierInfo = SupplierInfo.builder()
      .id(1L)
      .name("New Supplier")
      .contactPerson("Jane Contact")
      .phone("9876543210")
      .email("new.supplier@example.com")
      .build();

    when(supplierService.create(any(SupplierData.class))).thenReturn(
      createdSupplierInfo
    );

    mockMvc
      .perform(
        post("/api/suppliers")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(supplierData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.name").value("New Supplier"))
      .andExpect(jsonPath("$.contactPerson").value("Jane Contact"))
      .andExpect(jsonPath("$.phone").value("9876543210"))
      .andExpect(jsonPath("$.email").value("new.supplier@example.com"));

    verify(supplierService).create(any(SupplierData.class));
  }

  @Test
  void update_shouldUpdateSupplier() throws Exception {
    Long id = 1L;

    SupplierData supplierData = SupplierData.builder()
      .name("Updated Supplier")
      .contactPerson("Updated Contact")
      .phone("5555555555")
      .email("updated@example.com")
      .build();

    SupplierInfo updatedSupplierInfo = SupplierInfo.builder()
      .id(id)
      .name("Updated Supplier")
      .contactPerson("Updated Contact")
      .phone("5555555555")
      .email("updated@example.com")
      .build();

    when(supplierService.update(eq(id), any(SupplierData.class))).thenReturn(
      updatedSupplierInfo
    );

    mockMvc
      .perform(
        put("/api/suppliers/{id}", id)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(supplierData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(id))
      .andExpect(jsonPath("$.name").value("Updated Supplier"))
      .andExpect(jsonPath("$.contactPerson").value("Updated Contact"))
      .andExpect(jsonPath("$.phone").value("5555555555"))
      .andExpect(jsonPath("$.email").value("updated@example.com"));

    verify(supplierService).update(eq(id), any(SupplierData.class));
  }

  @Test
  void delete_shouldDeleteSupplier() throws Exception {
    Long id = 1L;
    doNothing().when(supplierService).delete(id);

    mockMvc
      .perform(delete("/api/suppliers/{id}", id))
      .andExpect(status().isNoContent());

    verify(supplierService).delete(id);
  }

  @Test
  void deleteAllById_shouldDeleteMultipleSuppliers() throws Exception {
    List<Long> ids = Arrays.asList(1L, 2L);
    doNothing().when(supplierService).deleteAllById(ids);

    mockMvc
      .perform(
        delete("/api/suppliers/delete-many")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(ids))
      )
      .andExpect(status().isNoContent());

    verify(supplierService).deleteAllById(ids);
  }
}
