package com.sigrap.sale.infrastructure.adapter.in.rest;

import com.sigrap.sale.domain.model.SaleReturn;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting domain SaleReturn entities to SaleReturnResponse DTOs.
 * This mapper is used by the REST adapter to translate domain objects to HTTP responses.
 */
@Component
public class SaleReturnResponseMapper {
    
    /**
     * Converts a domain SaleReturn entity to a SaleReturnResponse DTO.
     *
     * @param saleReturn the domain sale return entity
     * @return the sale return response DTO
     */
    public SaleReturnResponse toResponse(SaleReturn saleReturn) {
        if (saleReturn == null) {
            return null;
        }
        
        return new SaleReturnResponse(
            saleReturn.getId() != null ? saleReturn.getId().value() : null,
            saleReturn.getReturnNumber().value(),
            saleReturn.getSaleId().value(),
            saleReturn.getReturnDate(),
            saleReturn.getReason(),
            saleReturn.getStatus(),
            saleReturn.getRefundAmount(),
            saleReturn.getNotes(),
            saleReturn.getCreatedAt(),
            saleReturn.getUpdatedAt()
        );
    }
}
