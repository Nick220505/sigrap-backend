package com.sigrap.product.domain.model;

import com.sigrap.category.domain.model.CategoryId;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a product.
 * This is a pure POJO with no framework dependencies, containing business logic and invariants.
 */
public class Product {
    private final ProductId id;
    private ProductName name;
    private String description;
    private ProductPrice costPrice;
    private ProductPrice salePrice;
    private ProductStock stock;
    private ProductStock minimumStockThreshold;
    private CategoryId categoryId;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Constructor for creating a new product (without ID).
     * Used when creating a product before persistence.
     *
     * @param name the product name (required)
     * @param description the product description (optional)
     * @param costPrice the cost price (required)
     * @param salePrice the sale price (required)
     * @param stock the initial stock quantity (required)
     * @param minimumStockThreshold the minimum stock threshold (required)
     * @param categoryId the category identifier (optional)
     */
    public Product(ProductName name, String description, ProductPrice costPrice, 
                   ProductPrice salePrice, ProductStock stock, 
                   ProductStock minimumStockThreshold, CategoryId categoryId) {
        this(null, name, description, costPrice, salePrice, stock, 
             minimumStockThreshold, categoryId, LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Full constructor for reconstituting a product from persistence.
     *
     * @param id the product identifier
     * @param name the product name (required)
     * @param description the product description (optional)
     * @param costPrice the cost price (required)
     * @param salePrice the sale price (required)
     * @param stock the stock quantity (required)
     * @param minimumStockThreshold the minimum stock threshold (required)
     * @param categoryId the category identifier (optional)
     * @param createdAt the creation timestamp
     * @param updatedAt the last update timestamp
     */
    public Product(ProductId id, ProductName name, String description, 
                   ProductPrice costPrice, ProductPrice salePrice, ProductStock stock,
                   ProductStock minimumStockThreshold, CategoryId categoryId,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Product name cannot be null");
        this.description = description;
        this.costPrice = Objects.requireNonNull(costPrice, "Cost price cannot be null");
        this.salePrice = Objects.requireNonNull(salePrice, "Sale price cannot be null");
        this.stock = Objects.requireNonNull(stock, "Stock cannot be null");
        this.minimumStockThreshold = Objects.requireNonNull(minimumStockThreshold, 
            "Minimum stock threshold cannot be null");
        this.categoryId = categoryId;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }

    /**
     * Updates the product name.
     *
     * @param newName the new product name
     */
    public void updateName(ProductName newName) {
        Objects.requireNonNull(newName, "New name cannot be null");
        if (!this.name.equals(newName)) {
            this.name = newName;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the product description.
     *
     * @param newDescription the new description (can be null)
     */
    public void updateDescription(String newDescription) {
        if (!Objects.equals(this.description, newDescription)) {
            this.description = newDescription;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the cost price.
     *
     * @param newCostPrice the new cost price
     */
    public void updateCostPrice(ProductPrice newCostPrice) {
        Objects.requireNonNull(newCostPrice, "New cost price cannot be null");
        if (!this.costPrice.equals(newCostPrice)) {
            this.costPrice = newCostPrice;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the sale price.
     *
     * @param newSalePrice the new sale price
     */
    public void updateSalePrice(ProductPrice newSalePrice) {
        Objects.requireNonNull(newSalePrice, "New sale price cannot be null");
        if (!this.salePrice.equals(newSalePrice)) {
            this.salePrice = newSalePrice;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates both cost and sale prices.
     *
     * @param newCostPrice the new cost price
     * @param newSalePrice the new sale price
     */
    public void updatePrices(ProductPrice newCostPrice, ProductPrice newSalePrice) {
        Objects.requireNonNull(newCostPrice, "New cost price cannot be null");
        Objects.requireNonNull(newSalePrice, "New sale price cannot be null");
        
        boolean changed = false;
        if (!this.costPrice.equals(newCostPrice)) {
            this.costPrice = newCostPrice;
            changed = true;
        }
        if (!this.salePrice.equals(newSalePrice)) {
            this.salePrice = newSalePrice;
            changed = true;
        }
        
        if (changed) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Increases the stock by a given quantity.
     *
     * @param quantity the quantity to add
     */
    public void increaseStock(Integer quantity) {
        this.stock = this.stock.add(quantity);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Decreases the stock by a given quantity.
     *
     * @param quantity the quantity to subtract
     * @throws IllegalArgumentException if the quantity exceeds available stock
     */
    public void decreaseStock(Integer quantity) {
        this.stock = this.stock.subtract(quantity);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the stock to a specific value.
     *
     * @param newStock the new stock quantity
     */
    public void updateStock(ProductStock newStock) {
        Objects.requireNonNull(newStock, "New stock cannot be null");
        if (!this.stock.equals(newStock)) {
            this.stock = newStock;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the minimum stock threshold.
     *
     * @param newThreshold the new minimum stock threshold
     */
    public void updateMinimumStockThreshold(ProductStock newThreshold) {
        Objects.requireNonNull(newThreshold, "New threshold cannot be null");
        if (!this.minimumStockThreshold.equals(newThreshold)) {
            this.minimumStockThreshold = newThreshold;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the category.
     *
     * @param newCategoryId the new category identifier (can be null)
     */
    public void updateCategory(CategoryId newCategoryId) {
        if (!Objects.equals(this.categoryId, newCategoryId)) {
            this.categoryId = newCategoryId;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Checks if the stock is below the minimum threshold.
     *
     * @return true if stock is below threshold
     */
    public boolean isStockBelowThreshold() {
        return stock.isBelowThreshold(minimumStockThreshold);
    }

    /**
     * Checks if this product is new (not yet persisted).
     *
     * @return true if the product has no ID, false otherwise
     */
    public boolean isNew() {
        return id == null;
    }

    // Getters (no setters - controlled mutation through business methods)

    public ProductId getId() {
        return id;
    }

    public ProductName getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ProductPrice getCostPrice() {
        return costPrice;
    }

    public ProductPrice getSalePrice() {
        return salePrice;
    }

    public ProductStock getStock() {
        return stock;
    }

    public ProductStock getMinimumStockThreshold() {
        return minimumStockThreshold;
    }

    public CategoryId getCategoryId() {
        return categoryId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name=" + name +
                ", description='" + description + '\'' +
                ", costPrice=" + costPrice +
                ", salePrice=" + salePrice +
                ", stock=" + stock +
                ", minimumStockThreshold=" + minimumStockThreshold +
                ", categoryId=" + categoryId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
