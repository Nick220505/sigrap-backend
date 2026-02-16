package com.sigrap.sale.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.exception.GlobalExceptionHandler;
import com.sigrap.sale.application.port.in.*;
import com.sigrap.sale.application.port.in.command.CreateSaleCommand;
import com.sigrap.sale.application.port.in.command.UpdateSaleCommand;
import com.sigrap.sale.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Integration tests for SaleController.
 * Tests REST endpoints with mocked use cases.
 * 
 * These tests verify:
 * - HTTP request/response handling
 * - Request validation
 * - Proper status codes
 * - State transition endpoints
 * - Query endpoints
 * - Error handling
 */
class SaleControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CreateSaleUseCase createSaleUseCase;
    private GetSaleUseCase getSaleUseCase;
    private UpdateSaleUseCase updateSaleUseCase;
    private DeleteSaleUseCase deleteSaleUseCase;
    private CompleteSaleUseCase completeSaleUseCase;
    private CancelSaleUseCase cancelSaleUseCase;
    private SaleResponseMapper responseMapper;

    private Sale testSale;
    private SaleResponse testResponse;

    @BeforeEach
    void setUp() {
        createSaleUseCase = mock(CreateSaleUseCase.class);
        getSaleUseCase = mock(GetSaleUseCase.class);
        updateSaleUseCase = mock(UpdateSaleUseCase.class);
        deleteSaleUseCase = mock(DeleteSaleUseCase.class);
        completeSaleUseCase = mock(CompleteSaleUseCase.class);
        cancelSaleUseCase = mock(CancelSaleUseCase.class);
        responseMapper = mock(SaleResponseMapper.class);
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        SaleController controller = new SaleController(
            createSaleUseCase,
            getSaleUseCase,
            updateSaleUseCase,
            deleteSaleUseCase,
            completeSaleUseCase,
            cancelSaleUseCase,
            responseMapper
        );

        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        testSale = new Sale(
            new SaleId(1L),
            new SaleNumber("SALE-2024-001"),
            1L,
            1L,
            LocalDateTime.of(2024, 1, 15, 0, 0),
            new BigDecimal("150.00"),
            PaymentMethod.CASH,
            SaleStatus.PENDING,
            "Test sale",
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 15, 10, 0)
        );

        testResponse = new SaleResponse(
            1L,
            "SALE-2024-001",
            1L,
            1L,
            LocalDateTime.of(2024, 1, 15, 0, 0),
            new BigDecimal("150.00"),
            PaymentMethod.CASH,
            SaleStatus.PENDING,
            "Test sale",
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 15, 10, 0)
        );
    }

    @Test
    void shouldCreateSale() throws Exception {
        // Given
        SaleRequest request = new SaleRequest(
            "SALE-2024-001",
            1L,
            1L,
            LocalDateTime.of(2024, 1, 15, 0, 0),
            PaymentMethod.CASH,
            "Test sale"
        );

        when(createSaleUseCase.create(any(CreateSaleCommand.class))).thenReturn(testSale);
        when(responseMapper.toResponse(testSale)).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(post("/api/v2/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.saleNumber").value("SALE-2024-001"))
            .andExpect(jsonPath("$.customerId").value(1))
            .andExpect(jsonPath("$.employeeId").value(1))
            .andExpect(jsonPath("$.totalAmount").value(150.00))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.paymentMethod").value("CASH"));

        verify(createSaleUseCase).create(any(CreateSaleCommand.class));
        verify(responseMapper).toResponse(testSale);
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithInvalidData() throws Exception {
        // Given - missing required sale number
        SaleRequest request = new SaleRequest(
            "",
            1L,
            1L,
            LocalDateTime.of(2024, 1, 15, 0, 0),
            PaymentMethod.CASH,
            "Test sale"
        );

        // When & Then
        mockMvc.perform(post("/api/v2/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(createSaleUseCase, never()).create(any());
    }

    @Test
    void shouldGetSaleById() throws Exception {
        // Given
        when(getSaleUseCase.getById(any(SaleId.class))).thenReturn(testSale);
        when(responseMapper.toResponse(testSale)).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/v2/sales/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.saleNumber").value("SALE-2024-001"))
            .andExpect(jsonPath("$.status").value("PENDING"));

        verify(getSaleUseCase).getById(any(SaleId.class));
        verify(responseMapper).toResponse(testSale);
    }

    @Test
    void shouldReturnNotFoundWhenSaleDoesNotExist() throws Exception {
        // Given
        when(getSaleUseCase.getById(any(SaleId.class)))
            .thenThrow(new IllegalArgumentException("Sale not found with id: 999"));

        // When & Then
        mockMvc.perform(get("/api/v2/sales/999"))
            .andExpect(status().isNotFound()); // 404 for "not found" messages

        verify(getSaleUseCase).getById(any(SaleId.class));
    }

    @Test
    void shouldGetAllSales() throws Exception {
        // Given
        Sale sale2 = new Sale(
            new SaleId(2L),
            new SaleNumber("SALE-2024-002"),
            2L,
            2L,
            LocalDateTime.of(2024, 1, 20, 0, 0),
            new BigDecimal("200.00"),
            PaymentMethod.CREDIT_CARD,
            SaleStatus.COMPLETED,
            "Second sale",
            LocalDateTime.of(2024, 1, 20, 10, 0),
            LocalDateTime.of(2024, 1, 20, 10, 0)
        );

        SaleResponse response2 = new SaleResponse(
            2L,
            "SALE-2024-002",
            2L,
            2L,
            LocalDateTime.of(2024, 1, 20, 0, 0),
            new BigDecimal("200.00"),
            PaymentMethod.CREDIT_CARD,
            SaleStatus.COMPLETED,
            "Second sale",
            LocalDateTime.of(2024, 1, 20, 10, 0),
            LocalDateTime.of(2024, 1, 20, 10, 0)
        );

        when(getSaleUseCase.getAll()).thenReturn(List.of(testSale, sale2));
        when(responseMapper.toResponse(testSale)).thenReturn(testResponse);
        when(responseMapper.toResponse(sale2)).thenReturn(response2);

        // When & Then
        mockMvc.perform(get("/api/v2/sales"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].status").value("PENDING"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].status").value("COMPLETED"));

        verify(getSaleUseCase).getAll();
    }

    @Test
    void shouldGetSalesByCustomerId() throws Exception {
        // Given
        when(getSaleUseCase.getByCustomerId(1L)).thenReturn(List.of(testSale));
        when(responseMapper.toResponse(testSale)).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/v2/sales/customer/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].customerId").value(1));

        verify(getSaleUseCase).getByCustomerId(1L);
    }

    @Test
    void shouldReturnEmptyListWhenNoSalesForCustomer() throws Exception {
        // Given
        when(getSaleUseCase.getByCustomerId(999L)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v2/sales/customer/999"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));

        verify(getSaleUseCase).getByCustomerId(999L);
    }

    @Test
    void shouldGetSalesByStatus() throws Exception {
        // Given
        when(getSaleUseCase.getByStatus(SaleStatus.PENDING)).thenReturn(List.of(testSale));
        when(responseMapper.toResponse(testSale)).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/v2/sales/status/PENDING"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(getSaleUseCase).getByStatus(SaleStatus.PENDING);
    }

    @Test
    void shouldReturnEmptyListWhenNoSalesWithStatus() throws Exception {
        // Given
        when(getSaleUseCase.getByStatus(SaleStatus.CANCELLED)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v2/sales/status/CANCELLED"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));

        verify(getSaleUseCase).getByStatus(SaleStatus.CANCELLED);
    }

    @Test
    void shouldUpdateSale() throws Exception {
        // Given
        UpdateSaleRequest request = new UpdateSaleRequest(
            PaymentMethod.DEBIT_CARD,
            "Updated notes"
        );

        Sale updatedSale = new Sale(
            testSale.getId(),
            testSale.getSaleNumber(),
            testSale.getCustomerId(),
            testSale.getEmployeeId(),
            testSale.getSaleDate(),
            testSale.getTotalAmount(),
            PaymentMethod.DEBIT_CARD,
            testSale.getStatus(),
            "Updated notes",
            testSale.getCreatedAt(),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        SaleResponse updatedResponse = new SaleResponse(
            1L,
            "SALE-2024-001",
            1L,
            1L,
            LocalDateTime.of(2024, 1, 15, 0, 0),
            new BigDecimal("150.00"),
            PaymentMethod.DEBIT_CARD,
            SaleStatus.PENDING,
            "Updated notes",
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        when(updateSaleUseCase.update(any(SaleId.class), any(UpdateSaleCommand.class)))
            .thenReturn(updatedSale);
        when(responseMapper.toResponse(updatedSale)).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v2/sales/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.paymentMethod").value("DEBIT_CARD"))
            .andExpect(jsonPath("$.notes").value("Updated notes"));

        verify(updateSaleUseCase).update(any(SaleId.class), any(UpdateSaleCommand.class));
    }

    @Test
    void shouldCompleteSale() throws Exception {
        // Given
        Sale completedSale = new Sale(
            testSale.getId(),
            testSale.getSaleNumber(),
            testSale.getCustomerId(),
            testSale.getEmployeeId(),
            testSale.getSaleDate(),
            testSale.getTotalAmount(),
            testSale.getPaymentMethod(),
            SaleStatus.COMPLETED,
            testSale.getNotes(),
            testSale.getCreatedAt(),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        SaleResponse completedResponse = new SaleResponse(
            1L,
            "SALE-2024-001",
            1L,
            1L,
            LocalDateTime.of(2024, 1, 15, 0, 0),
            new BigDecimal("150.00"),
            PaymentMethod.CASH,
            SaleStatus.COMPLETED,
            "Test sale",
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        when(completeSaleUseCase.complete(any(SaleId.class))).thenReturn(completedSale);
        when(responseMapper.toResponse(completedSale)).thenReturn(completedResponse);

        // When & Then
        mockMvc.perform(post("/api/v2/sales/1/complete"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(completeSaleUseCase).complete(any(SaleId.class));
    }

    @Test
    void shouldReturnBadRequestWhenCompletingInvalidSale() throws Exception {
        // Given
        when(completeSaleUseCase.complete(any(SaleId.class)))
            .thenThrow(new IllegalStateException("Cannot complete sale in CANCELLED status"));

        // When & Then
        mockMvc.perform(post("/api/v2/sales/1/complete"))
            .andExpect(status().isBadRequest());

        verify(completeSaleUseCase).complete(any(SaleId.class));
    }

    @Test
    void shouldCancelSale() throws Exception {
        // Given
        Sale cancelledSale = new Sale(
            testSale.getId(),
            testSale.getSaleNumber(),
            testSale.getCustomerId(),
            testSale.getEmployeeId(),
            testSale.getSaleDate(),
            testSale.getTotalAmount(),
            testSale.getPaymentMethod(),
            SaleStatus.CANCELLED,
            testSale.getNotes(),
            testSale.getCreatedAt(),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        SaleResponse cancelledResponse = new SaleResponse(
            1L,
            "SALE-2024-001",
            1L,
            1L,
            LocalDateTime.of(2024, 1, 15, 0, 0),
            new BigDecimal("150.00"),
            PaymentMethod.CASH,
            SaleStatus.CANCELLED,
            "Test sale",
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        when(cancelSaleUseCase.cancel(any(SaleId.class))).thenReturn(cancelledSale);
        when(responseMapper.toResponse(cancelledSale)).thenReturn(cancelledResponse);

        // When & Then
        mockMvc.perform(post("/api/v2/sales/1/cancel"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(cancelSaleUseCase).cancel(any(SaleId.class));
    }

    @Test
    void shouldReturnBadRequestWhenCancellingInvalidSale() throws Exception {
        // Given
        when(cancelSaleUseCase.cancel(any(SaleId.class)))
            .thenThrow(new IllegalStateException("Cannot cancel sale in COMPLETED status"));

        // When & Then
        mockMvc.perform(post("/api/v2/sales/1/cancel"))
            .andExpect(status().isBadRequest());

        verify(cancelSaleUseCase).cancel(any(SaleId.class));
    }

    @Test
    void shouldDeleteSale() throws Exception {
        // Given
        doNothing().when(deleteSaleUseCase).delete(any(SaleId.class));

        // When & Then
        mockMvc.perform(delete("/api/v2/sales/1"))
            .andExpect(status().isNoContent());

        verify(deleteSaleUseCase).delete(any(SaleId.class));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentSale() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("Sale not found with id: 999"))
            .when(deleteSaleUseCase).delete(any(SaleId.class));

        // When & Then
        mockMvc.perform(delete("/api/v2/sales/999"))
            .andExpect(status().isNotFound()); // 404 for "not found" messages

        verify(deleteSaleUseCase).delete(any(SaleId.class));
    }

    @Test
    void shouldCreateSaleWithMinimalData() throws Exception {
        // Given - only required fields
        SaleRequest request = new SaleRequest(
            "SALE-2024-MIN",
            1L,
            1L,
            LocalDateTime.of(2024, 1, 15, 0, 0),
            PaymentMethod.CASH,
            null
        );

        Sale minimalSale = new Sale(
            new SaleId(1L),
            new SaleNumber("SALE-2024-MIN"),
            1L,
            1L,
            LocalDateTime.of(2024, 1, 15, 0, 0),
            BigDecimal.ZERO,
            PaymentMethod.CASH,
            SaleStatus.PENDING,
            null,
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 15, 10, 0)
        );

        SaleResponse minimalResponse = new SaleResponse(
            1L,
            "SALE-2024-MIN",
            1L,
            1L,
            LocalDateTime.of(2024, 1, 15, 0, 0),
            BigDecimal.ZERO,
            PaymentMethod.CASH,
            SaleStatus.PENDING,
            null,
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 15, 10, 0)
        );

        when(createSaleUseCase.create(any(CreateSaleCommand.class))).thenReturn(minimalSale);
        when(responseMapper.toResponse(minimalSale)).thenReturn(minimalResponse);

        // When & Then
        mockMvc.perform(post("/api/v2/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.saleNumber").value("SALE-2024-MIN"))
            .andExpect(jsonPath("$.notes").isEmpty());

        verify(createSaleUseCase).create(any(CreateSaleCommand.class));
    }

    @Test
    void shouldValidateSaleNumberMaxLength() throws Exception {
        // Given - sale number exceeds max length
        String longSaleNumber = "A".repeat(51);
        SaleRequest request = new SaleRequest(
            longSaleNumber,
            1L,
            1L,
            LocalDateTime.of(2024, 1, 15, 0, 0),
            PaymentMethod.CASH,
            "Test sale"
        );

        // When & Then
        mockMvc.perform(post("/api/v2/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(createSaleUseCase, never()).create(any());
    }

    @Test
    void shouldValidateNotesMaxLength() throws Exception {
        // Given - notes exceed max length
        String longNotes = "A".repeat(1001);
        SaleRequest request = new SaleRequest(
            "SALE-2024-001",
            1L,
            1L,
            LocalDateTime.of(2024, 1, 15, 0, 0),
            PaymentMethod.CASH,
            longNotes
        );

        // When & Then
        mockMvc.perform(post("/api/v2/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(createSaleUseCase, never()).create(any());
    }

    @Test
    void shouldHandleInvalidStatusInQuery() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v2/sales/status/INVALID_STATUS"))
            .andExpect(status().isBadRequest());

        verify(getSaleUseCase, never()).getByStatus(any());
    }

    @Test
    void shouldGetSalesByAllValidStatuses() throws Exception {
        // Test all valid status values
        for (SaleStatus status : SaleStatus.values()) {
            when(getSaleUseCase.getByStatus(status)).thenReturn(List.of());

            mockMvc.perform(get("/api/v2/sales/status/" + status.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

            verify(getSaleUseCase).getByStatus(status);
        }
    }

    @Test
    void shouldHandleDifferentPaymentMethods() throws Exception {
        // Given
        String[] paymentMethods = {"CASH", "CREDIT_CARD", "DEBIT_CARD", "BANK_TRANSFER"};
        
        for (String paymentMethod : paymentMethods) {
            SaleRequest request = new SaleRequest(
                "SALE-PM-" + paymentMethod,
                1L,
                1L,
                LocalDateTime.of(2024, 1, 15, 0, 0),
                PaymentMethod.valueOf(paymentMethod),
                "Payment method test"
            );

            Sale sale = new Sale(
                new SaleId(1L),
                new SaleNumber("SALE-PM-" + paymentMethod),
                1L,
                1L,
                LocalDateTime.of(2024, 1, 15, 0, 0),
                BigDecimal.ZERO,
                PaymentMethod.valueOf(paymentMethod),
                SaleStatus.PENDING,
                "Payment method test",
                LocalDateTime.of(2024, 1, 15, 10, 0),
                LocalDateTime.of(2024, 1, 15, 10, 0)
            );

            SaleResponse response = new SaleResponse(
                1L,
                "SALE-PM-" + paymentMethod,
                1L,
                1L,
                LocalDateTime.of(2024, 1, 15, 0, 0),
                BigDecimal.ZERO,
                PaymentMethod.valueOf(paymentMethod),
                SaleStatus.PENDING,
                "Payment method test",
                LocalDateTime.of(2024, 1, 15, 10, 0),
                LocalDateTime.of(2024, 1, 15, 10, 0)
            );

            when(createSaleUseCase.create(any(CreateSaleCommand.class))).thenReturn(sale);
            when(responseMapper.toResponse(sale)).thenReturn(response);

            // When & Then
            mockMvc.perform(post("/api/v2/sales")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentMethod").value(paymentMethod));
        }
    }

    @Test
    void shouldReturnEmptyListWhenNoSales() throws Exception {
        // Given
        when(getSaleUseCase.getAll()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v2/sales"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));

        verify(getSaleUseCase).getAll();
    }

    @Test
    void shouldUpdateSaleWithNullNotes() throws Exception {
        // Given
        UpdateSaleRequest request = new UpdateSaleRequest(
            PaymentMethod.CREDIT_CARD,
            null
        );

        Sale updatedSale = new Sale(
            testSale.getId(),
            testSale.getSaleNumber(),
            testSale.getCustomerId(),
            testSale.getEmployeeId(),
            testSale.getSaleDate(),
            testSale.getTotalAmount(),
            PaymentMethod.CREDIT_CARD,
            testSale.getStatus(),
            null,
            testSale.getCreatedAt(),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        SaleResponse updatedResponse = new SaleResponse(
            1L,
            "SALE-2024-001",
            1L,
            1L,
            LocalDateTime.of(2024, 1, 15, 0, 0),
            new BigDecimal("150.00"),
            PaymentMethod.CREDIT_CARD,
            SaleStatus.PENDING,
            null,
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        when(updateSaleUseCase.update(any(SaleId.class), any(UpdateSaleCommand.class)))
            .thenReturn(updatedSale);
        when(responseMapper.toResponse(updatedSale)).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v2/sales/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paymentMethod").value("CREDIT_CARD"))
            .andExpect(jsonPath("$.notes").isEmpty());

        verify(updateSaleUseCase).update(any(SaleId.class), any(UpdateSaleCommand.class));
    }
}
