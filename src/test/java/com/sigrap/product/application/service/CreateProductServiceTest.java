package com.sigrap.product.application.service;

import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.product.application.port.in.command.CreateProductCommand;
import com.sigrap.product.domain.model.*;
import com.sigrap.product.domain.port.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CreateProductService.
 * Tests use case orchestration with mocked repository port.
 */
@ExtendWith(MockitoExtension.class)
class CreateProductServiceTest {
    
    @Mock
    private ProductRepositoryPort productRepository;
    
    @InjectMocks
    private CreateProductService createProductService;
    
    @Test
    void shouldCreateProduct() {
        // Given
        CreateProductCommand command = new CreateProductCommand(
            "Laptop",
            "High-performance laptop",
            new BigDecimal("500.00"),
            new BigDecimal("800.00"),
            10,
            5,
            1L
        );
        
        when(productRepository.existsByName(any(ProductName.class))).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return new Product(
                new ProductId(1L),
                product.getName(),
                product.getDescription(),
                product.getCostPrice(),
                product.getSalePrice(),
                product.getStock(),
                product.getMinimumStockThreshold(),
                product.getCategoryId(),
                LocalDateTime.now(),
                LocalDateTime.now()
            );
        });
        
        // When
        Product result = createProductService.create(command);
        
        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Laptop", result.getName().value());
        assertEquals("High-performance laptop", result.getDescription());
        assertEquals(new BigDecimal("500.00"), result.getCostPrice().value());
        assertEquals(new BigDecimal("800.00"), result.getSalePrice().value());
        assertEquals(10, result.getStock().value());
        assertEquals(5, result.getMinimumStockThreshold().value());
        assertEquals(1L, result.getCategoryId().value());
        
        verify(productRepository).existsByName(any(ProductName.class));
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    void shouldCreateProductWithoutCategory() {
        // Given
        CreateProductCommand command = new CreateProductCommand(
            "Mouse",
            "Wireless mouse",
            new BigDecimal("10.00"),
            new BigDecimal("20.00"),
            50,
            10,
            null
        );
        
        when(productRepository.existsByName(any(ProductName.class))).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return new Product(
                new ProductId(2L),
                product.getName(),
                product.getDescription(),
                product.getCostPrice(),
                product.getSalePrice(),
                product.getStock(),
                product.getMinimumStockThreshold(),
                product.getCategoryId(),
                LocalDateTime.now(),
                LocalDateTime.now()
            );
        });
        
        // When
        Product result = createProductService.create(command);
        
        // Then
        assertNotNull(result);
        assertNull(result.getCategoryId());
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    void shouldThrowExceptionWhenNameExists() {
        // Given
        CreateProductCommand command = new CreateProductCommand(
            "Existing Product",
            "Description",
            new BigDecimal("100.00"),
            new BigDecimal("150.00"),
            20,
            5,
            null
        );
        
        when(productRepository.existsByName(any(ProductName.class))).thenReturn(true);
        
        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createProductService.create(command)
        );
        
        assertTrue(exception.getMessage().contains("already exists"));
        verify(productRepository).existsByName(any(ProductName.class));
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    void shouldThrowExceptionForInvalidName() {
        // Given
        CreateProductCommand command = new CreateProductCommand(
            "",
            "Description",
            new BigDecimal("100.00"),
            new BigDecimal("150.00"),
            20,
            5,
            null
        );
        
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> createProductService.create(command));
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    void shouldThrowExceptionForInvalidPrice() {
        // Given
        CreateProductCommand command = new CreateProductCommand(
            "Product",
            "Description",
            new BigDecimal("-10.00"),
            new BigDecimal("150.00"),
            20,
            5,
            null
        );
        
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> createProductService.create(command));
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    void shouldThrowExceptionForInvalidStock() {
        // Given
        CreateProductCommand command = new CreateProductCommand(
            "Product",
            "Description",
            new BigDecimal("100.00"),
            new BigDecimal("150.00"),
            -5,
            5,
            null
        );
        
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> createProductService.create(command));
        verify(productRepository, never()).save(any(Product.class));
    }
}
