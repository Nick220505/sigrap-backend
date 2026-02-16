package com.sigrap.product.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.category.domain.model.Category;
import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.category.domain.model.CategoryName;
import com.sigrap.category.domain.port.CategoryRepositoryPort;
import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.product.application.port.in.command.UpdateProductCommand;
import com.sigrap.product.domain.model.*;
import com.sigrap.product.domain.port.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UpdateProductService.
 * Tests update operations with mocked repository port.
 */
@ExtendWith(MockitoExtension.class)
class UpdateProductServiceTest {
    
    @Mock
    private ProductRepositoryPort productRepository;
    
    @Mock
    private CategoryRepositoryPort categoryRepository;
    
    @Mock
    private EventPublisherPort eventPublisher;
    
    @InjectMocks
    private UpdateProductService updateProductService;
    
    private Product createTestProduct(Long id, String name) {
        return new Product(
            new ProductId(id),
            new ProductName(name),
            "Old description",
            new ProductPrice(new BigDecimal("100.00")),
            new ProductPrice(new BigDecimal("150.00")),
            new ProductStock(20),
            new ProductStock(5),
            new CategoryId(1L),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
    
    private Category createTestCategory(Long id) {
        return new Category(
            new CategoryId(id),
            new CategoryName("Test Category"),
            "Test Description",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
    
    @Test
    void shouldUpdateProduct() {
        // Given
        ProductId id = new ProductId(1L);
        Product existingProduct = createTestProduct(1L, "Old Name");
        UpdateProductCommand command = new UpdateProductCommand(
            "New Name",
            "New description",
            new BigDecimal("120.00"),
            new BigDecimal("180.00"),
            30,
            10,
            2L
        );
        
        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByNameAndIdNot(any(ProductName.class), eq(id))).thenReturn(false);
        when(categoryRepository.findById(new CategoryId(2L))).thenReturn(Optional.of(createTestCategory(2L)));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Product result = updateProductService.update(id, command);
        
        // Then
        assertNotNull(result);
        assertEquals("New Name", result.getName().value());
        assertEquals("New description", result.getDescription());
        assertEquals(new BigDecimal("120.00"), result.getCostPrice().value());
        assertEquals(new BigDecimal("180.00"), result.getSalePrice().value());
        assertEquals(30, result.getStock().value());
        assertEquals(10, result.getMinimumStockThreshold().value());
        assertEquals(2L, result.getCategoryId().value());
        
        verify(productRepository).findById(id);
        verify(productRepository).existsByNameAndIdNot(any(ProductName.class), eq(id));
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    void shouldUpdateProductWithSameName() {
        // Given
        ProductId id = new ProductId(1L);
        Product existingProduct = createTestProduct(1L, "Same Name");
        UpdateProductCommand command = new UpdateProductCommand(
            "Same Name",
            "Updated description",
            new BigDecimal("120.00"),
            new BigDecimal("180.00"),
            30,
            10,
            1L
        );
        
        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(new CategoryId(1L))).thenReturn(Optional.of(createTestCategory(1L)));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Product result = updateProductService.update(id, command);
        
        // Then
        assertNotNull(result);
        assertEquals("Same Name", result.getName().value());
        verify(productRepository).findById(id);
        verify(productRepository, never()).existsByNameAndIdNot(any(), any());
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    void shouldUpdateProductWithoutCategory() {
        // Given
        ProductId id = new ProductId(1L);
        Product existingProduct = createTestProduct(1L, "Product");
        UpdateProductCommand command = new UpdateProductCommand(
            "Updated Product",
            "Description",
            new BigDecimal("100.00"),
            new BigDecimal("150.00"),
            20,
            5,
            null
        );
        
        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByNameAndIdNot(any(ProductName.class), eq(id))).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Product result = updateProductService.update(id, command);
        
        // Then
        assertNotNull(result);
        assertNull(result.getCategoryId());
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        ProductId id = new ProductId(999L);
        UpdateProductCommand command = new UpdateProductCommand(
            "Name",
            "Description",
            new BigDecimal("100.00"),
            new BigDecimal("150.00"),
            20,
            5,
            1L
        );
        
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        
        // When/Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> updateProductService.update(id, command)
        );
        
        assertTrue(exception.getMessage().contains("999"));
        verify(productRepository).findById(id);
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    void shouldThrowExceptionWhenNameConflicts() {
        // Given
        ProductId id = new ProductId(1L);
        Product existingProduct = createTestProduct(1L, "Old Name");
        UpdateProductCommand command = new UpdateProductCommand(
            "Conflicting Name",
            "Description",
            new BigDecimal("100.00"),
            new BigDecimal("150.00"),
            20,
            5,
            1L
        );
        
        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByNameAndIdNot(any(ProductName.class), eq(id))).thenReturn(true);
        
        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> updateProductService.update(id, command)
        );
        
        assertTrue(exception.getMessage().contains("already exists"));
        verify(productRepository).findById(id);
        verify(productRepository).existsByNameAndIdNot(any(ProductName.class), eq(id));
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    void shouldThrowExceptionForInvalidName() {
        // Given
        ProductId id = new ProductId(1L);
        Product existingProduct = createTestProduct(1L, "Product");
        UpdateProductCommand command = new UpdateProductCommand(
            "",
            "Description",
            new BigDecimal("100.00"),
            new BigDecimal("150.00"),
            20,
            5,
            1L
        );
        
        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> updateProductService.update(id, command));
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    void shouldThrowExceptionForInvalidPrice() {
        // Given
        ProductId id = new ProductId(1L);
        Product existingProduct = createTestProduct(1L, "Product");
        UpdateProductCommand command = new UpdateProductCommand(
            "Product",
            "Description",
            new BigDecimal("-10.00"),
            new BigDecimal("150.00"),
            20,
            5,
            1L
        );
        
        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> updateProductService.update(id, command));
        verify(productRepository, never()).save(any(Product.class));
    }
}
