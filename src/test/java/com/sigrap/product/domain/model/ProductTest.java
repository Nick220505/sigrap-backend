package com.sigrap.product.domain.model;

import com.sigrap.category.domain.model.CategoryId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Product domain entity.
 * Pure unit tests with no Spring context, testing business logic and invariants.
 */
class ProductTest {

    @Test
    void shouldCreateNewProductWithoutId() {
        // Given
        ProductName name = new ProductName("Laptop");
        String description = "High-performance laptop";
        ProductPrice costPrice = new ProductPrice(new BigDecimal("500.00"));
        ProductPrice salePrice = new ProductPrice(new BigDecimal("800.00"));
        ProductStock stock = new ProductStock(10);
        ProductStock threshold = new ProductStock(5);
        CategoryId categoryId = new CategoryId(1L);

        // When
        Product product = new Product(name, description, costPrice, salePrice, 
                                     stock, threshold, categoryId);

        // Then
        assertNotNull(product);
        assertNull(product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(costPrice, product.getCostPrice());
        assertEquals(salePrice, product.getSalePrice());
        assertEquals(stock, product.getStock());
        assertEquals(threshold, product.getMinimumStockThreshold());
        assertEquals(categoryId, product.getCategoryId());
        assertNotNull(product.getCreatedAt());
        assertNotNull(product.getUpdatedAt());
        assertTrue(product.isNew());
    }

    @Test
    void shouldCreateProductWithAllFields() {
        // Given
        ProductId id = new ProductId(1L);
        ProductName name = new ProductName("Mouse");
        String description = "Wireless mouse";
        ProductPrice costPrice = new ProductPrice(new BigDecimal("10.00"));
        ProductPrice salePrice = new ProductPrice(new BigDecimal("20.00"));
        ProductStock stock = new ProductStock(50);
        ProductStock threshold = new ProductStock(10);
        CategoryId categoryId = new CategoryId(2L);
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        // When
        Product product = new Product(id, name, description, costPrice, salePrice,
                                     stock, threshold, categoryId, createdAt, updatedAt);

        // Then
        assertNotNull(product);
        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(costPrice, product.getCostPrice());
        assertEquals(salePrice, product.getSalePrice());
        assertEquals(stock, product.getStock());
        assertEquals(threshold, product.getMinimumStockThreshold());
        assertEquals(categoryId, product.getCategoryId());
        assertEquals(createdAt, product.getCreatedAt());
        assertEquals(updatedAt, product.getUpdatedAt());
        assertFalse(product.isNew());
    }

    @Test
    void shouldThrowExceptionForNullName() {
        // When & Then
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new Product(null, "Description", 
                new ProductPrice(BigDecimal.TEN), 
                new ProductPrice(BigDecimal.TEN),
                new ProductStock(10), 
                new ProductStock(5), 
                null)
        );
        assertEquals("Product name cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNullCostPrice() {
        // When & Then
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new Product(new ProductName("Test"), "Description", 
                null, 
                new ProductPrice(BigDecimal.TEN),
                new ProductStock(10), 
                new ProductStock(5), 
                null)
        );
        assertEquals("Cost price cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNullSalePrice() {
        // When & Then
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new Product(new ProductName("Test"), "Description", 
                new ProductPrice(BigDecimal.TEN), 
                null,
                new ProductStock(10), 
                new ProductStock(5), 
                null)
        );
        assertEquals("Sale price cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNullStock() {
        // When & Then
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new Product(new ProductName("Test"), "Description", 
                new ProductPrice(BigDecimal.TEN), 
                new ProductPrice(BigDecimal.TEN),
                null, 
                new ProductStock(5), 
                null)
        );
        assertEquals("Stock cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNullMinimumStockThreshold() {
        // When & Then
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new Product(new ProductName("Test"), "Description", 
                new ProductPrice(BigDecimal.TEN), 
                new ProductPrice(BigDecimal.TEN),
                new ProductStock(10), 
                null, 
                null)
        );
        assertEquals("Minimum stock threshold cannot be null", exception.getMessage());
    }

    @Test
    void shouldAllowNullDescriptionAndCategory() {
        // Given
        ProductName name = new ProductName("Test Product");

        // When
        Product product = new Product(name, null, 
            new ProductPrice(BigDecimal.TEN), 
            new ProductPrice(BigDecimal.TEN),
            new ProductStock(10), 
            new ProductStock(5), 
            null);

        // Then
        assertNotNull(product);
        assertNull(product.getDescription());
        assertNull(product.getCategoryId());
    }

