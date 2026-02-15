package com.sigrap.supplier.infrastructure.adapter.in.rest;

import com.sigrap.supplier.application.port.in.ApprovePurchaseOrderUseCase;
import com.sigrap.supplier.application.port.in.CancelPurchaseOrderUseCase;
import com.sigrap.supplier.application.port.in.CreatePurchaseOrderUseCase;
import com.sigrap.supplier.application.port.in.DeletePurchaseOrderUseCase;
import com.sigrap.supplier.application.port.in.GetPurchaseOrderUseCase;
import com.sigrap.supplier.application.port.in.ReceivePurchaseOrderUseCase;
import com.sigrap.supplier.application.port.in.UpdatePurchaseOrderUseCase;
import com.sigrap.supplier.application.port.in.command.CreatePurchaseOrderCommand;
import com.sigrap.supplier.application.port.in.command.UpdatePurchaseOrderCommand;
import com.sigrap.supplier.domain.model.PurchaseOrder;
import com.sigrap.supplier.domain.model.PurchaseOrderId;
import com.sigrap.supplier.domain.model.PurchaseOrderStatus;
import com.sigrap.supplier.domain.model.SupplierId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for purchase order operations.
 * This is an input adapter that translates HTTP requests to use case calls.
 * It handles HTTP concerns (validation, status codes, request/response mapping)
 * and delegates business logic to use cases.
 */
@RestController
@RequestMapping("/api/v2/purchase-orders")
public class PurchaseOrderController {
    
    private final CreatePurchaseOrderUseCase createPurchaseOrderUseCase;
    private final GetPurchaseOrderUseCase getPurchaseOrderUseCase;
    private final UpdatePurchaseOrderUseCase updatePurchaseOrderUseCase;
    private final DeletePurchaseOrderUseCase deletePurchaseOrderUseCase;
    private final ApprovePurchaseOrderUseCase approvePurchaseOrderUseCase;
    private final ReceivePurchaseOrderUseCase receivePurchaseOrderUseCase;
    private final CancelPurchaseOrderUseCase cancelPurchaseOrderUseCase;
    private final PurchaseOrderResponseMapper responseMapper;
    
    /**
     * Constructor injection of use cases and mapper.
     *
     * @param createPurchaseOrderUseCase use case for creating purchase orders
     * @param getPurchaseOrderUseCase use case for retrieving purchase orders
     * @param updatePurchaseOrderUseCase use case for updating purchase orders
     * @param deletePurchaseOrderUseCase use case for deleting purchase orders
     * @param approvePurchaseOrderUseCase use case for approving purchase orders
     * @param receivePurchaseOrderUseCase use case for receiving purchase orders
     * @param cancelPurchaseOrderUseCase use case for cancelling purchase orders
     * @param responseMapper mapper for converting domain entities to response DTOs
     */
    public PurchaseOrderController(
            CreatePurchaseOrderUseCase createPurchaseOrderUseCase,
            GetPurchaseOrderUseCase getPurchaseOrderUseCase,
            UpdatePurchaseOrderUseCase updatePurchaseOrderUseCase,
            DeletePurchaseOrderUseCase deletePurchaseOrderUseCase,
            ApprovePurchaseOrderUseCase approvePurchaseOrderUseCase,
            ReceivePurchaseOrderUseCase receivePurchaseOrderUseCase,
            CancelPurchaseOrderUseCase cancelPurchaseOrderUseCase,
            PurchaseOrderResponseMapper responseMapper) {
        this.createPurchaseOrderUseCase = createPurchaseOrderUseCase;
        this.getPurchaseOrderUseCase = getPurchaseOrderUseCase;
        this.updatePurchaseOrderUseCase = updatePurchaseOrderUseCase;
        this.deletePurchaseOrderUseCase = deletePurchaseOrderUseCase;
        this.approvePurchaseOrderUseCase = approvePurchaseOrderUseCase;
        this.receivePurchaseOrderUseCase = receivePurchaseOrderUseCase;
        this.cancelPurchaseOrderUseCase = cancelPurchaseOrderUseCase;
        this.responseMapper = responseMapper;
    }
    
    /**
     * Creates a new purchase order.
     * POST /api/v2/purchase-orders
     *
     * @param request the purchase order creation request
     * @return the created purchase order response with HTTP 201 status
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseOrderResponse create(@Valid @RequestBody PurchaseOrderRequest request) {
        CreatePurchaseOrderCommand command = new CreatePurchaseOrderCommand(
            request.orderNumber(),
            request.supplierId(),
            request.orderDate(),
            request.expectedDeliveryDate(),
            request.totalAmount(),
            request.notes()
        );
        PurchaseOrder purchaseOrder = createPurchaseOrderUseCase.create(command);
        return responseMapper.toResponse(purchaseOrder);
    }
    
    /**
     * Retrieves a purchase order by its ID.
     * GET /api/v2/purchase-orders/{id}
     *
     * @param id the purchase order identifier
     * @return the purchase order response with HTTP 200 status
     * @throws IllegalArgumentException if the purchase order is not found
     */
    @GetMapping("/{id}")
    public PurchaseOrderResponse getById(@PathVariable Long id) {
        PurchaseOrderId purchaseOrderId = new PurchaseOrderId(id);
        PurchaseOrder purchaseOrder = getPurchaseOrderUseCase.getById(purchaseOrderId);
        return responseMapper.toResponse(purchaseOrder);
    }
    
