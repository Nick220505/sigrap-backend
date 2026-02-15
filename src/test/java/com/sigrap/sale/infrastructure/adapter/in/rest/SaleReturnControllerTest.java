package com.sigrap.sale.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.sale.application.port.in.*;
import com.sigrap.sale.application.port.in.command.CreateSaleReturnCommand;
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
 * Integration tests for SaleReturnController.
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
class SaleReturnControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CreateSaleReturnUseCase createSaleReturnUseCase;
    private GetSaleReturnUseCase getSaleReturnUseCase;
    private ApproveSaleReturnUseCase approveSaleReturnUseCase;
    private RejectSaleReturnUseCase rejectSaleReturnUseCase;
    private CompleteSaleReturnUseCase completeSaleReturnUseCase;
    private SaleReturnResponseMapper responseMapper;

    private SaleReturn testSaleReturn;
    private SaleReturnResponse testResponse;

    @BeforeEach
    void setUp() {
        createSaleReturnUseCase = mock(CreateSaleReturnUseCase.class);
        getSaleReturnUseCase = mock(GetSaleReturnUseCase.class);
        approveSaleReturnUseCase = mock(ApproveSaleReturnUseCase.class);
        rejectSaleReturnUseCase = mock(RejectSaleReturnUseCase.class);
        completeSaleReturnUseCase = mock(CompleteSaleReturnUseCase.class);
        responseMapper = mock(SaleReturnResponseMapper.class);
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        SaleReturnController controller = new SaleReturnController(
            createSaleReturnUseCase,
            getSaleReturnUseCase,
            approveSaleReturnUseCase,
            rejectSaleReturnUseCase,
            completeSaleReturnUseCase,
            responseMapper
        );

        mockMvc = standaloneSetup(controller).build();

        testSaleReturn = new SaleReturn(
            new SaleReturnId(1L),
            new SaleReturnNumber("RET-2024-001"),
            new SaleId(1L),
            LocalDate.of(2024, 1, 15),
            "Defective product",
            SaleReturnStatus.PENDING,
            new BigDecimal("50.00"),
            "Test return",
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 15, 10, 0)
        );

        testResponse = new SaleReturnResponse(
            1L,
            "RET-2024-001",
            1L,
            LocalDate.of(2024, 1, 15),
            "Defective product",
            "PENDING",
            new BigDecimal("50.00"),
            "Test return",
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 15, 10, 0)
        );
    }

    @Test
    void shouldCreateSaleReturn() throws Exception {
        // Given
        SaleReturnRequest request = new SaleReturnRequest(
            "RET-2024-001",
            1L,
            LocalDate.of(2024, 1, 15),
            "Defective product",
            new BigDecimal("50.00"),
            "Test return"
        );

        when(createSaleReturnUseCase.create(any(CreateSaleReturnCommand.class)))
            .thenReturn(testSaleReturn);
        when(responseMapper.toResponse(testSaleReturn)).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(post("/api/v2/sale-returns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.returnNumber").value("RET-2024-001"))
            .andExpect(jsonPath("$.saleId").value(1))
            .andExpect(jsonPath("$.reason").value("Defective product"))
            .andExpect(jsonPath("$.refundAmount").value(50.00))
            .andExpect(jsonPath("$.status").value("PENDING"));

        verify(createSaleReturnUseCase).create(any(CreateSaleReturnCommand.class));
        verify(responseMapper).toResponse(testSaleReturn);
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithInvalidData() throws Exception {
        // Given - missing required return number
        SaleReturnRequest request = new SaleReturnRequest(
            "",
            1L,
            LocalDate.of(2024, 1, 15),
            "Defective product",
            new BigDecimal("50.00"),
            "Test return"
        );

        // When & Then
        mockMvc.perform(post("/api/v2/sale-returns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(createSaleReturnUseCase, never()).create(any());
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithNegativeRefundAmount() throws Exception {
        // Given - negative refund amount
        SaleReturnRequest request = new SaleReturnRequest(
            "RET-2024-001",
            1L,
            LocalDate.of(2024, 1, 15),
            "Defective product",
            new BigDecimal("-50.00"),
            "Test return"
        );

        // When & Then
        mockMvc.perform(post("/api/v2/sale-returns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(createSaleReturnUseCase, never()).create(any());
    }

    @Test
    void shouldGetSaleReturnById() throws Exception {
        // Given
        when(getSaleReturnUseCase.getById(any(SaleReturnId.class))).thenReturn(testSaleReturn);
        when(responseMapper.toResponse(testSaleReturn)).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/v2/sale-returns/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.returnNumber").value("RET-2024-001"))
            .andExpect(jsonPath("$.status").value("PENDING"));

        verify(getSaleReturnUseCase).getById(any(SaleReturnId.class));
        verify(responseMapper).toResponse(testSaleReturn);
    }

    @Test
    void shouldReturnNotFoundWhenSaleReturnDoesNotExist() throws Exception {
        // Given
        when(getSaleReturnUseCase.getById(any(SaleReturnId.class)))
            .thenThrow(new IllegalArgumentException("Sale return not found with id: 999"));

        // When & Then
        mockMvc.perform(get("/api/v2/sale-returns/999"))
            .andExpect(status().isBadRequest());

        verify(getSaleReturnUseCase).getById(any(SaleReturnId.class));
    }

    @Test
    void shouldGetAllSaleReturns() throws Exception {
        // Given
        SaleReturn return2 = new SaleReturn(
            new SaleReturnId(2L),
            new SaleReturnNumber("RET-2024-002"),
            new SaleId(2L),
            LocalDate.of(2024, 1, 20),
            "Wrong item",
            SaleReturnStatus.APPROVED,
            new BigDecimal("75.00"),
            "Second return",
            LocalDateTime.of(2024, 1, 20, 10, 0),
            LocalDateTime.of(2024, 1, 20, 10, 0)
        );

        SaleReturnResponse response2 = new SaleReturnResponse(
            2L,
            "RET-2024-002",
            2L,
            LocalDate.of(2024, 1, 20),
            "Wrong item",
            "APPROVED",
            new BigDecimal("75.00"),
            "Second return",
            LocalDateTime.of(2024, 1, 20, 10, 0),
            LocalDateTime.of(2024, 1, 20, 10, 0)
        );

        when(getSaleReturnUseCase.getAll()).thenReturn(List.of(testSaleReturn, return2));
        when(responseMapper.toResponse(testSaleReturn)).thenReturn(testResponse);
        when(responseMapper.toResponse(return2)).thenReturn(response2);

        // When & Then
        mockMvc.perform(get("/api/v2/sale-returns"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].status").value("PENDING"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].status").value("APPROVED"));

        verify(getSaleReturnUseCase).getAll();
    }

    @Test
    void shouldGetSaleReturnsBySaleId() throws Exception {
        // Given
        when(getSaleReturnUseCase.getBySaleId(any(SaleId.class)))
            .thenReturn(List.of(testSaleReturn));
        when(responseMapper.toResponse(testSaleReturn)).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/v2/sale-returns/sale/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].saleId").value(1));

        verify(getSaleReturnUseCase).getBySaleId(any(SaleId.class));
    }

    @Test
    void shouldReturnEmptyListWhenNoReturnsForSale() throws Exception {
        // Given
        when(getSaleReturnUseCase.getBySaleId(any(SaleId.class)))
            .thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v2/sale-returns/sale/999"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));

        verify(getSaleReturnUseCase).getBySaleId(any(SaleId.class));
    }

    @Test
    void shouldGetSaleReturnsByStatus() throws Exception {
        // Given
        when(getSaleReturnUseCase.getByStatus(SaleReturnStatus.PENDING))
            .thenReturn(List.of(testSaleReturn));
        when(responseMapper.toResponse(testSaleReturn)).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/v2/sale-returns/status/PENDING"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(getSaleReturnUseCase).getByStatus(SaleReturnStatus.PENDING);
    }

    @Test
    void shouldReturnEmptyListWhenNoReturnsWithStatus() throws Exception {
        // Given
        when(getSaleReturnUseCase.getByStatus(SaleReturnStatus.COMPLETED))
            .thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v2/sale-returns/status/COMPLETED"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));

        verify(getSaleReturnUseCase).getByStatus(SaleReturnStatus.COMPLETED);
    }

    @Test
    void shouldApproveSaleReturn() throws Exception {
        // Given
        SaleReturn approvedReturn = new SaleReturn(
            testSaleReturn.getId(),
            testSaleReturn.getReturnNumber(),
            testSaleReturn.getSaleId(),
            testSaleReturn.getReturnDate(),
            testSaleReturn.getReason(),
            SaleReturnStatus.APPROVED,
            testSaleReturn.getRefundAmount(),
            testSaleReturn.getNotes(),
            testSaleReturn.getCreatedAt(),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        SaleReturnResponse approvedResponse = new SaleReturnResponse(
            1L,
            "RET-2024-001",
            1L,
            LocalDate.of(2024, 1, 15),
            "Defective product",
            "APPROVED",
            new BigDecimal("50.00"),
            "Test return",
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        when(approveSaleReturnUseCase.approve(any(SaleReturnId.class))).thenReturn(approvedReturn);
        when(responseMapper.toResponse(approvedReturn)).thenReturn(approvedResponse);

        // When & Then
        mockMvc.perform(post("/api/v2/sale-returns/1/approve"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(approveSaleReturnUseCase).approve(any(SaleReturnId.class));
    }

    @Test
    void shouldReturnBadRequestWhenApprovingInvalidReturn() throws Exception {
        // Given
        when(approveSaleReturnUseCase.approve(any(SaleReturnId.class)))
            .thenThrow(new IllegalStateException("Cannot approve sale return in COMPLETED status"));

        // When & Then
        mockMvc.perform(post("/api/v2/sale-returns/1/approve"))
            .andExpect(status().isBadRequest());

        verify(approveSaleReturnUseCase).approve(any(SaleReturnId.class));
    }

    @Test
    void shouldRejectSaleReturn() throws Exception {
        // Given
        SaleReturn rejectedReturn = new SaleReturn(
            testSaleReturn.getId(),
            testSaleReturn.getReturnNumber(),
            testSaleReturn.getSaleId(),
            testSaleReturn.getReturnDate(),
            testSaleReturn.getReason(),
            SaleReturnStatus.REJECTED,
            testSaleReturn.getRefundAmount(),
            testSaleReturn.getNotes(),
            testSaleReturn.getCreatedAt(),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        SaleReturnResponse rejectedResponse = new SaleReturnResponse(
            1L,
            "RET-2024-001",
            1L,
            LocalDate.of(2024, 1, 15),
            "Defective product",
            "REJECTED",
            new BigDecimal("50.00"),
            "Test return",
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        when(rejectSaleReturnUseCase.reject(any(SaleReturnId.class))).thenReturn(rejectedReturn);
        when(responseMapper.toResponse(rejectedReturn)).thenReturn(rejectedResponse);

        // When & Then
        mockMvc.perform(post("/api/v2/sale-returns/1/reject"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("REJECTED"));

        verify(rejectSaleReturnUseCase).reject(any(SaleReturnId.class));
    }

    @Test
    void shouldReturnBadRequestWhenRejectingInvalidReturn() throws Exception {
        // Given
        when(rejectSaleReturnUseCase.reject(any(SaleReturnId.class)))
            .thenThrow(new IllegalStateException("Cannot reject sale return in COMPLETED status"));

        // When & Then
        mockMvc.perform(post("/api/v2/sale-returns/1/reject"))
            .andExpect(status().isBadRequest());

        verify(rejectSaleReturnUseCase).reject(any(SaleReturnId.class));
    }

    @Test
    void shouldCompleteSaleReturn() throws Exception {
        // Given
        SaleReturn completedReturn = new SaleReturn(
            testSaleReturn.getId(),
            testSaleReturn.getReturnNumber(),
            testSaleReturn.getSaleId(),
            testSaleReturn.getReturnDate(),
            testSaleReturn.getReason(),
            SaleReturnStatus.COMPLETED,
            testSaleReturn.getRefundAmount(),
            testSaleReturn.getNotes(),
            testSaleReturn.getCreatedAt(),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        SaleReturnResponse completedResponse = new SaleReturnResponse(
            1L,
            "RET-2024-001",
            1L,
            LocalDate.of(2024, 1, 15),
            "Defective product",
            "COMPLETED",
            new BigDecimal("50.00"),
            "Test return",
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 16, 10, 0)
        );

        when(completeSaleReturnUseCase.complete(any(SaleReturnId.class))).thenReturn(completedReturn);
        when(responseMapper.toResponse(completedReturn)).thenReturn(completedResponse);

        // When & Then
        mockMvc.perform(post("/api/v2/sale-returns/1/complete"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(completeSaleReturnUseCase).complete(any(SaleReturnId.class));
    }

    @Test
    void shouldReturnBadRequestWhenCompletingInvalidReturn() throws Exception {
        // Given
        when(completeSaleReturnUseCase.complete(any(SaleReturnId.class)))
            .thenThrow(new IllegalStateException("Cannot complete sale return in PENDING status"));

        // When & Then
        mockMvc.perform(post("/api/v2/sale-returns/1/complete"))
            .andExpect(status().isBadRequest());

        verify(completeSaleReturnUseCase).complete(any(SaleReturnId.class));
    }

    @Test
    void shouldCreateSaleReturnWithMinimalData() throws Exception {
        // Given - only required fields
        SaleReturnRequest request = new SaleReturnRequest(
            "RET-2024-MIN",
            1L,
            LocalDate.of(2024, 1, 15),
            "Reason",
            new BigDecimal("50.00"),
            null
        );

        SaleReturn minimalReturn = new SaleReturn(
            new SaleReturnId(1L),
            new SaleReturnNumber("RET-2024-MIN"),
            new SaleId(1L),
            LocalDate.of(2024, 1, 15),
            "Reason",
            SaleReturnStatus.PENDING,
            new BigDecimal("50.00"),
            null,
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 15, 10, 0)
        );

        SaleReturnResponse minimalResponse = new SaleReturnResponse(
            1L,
            "RET-2024-MIN",
            1L,
            LocalDate.of(2024, 1, 15),
            "Reason",
            "PENDING",
            new BigDecimal("50.00"),
            null,
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 15, 10, 0)
        );

        when(createSaleReturnUseCase.create(any(CreateSaleReturnCommand.class))).thenReturn(minimalReturn);
        when(responseMapper.toResponse(minimalReturn)).thenReturn(minimalResponse);

        // When & Then
        mockMvc.perform(post("/api/v2/sale-returns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.returnNumber").value("RET-2024-MIN"))
            .andExpect(jsonPath("$.notes").isEmpty());

        verify(createSaleReturnUseCase).create(any(CreateSaleReturnCommand.class));
    }

    @Test
    void shouldValidateReturnNumberMaxLength() throws Exception {
        // Given - return number exceeds max length
        String longReturnNumber = "A".repeat(51);
        SaleReturnRequest request = new SaleReturnRequest(
            longReturnNumber,
            1L,
            LocalDate.of(2024, 1, 15),
            "Reason",
            new BigDecimal("50.00"),
            "Test return"
        );

        // When & Then
        mockMvc.perform(post("/api/v2/sale-returns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(createSaleReturnUseCase, never()).create(any());
    }

    @Test
    void shouldValidateReasonMaxLength() throws Exception {
        // Given - reason exceeds max length
        String longReason = "A".repeat(501);
        SaleReturnRequest request = new SaleReturnRequest(
            "RET-2024-001",
            1L,
            LocalDate.of(2024, 1, 15),
            longReason,
            new BigDecimal("50.00"),
            "Test return"
        );

        // When & Then
        mockMvc.perform(post("/api/v2/sale-returns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(createSaleReturnUseCase, never()).create(any());
    }

    @Test
    void shouldValidateNotesMaxLength() throws Exception {
        // Given - notes exceed max length
        String longNotes = "A".repeat(1001);
        SaleReturnRequest request = new SaleReturnRequest(
            "RET-2024-001",
            1L,
            LocalDate.of(2024, 1, 15),
            "Reason",
            new BigDecimal("50.00"),
            longNotes
        );

        // When & Then
        mockMvc.perform(post("/api/v2/sale-returns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(createSaleReturnUseCase, never()).create(any());
    }

    @Test
    void shouldHandleInvalidStatusInQuery() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v2/sale-returns/status/INVALID_STATUS"))
            .andExpect(status().isBadRequest());

        verify(getSaleReturnUseCase, never()).getByStatus(any());
    }

    @Test
    void shouldGetReturnsByAllValidStatuses() throws Exception {
        // Test all valid status values
        for (SaleReturnStatus status : SaleReturnStatus.values()) {
            when(getSaleReturnUseCase.getByStatus(status)).thenReturn(List.of());

            mockMvc.perform(get("/api/v2/sale-returns/status/" + status.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

            verify(getSaleReturnUseCase).getByStatus(status);
        }
    }

    @Test
    void shouldReturnEmptyListWhenNoSaleReturns() throws Exception {
        // Given
        when(getSaleReturnUseCase.getAll()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v2/sale-returns"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));

        verify(getSaleReturnUseCase).getAll();
    }

    @Test
    void shouldHandleZeroRefundAmount() throws Exception {
        // Given
        SaleReturnRequest request = new SaleReturnRequest(
            "RET-2024-ZERO",
            1L,
            LocalDate.of(2024, 1, 15),
            "Exchange only",
            BigDecimal.ZERO,
            "No refund"
        );

        SaleReturn zeroRefundReturn = new SaleReturn(
            new SaleReturnId(1L),
            new SaleReturnNumber("RET-2024-ZERO"),
            new SaleId(1L),
            LocalDate.of(2024, 1, 15),
            "Exchange only",
            SaleReturnStatus.PENDING,
            BigDecimal.ZERO,
            "No refund",
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 15, 10, 0)
        );

        SaleReturnResponse zeroRefundResponse = new SaleReturnResponse(
            1L,
            "RET-2024-ZERO",
            1L,
            LocalDate.of(2024, 1, 15),
            "Exchange only",
            "PENDING",
            BigDecimal.ZERO,
            "No refund",
            LocalDateTime.of(2024, 1, 15, 10, 0),
            LocalDateTime.of(2024, 1, 15, 10, 0)
        );

        when(createSaleReturnUseCase.create(any(CreateSaleReturnCommand.class))).thenReturn(zeroRefundReturn);
        when(responseMapper.toResponse(zeroRefundReturn)).thenReturn(zeroRefundResponse);

        // When & Then
        mockMvc.perform(post("/api/v2/sale-returns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.refundAmount").value(0.00));

        verify(createSaleReturnUseCase).create(any(CreateSaleReturnCommand.class));
    }

    @Test
    void shouldHandleDifferentReturnReasons() throws Exception {
        // Given
        String[] reasons = {
            "Defective product",
            "Wrong item received",
            "Changed mind",
            "Product damaged",
            "Not as described"
        };
        
        for (String reason : reasons) {
            SaleReturnRequest request = new SaleReturnRequest(
                "RET-REASON-" + reason.hashCode(),
                1L,
                LocalDate.of(2024, 1, 15),
                reason,
                new BigDecimal("50.00"),
                "Return reason test"
            );

            SaleReturn saleReturn = new SaleReturn(
                new SaleReturnId(1L),
                new SaleReturnNumber("RET-REASON-" + reason.hashCode()),
                new SaleId(1L),
                LocalDate.of(2024, 1, 15),
                reason,
                SaleReturnStatus.PENDING,
                new BigDecimal("50.00"),
                "Return reason test",
                LocalDateTime.of(2024, 1, 15, 10, 0),
                LocalDateTime.of(2024, 1, 15, 10, 0)
            );

            SaleReturnResponse response = new SaleReturnResponse(
                1L,
                "RET-REASON-" + reason.hashCode(),
                1L,
                LocalDate.of(2024, 1, 15),
                reason,
                "PENDING",
                new BigDecimal("50.00"),
                "Return reason test",
                LocalDateTime.of(2024, 1, 15, 10, 0),
                LocalDateTime.of(2024, 1, 15, 10, 0)
            );

            when(createSaleReturnUseCase.create(any(CreateSaleReturnCommand.class))).thenReturn(saleReturn);
            when(responseMapper.toResponse(saleReturn)).thenReturn(response);

            // When & Then
            mockMvc.perform(post("/api/v2/sale-returns")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reason").value(reason));
        }
    }
}
