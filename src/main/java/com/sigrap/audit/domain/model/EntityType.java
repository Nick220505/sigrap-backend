package com.sigrap.audit.domain.model;

/**
 * Value object representing the type of entity being audited.
 * This is a simple wrapper around a string to provide type safety.
 */
public record EntityType(String value) {
    
    public EntityType {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Entity type cannot be blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Entity type cannot exceed 100 characters");
        }
    }
    
    // Common entity types as constants
    public static final EntityType CATEGORY = new EntityType("Category");
    public static final EntityType PRODUCT = new EntityType("Product");
    public static final EntityType CUSTOMER = new EntityType("Customer");
    public static final EntityType SUPPLIER = new EntityType("Supplier");
    public static final EntityType SALE = new EntityType("Sale");
    public static final EntityType USER = new EntityType("User");
    public static final EntityType PURCHASE_ORDER = new EntityType("PurchaseOrder");
    public static final EntityType SALE_RETURN = new EntityType("SaleReturn");
    public static final EntityType ATTENDANCE = new EntityType("Attendance");
    public static final EntityType SCHEDULE = new EntityType("Schedule");
}
