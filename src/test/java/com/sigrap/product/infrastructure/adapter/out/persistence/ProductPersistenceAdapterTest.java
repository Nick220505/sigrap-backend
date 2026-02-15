package com.sigrap.product.infrastructure.adapter.out.persistence;

import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.product.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ProductPersistenceAdapter.
 * Tests the adapter with a real database (H2) using Spring Boot test context.
 * 
 * These tests verify:
 * - Correct mapping between domain and JPA entities
 * - Database operations work as expected
 * - The adapter properly implements the repository port
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductPersistenceAdapterTest {

    @Autowired
    private ProductPersistenceAdapter adapter;

    @Autowired
    private ProductJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        jpaRepository.deleteAll();
    }

    @Test
    void shouldSaveNewProduct() {
        // Given
        Product product = new Product(
            new ProductName("Laptop"),
            "High-performance laptop",
            new ProductPrice(new BigDecimal("800.00")),
            new ProductPrice(new BigDecimal("1200.00")),
            new ProductStock(50),
            new ProductStock(10),
            new CategoryId(1L)
        );

        // When
        Product saved = adapter.save(product);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Laptop", saved.getName().value());
        assertEquals("High-performance laptop", saved.getDescription());
        assertEquals(0, new BigDecimal("800.00").compareTo(saved.getCostPrice().value()));
        assertEquals(0, new BigDecimal("1200.00").compareTo(saved.getSalePrice().value()));
        assertEquals(50, saved.getStock().value());
        assertEquals(10, saved.getMinimumStockThreshold().value());
        assertEquals(1L, saved.getCategoryId().value());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldUpdateExistingProduct() {
        // Given - save initial product
        Product product = new Product(
            new ProductName("Mouse"),
            "Wireless mouse",
            new ProductPrice(new BigDecimal("15.00")),
            new ProductPrice(new BigDecimal("25.00")),
            new ProductStock(100),
            new ProductStock(20),
            new CategoryId(1L)
        );
        Product saved = adapter.save(product);
        
        // When - update the product
        Product updated = new Product(
            saved.getId(),
            new ProductName("Gaming Mouse"),
            "RGB gaming mouse",
            new ProductPrice(new BigDecimal("20.00")),
            new ProductPrice(new BigDecimal("35.00")),
            new ProductStock(80),
            new ProductStock(15),
            new CategoryId(1L),
            saved.getCreatedAt(),
            saved.getUpdatedAt()
        );
        Product result = adapter.save(updated);

        // Then
        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("Gaming Mouse", result.getName().value());
        assertEquals("RGB gaming mouse", result.getDescription());
        assertEquals(0, new BigDecimal("20.00").compareTo(result.getCostPrice().value()));
        assertEquals(0, new BigDecimal("35.00").compareTo(result.getSalePrice().value()));
        assertEquals(80, result.getStock().value());
        assertEquals(15, result.getMinimumStockThreshold().value());
    }

    @Test
    void shouldFindProductById() {
        // Given
        Product product = new Product(
            new ProductName("Keyboard"),
            "Mechanical keyboard",
            new ProductPrice(new BigDecimal("50.00")),
            new ProductPrice(new BigDecimal("80.00")),
            new ProductStock(30),
            new ProductStock(5),
            new CategoryId(1L)
        );
        Product saved = adapter.save(product);

        // When
        Optional<Product> found = adapter.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("Keyboard", found.get().getName().value());
        assertEquals("Mechanical keyboard", found.get().getDescription());
    }

    @Test
    void shouldReturnEmptyWhenProductNotFound() {
        // Given
        ProductId nonExistentId = new ProductId(999L);

        // When
        Optional<Product> found = adapter.findById(nonExistentId);

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindAllProducts() {
        // Given
        adapter.save(new Product(
            new ProductName("Product 1"),
            "Description 1",
            new ProductPrice(new BigDecimal("10.00")),
            new ProductPrice(new BigDecimal("15.00")),
            new ProductStock(100),
            new ProductStock(10),
            new CategoryId(1L)
        ));
        adapter.save(new Product(
            new ProductName("Product 2"),
            "Description 2",
            new ProductPrice(new BigDecimal("20.00")),
            new ProductPrice(new BigDecimal("30.00")),
            new ProductStock(50),
            new ProductStock(5),
            new CategoryId(1L)
        ));
        adapter.save(new Product(
            new ProductName("Product 3"),
            "Description 3",
            new ProductPrice(new BigDecimal("30.00")),
            new ProductPrice(new BigDecimal("45.00")),
            new ProductStock(25),
            new ProductStock(3),
            new CategoryId(2L)
        ));

        // When
        List<Product> products = adapter.findAll();

        // Then
        assertNotNull(products);
        assertEquals(3, products.size());
        assertTrue(products.stream().anyMatch(p -> p.getName().value().equals("Product 1")));
        assertTrue(products.stream().anyMatch(p -> p.getName().value().equals("Product 2")));
        assertTrue(products.stream().anyMatch(p -> p.getName().value().equals("Product 3")));
    }

    @Test
    void shouldReturnEmptyListWhenNoProducts() {
        // When
        List<Product> products = adapter.findAll();

        // Then
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void shouldFindProductsByCategoryId() {
        // Given
        CategoryId categoryId1 = new CategoryId(1L);
        CategoryId categoryId2 = new CategoryId(2L);
        
        adapter.save(new Product(
            new ProductName("Product A"),
            "Category 1 product",
            new ProductPrice(new BigDecimal("10.00")),
            new ProductPrice(new BigDecimal("15.00")),
            new ProductStock(100),
            new ProductStock(10),
            categoryId1
        ));
        adapter.save(new Product(
            new ProductName("Product B"),
            "Category 1 product",
            new ProductPrice(new BigDecimal("20.00")),
            new ProductPrice(new BigDecimal("30.00")),
            new ProductStock(50),
            new ProductStock(5),
            categoryId1
        ));
        adapter.save(new Product(
            new ProductName("Product C"),
            "Category 2 product",
            new ProductPrice(new BigDecimal("30.00")),
            new ProductPrice(new BigDecimal("45.00")),
            new ProductStock(25),
            new ProductStock(3),
            categoryId2
        ));

        // When
        List<Product> category1Products = adapter.findByCategoryId(categoryId1);
        List<Product> category2Products = adapter.findByCategoryId(categoryId2);

        // Then
        assertEquals(2, category1Products.size());
        assertTrue(category1Products.stream().allMatch(p -> p.getCategoryId().equals(categoryId1)));
        
        assertEquals(1, category2Products.size());
        assertTrue(category2Products.stream().allMatch(p -> p.getCategoryId().equals(categoryId2)));
    }

    @Test
    void shouldReturnEmptyListWhenNoCategoryProducts() {
        // Given
        CategoryId nonExistentCategoryId = new CategoryId(999L);

        // When
        List<Product> products = adapter.findByCategoryId(nonExistentCategoryId);

        // Then
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void shouldReturnTrueWhenProductNameExists() {
        // Given
        adapter.save(new Product(
            new ProductName("Unique Product"),
            "Description",
            new ProductPrice(new BigDecimal("10.00")),
            new ProductPrice(new BigDecimal("15.00")),
            new ProductStock(100),
            new ProductStock(10),
            new CategoryId(1L)
        ));

        // When
        boolean exists = adapter.existsByName(new ProductName("Unique Product"));

        // Then
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenProductNameDoesNotExist() {
        // Given
        ProductName nonExistentName = new ProductName("Non-existent Product");

        // When
        boolean exists = adapter.existsByName(nonExistentName);

        // Then
        assertFalse(exists);
    }

    @Test
    void shouldReturnTrueWhenProductNameExistsForDifferentId() {
        // Given
        Product product1 = adapter.save(new Product(
            new ProductName("Duplicate Name"),
            "First product",
            new ProductPrice(new BigDecimal("10.00")),
            new ProductPrice(new BigDecimal("15.00")),
            new ProductStock(100),
            new ProductStock(10),
            new CategoryId(1L)
        ));
        
        adapter.save(new Product(
            new ProductName("Another Product"),
            "Second product",
            new ProductPrice(new BigDecimal("20.00")),
            new ProductPrice(new BigDecimal("30.00")),
            new ProductStock(50),
            new ProductStock(5),
            new CategoryId(1L)
        ));

        // When
        boolean exists = adapter.existsByNameAndIdNot(
            new ProductName("Another Product"),
            product1.getId()
        );

        // Then
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenProductNameOnlyExistsForExcludedId() {
        // Given
        Product product = adapter.save(new Product(
            new ProductName("Unique Name"),
            "Description",
            new ProductPrice(new BigDecimal("10.00")),
            new ProductPrice(new BigDecimal("15.00")),
            new ProductStock(100),
            new ProductStock(10),
            new CategoryId(1L)
        ));

        // When
        boolean exists = adapter.existsByNameAndIdNot(
            new ProductName("Unique Name"),
            product.getId()
        );

        // Then
        assertFalse(exists);
    }

    @Test
    void shouldDeleteProductById() {
        // Given
        Product product = new Product(
            new ProductName("Temporary Product"),
            "To be deleted",
            new ProductPrice(new BigDecimal("10.00")),
            new ProductPrice(new BigDecimal("15.00")),
            new ProductStock(100),
            new ProductStock(10),
            new CategoryId(1L)
        );
        Product saved = adapter.save(product);
        ProductId savedId = saved.getId();

        // When
        adapter.deleteById(savedId);

        // Then
        Optional<Product> found = adapter.findById(savedId);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldDeleteMultipleProductsById() {
        // Given
        Product prod1 = adapter.save(new Product(
            new ProductName("Product A"),
            "Description A",
            new ProductPrice(new BigDecimal("10.00")),
            new ProductPrice(new BigDecimal("15.00")),
            new ProductStock(100),
            new ProductStock(10),
            new CategoryId(1L)
        ));
        Product prod2 = adapter.save(new Product(
            new ProductName("Product B"),
            "Description B",
            new ProductPrice(new BigDecimal("20.00")),
            new ProductPrice(new BigDecimal("30.00")),
            new ProductStock(50),
            new ProductStock(5),
            new CategoryId(1L)
        ));
        Product prod3 = adapter.save(new Product(
            new ProductName("Product C"),
            "Description C",
            new ProductPrice(new BigDecimal("30.00")),
            new ProductPrice(new BigDecimal("45.00")),
            new ProductStock(25),
            new ProductStock(3),
            new CategoryId(1L)
        ));
        
        List<ProductId> idsToDelete = List.of(prod1.getId(), prod2.getId());

        // When
        adapter.deleteAllById(idsToDelete);

        // Then
        assertTrue(adapter.findById(prod1.getId()).isEmpty());
        assertTrue(adapter.findById(prod2.getId()).isEmpty());
        assertTrue(adapter.findById(prod3.getId()).isPresent());
    }

    @Test
    void shouldPreserveTimestampsWhenSaving() {
        // Given
        Product product = new Product(
            new ProductName("Test Product"),
            "Test description",
            new ProductPrice(new BigDecimal("10.00")),
            new ProductPrice(new BigDecimal("15.00")),
            new ProductStock(100),
            new ProductStock(10),
            new CategoryId(1L)
        );

        // When
        Product saved = adapter.save(product);

        // Then
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldHandleProductWithNullDescription() {
        // Given
        Product product = new Product(
            new ProductName("No Description Product"),
            null,
            new ProductPrice(new BigDecimal("10.00")),
            new ProductPrice(new BigDecimal("15.00")),
            new ProductStock(100),
            new ProductStock(10),
            new CategoryId(1L)
        );

        // When
        Product saved = adapter.save(product);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("No Description Product", saved.getName().value());
        assertNull(saved.getDescription());
    }

    @Test
    void shouldMapAllFieldsCorrectlyBetweenDomainAndJpa() {
        // Given
        Product product = new Product(
            new ProductName("Complete Product"),
            "Full description",
            new ProductPrice(new BigDecimal("100.50")),
            new ProductPrice(new BigDecimal("150.75")),
            new ProductStock(200),
            new ProductStock(25),
            new CategoryId(5L)
        );

        // When
        Product saved = adapter.save(product);
        Optional<Product> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        Product result = retrieved.get();
        assertEquals(saved.getId().value(), result.getId().value());
        assertEquals(saved.getName().value(), result.getName().value());
        assertEquals(saved.getDescription(), result.getDescription());
        assertEquals(saved.getCostPrice().value(), result.getCostPrice().value());
        assertEquals(saved.getSalePrice().value(), result.getSalePrice().value());
        assertEquals(saved.getStock().value(), result.getStock().value());
        assertEquals(saved.getMinimumStockThreshold().value(), result.getMinimumStockThreshold().value());
        assertEquals(saved.getCategoryId().value(), result.getCategoryId().value());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void shouldEnforceUniqueNameConstraint() {
        // Given
        adapter.save(new Product(
            new ProductName("Unique Name"),
            "First",
            new ProductPrice(new BigDecimal("10.00")),
            new ProductPrice(new BigDecimal("15.00")),
            new ProductStock(100),
            new ProductStock(10),
            new CategoryId(1L)
        ));

        // When & Then
        assertThrows(Exception.class, () -> {
            adapter.save(new Product(
                new ProductName("Unique Name"),
                "Second",
                new ProductPrice(new BigDecimal("20.00")),
                new ProductPrice(new BigDecimal("30.00")),
                new ProductStock(50),
                new ProductStock(5),
                new CategoryId(1L)
            ));
        });
    }

    @Test
    void shouldHandleProductsWithDifferentStockLevels() {
        // Given
        Product lowStock = new Product(
            new ProductName("Low Stock Product"),
            "Below threshold",
            new ProductPrice(new BigDecimal("10.00")),
            new ProductPrice(new BigDecimal("15.00")),
            new ProductStock(5),
            new ProductStock(10),
            new CategoryId(1L)
        );
        
        Product highStock = new Product(
            new ProductName("High Stock Product"),
            "Above threshold",
            new ProductPrice(new BigDecimal("10.00")),
            new ProductPrice(new BigDecimal("15.00")),
            new ProductStock(1000),
            new ProductStock(10),
            new CategoryId(1L)
        );

        // When
        Product savedLow = adapter.save(lowStock);
        Product savedHigh = adapter.save(highStock);

        // Then
        assertEquals(5, savedLow.getStock().value());
        assertTrue(savedLow.isStockBelowThreshold());
        
        assertEquals(1000, savedHigh.getStock().value());
        assertFalse(savedHigh.isStockBelowThreshold());
    }

    @Test
    void shouldHandleProductsWithZeroStock() {
        // Given
        Product product = new Product(
            new ProductName("Out of Stock Product"),
            "No stock",
            new ProductPrice(new BigDecimal("10.00")),
            new ProductPrice(new BigDecimal("15.00")),
            new ProductStock(0),
            new ProductStock(10),
            new CategoryId(1L)
        );

        // When
        Product saved = adapter.save(product);

        // Then
        assertNotNull(saved);
        assertEquals(0, saved.getStock().value());
        assertTrue(saved.isStockBelowThreshold());
    }

    @Test
    void shouldHandleLargePriceValues() {
        // Given
        Product product = new Product(
            new ProductName("Expensive Product"),
            "High-value item",
            new ProductPrice(new BigDecimal("99999.99")),
            new ProductPrice(new BigDecimal("149999.99")),
            new ProductStock(1),
            new ProductStock(1),
            new CategoryId(1L)
        );

        // When
        Product saved = adapter.save(product);
        Optional<Product> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(0, new BigDecimal("99999.99").compareTo(retrieved.get().getCostPrice().value()));
        assertEquals(0, new BigDecimal("149999.99").compareTo(retrieved.get().getSalePrice().value()));
    }
}
