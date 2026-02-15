package com.sigrap.product.application.service;

import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.product.domain.model.*;
import com.sigrap.product.domain.port.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GetProductService.
 * Tests retrieval operations with mocked repository port.
 */
@ExtendWith(MockitoExtension.class)
class GetProductServiceTest {
    
    @Mock
    private ProductRepositoryPort productRepository;
    
    @InjectMocks
    private GetProductService getProductService;
    
    private Product createTestProduct(Long id, String name) {
        return new Product(
            new ProductId(id),
            new ProductName(name),
            "Test description",
            new ProductPrice(new BigDecimal("100.00")),
            new ProductPrice(new BigDecimal("150.00")),
            new ProductStock(20),
            new ProductStock(5),
            new CategoryId(1L),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
    
    @Test
    void shouldGetProductById() {
        // Given
        ProductId id = new ProductId(1L);
        Product product = createTestProduct(1L, "Laptop");
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        
        // When
        Product result = getProductService.getById(id);
        
        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Laptop", result.getName().value());
        verify(productRepository).findById(id);
    }
    
    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        ProductId id = new ProductId(999L);
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        
        // When/Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> getProductService.getById(id)
        );
        
        assertTrue(exception.getMessage().contains("999"));
        verify(productRepository).findById(id);
    }
    
    @Test
    void shouldFindProductById() {
        // Given
        ProductId id = new ProductId(1L);
        Product product = createTestProduct(1L, "Mouse");
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        
        // When
        Optional<Product> result = getProductService.findById(id);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals("Mouse", result.get().getName().value());
        verify(productRepository).findById(id);
    }
    
    @Test
    void shouldReturnEmptyWhenProductNotFoundByFindById() {
        // Given
        ProductId id = new ProductId(999L);
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        
        // When
        Optional<Product> result = getProductService.findById(id);
        
        // Then
        assertTrue(result.isEmpty());
        verify(productRepository).findById(id);
    }
    
    @Test
    void shouldGetAllProducts() {
        // Given
        List<Product> products = List.of(
            createTestProduct(1L, "Laptop"),
            createTestProduct(2L, "Mouse"),
            createTestProduct(3L, "Keyboard")
        );
        when(productRepository.findAll()).thenReturn(products);
        
        // When
        List<Product> result = getProductService.getAll();
        
        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Laptop", result.get(0).getName().value());
        assertEquals("Mouse", result.get(1).getName().value());
        assertEquals("Keyboard", result.get(2).getName().value());
        verify(productRepository).findAll();
    }
    
    @Test
    void shouldReturnEmptyListWhenNoProducts() {
        // Given
        when(productRepository.findAll()).thenReturn(List.of());
        
        // When
        List<Product> result = getProductService.getAll();
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository).findAll();
    }
    
    @Test
    void shouldGetProductsByCategoryId() {
        // Given
        CategoryId categoryId = new CategoryId(1L);
        List<Product> products = List.of(
            createTestProduct(1L, "Laptop"),
            createTestProduct(2L, "Mouse")
        );
        when(productRepository.findByCategoryId(categoryId)).thenReturn(products);
        
        // When
        List<Product> result = getProductService.getByCategoryId(categoryId);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository).findByCategoryId(categoryId);
    }
    
    @Test
    void shouldReturnEmptyListWhenNoCategoryProducts() {
        // Given
        CategoryId categoryId = new CategoryId(999L);
        when(productRepository.findByCategoryId(categoryId)).thenReturn(List.of());
        
        // When
        List<Product> result = getProductService.getByCategoryId(categoryId);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository).findByCategoryId(categoryId);
    }
}
