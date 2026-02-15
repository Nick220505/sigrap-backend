package com.sigrap.audit.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for audit domain value objects.
 */
class ValueObjectsTest {
    
    @Test
    void shouldCreateValidAuditLogId() {
        // Given/When
        AuditLogId id = new AuditLogId(1L);
        
        // Then
        assertEquals(1L, id.value());
    }
    
    @Test
    void shouldThrowExceptionForNullAuditLogId() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new AuditLogId(null)
        );
    }
    
    @Test
    void shouldThrowExceptionForNegativeAuditLogId() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new AuditLogId(-1L)
        );
    }
    
    @Test
    void shouldThrowExceptionForZeroAuditLogId() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new AuditLogId(0L)
        );
    }
    
    @Test
    void shouldCreateValidEntityType() {
        // Given/When
        EntityType entityType = new EntityType("Product");
        
        // Then
        assertEquals("Product", entityType.value());
    }
    
    @Test
    void shouldThrowExceptionForNullEntityType() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new EntityType(null)
        );
    }
    
    @Test
    void shouldThrowExceptionForBlankEntityType() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new EntityType("   ")
        );
    }
    
    @Test
    void shouldThrowExceptionForTooLongEntityType() {
        // Given
        String longName = "A".repeat(101);
        
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new EntityType(longName)
        );
    }
    
    @Test
    void shouldProvideCommonEntityTypeConstants() {
        // When/Then
        assertEquals("Category", EntityType.CATEGORY.value());
        assertEquals("Product", EntityType.PRODUCT.value());
        assertEquals("Customer", EntityType.CUSTOMER.value());
        assertEquals("Supplier", EntityType.SUPPLIER.value());
        assertEquals("Sale", EntityType.SALE.value());
        assertEquals("User", EntityType.USER.value());
        assertEquals("PurchaseOrder", EntityType.PURCHASE_ORDER.value());
        assertEquals("SaleReturn", EntityType.SALE_RETURN.value());
    }
    
    @Test
    void shouldGetAuditActionValue() {
        // When/Then
        assertEquals("CREATE", AuditAction.CREATE.getValue());
        assertEquals("UPDATE", AuditAction.UPDATE.getValue());
        assertEquals("DELETE", AuditAction.DELETE.getValue());
        assertEquals("VIEW", AuditAction.VIEW.getValue());
        assertEquals("LOGIN", AuditAction.LOGIN.getValue());
        assertEquals("LOGOUT", AuditAction.LOGOUT.getValue());
        assertEquals("LOGIN_FAILED", AuditAction.LOGIN_FAILED.getValue());
        assertEquals("ACCESS_DENIED", AuditAction.ACCESS_DENIED.getValue());
    }
    
    @Test
    void shouldGetAuditStatusValue() {
        // When/Then
        assertEquals("SUCCESS", AuditStatus.SUCCESS.getValue());
        assertEquals("ERROR", AuditStatus.ERROR.getValue());
        assertEquals("PARTIAL_SUCCESS", AuditStatus.PARTIAL_SUCCESS.getValue());
        assertEquals("PENDING", AuditStatus.PENDING.getValue());
    }
}
