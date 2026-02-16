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

@RestController("saleReturnControllerV2")
@RequestMapping("/api/v2/sale-returns")
@Tag(name = "Sale Return Management", description = "APIs for managing sale returns including approval, rejection, and completion")
public class SaleReturnController {
    
    private final CreateSaleReturnUseCase createSaleReturnUseCase;
    private final GetSaleReturnUseCase getSaleReturnUseCase;
    private final ApproveSaleReturnUseCase approveSaleReturnUseCase;
    private final RejectSaleReturnUseCase rejectSaleReturnUseCase;
    private final CompleteSaleReturnUseCase completeSaleReturnUseCase;
    private final SaleReturnResponseMapper responseMapper;
    
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
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new sale return", description = "Creates a new sale return request with refund details")
    @ApiResponse(responseCode = "201", description = "Sale return created successfully",
        content = @Content(schema = @Schema(implementation = SaleReturnResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request - validation errors")
    @ApiResponse(responseCode = "404", description = "Sale not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
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
    
    @GetMapping("/{id}")
    @Operation(summary = "Get sale return by ID", description = "Retrieves a single sale return by its unique identifier")
    @ApiResponse(responseCode = "200", description = "Sale return found successfully",
        content = @Content(schema = @Schema(implementation = SaleReturnResponse.class)))
    @ApiResponse(responseCode = "404", description = "Sale return not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public SaleReturnResponse getById(
        @Parameter(description = "Sale return unique identifier", required = true, example = "1")
        @PathVariable Long id) {
        SaleReturnId saleReturnId = new SaleReturnId(id);
        SaleReturn saleReturn = getSaleReturnUseCase.getById(saleReturnId);
        return responseMapper.toResponse(saleReturn);
    }
    
    @GetMapping
    @Operation(summary = "Get all sale returns", description = "Retrieves a list of all sale returns in the system")
    @ApiResponse(responseCode = "200", description = "List of sale returns retrieved successfully",
        content = @Content(schema = @Schema(implementation = SaleReturnResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public List<SaleReturnResponse> getAll() {
        return getSaleReturnUseCase.getAll().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    @GetMapping("/sale/{saleId}")
    @Operation(summary = "Get sale returns by sale", description = "Retrieves all returns for a specific sale")
    @ApiResponse(responseCode = "200", description = "List of sale returns retrieved successfully",
        content = @Content(schema = @Schema(implementation = SaleReturnResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public List<SaleReturnResponse> getBySaleId(
        @Parameter(description = "Sale unique identifier", required = true, example = "1")
        @PathVariable Long saleId) {
        SaleId id = new SaleId(saleId);
        return getSaleReturnUseCase.getBySaleId(id).stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get sale returns by status", description = "Retrieves all sale returns with a specific status")
    @ApiResponse(responseCode = "200", description = "List of sale returns with status retrieved successfully",
        content = @Content(schema = @Schema(implementation = SaleReturnResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public List<SaleReturnResponse> getByStatus(
        @Parameter(description = "Sale return status", required = true, example = "PENDING")
        @PathVariable SaleReturnStatus status) {
        return getSaleReturnUseCase.getByStatus(status).stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve a sale return", description = "Changes the sale return status to APPROVED")
    @ApiResponse(responseCode = "200", description = "Sale return approved successfully",
        content = @Content(schema = @Schema(implementation = SaleReturnResponse.class)))
    @ApiResponse(responseCode = "404", description = "Sale return not found")
    @ApiResponse(responseCode = "400", description = "Sale return cannot be approved in current state")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public SaleReturnResponse approve(
        @Parameter(description = "Sale return unique identifier", required = true, example = "1")
        @PathVariable Long id) {
        SaleReturnId saleReturnId = new SaleReturnId(id);
        SaleReturn saleReturn = approveSaleReturnUseCase.approve(saleReturnId);
        return responseMapper.toResponse(saleReturn);
    }
    
    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject a sale return", description = "Changes the sale return status to REJECTED")
    @ApiResponse(responseCode = "200", description = "Sale return rejected successfully",
        content = @Content(schema = @Schema(implementation = SaleReturnResponse.class)))
    @ApiResponse(responseCode = "404", description = "Sale return not found")
    @ApiResponse(responseCode = "400", description = "Sale return cannot be rejected in current state")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public SaleReturnResponse reject(
        @Parameter(description = "Sale return unique identifier", required = true, example = "1")
        @PathVariable Long id) {
        SaleReturnId saleReturnId = new SaleReturnId(id);
        SaleReturn saleReturn = rejectSaleReturnUseCase.reject(saleReturnId);
        return responseMapper.toResponse(saleReturn);
    }
    
    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete a sale return", description = "Changes the sale return status to COMPLETED and processes the refund")
    @ApiResponse(responseCode = "200", description = "Sale return completed successfully",
        content = @Content(schema = @Schema(implementation = SaleReturnResponse.class)))
    @ApiResponse(responseCode = "404", description = "Sale return not found")
    @ApiResponse(responseCode = "400", description = "Sale return cannot be completed in current state")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    public SaleReturnResponse complete(
        @Parameter(description = "Sale return unique identifier", required = true, example = "1")
        @PathVariable Long id) {
        SaleReturnId saleReturnId = new SaleReturnId(id);
        SaleReturn saleReturn = completeSaleReturnUseCase.complete(saleReturnId);
        return responseMapper.toResponse(saleReturn);
    }
}
