package com.sigrap.supplier.infrastructure.adapter.in.rest;

import com.sigrap.supplier.domain.model.PurchaseOrder;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting domain PurchaseOrder entities to PurchaseOrderResponse DTOs.
 * This mapper is used by the REST adapter to translate domain objects to HTTP responses.
 */
@Component
public class PurchaseOrderResponseMapper {
    
    /**
     * Converts a domain PurchaseOrder entity to a PurchaseOrderResponse DTO.
     *
     * @param purchaseOrder the domain purchase order entity
     * @return the purchase order response DTO
     */
    public PurchaseOrderResponse toResponse(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null) {
            return null;
        }
        
        return new PurchaseOrderResponse(
            purchaseOrder.getId() != null ? purchaseOrder.getId().value() : null,
            purchaseOrder.getOrderNumber().value(),
            purchaseOrder.getSupplierId().value(),
            purchaseOrder.getOrderDate(),
            purchaseOrder.getExpectedDeliveryDate(),
            purchaseOrder.getStatus().name(),
            purchaseOrder.getTotalAmount(),
            purchaseOrder.getNotes(),
            purchaseOrder.getCreatedAt(),
            purchaseOrder.getUpdatedAt()
        );
    }
}
