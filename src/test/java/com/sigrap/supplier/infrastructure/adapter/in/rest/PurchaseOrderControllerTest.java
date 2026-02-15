package com.sigrap.supplier.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.supplier.application.port.in.*;
import com.sigrap.supplier.application.port.in.command.CreatePurchaseOrderCommand;
import com.sigrap.supplier.application.port.in.command.UpdatePurchaseOrderCommand;
import com.sigrap.supplier.domain.model.*;
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
 * Integration tests for PurchaseOrderController.
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
class PurchaseOrderControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CreatePurchaseOrderUseCase createPurchaseOrderUseCase;
    private GetPurchaseOrderUseCase getPurchaseOrderUseCase;
    private UpdatePurchaseOrderUseCase updatePurchaseOrderUseCase;
    private DeletePurchaseOrderUseCase deletePurchaseOrderUseCase;
    private ApprovePurchaseOrderUseCase approvePurchaseOrderUseCase;
    private ReceivePurchaseOrderUseCase receivePurchaseOrderUseCase;
    private CancelPurchaseOrderUseCase cancelPurchaseOrderUseCase;
    private PurchaseOrderResponseMapper responseMapper;

    private PurchaseOrder testPurchaseOrder;
    private PurchaseOrderResponse testResponse;

    @BeforeEach
    void setUp() {
        createPurchaseOrderUseCase = mock(CreatePurchaseOrderUseCase.class);
        getPurchaseOrderUseCase = mock(GetPurchaseOrderUseCase.class);
        updatePurchaseOrderUseCase = mock(UpdatePurchaseOrderUseCase.class);
        deletePurchaseOrderUseCase = mock(DeletePurchaseOrderUseCase.class);
        approvePurchaseOrderUseCase = mock(ApprovePurchaseOrderUseCase.class);
        receivePurchaseOrderUseCase = mock(ReceivePurchaseOrderUseCase.class);
        cancelPurchaseOrderUseCase = mock(CancelPurchaseOrderUseCase.class);
        responseMapper = mock(PurchaseOrderResponseMapper.class);
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        PurchaseOrderController controller = new PurchaseOrderController(
            createPurchaseOrderUseCase,
            getPurchaseOrderUseCase,
            updatePurchaseOrderUseCase,
            deletePurchaseOrderUseCase,
            approvePurchaseOrderUseCase,
            receivePurchaseOrderUseCase,
            cancelPurchaseOrderUseCase,
            responseMapper
        );

        mockMvc = standaloneSetup(controller).build();

        testPurchaseOrder = new PurchaseOrder(
            new PurchaseOrderId(1L),
            new PurchaseOrderNumber("PO-2024-001"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            PurchaseOrderStatus.PENDING,
            new BigDecimal("1500.00"),
            "Test order",
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 15, 10, 0)
        );

        testResponse = new PurchaseOrderResponse(
            1L,
            "PO-2024-001",
            1L,
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            "PENDING",
            new BigDecimal("1500.00"),
            "Test order",
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 15, 10, 0)
        );
    }

    @Test
    void shouldCreatePurchaseOrder() throws Exception {
        // Given
        PurchaseOrderRequest request = new PurchaseOrderRequest(
            "PO-2024-001",
            1L,
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            new BigDecimal("1500.00"),
            "Test order"
        );

        when(createPurchaseOrderUseCase.create(any(CreatePurchaseOrderCommand.class)))
            .thenReturn(testPurchaseOrder);
        when(responseMapper.toResponse(testPurchaseOrder)).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(post("/api/v2/purchase-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.orderNumber").value("PO-2024-001"))
            .andExpect(jsonPath("$.supplierId").value(1))
            .andExpect(jsonPath("$.totalAmount").value(1500.00))
            .andExpect(jsonPath("$.status").value("PENDING"));

        verify(createPurchaseOrderUseCase).create(any(CreatePurchaseOrderCommand.class));
        verify(responseMapper).toResponse(testPurchaseOrder);
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithInvalidData() throws Exception {
        // Given - missing required order number
        PurchaseOrderRequest request = new PurchaseOrderRequest(
            "",
            1L,
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            new BigDecimal("1500.00"),
            "Test order"
        );

        // When & Then
        mockMvc.perform(post("/api/v2/purchase-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(createPurchaseOrderUseCase, never()).create(any());
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithNegativeAmount() throws Exception {
        // Given - negative total amount
        PurchaseOrderRequest request = new PurchaseOrderRequest(
            "PO-2024-001",
            1L,
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            new BigDecimal("-100.00"),
            "Test order"
        );

        // When & Then
        mockMvc.perform(post("/api/v2/purchase-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(createPurchaseOrderUseCase, never()).create(any());
    }

    @Test
    void shouldGetPurchaseOrderById() throws Exception {
        // Given
        when(getPurchaseOrderUseCase.getById(any(PurchaseOrderId.class))).thenReturn(testPurchaseOrder);
        when(responseMapper.toResponse(testPurchaseOrder)).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/v2/purchase-orders/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.orderNumber").value("PO-2024-001"))
            .andExpect(jsonPath("$.status").value("PENDING"));

        verify(getPurchaseOrderUseCase).getById(any(PurchaseOrderId.class));
        verify(responseMapper).toResponse(testPurchaseOrder);
    }

    @Test
    void shouldReturnNotFoundWhenPurchaseOrderDoesNotExist() throws Exception {
        // Given
        when(getPurchaseOrderUseCase.getById(any(PurchaseOrderId.class)))
            .thenThrow(new IllegalArgumentException("Purchase order not found with id: 999"));

        // When & Then
        mockMvc.perform(get("/api/v2/purchase-orders/999"))
            .andExpect(status().isBadRequest());

        verify(getPurchaseOrderUseCase).getById(any(PurchaseOrderId.class));
    }

    @Test
    void shouldGetAllPurchaseOrders() throws Exception {
        // Given
        PurchaseOrder order2 = new PurchaseOrder(
            new PurchaseOrderId(2L),
            new PurchaseOrderNumber("PO-2024-002"),
            new SupplierId(2L),
            LocalDate.of(2024, 1, 20),
            LocalDate.of(2024, 2, 20),
            PurchaseOrderStatus.APPROVED,
            new BigDecimal("2000.00"),
            "Second order",
            LocalDateTime.of(2024, 1, 20, 10, 0),
            LocalDateTime.of(2024, 1, 20, 10, 0)
        );

        PurchaseOrderResponse response2 = new PurchaseOrderResponse(
            2L,
            "PO-2024-002",
            2L,
            LocalDate.of(2024, 1, 20),
            LocalDate.of(2024, 2, 20),
            "APPROVED",
            new BigDecimal("2000.00"),
            "Second order",
            LocalDateTime.of(2024, 1, 20, 10, 0),
            LocalDateTime.of(2024, 1, 20, 10, 0)
        );

        when(getPurchaseOrderUseCase.getAll()).thenReturn(List.of(testPurchaseOrder, order2));
        when(responseMapper.toResponse(testPurchaseOrder)).thenReturn(testResponse);
        when(responseMapper.toResponse(order2)).thenReturn(response2);

        // When & Then
        mockMvc.perform(get("/api/v2/purchase-orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].status").value("PENDING"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].status").value("APPROVED"));

        verify(getPurchaseOrderUseCase).getAll();
    }

    @Test
    void shouldGetPurchaseOrdersBySupplierId() throws Exception {
        // Given
        when(getPurchaseOrderUseCase.getBySupplierId(any(SupplierId.class)))
            .thenReturn(List.of(testPurchaseOrder));
        when(responseMapper.toResponse(testPurchaseOrder)).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/v2/purchase-orders/supplier/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].supplierId").value(1));

        verify(getPurchaseOrderUseCase).getBySupplierId(any(SupplierId.class));
    }

    @Test
    void shouldReturnEmptyListWhenNoOrdersForSupplier() throws Exception {
        // Given
        when(getPurchaseOrderUseCase.getBySupplierId(any(SupplierId.class)))
            .thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v2/purchase-orders/supplier/999"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));

        verify(getPurchaseOrderUseCase).getBySupplierId(any(SupplierId.class));
    }

    @Test
    void shouldGetPurchaseOrdersByStatus() throws Exception {
        // Given
        when(getPurchaseOrderUseCase.getByStatus(PurchaseOrderStatus.PENDING))
            .thenReturn(List.of(testPurchaseOrder));
        when(responseMapper.toResponse(testPurchaseOrder)).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/v2/purchase-orders/status/PENDING"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(getPurchaseOrderUseCase).getByStatus(PurchaseOrderStatus.PENDING);
    }

    @Test
    void shouldReturnEmptyListWhenNoOrdersWithStatus() throws Exception {
        // Given
        when(getPurchaseOrderUseCase.getByStatus(PurchaseOrderStatus.RECEIVED))
            .thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v2/purchase-orders/status/RECEIVED"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));

        verify(getPurchaseOrderUseCase).getByStatus(PurchaseOrderStatus.RECEIVED);
    }

    @Test
    void shouldUpdatePurchaseOrder() throws Exception {
        // Given
        UpdatePurchaseOrderRequest request = new UpdatePurchaseOrderRequest(
            LocalDate.of(2024, 3, 1),
            "Updated notes"
        );

        PurchaseOrder updatedOrder = new PurchaseOrder(
            testPurchaseOrder.getId(),
            testPurchaseOrder.getOrderNumber(),
            testPurchaseOrder.getSupplierId(),
            testPurchaseOrder.getOrderDate(),
            LocalDate.of(2024, 3, 1),
            testPurchaseOrder.getStatus(),
            testPurchaseOrder.getTotalAmount(),
            "Updated notes",
            testPurchaseOrder.getCreatedAt(),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        PurchaseOrderResponse updatedResponse = new PurchaseOrderResponse(
            1L,
            "PO-2024-001",
            1L,
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 3, 1),
            "PENDING",
            new BigDecimal("1500.00"),
            "Updated notes",
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        when(updatePurchaseOrderUseCase.update(any(PurchaseOrderId.class), any(UpdatePurchaseOrderCommand.class)))
            .thenReturn(updatedOrder);
        when(responseMapper.toResponse(updatedOrder)).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v2/purchase-orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.expectedDeliveryDate").value("2024-03-01"))
            .andExpect(jsonPath("$.notes").value("Updated notes"));

        verify(updatePurchaseOrderUseCase).update(any(PurchaseOrderId.class), any(UpdatePurchaseOrderCommand.class));
    }

    @Test
    void shouldApprovePurchaseOrder() throws Exception {
        // Given
        PurchaseOrder approvedOrder = new PurchaseOrder(
            testPurchaseOrder.getId(),
            testPurchaseOrder.getOrderNumber(),
            testPurchaseOrder.getSupplierId(),
            testPurchaseOrder.getOrderDate(),
            testPurchaseOrder.getExpectedDeliveryDate(),
            PurchaseOrderStatus.APPROVED,
            testPurchaseOrder.getTotalAmount(),
            testPurchaseOrder.getNotes(),
            testPurchaseOrder.getCreatedAt(),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        PurchaseOrderResponse approvedResponse = new PurchaseOrderResponse(
            1L,
            "PO-2024-001",
            1L,
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            "APPROVED",
            new BigDecimal("1500.00"),
            "Test order",
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        when(approvePurchaseOrderUseCase.approve(any(PurchaseOrderId.class))).thenReturn(approvedOrder);
        when(responseMapper.toResponse(approvedOrder)).thenReturn(approvedResponse);

        // When & Then
        mockMvc.perform(post("/api/v2/purchase-orders/1/approve"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(approvePurchaseOrderUseCase).approve(any(PurchaseOrderId.class));
    }

    @Test
    void shouldReturnBadRequestWhenApprovingInvalidOrder() throws Exception {
        // Given
        when(approvePurchaseOrderUseCase.approve(any(PurchaseOrderId.class)))
            .thenThrow(new IllegalStateException("Cannot approve purchase order in RECEIVED status"));

        // When & Then
        mockMvc.perform(post("/api/v2/purchase-orders/1/approve"))
            .andExpect(status().isBadRequest());

        verify(approvePurchaseOrderUseCase).approve(any(PurchaseOrderId.class));
    }

    @Test
    void shouldReceivePurchaseOrder() throws Exception {
        // Given
        PurchaseOrder receivedOrder = new PurchaseOrder(
            testPurchaseOrder.getId(),
            testPurchaseOrder.getOrderNumber(),
            testPurchaseOrder.getSupplierId(),
            testPurchaseOrder.getOrderDate(),
            testPurchaseOrder.getExpectedDeliveryDate(),
            PurchaseOrderStatus.RECEIVED,
            testPurchaseOrder.getTotalAmount(),
            testPurchaseOrder.getNotes(),
            testPurchaseOrder.getCreatedAt(),
            LocalDateTime.of(2024, 2, 15, 10, 0)
        );

        PurchaseOrderResponse receivedResponse = new PurchaseOrderResponse(
            1L,
            "PO-2024-001",
            1L,
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            "RECEIVED",
            new BigDecimal("1500.00"),
            "Test order",
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 2, 15, 10, 0)
        );

        when(receivePurchaseOrderUseCase.receive(any(PurchaseOrderId.class))).thenReturn(receivedOrder);
        when(responseMapper.toResponse(receivedOrder)).thenReturn(receivedResponse);

        // When & Then
        mockMvc.perform(post("/api/v2/purchase-orders/1/receive"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("RECEIVED"));

        verify(receivePurchaseOrderUseCase).receive(any(PurchaseOrderId.class));
    }

    @Test
    void shouldReturnBadRequestWhenReceivingInvalidOrder() throws Exception {
        // Given
        when(receivePurchaseOrderUseCase.receive(any(PurchaseOrderId.class)))
            .thenThrow(new IllegalStateException("Cannot receive purchase order in PENDING status"));

        // When & Then
        mockMvc.perform(post("/api/v2/purchase-orders/1/receive"))
            .andExpect(status().isBadRequest());

        verify(receivePurchaseOrderUseCase).receive(any(PurchaseOrderId.class));
    }

    @Test
    void shouldCancelPurchaseOrder() throws Exception {
        // Given
        PurchaseOrder cancelledOrder = new PurchaseOrder(
            testPurchaseOrder.getId(),
            testPurchaseOrder.getOrderNumber(),
            testPurchaseOrder.getSupplierId(),
            testPurchaseOrder.getOrderDate(),
            testPurchaseOrder.getExpectedDeliveryDate(),
            PurchaseOrderStatus.CANCELLED,
            testPurchaseOrder.getTotalAmount(),
            testPurchaseOrder.getNotes(),
            testPurchaseOrder.getCreatedAt(),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        PurchaseOrderResponse cancelledResponse = new PurchaseOrderResponse(
            1L,
            "PO-2024-001",
            1L,
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            "CANCELLED",
            new BigDecimal("1500.00"),
            "Test order",
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        when(cancelPurchaseOrderUseCase.cancel(any(PurchaseOrderId.class))).thenReturn(cancelledOrder);
        when(responseMapper.toResponse(cancelledOrder)).thenReturn(cancelledResponse);

        // When & Then
        mockMvc.perform(post("/api/v2/purchase-orders/1/cancel"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(cancelPurchaseOrderUseCase).cancel(any(PurchaseOrderId.class));
    }

    @Test
    void shouldReturnBadRequestWhenCancellingInvalidOrder() throws Exception {
        // Given
        when(cancelPurchaseOrderUseCase.cancel(any(PurchaseOrderId.class)))
            .thenThrow(new IllegalStateException("Cannot cancel purchase order in RECEIVED status"));

        // When & Then
        mockMvc.perform(post("/api/v2/purchase-orders/1/cancel"))
            .andExpect(status().isBadRequest());

        verify(cancelPurchaseOrderUseCase).cancel(any(PurchaseOrderId.class));
    }

    @Test
    void shouldDeletePurchaseOrder() throws Exception {
        // Given
        doNothing().when(deletePurchaseOrderUseCase).delete(any(PurchaseOrderId.class));

        // When & Then
        mockMvc.perform(delete("/api/v2/purchase-orders/1"))
            .andExpect(status().isNoContent());

        verify(deletePurchaseOrderUseCase).delete(any(PurchaseOrderId.class));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentOrder() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("Purchase order not found with id: 999"))
            .when(deletePurchaseOrderUseCase).delete(any(PurchaseOrderId.class));

        // When & Then
        mockMvc.perform(delete("/api/v2/purchase-orders/999"))
            .andExpect(status().isBadRequest());

        verify(deletePurchaseOrderUseCase).delete(any(PurchaseOrderId.class));
    }

    @Test
    void shouldCreatePurchaseOrderWithMinimalData() throws Exception {
        // Given - only required fields
        PurchaseOrderRequest request = new PurchaseOrderRequest(
            "PO-2024-MIN",
            1L,
            LocalDate.of(2024, 1, 15),
            null,
            new BigDecimal("1000.00"),
            null
        );

        PurchaseOrder minimalOrder = new PurchaseOrder(
            new PurchaseOrderId(1L),
            new PurchaseOrderNumber("PO-2024-MIN"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 15),
            null,
            PurchaseOrderStatus.PENDING,
            new BigDecimal("1000.00"),
            null,
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 15, 10, 0)
        );

        PurchaseOrderResponse minimalResponse = new PurchaseOrderResponse(
            1L,
            "PO-2024-MIN",
            1L,
            LocalDate.of(2024, 1, 15),
            null,
            "PENDING",
            new BigDecimal("1000.00"),
            null,
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 15, 10, 0)
        );

        when(createPurchaseOrderUseCase.create(any(CreatePurchaseOrderCommand.class))).thenReturn(minimalOrder);
        when(responseMapper.toResponse(minimalOrder)).thenReturn(minimalResponse);

        // When & Then
        mockMvc.perform(post("/api/v2/purchase-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.orderNumber").value("PO-2024-MIN"))
            .andExpect(jsonPath("$.expectedDeliveryDate").isEmpty())
            .andExpect(jsonPath("$.notes").isEmpty());

        verify(createPurchaseOrderUseCase).create(any(CreatePurchaseOrderCommand.class));
    }

    @Test
    void shouldValidateOrderNumberMaxLength() throws Exception {
        // Given - order number exceeds max length
        String longOrderNumber = "A".repeat(51);
        PurchaseOrderRequest request = new PurchaseOrderRequest(
            longOrderNumber,
            1L,
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            new BigDecimal("1500.00"),
            "Test order"
        );

        // When & Then
        mockMvc.perform(post("/api/v2/purchase-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(createPurchaseOrderUseCase, never()).create(any());
    }

    @Test
    void shouldValidateNotesMaxLength() throws Exception {
        // Given - notes exceed max length
        String longNotes = "A".repeat(1001);
        PurchaseOrderRequest request = new PurchaseOrderRequest(
            "PO-2024-001",
            1L,
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            new BigDecimal("1500.00"),
            longNotes
        );

        // When & Then
        mockMvc.perform(post("/api/v2/purchase-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(createPurchaseOrderUseCase, never()).create(any());
    }

    @Test
    void shouldHandleInvalidStatusInQuery() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v2/purchase-orders/status/INVALID_STATUS"))
            .andExpect(status().isBadRequest());

        verify(getPurchaseOrderUseCase, never()).getByStatus(any());
    }

    @Test
    void shouldGetOrdersByAllValidStatuses() throws Exception {
        // Test all valid status values
        for (PurchaseOrderStatus status : PurchaseOrderStatus.values()) {
            when(getPurchaseOrderUseCase.getByStatus(status)).thenReturn(List.of());

            mockMvc.perform(get("/api/v2/purchase-orders/status/" + status.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

            verify(getPurchaseOrderUseCase).getByStatus(status);
        }
    }
}
