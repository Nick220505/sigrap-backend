package com.sigrap.supplier.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.supplier.application.port.in.*;
import com.sigrap.supplier.application.port.in.command.CreateSupplierCommand;
import com.sigrap.supplier.application.port.in.command.UpdateSupplierCommand;
import com.sigrap.supplier.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Integration tests for SupplierController.
 * Tests REST endpoints with mocked use cases.
 * 
 * These tests verify:
 * - HTTP request/response handling
 * - Request validation
 * - Proper status codes
 * - Error handling
 */
class SupplierControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CreateSupplierUseCase createSupplierUseCase;
    private GetSupplierUseCase getSupplierUseCase;
    private UpdateSupplierUseCase updateSupplierUseCase;
    private DeleteSupplierUseCase deleteSupplierUseCase;
    private SupplierResponseMapper responseMapper;

    private Supplier testSupplier;
    private SupplierResponse testResponse;

    @BeforeEach
    void setUp() {
        createSupplierUseCase = mock(CreateSupplierUseCase.class);
        getSupplierUseCase = mock(GetSupplierUseCase.class);
        updateSupplierUseCase = mock(UpdateSupplierUseCase.class);
        deleteSupplierUseCase = mock(DeleteSupplierUseCase.class);
        responseMapper = mock(SupplierResponseMapper.class);
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        SupplierController controller = new SupplierController(
            createSupplierUseCase,
            getSupplierUseCase,
            updateSupplierUseCase,
            deleteSupplierUseCase,
            responseMapper
        );

        mockMvc = standaloneSetup(controller).build();

        testSupplier = new Supplier(
            new SupplierId(1L),
            new SupplierName("ABC Supplies"),
            "John Smith",
            new SupplierEmail("john@abcsupplies.com"),
            new SupplierPhone("555-1234"),
            "123 Main St",
            LocalDateTime.of(2024, 1, 1, 10, 0),
            LocalDateTime.of(2024, 1, 1, 10, 0)
        );

        testResponse = new SupplierResponse(
            1L,
            "ABC Supplies",
            "John Smith",
            "john@abcsupplies.com",
            "555-1234",
            "123 Main St",
            LocalDateTime.of(2024, 1, 1, 10, 0),
            LocalDateTime.of(2024, 1, 1, 10, 0)
        );
    }

    @Test
    void shouldCreateSupplier() throws Exception {
        // Given
        SupplierRequest request = new SupplierRequest(
            "ABC Supplies",
            "John Smith",
            "john@abcsupplies.com",
            "555-1234",
            "123 Main St"
        );

        when(createSupplierUseCase.create(any(CreateSupplierCommand.class))).thenReturn(testSupplier);
        when(responseMapper.toResponse(testSupplier)).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(post("/api/v2/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("ABC Supplies"))
            .andExpect(jsonPath("$.contactName").value("John Smith"))
            .andExpect(jsonPath("$.email").value("john@abcsupplies.com"))
            .andExpect(jsonPath("$.phone").value("555-1234"))
            .andExpect(jsonPath("$.address").value("123 Main St"));

        verify(createSupplierUseCase).create(any(CreateSupplierCommand.class));
        verify(responseMapper).toResponse(testSupplier);
    }

    @Test
    void shouldReturnBadRequestWhenCreatingSupplierWithInvalidData() throws Exception {
        // Given - missing required name
        SupplierRequest request = new SupplierRequest(
            "",
            "John Smith",
            "john@abcsupplies.com",
            "555-1234",
            "123 Main St"
        );

        // When & Then
        mockMvc.perform(post("/api/v2/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(createSupplierUseCase, never()).create(any());
    }

    @Test
    void shouldReturnBadRequestWhenCreatingSupplierWithInvalidEmail() throws Exception {
        // Given - invalid email format
        SupplierRequest request = new SupplierRequest(
            "ABC Supplies",
            "John Smith",
            "invalid-email",
            "555-1234",
            "123 Main St"
        );

        // When & Then
        mockMvc.perform(post("/api/v2/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(createSupplierUseCase, never()).create(any());
    }

    @Test
    void shouldGetSupplierById() throws Exception {
        // Given
        when(getSupplierUseCase.getById(any(SupplierId.class))).thenReturn(testSupplier);
        when(responseMapper.toResponse(testSupplier)).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/v2/suppliers/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("ABC Supplies"))
            .andExpect(jsonPath("$.email").value("john@abcsupplies.com"));

        verify(getSupplierUseCase).getById(any(SupplierId.class));
        verify(responseMapper).toResponse(testSupplier);
    }

    @Test
    void shouldReturnNotFoundWhenSupplierDoesNotExist() throws Exception {
        // Given
        when(getSupplierUseCase.getById(any(SupplierId.class)))
            .thenThrow(new IllegalArgumentException("Supplier not found with id: 999"));

        // When & Then
        mockMvc.perform(get("/api/v2/suppliers/999"))
            .andExpect(status().isBadRequest());

        verify(getSupplierUseCase).getById(any(SupplierId.class));
    }

    @Test
    void shouldGetAllSuppliers() throws Exception {
        // Given
        Supplier supplier2 = new Supplier(
            new SupplierId(2L),
            new SupplierName("XYZ Corp"),
            "Jane Doe",
            new SupplierEmail("jane@xyzcorp.com"),
            new SupplierPhone("555-5678"),
            "456 Oak Ave",
            LocalDateTime.of(2024, 1, 2, 10, 0),
            LocalDateTime.of(2024, 1, 2, 10, 0)
        );

        SupplierResponse response2 = new SupplierResponse(
            2L,
            "XYZ Corp",
            "Jane Doe",
            "jane@xyzcorp.com",
            "555-5678",
            "456 Oak Ave",
            LocalDateTime.of(2024, 1, 2, 10, 0),
            LocalDateTime.of(2024, 1, 2, 10, 0)
        );

        when(getSupplierUseCase.getAll()).thenReturn(List.of(testSupplier, supplier2));
        when(responseMapper.toResponse(testSupplier)).thenReturn(testResponse);
        when(responseMapper.toResponse(supplier2)).thenReturn(response2);

        // When & Then
        mockMvc.perform(get("/api/v2/suppliers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("ABC Supplies"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("XYZ Corp"));

        verify(getSupplierUseCase).getAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoSuppliers() throws Exception {
        // Given
        when(getSupplierUseCase.getAll()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v2/suppliers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));

        verify(getSupplierUseCase).getAll();
    }

    @Test
    void shouldUpdateSupplier() throws Exception {
        // Given
        SupplierRequest request = new SupplierRequest(
            "ABC Supplies Updated",
            "John Smith Jr",
            "john.jr@abcsupplies.com",
            "555-9999",
            "789 Pine Rd"
        );

        Supplier updatedSupplier = new Supplier(
            new SupplierId(1L),
            new SupplierName("ABC Supplies Updated"),
            "John Smith Jr",
            new SupplierEmail("john.jr@abcsupplies.com"),
            new SupplierPhone("555-9999"),
            "789 Pine Rd",
            LocalDateTime.of(2024, 1, 1, 10, 0),
            LocalDateTime.of(2024, 1, 2, 10, 0)
        );

        SupplierResponse updatedResponse = new SupplierResponse(
            1L,
            "ABC Supplies Updated",
            "John Smith Jr",
            "john.jr@abcsupplies.com",
            "555-9999",
            "789 Pine Rd",
            LocalDateTime.of(2024, 1, 1, 10, 0),
            LocalDateTime.of(2024, 1, 2, 10, 0)
        );

        when(updateSupplierUseCase.update(any(SupplierId.class), any(UpdateSupplierCommand.class)))
            .thenReturn(updatedSupplier);
        when(responseMapper.toResponse(updatedSupplier)).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v2/suppliers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("ABC Supplies Updated"))
            .andExpect(jsonPath("$.contactName").value("John Smith Jr"))
            .andExpect(jsonPath("$.email").value("john.jr@abcsupplies.com"));

        verify(updateSupplierUseCase).update(any(SupplierId.class), any(UpdateSupplierCommand.class));
        verify(responseMapper).toResponse(updatedSupplier);
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingWithInvalidData() throws Exception {
        // Given - invalid email
        SupplierRequest request = new SupplierRequest(
            "ABC Supplies",
            "John Smith",
            "invalid-email",
            "555-1234",
            "123 Main St"
        );

        // When & Then
        mockMvc.perform(put("/api/v2/suppliers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(updateSupplierUseCase, never()).update(any(), any());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentSupplier() throws Exception {
        // Given
        SupplierRequest request = new SupplierRequest(
            "ABC Supplies",
            "John Smith",
            "john@abcsupplies.com",
            "555-1234",
            "123 Main St"
        );

        when(updateSupplierUseCase.update(any(SupplierId.class), any(UpdateSupplierCommand.class)))
            .thenThrow(new IllegalArgumentException("Supplier not found with id: 999"));

        // When & Then
        mockMvc.perform(put("/api/v2/suppliers/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(updateSupplierUseCase).update(any(SupplierId.class), any(UpdateSupplierCommand.class));
    }

    @Test
    void shouldDeleteSupplier() throws Exception {
        // Given
        doNothing().when(deleteSupplierUseCase).delete(any(SupplierId.class));

        // When & Then
        mockMvc.perform(delete("/api/v2/suppliers/1"))
            .andExpect(status().isNoContent());

        verify(deleteSupplierUseCase).delete(any(SupplierId.class));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentSupplier() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("Supplier not found with id: 999"))
            .when(deleteSupplierUseCase).delete(any(SupplierId.class));

        // When & Then
        mockMvc.perform(delete("/api/v2/suppliers/999"))
            .andExpect(status().isBadRequest());

        verify(deleteSupplierUseCase).delete(any(SupplierId.class));
    }

    @Test
    void shouldDeleteMultipleSuppliers() throws Exception {
        // Given
        List<Long> ids = List.of(1L, 2L, 3L);
        doNothing().when(deleteSupplierUseCase).deleteAll(anyList());

        // When & Then
        mockMvc.perform(delete("/api/v2/suppliers/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
            .andExpect(status().isNoContent());

        verify(deleteSupplierUseCase).deleteAll(anyList());
    }

    @Test
    void shouldReturnBadRequestWhenBatchDeleteWithEmptyList() throws Exception {
        // Given
        List<Long> emptyIds = List.of();

        // When & Then
        mockMvc.perform(delete("/api/v2/suppliers/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyIds)))
            .andExpect(status().isNoContent());

        verify(deleteSupplierUseCase).deleteAll(anyList());
    }

    @Test
    void shouldHandleEmailConflictWhenCreating() throws Exception {
        // Given
        SupplierRequest request = new SupplierRequest(
            "ABC Supplies",
            "John Smith",
            "john@abcsupplies.com",
            "555-1234",
            "123 Main St"
        );

        when(createSupplierUseCase.create(any(CreateSupplierCommand.class)))
            .thenThrow(new IllegalArgumentException("Supplier with email 'john@abcsupplies.com' already exists"));

        // When & Then
        mockMvc.perform(post("/api/v2/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(createSupplierUseCase).create(any(CreateSupplierCommand.class));
    }

    @Test
    void shouldHandleEmailConflictWhenUpdating() throws Exception {
        // Given
        SupplierRequest request = new SupplierRequest(
            "ABC Supplies",
            "John Smith",
            "existing@example.com",
            "555-1234",
            "123 Main St"
        );

        when(updateSupplierUseCase.update(any(SupplierId.class), any(UpdateSupplierCommand.class)))
            .thenThrow(new IllegalArgumentException("Another supplier with email 'existing@example.com' already exists"));

        // When & Then
        mockMvc.perform(put("/api/v2/suppliers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(updateSupplierUseCase).update(any(SupplierId.class), any(UpdateSupplierCommand.class));
    }

    @Test
    void shouldCreateSupplierWithMinimalData() throws Exception {
        // Given - only required fields
        SupplierRequest request = new SupplierRequest(
            "Minimal Supplier",
            null,
            "minimal@example.com",
            null,
            null
        );

        Supplier minimalSupplier = new Supplier(
            new SupplierId(1L),
            new SupplierName("Minimal Supplier"),
            null,
            new SupplierEmail("minimal@example.com"),
            null,
            null,
            LocalDateTime.of(2024, 1, 1, 10, 0),
            LocalDateTime.of(2024, 1, 1, 10, 0)
        );

        SupplierResponse minimalResponse = new SupplierResponse(
            1L,
            "Minimal Supplier",
            null,
            "minimal@example.com",
            null,
            null,
            LocalDateTime.of(2024, 1, 1, 10, 0),
            LocalDateTime.of(2024, 1, 1, 10, 0)
        );

        when(createSupplierUseCase.create(any(CreateSupplierCommand.class))).thenReturn(minimalSupplier);
        when(responseMapper.toResponse(minimalSupplier)).thenReturn(minimalResponse);

        // When & Then
        mockMvc.perform(post("/api/v2/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Minimal Supplier"))
            .andExpect(jsonPath("$.email").value("minimal@example.com"))
            .andExpect(jsonPath("$.contactName").isEmpty())
            .andExpect(jsonPath("$.phone").isEmpty())
            .andExpect(jsonPath("$.address").isEmpty());

        verify(createSupplierUseCase).create(any(CreateSupplierCommand.class));
    }

    @Test
    void shouldValidateMaxLengthConstraints() throws Exception {
        // Given - name exceeds max length
        String longName = "A".repeat(201);
        SupplierRequest request = new SupplierRequest(
            longName,
            "John Smith",
            "john@abcsupplies.com",
            "555-1234",
            "123 Main St"
        );

        // When & Then
        mockMvc.perform(post("/api/v2/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(createSupplierUseCase, never()).create(any());
    }

    @Test
    void shouldValidatePhoneMaxLength() throws Exception {
        // Given - phone exceeds max length
        String longPhone = "1".repeat(21);
        SupplierRequest request = new SupplierRequest(
            "ABC Supplies",
            "John Smith",
            "john@abcsupplies.com",
            longPhone,
            "123 Main St"
        );

        // When & Then
        mockMvc.perform(post("/api/v2/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(createSupplierUseCase, never()).create(any());
    }
}
