package com.sigrap.supplier.infrastructure.adapter.in.rest;

import com.sigrap.supplier.application.port.in.CreateSupplierUseCase;
import com.sigrap.supplier.application.port.in.DeleteSupplierUseCase;
import com.sigrap.supplier.application.port.in.GetSupplierUseCase;
import com.sigrap.supplier.application.port.in.UpdateSupplierUseCase;
import com.sigrap.supplier.application.port.in.command.CreateSupplierCommand;
import com.sigrap.supplier.application.port.in.command.UpdateSupplierCommand;
import com.sigrap.supplier.domain.model.Supplier;
import com.sigrap.supplier.domain.model.SupplierId;
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
@RequestMapping("/api/v2/suppliers")
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
     * POST /api/v2/suppliers
     *
     * @param request the supplier creation request
     * @return the created supplier response with HTTP 201 status
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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
     * GET /api/v2/suppliers/{id}
     *
     * @param id the supplier identifier
     * @return the supplier response with HTTP 200 status
     * @throws IllegalArgumentException if the supplier is not found
     */
    @GetMapping("/{id}")
    public SupplierResponse getById(@PathVariable Long id) {
        SupplierId supplierId = new SupplierId(id);
        Supplier supplier = getSupplierUseCase.getById(supplierId);
        return responseMapper.toResponse(supplier);
    }
    
    /**
     * Retrieves all suppliers.
     * GET /api/v2/suppliers
     *
     * @return a list of all supplier responses with HTTP 200 status
     */
    @GetMapping
    public List<SupplierResponse> getAll() {
        return getSupplierUseCase.getAll().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Updates an existing supplier.
     * PUT /api/v2/suppliers/{id}
     *
     * @param id the supplier identifier
     * @param request the supplier update request
     * @return the updated supplier response with HTTP 200 status
     * @throws IllegalArgumentException if the supplier is not found or email conflicts
     */
    @PutMapping("/{id}")
    public SupplierResponse update(
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
     * DELETE /api/v2/suppliers/{id}
     *
     * @param id the supplier identifier
     * @return HTTP 204 No Content status
     * @throws IllegalArgumentException if the supplier is not found
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        SupplierId supplierId = new SupplierId(id);
        deleteSupplierUseCase.delete(supplierId);
    }
    
    /**
     * Deletes multiple suppliers by their IDs (batch delete).
     * DELETE /api/v2/suppliers/batch
     *
     * @param ids the list of supplier identifiers to delete
     * @return HTTP 204 No Content status
     * @throws IllegalArgumentException if any of the suppliers are not found
     */
    @DeleteMapping("/batch")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll(@RequestBody List<Long> ids) {
        List<SupplierId> supplierIds = ids.stream()
            .map(SupplierId::new)
            .toList();
        deleteSupplierUseCase.deleteAll(supplierIds);
    }
}
