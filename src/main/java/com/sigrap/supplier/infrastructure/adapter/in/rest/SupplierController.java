package com.sigrap.supplier.infrastructure.adapter.in.rest;

import com.sigrap.supplier.application.port.in.CreateSupplierUseCase;
import com.sigrap.supplier.application.port.in.DeleteSupplierUseCase;
import com.sigrap.supplier.application.port.in.GetSupplierUseCase;
import com.sigrap.supplier.application.port.in.UpdateSupplierUseCase;
import com.sigrap.supplier.application.port.in.command.CreateSupplierCommand;
import com.sigrap.supplier.application.port.in.command.UpdateSupplierCommand;
import com.sigrap.supplier.domain.model.Supplier;
import com.sigrap.supplier.domain.model.SupplierId;
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
 * REST controller for supplier operations.
 * This is an input adapter that translates HTTP requests to use case calls.
 * It handles HTTP concerns (validation, status codes, request/response mapping)
 * and delegates business logic to use cases.
 */
@RestController
@RequestMapping("/api/suppliers")
@Tag(name = "Supplier Management", description = "APIs for managing suppliers and their contact information")
public class SupplierController {
    
    private final CreateSupplierUseCase createSupplierUseCase;
    private final GetSupplierUseCase getSupplierUseCase;
    private final UpdateSupplierUseCase updateSupplierUseCase;
    private final DeleteSupplierUseCase deleteSupplierUseCase;
    private final SupplierResponseMapper responseMapper;
    
    /**
     * Constructor injection of use cases and mapper.
     *
     * @param createSupplierUseCase use case for creating suppliers
     * @param getSupplierUseCase use case for retrieving suppliers
     * @param updateSupplierUseCase use case for updating suppliers
     * @param deleteSupplierUseCase use case for deleting suppliers
     * @param responseMapper mapper for converting domain entities to response DTOs
     */
    public SupplierController(
            CreateSupplierUseCase createSupplierUseCase,
            GetSupplierUseCase getSupplierUseCase,
            UpdateSupplierUseCase updateSupplierUseCase,
            DeleteSupplierUseCase deleteSupplierUseCase,
            SupplierResponseMapper responseMapper) {
        this.createSupplierUseCase = createSupplierUseCase;
        this.getSupplierUseCase = getSupplierUseCase;
        this.updateSupplierUseCase = updateSupplierUseCase;
        this.deleteSupplierUseCase = deleteSupplierUseCase;
        this.responseMapper = responseMapper;
    }
    
    /**
     * Creates a new supplier.
     * POST /api/suppliers
     *
     * @param request the supplier creation request
     * @return the created supplier response with HTTP 201 status
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a new supplier",
        description = "Creates a new supplier with company name, contact information, and address"
    )
    @ApiResponse(responseCode = "201", description = "Supplier created successfully",
        content = @Content(schema = @Schema(implementation = SupplierResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request - validation errors")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public SupplierResponse create(@Valid @RequestBody SupplierRequest request) {
        CreateSupplierCommand command = new CreateSupplierCommand(
            request.name(),
            request.contactName(),
            request.email(),
            request.phone(),
            request.address()
        );
        Supplier supplier = createSupplierUseCase.create(command);
        return responseMapper.toResponse(supplier);
    }
    
    /**
     * Retrieves a supplier by its ID.
     * GET /api/suppliers/{id}
     *
     * @param id the supplier identifier
     * @return the supplier response with HTTP 200 status
     * @throws IllegalArgumentException if the supplier is not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID", description = "Retrieves a single supplier by its unique identifier")
    @ApiResponse(responseCode = "200", description = "Supplier found successfully",
        content = @Content(schema = @Schema(implementation = SupplierResponse.class)))
    @ApiResponse(responseCode = "404", description = "Supplier not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public SupplierResponse getById(
        @Parameter(description = "Supplier unique identifier", required = true, example = "1")
        @PathVariable Long id) {
        SupplierId supplierId = new SupplierId(id);
        Supplier supplier = getSupplierUseCase.getById(supplierId);
        return responseMapper.toResponse(supplier);
    }
    
    /**
     * Retrieves all suppliers.
     * GET /api/suppliers
     *
     * @return a list of all supplier responses with HTTP 200 status
     */
    @GetMapping
    @Operation(summary = "Get all suppliers", description = "Retrieves a list of all suppliers in the system")
    @ApiResponse(responseCode = "200", description = "List of suppliers retrieved successfully",
        content = @Content(schema = @Schema(implementation = SupplierResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public List<SupplierResponse> getAll() {
        return getSupplierUseCase.getAll().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Updates an existing supplier.
     * PUT /api/suppliers/{id}
     *
     * @param id the supplier identifier
     * @param request the supplier update request
     * @return the updated supplier response with HTTP 200 status
     * @throws IllegalArgumentException if the supplier is not found or email conflicts
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing supplier", description = "Updates a supplier's information")
    @ApiResponse(responseCode = "200", description = "Supplier updated successfully",
        content = @Content(schema = @Schema(implementation = SupplierResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request - validation errors")
    @ApiResponse(responseCode = "404", description = "Supplier not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public SupplierResponse update(
            @Parameter(description = "Supplier unique identifier", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request) {
        SupplierId supplierId = new SupplierId(id);
        UpdateSupplierCommand command = new UpdateSupplierCommand(
            request.name(),
            request.contactName(),
            request.email(),
            request.phone(),
            request.address()
        );
        Supplier supplier = updateSupplierUseCase.update(supplierId, command);
        return responseMapper.toResponse(supplier);
    }
    
    /**
     * Deletes a supplier by its ID.
     * DELETE /api/suppliers/{id}
     *
     * @param id the supplier identifier
     * @return HTTP 204 No Content status
     * @throws IllegalArgumentException if the supplier is not found
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a supplier", description = "Deletes a supplier by its unique identifier")
    @ApiResponse(responseCode = "204", description = "Supplier deleted successfully")
    @ApiResponse(responseCode = "404", description = "Supplier not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public void delete(
        @Parameter(description = "Supplier unique identifier", required = true, example = "1")
        @PathVariable Long id) {
        SupplierId supplierId = new SupplierId(id);
        deleteSupplierUseCase.delete(supplierId);
    }
    
    /**
     * Deletes multiple suppliers by their IDs (batch delete).
     * DELETE /api/suppliers/batch
     *
     * @param ids the list of supplier identifiers to delete
     * @return HTTP 204 No Content status
     * @throws IllegalArgumentException if any of the suppliers are not found
     */
    @DeleteMapping("/batch")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete multiple suppliers", description = "Deletes multiple suppliers in a single operation")
    @ApiResponse(responseCode = "204", description = "Suppliers deleted successfully")
    @ApiResponse(responseCode = "404", description = "One or more suppliers not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public void deleteAll(
        @Parameter(description = "List of supplier IDs to delete", required = true)
        @RequestBody List<Long> ids) {
        List<SupplierId> supplierIds = ids.stream()
            .map(SupplierId::new)
            .toList();
        deleteSupplierUseCase.deleteAll(supplierIds);
    }
}
