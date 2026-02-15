package com.sigrap.sale.infrastructure.adapter.in.rest;

import com.sigrap.sale.application.port.in.ApproveSaleReturnUseCase;
import com.sigrap.sale.application.port.in.CompleteSaleReturnUseCase;
import com.sigrap.sale.application.port.in.CreateSaleReturnUseCase;
import com.sigrap.sale.application.port.in.GetSaleReturnUseCase;
import com.sigrap.sale.application.port.in.RejectSaleReturnUseCase;
import com.sigrap.sale.application.port.in.command.CreateSaleReturnCommand;
import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleReturn;
import com.sigrap.sale.domain.model.SaleReturnId;
import com.sigrap.sale.domain.model.SaleReturnStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for sale return operations (Hexagonal Architecture).
 * This is an input adapter that translates HTTP requests to use case calls.
 * It handles HTTP concerns (validation, status codes, request/response mapping)
 * and delegates business logic to use cases.
 * 
 * <p>Mapped to /api/v2/sale-returns for coexistence with legacy endpoints.
 */
@RestController("saleReturnControllerV2")
@RequestMapping("/api/v2/sale-returns")
public class SaleReturnController {
    
    private final CreateSaleReturnUseCase createSaleReturnUseCase;
    private final GetSaleReturnUseCase getSaleReturnUseCase;
    private final ApproveSaleReturnUseCase approveSaleReturnUseCase;
    private final RejectSaleReturnUseCase rejectSaleReturnUseCase;
    private final CompleteSaleReturnUseCase completeSaleReturnUseCase;
    private final SaleReturnResponseMapper responseMapper;
    
    /**
     * Constructor injection of use cases and mapper.
     *
     * @param createSaleReturnUseCase use case for creating sale returns
     * @param getSaleReturnUseCase use case for retrieving sale returns
     * @param approveSaleReturnUseCase use case for approving sale returns
     * @param rejectSaleReturnUseCase use case for rejecting sale returns
     * @param completeSaleReturnUseCase use case for completing sale returns
     * @param responseMapper mapper for converting domain entities to response DTOs
     */
    public SaleReturnController(
            CreateSaleReturnUseCase createSaleReturnUseCase,
            GetSaleReturnUseCase getSaleReturnUseCase,
            ApproveSaleReturnUseCase approveSaleReturnUseCase,
            RejectSaleReturnUseCase rejectSaleReturnUseCase,
            CompleteSaleReturnUseCase completeSaleReturnUseCase,
            SaleReturnResponseMapper responseMapper) {
        this.createSaleReturnUseCase = createSaleReturnUseCase;
        this.getSaleReturnUseCase = getSaleReturnUseCase;
        this.approveSaleReturnUseCase = approveSaleReturnUseCase;
        this.rejectSaleReturnUseCase = rejectSaleReturnUseCase;
        this.completeSaleReturnUseCase = completeSaleReturnUseCase;
        this.responseMapper = responseMapper;
    }
    
    /**
     * Creates a new sale return.
     * POST /api/v2/sale-returns
     *
     * @param request the sale return creation request
     * @return the created sale return response with HTTP 201 status
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale is not found
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SaleReturnResponse create(@Valid @RequestBody SaleReturnRequest request) {
        CreateSaleReturnCommand command = new CreateSaleReturnCommand(
            request.returnNumber(),
            request.saleId(),
            request.returnDate(),
            request.reason(),
            request.refundAmount(),
            request.notes()
        );
        SaleReturn saleReturn = createSaleReturnUseCase.create(command);
        return responseMapper.toResponse(saleReturn);
    }
    
    /**
     * Retrieves a sale return by its ID.
     * GET /api/v2/sale-returns/{id}
     *
     * @param id the sale return identifier
     * @return the sale return response with HTTP 200 status
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale return is not found
     */
    @GetMapping("/{id}")
    public SaleReturnResponse getById(@PathVariable Long id) {
        SaleReturnId saleReturnId = new SaleReturnId(id);
        SaleReturn saleReturn = getSaleReturnUseCase.getById(saleReturnId);
        return responseMapper.toResponse(saleReturn);
    }
    
    /**
     * Retrieves all sale returns.
     * GET /api/v2/sale-returns
     *
     * @return a list of all sale return responses with HTTP 200 status
     */
    @GetMapping
    public List<SaleReturnResponse> getAll() {
        return getSaleReturnUseCase.getAll().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Retrieves all sale returns for a specific sale.
     * GET /api/v2/sale-returns/sale/{saleId}
     *
     * @param saleId the sale identifier
     * @return a list of sale return responses for the sale with HTTP 200 status
     */
    @GetMapping("/sale/{saleId}")
    public List<SaleReturnResponse> getBySaleId(@PathVariable Long saleId) {
        SaleId id = new SaleId(saleId);
        return getSaleReturnUseCase.getBySaleId(id).stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Retrieves all sale returns with a specific status.
     * GET /api/v2/sale-returns/status/{status}
     *
     * @param status the sale return status
     * @return a list of sale return responses with the given status with HTTP 200 status
     */
    @GetMapping("/status/{status}")
    public List<SaleReturnResponse> getByStatus(@PathVariable SaleReturnStatus status) {
        return getSaleReturnUseCase.getByStatus(status).stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Approves a sale return.
     * POST /api/v2/sale-returns/{id}/approve
     *
     * @param id the sale return identifier
     * @return the approved sale return response with HTTP 200 status
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale return is not found
     * @throws IllegalStateException if the sale return cannot be approved
     */
    @PostMapping("/{id}/approve")
    public SaleReturnResponse approve(@PathVariable Long id) {
        SaleReturnId saleReturnId = new SaleReturnId(id);
        SaleReturn saleReturn = approveSaleReturnUseCase.approve(saleReturnId);
        return responseMapper.toResponse(saleReturn);
    }
    
    /**
     * Rejects a sale return.
     * POST /api/v2/sale-returns/{id}/reject
     *
     * @param id the sale return identifier
     * @return the rejected sale return response with HTTP 200 status
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale return is not found
     * @throws IllegalStateException if the sale return cannot be rejected
     */
    @PostMapping("/{id}/reject")
    public SaleReturnResponse reject(@PathVariable Long id) {
        SaleReturnId saleReturnId = new SaleReturnId(id);
        SaleReturn saleReturn = rejectSaleReturnUseCase.reject(saleReturnId);
        return responseMapper.toResponse(saleReturn);
    }
    
    /**
     * Completes a sale return.
     * POST /api/v2/sale-returns/{id}/complete
     *
     * @param id the sale return identifier
     * @return the completed sale return response with HTTP 200 status
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale return is not found
     * @throws IllegalStateException if the sale return cannot be completed
     */
    @PostMapping("/{id}/complete")
    public SaleReturnResponse complete(@PathVariable Long id) {
        SaleReturnId saleReturnId = new SaleReturnId(id);
        SaleReturn saleReturn = completeSaleReturnUseCase.complete(saleReturnId);
        return responseMapper.toResponse(saleReturn);
    }
}