    @Test
    void shouldUpdateNameSuccessfully() {
        // Given
        Product product = createTestProduct();
        LocalDateTime originalUpdatedAt = product.getUpdatedAt();
        ProductName newName = new ProductName("Updated Name");

        // When
        product.updateName(newName);

        // Then
        assertEquals(newName, product.getName());
        assertTrue(product.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldNotUpdateTimestampWhenNameIsTheSame() {
        // Given
        ProductName name = new ProductName("Same Name");
        Product product = new Product(new ProductId(1L), name, "Description",
            new ProductPrice(BigDecimal.TEN), new ProductPrice(BigDecimal.TEN),
            new ProductStock(10), new ProductStock(5), null,
            LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1));
        LocalDateTime originalUpdatedAt = product.getUpdatedAt();

        // When
        product.updateName(name);

        // Then
        assertEquals(name, product.getName());
        assertEquals(originalUpdatedAt, product.getUpdatedAt());
    }

    @Test
    void shouldUpdateDescriptionSuccessfully() {
        // Given
        Product product = createTestProduct();
        LocalDateTime originalUpdatedAt = product.getUpdatedAt();
        String newDescription = "New Description";

        // When
        product.updateDescription(newDescription);

        // Then
        assertEquals(newDescription, product.getDescription());
        assertTrue(product.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldUpdateCostPriceSuccessfully() {
        // Given
        Product product = createTestProduct();
        LocalDateTime originalUpdatedAt = product.getUpdatedAt();
        ProductPrice newCostPrice = new ProductPrice(new BigDecimal("600.00"));

        // When
        product.updateCostPrice(newCostPrice);

        // Then
        assertEquals(newCostPrice, product.getCostPrice());
        assertTrue(product.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldUpdateSalePriceSuccessfully() {
        // Given
        Product product = createTestProduct();
        LocalDateTime originalUpdatedAt = product.getUpdatedAt();
        ProductPrice newSalePrice = new ProductPrice(new BigDecimal("900.00"));

        // When
        product.updateSalePrice(newSalePrice);

        // Then
        assertEquals(newSalePrice, product.getSalePrice());
        assertTrue(product.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldUpdateBothPricesSuccessfully() {
        // Given
        Product product = createTestProduct();
        LocalDateTime originalUpdatedAt = product.getUpdatedAt();
        ProductPrice newCostPrice = new ProductPrice(new BigDecimal("600.00"));
        ProductPrice newSalePrice = new ProductPrice(new BigDecimal("900.00"));

        // When
        product.updatePrices(newCostPrice, newSalePrice);

        // Then
        assertEquals(newCostPrice, product.getCostPrice());
        assertEquals(newSalePrice, product.getSalePrice());
        assertTrue(product.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldNotUpdateTimestampWhenPricesAreTheSame() {
        // Given
        ProductPrice costPrice = new ProductPrice(new BigDecimal("500.00"));
        ProductPrice salePrice = new ProductPrice(new BigDecimal("800.00"));
        Product product = new Product(new ProductId(1L), new ProductName("Test"), "Description",
            costPrice, salePrice, new ProductStock(10), new ProductStock(5), null,
            LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1));
        LocalDateTime originalUpdatedAt = product.getUpdatedAt();

        // When
        product.updatePrices(costPrice, salePrice);

        // Then
        assertEquals(originalUpdatedAt, product.getUpdatedAt());
    }

    @Test
    void shouldIncreaseStockSuccessfully() {
        // Given
        Product product = createTestProduct();
        LocalDateTime originalUpdatedAt = product.getUpdatedAt();
        Integer originalStock = product.getStock().value();

        // When
        product.increaseStock(20);

        // Then
        assertEquals(originalStock + 20, product.getStock().value());
        assertTrue(product.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldDecreaseStockSuccessfully() {
        // Given
        Product product = createTestProduct();
        LocalDateTime originalUpdatedAt = product.getUpdatedAt();
        Integer originalStock = product.getStock().value();

        // When
        product.decreaseStock(5);

        // Then
        assertEquals(originalStock - 5, product.getStock().value());
        assertTrue(product.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldThrowExceptionWhenDecreasingStockBelowZero() {
        // Given
        Product product = createTestProduct();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            product.decreaseStock(100)
        );
    }

    @Test
    void shouldUpdateStockSuccessfully() {
        // Given
        Product product = createTestProduct();
        LocalDateTime originalUpdatedAt = product.getUpdatedAt();
        ProductStock newStock = new ProductStock(50);

        // When
        product.updateStock(newStock);

        // Then
        assertEquals(newStock, product.getStock());
        assertTrue(product.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldUpdateMinimumStockThresholdSuccessfully() {
        // Given
        Product product = createTestProduct();
        LocalDateTime originalUpdatedAt = product.getUpdatedAt();
        ProductStock newThreshold = new ProductStock(15);

        // When
        product.updateMinimumStockThreshold(newThreshold);

        // Then
        assertEquals(newThreshold, product.getMinimumStockThreshold());
        assertTrue(product.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldUpdateCategorySuccessfully() {
        // Given
        Product product = createTestProduct();
        LocalDateTime originalUpdatedAt = product.getUpdatedAt();
        CategoryId newCategoryId = new CategoryId(5L);

        // When
        product.updateCategory(newCategoryId);

        // Then
        assertEquals(newCategoryId, product.getCategoryId());
        assertTrue(product.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldReturnTrueWhenStockIsBelowThreshold() {
        // Given
        Product product = new Product(new ProductName("Test"), "Description",
            new ProductPrice(BigDecimal.TEN), new ProductPrice(BigDecimal.TEN),
            new ProductStock(3), new ProductStock(5), null);

        // When
        boolean result = product.isStockBelowThreshold();

        // Then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenStockIsAboveThreshold() {
        // Given
        Product product = new Product(new ProductName("Test"), "Description",
            new ProductPrice(BigDecimal.TEN), new ProductPrice(BigDecimal.TEN),
            new ProductStock(10), new ProductStock(5), null);

        // When
        boolean result = product.isStockBelowThreshold();

        // Then
        assertFalse(result);
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        // Given
        ProductId id = new ProductId(1L);
        Product product1 = new Product(id, new ProductName("Product 1"), "Description 1",
            new ProductPrice(BigDecimal.TEN), new ProductPrice(BigDecimal.TEN),
            new ProductStock(10), new ProductStock(5), null,
            LocalDateTime.now(), LocalDateTime.now());
        Product product2 = new Product(id, new ProductName("Product 2"), "Description 2",
            new ProductPrice(BigDecimal.ONE), new ProductPrice(BigDecimal.ONE),
            new ProductStock(20), new ProductStock(10), null,
            LocalDateTime.now(), LocalDateTime.now());

        // When & Then
        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        // Given
        Product product1 = new Product(new ProductId(1L), new ProductName("Product"), "Description",
            new ProductPrice(BigDecimal.TEN), new ProductPrice(BigDecimal.TEN),
            new ProductStock(10), new ProductStock(5), null,
            LocalDateTime.now(), LocalDateTime.now());
        Product product2 = new Product(new ProductId(2L), new ProductName("Product"), "Description",
            new ProductPrice(BigDecimal.TEN), new ProductPrice(BigDecimal.TEN),
            new ProductStock(10), new ProductStock(5), null,
            LocalDateTime.now(), LocalDateTime.now());

        // When & Then
        assertNotEquals(product1, product2);
    }

    @Test
    void shouldHaveProperToStringRepresentation() {
        // Given
        Product product = createTestProduct();

        // When
        String result = product.toString();

        // Then
        assertTrue(result.contains("Product{"));
        assertTrue(result.contains("id="));
        assertTrue(result.contains("name="));
        assertTrue(result.contains("Laptop"));
    }

    @Test
    void shouldIdentifyNewProductCorrectly() {
        // Given
        Product newProduct = new Product(new ProductName("New Product"), "Description",
            new ProductPrice(BigDecimal.TEN), new ProductPrice(BigDecimal.TEN),
            new ProductStock(10), new ProductStock(5), null);
        Product existingProduct = new Product(new ProductId(1L), new ProductName("Existing"), "Description",
            new ProductPrice(BigDecimal.TEN), new ProductPrice(BigDecimal.TEN),
            new ProductStock(10), new ProductStock(5), null,
            LocalDateTime.now(), LocalDateTime.now());

        // When & Then
        assertTrue(newProduct.isNew());
        assertFalse(existingProduct.isNew());
    }

    // Helper method to create a test product
    private Product createTestProduct() {
        return new Product(
            new ProductId(1L),
            new ProductName("Laptop"),
            "High-performance laptop",
            new ProductPrice(new BigDecimal("500.00")),
            new ProductPrice(new BigDecimal("800.00")),
            new ProductStock(10),
            new ProductStock(5),
            new CategoryId(1L),
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(1)
        );
    }
}
