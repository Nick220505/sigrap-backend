package com.sigrap.sale.infrastructure.adapter.in.rest;

import com.sigrap.sale.application.port.in.CancelSaleUseCase;
import com.sigrap.sale.application.port.in.CompleteSaleUseCase;
import com.sigrap.sale.application.port.in.CreateSaleUseCase;
import com.sigrap.sale.application.port.in.DeleteSaleUseCase;
import com.sigrap.sale.application.port.in.GetSaleUseCase;
import com.sigrap.sale.application.port.in.UpdateSaleUseCase;
import com.sigrap.sale.application.port.in.command.CreateSaleCommand;
import com.sigrap.sale.application.port.in.command.UpdateSaleCommand;
import com.sigrap.sale.domain.model.Sale;
import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for sale operations (Hexagonal Architecture).
 * This is an input adapter that translates HTTP requests to use case calls.
 * It handles HTTP concerns (validation, status codes, request/response mapping)
 * and delegates business logic to use cases.
 * 
 * <p>Mapped to /api/sales for coexistence with legacy endpoints.
 */
@RestController("saleController")
@RequestMapping("/api/sales")
@Tag(name = "Sale Management", description = "APIs for managing sales transactions including creation, completion, and cancellation")
public class SaleController {
    
    private final CreateSaleUseCase createSaleUseCase;
    private final GetSaleUseCase getSaleUseCase;
    private final UpdateSaleUseCase updateSaleUseCase;
    private final DeleteSaleUseCase deleteSaleUseCase;
    private final CompleteSaleUseCase completeSaleUseCase;
    private final CancelSaleUseCase cancelSaleUseCase;
    private final SaleResponseMapper responseMapper;
    
    public SaleController(
            CreateSaleUseCase createSaleUseCase,
            GetSaleUseCase getSaleUseCase,
            UpdateSaleUseCase updateSaleUseCase,
            DeleteSaleUseCase deleteSaleUseCase,
            CompleteSaleUseCase completeSaleUseCase,
            CancelSaleUseCase cancelSaleUseCase,
            SaleResponseMapper responseMapper) {
        this.createSaleUseCase = createSaleUseCase;
        this.getSaleUseCase = getSaleUseCase;
        this.updateSaleUseCase = updateSaleUseCase;
        this.deleteSaleUseCase = deleteSaleUseCase;
        this.completeSaleUseCase = completeSaleUseCase;
        this.cancelSaleUseCase = cancelSaleUseCase;
        this.responseMapper = responseMapper;
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new sale", description = "Creates a new sale transaction with customer, employee, and payment details")
    @ApiResponse(responseCode = "201", description = "Sale created successfully",
        content = @Content(schema = @Schema(implementation = SaleResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request - validation errors")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public SaleResponse create(@Valid @RequestBody SaleRequest request) {
        CreateSaleCommand command = new CreateSaleCommand(
            request.saleNumber(),
            request.customerId(),
            request.employeeId(),
            request.saleDate(),
            request.paymentMethod(),
            request.notes()
        );
        Sale sale = createSaleUseCase.create(command);
        return responseMapper.toResponse(sale);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get sale by ID", description = "Retrieves a single sale by its unique identifier")
    @ApiResponse(responseCode = "200", description = "Sale found successfully",
        content = @Content(schema = @Schema(implementation = SaleResponse.class)))
    @ApiResponse(responseCode = "404", description = "Sale not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public SaleResponse getById(
        @Parameter(description = "Sale unique identifier", required = true, example = "1")
        @PathVariable Long id) {
        SaleId saleId = new SaleId(id);
        Sale sale = getSaleUseCase.getById(saleId);
        return responseMapper.toResponse(sale);
    }
    
    @GetMapping
    @Operation(summary = "Get all sales", description = "Retrieves a list of all sales in the system")
    @ApiResponse(responseCode = "200", description = "List of sales retrieved successfully",
        content = @Content(schema = @Schema(implementation = SaleResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public List<SaleResponse> getAll() {
        return getSaleUseCase.getAll().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get sales by customer", description = "Retrieves all sales for a specific customer")
    @ApiResponse(responseCode = "200", description = "List of customer sales retrieved successfully",
        content = @Content(schema = @Schema(implementation = SaleResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public List<SaleResponse> getByCustomerId(
        @Parameter(description = "Customer unique identifier", required = true, example = "1")
        @PathVariable Long customerId) {
        return getSaleUseCase.getByCustomerId(customerId).stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get sales by status", description = "Retrieves all sales with a specific status")
    @ApiResponse(responseCode = "200", description = "List of sales with status retrieved successfully",
        content = @Content(schema = @Schema(implementation = SaleResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public List<SaleResponse> getByStatus(
        @Parameter(description = "Sale status", required = true, example = "PENDING")
        @PathVariable SaleStatus status) {
        return getSaleUseCase.getByStatus(status).stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing sale", description = "Updates a sale's payment method and notes")
    @ApiResponse(responseCode = "200", description = "Sale updated successfully",
        content = @Content(schema = @Schema(implementation = SaleResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request - validation errors")
    @ApiResponse(responseCode = "404", description = "Sale not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public SaleResponse update(
            @Parameter(description = "Sale unique identifier", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateSaleRequest request) {
        SaleId saleId = new SaleId(id);
        UpdateSaleCommand command = new UpdateSaleCommand(
            request.paymentMethod(),
            request.notes()
        );
        Sale sale = updateSaleUseCase.update(saleId, command);
        return responseMapper.toResponse(sale);
    }
    
    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete a sale", description = "Changes the sale status to COMPLETED")
    @ApiResponse(responseCode = "200", description = "Sale completed successfully",
        content = @Content(schema = @Schema(implementation = SaleResponse.class)))
    @ApiResponse(responseCode = "404", description = "Sale not found")
    @ApiResponse(responseCode = "400", description = "Sale cannot be completed in current state")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public SaleResponse complete(
        @Parameter(description = "Sale unique identifier", required = true, example = "1")
        @PathVariable Long id) {
        SaleId saleId = new SaleId(id);
        Sale sale = completeSaleUseCase.complete(saleId);
        return responseMapper.toResponse(sale);
    }
    
    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a sale", description = "Changes the sale status to CANCELLED")
    @ApiResponse(responseCode = "200", description = "Sale cancelled successfully",
        content = @Content(schema = @Schema(implementation = SaleResponse.class)))
    @ApiResponse(responseCode = "404", description = "Sale not found")
    @ApiResponse(responseCode = "400", description = "Sale cannot be cancelled in current state")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public SaleResponse cancel(
        @Parameter(description = "Sale unique identifier", required = true, example = "1")
        @PathVariable Long id) {
        SaleId saleId = new SaleId(id);
        Sale sale = cancelSaleUseCase.cancel(saleId);
        return responseMapper.toResponse(sale);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a sale", description = "Deletes a sale by its unique identifier")
    @ApiResponse(responseCode = "204", description = "Sale deleted successfully")
    @ApiResponse(responseCode = "404", description = "Sale not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public void delete(
        @Parameter(description = "Sale unique identifier", required = true, example = "1")
        @PathVariable Long id) {
        SaleId saleId = new SaleId(id);
        deleteSaleUseCase.delete(saleId);
    }
}
