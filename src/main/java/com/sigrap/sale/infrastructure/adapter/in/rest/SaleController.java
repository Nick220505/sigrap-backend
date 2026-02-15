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
 * <p>Mapped to /api/v2/sales for coexistence with legacy endpoints.
 */
@RestController("saleControllerV2")
@RequestMapping("/api/v2/sales")
public class SaleController {
    
    private final CreateSaleUseCase createSaleUseCase;
    private final GetSaleUseCase getSaleUseCase;
    private final UpdateSaleUseCase updateSaleUseCase;
    private final DeleteSaleUseCase deleteSaleUseCase;
    private final CompleteSaleUseCase completeSaleUseCase;
    private final CancelSaleUseCase cancelSaleUseCase;
    private final SaleResponseMapper responseMapper;
    
    /**
     * Constructor injection of use cases and mapper.
     *
     * @param createSaleUseCase use case for creating sales
     * @param getSaleUseCase use case for retrieving sales
     * @param updateSaleUseCase use case for updating sales
     * @param deleteSaleUseCase use case for deleting sales
     * @param completeSaleUseCase use case for completing sales
     * @param cancelSaleUseCase use case for cancelling sales
     * @param responseMapper mapper for converting domain entities to response DTOs
     */
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
    
    /**
     * Creates a new sale.
     * POST /api/v2/sales
     *
     * @param request the sale creation request
     * @return the created sale response with HTTP 201 status
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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
    
    /**
     * Retrieves a sale by its ID.
     * GET /api/v2/sales/{id}
     *
     * @param id the sale identifier
     * @return the sale response with HTTP 200 status
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale is not found
     */
    @GetMapping("/{id}")
    public SaleResponse getById(@PathVariable Long id) {
        SaleId saleId = new SaleId(id);
        Sale sale = getSaleUseCase.getById(saleId);
        return responseMapper.toResponse(sale);
    }
    
    /**
     * Retrieves all sales.
     * GET /api/v2/sales
     *
     * @return a list of all sale responses with HTTP 200 status
     */
    @GetMapping
    public List<SaleResponse> getAll() {
        return getSaleUseCase.getAll().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Retrieves all sales for a specific customer.
     * GET /api/v2/sales/customer/{customerId}
     *
     * @param customerId the customer identifier
     * @return a list of sale responses for the customer with HTTP 200 status
     */
    @GetMapping("/customer/{customerId}")
    public List<SaleResponse> getByCustomerId(@PathVariable Long customerId) {
        return getSaleUseCase.getByCustomerId(customerId).stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Retrieves all sales with a specific status.
     * GET /api/v2/sales/status/{status}
     *
     * @param status the sale status
     * @return a list of sale responses with the given status with HTTP 200 status
     */
    @GetMapping("/status/{status}")
    public List<SaleResponse> getByStatus(@PathVariable SaleStatus status) {
        return getSaleUseCase.getByStatus(status).stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Updates an existing sale.
     * PUT /api/v2/sales/{id}
     *
     * @param id the sale identifier
     * @param request the sale update request
     * @return the updated sale response with HTTP 200 status
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale is not found
     */
    @PutMapping("/{id}")
    public SaleResponse update(
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
    
    /**
     * Completes a sale.
     * POST /api/v2/sales/{id}/complete
     *
     * @param id the sale identifier
     * @return the completed sale response with HTTP 200 status
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale is not found
     * @throws IllegalStateException if the sale cannot be completed
     */
    @PostMapping("/{id}/complete")
    public SaleResponse complete(@PathVariable Long id) {
        SaleId saleId = new SaleId(id);
        Sale sale = completeSaleUseCase.complete(saleId);
        return responseMapper.toResponse(sale);
    }
    
    /**
     * Cancels a sale.
     * POST /api/v2/sales/{id}/cancel
     *
     * @param id the sale identifier
     * @return the cancelled sale response with HTTP 200 status
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale is not found
     * @throws IllegalStateException if the sale cannot be cancelled
     */
    @PostMapping("/{id}/cancel")
    public SaleResponse cancel(@PathVariable Long id) {
        SaleId saleId = new SaleId(id);
        Sale sale = cancelSaleUseCase.cancel(saleId);
        return responseMapper.toResponse(sale);
    }
    
    /**
     * Deletes a sale by its ID.
     * DELETE /api/v2/sales/{id}
     *
     * @param id the sale identifier
     * @return HTTP 204 No Content status
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale is not found
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        SaleId saleId = new SaleId(id);
        deleteSaleUseCase.delete(saleId);
    }
}