    /**
     * Retrieves all purchase orders.
     * GET /api/v2/purchase-orders
     *
     * @return a list of all purchase order responses with HTTP 200 status
     */
    @GetMapping
    public List<PurchaseOrderResponse> getAll() {
        return getPurchaseOrderUseCase.getAll().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Retrieves all purchase orders for a specific supplier.
     * GET /api/v2/purchase-orders/supplier/{supplierId}
     *
     * @param supplierId the supplier identifier
     * @return a list of purchase order responses for the supplier with HTTP 200 status
     */
    @GetMapping("/supplier/{supplierId}")
    public List<PurchaseOrderResponse> getBySupplierId(@PathVariable Long supplierId) {
        SupplierId id = new SupplierId(supplierId);
        return getPurchaseOrderUseCase.getBySupplierId(id).stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Retrieves all purchase orders with a specific status.
     * GET /api/v2/purchase-orders/status/{status}
     *
     * @param status the purchase order status (PENDING, APPROVED, RECEIVED, CANCELLED)
     * @return a list of purchase order responses with the given status with HTTP 200 status
     */
    @GetMapping("/status/{status}")
    public List<PurchaseOrderResponse> getByStatus(@PathVariable String status) {
        PurchaseOrderStatus orderStatus = PurchaseOrderStatus.valueOf(status.toUpperCase());
        return getPurchaseOrderUseCase.getByStatus(orderStatus).stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Updates an existing purchase order.
     * PUT /api/v2/purchase-orders/{id}
     *
     * @param id the purchase order identifier
     * @param request the purchase order update request
     * @return the updated purchase order response with HTTP 200 status
     * @throws IllegalArgumentException if the purchase order is not found
     */
    @PutMapping("/{id}")
    public PurchaseOrderResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePurchaseOrderRequest request) {
        PurchaseOrderId purchaseOrderId = new PurchaseOrderId(id);
        UpdatePurchaseOrderCommand command = new UpdatePurchaseOrderCommand(
            request.expectedDeliveryDate(),
            request.notes()
        );
        PurchaseOrder purchaseOrder = updatePurchaseOrderUseCase.update(purchaseOrderId, command);
        return responseMapper.toResponse(purchaseOrder);
    }
    
    /**
     * Approves a purchase order.
     * POST /api/v2/purchase-orders/{id}/approve
     *
     * @param id the purchase order identifier
     * @return the approved purchase order response with HTTP 200 status
     * @throws IllegalArgumentException if the purchase order is not found
     * @throws IllegalStateException if the purchase order cannot be approved
     */
    @PostMapping("/{id}/approve")
    public PurchaseOrderResponse approve(@PathVariable Long id) {
        PurchaseOrderId purchaseOrderId = new PurchaseOrderId(id);
        PurchaseOrder purchaseOrder = approvePurchaseOrderUseCase.approve(purchaseOrderId);
        return responseMapper.toResponse(purchaseOrder);
    }
    
    /**
     * Marks a purchase order as received.
     * POST /api/v2/purchase-orders/{id}/receive
     *
     * @param id the purchase order identifier
     * @return the received purchase order response with HTTP 200 status
     * @throws IllegalArgumentException if the purchase order is not found
     * @throws IllegalStateException if the purchase order cannot be received
     */
    @PostMapping("/{id}/receive")
    public PurchaseOrderResponse receive(@PathVariable Long id) {
        PurchaseOrderId purchaseOrderId = new PurchaseOrderId(id);
        PurchaseOrder purchaseOrder = receivePurchaseOrderUseCase.receive(purchaseOrderId);
        return responseMapper.toResponse(purchaseOrder);
    }
    
    /**
     * Cancels a purchase order.
     * POST /api/v2/purchase-orders/{id}/cancel
     *
     * @param id the purchase order identifier
     * @return the cancelled purchase order response with HTTP 200 status
     * @throws IllegalArgumentException if the purchase order is not found
     * @throws IllegalStateException if the purchase order cannot be cancelled
     */
    @PostMapping("/{id}/cancel")
    public PurchaseOrderResponse cancel(@PathVariable Long id) {
        PurchaseOrderId purchaseOrderId = new PurchaseOrderId(id);
        PurchaseOrder purchaseOrder = cancelPurchaseOrderUseCase.cancel(purchaseOrderId);
        return responseMapper.toResponse(purchaseOrder);
    }
    
    /**
     * Deletes a purchase order by its ID.
     * DELETE /api/v2/purchase-orders/{id}
     *
     * @param id the purchase order identifier
     * @return HTTP 204 No Content status
     * @throws IllegalArgumentException if the purchase order is not found
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        PurchaseOrderId purchaseOrderId = new PurchaseOrderId(id);
        deletePurchaseOrderUseCase.delete(purchaseOrderId);
    }
}
