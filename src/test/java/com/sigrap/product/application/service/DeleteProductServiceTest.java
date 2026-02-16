package com.sigrap.product.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
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
 * Unit tests for DeleteProductService.
 * Tests deletion operations with mocked repository port.
 */
@ExtendWith(MockitoExtension.class)
class DeleteProductServiceTest {
    
    @Mock
    private ProductRepositoryPort productRepository;
    
    @Mock
    private EventPublisherPort eventPublisher;
    
    @InjectMocks
    private DeleteProductService deleteProductService;
    
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
    void shouldDeleteProduct() {
        // Given
        ProductId id = new ProductId(1L);
        Product product = createTestProduct(1L, "Laptop");
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).deleteById(id);
        
        // When
        deleteProductService.delete(id);
        
        // Then
        verify(productRepository).findById(id);
        verify(productRepository).deleteById(id);
    }
    
    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        ProductId id = new ProductId(999L);
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        
        // When/Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> deleteProductService.delete(id)
        );
        
        assertTrue(exception.getMessage().contains("999"));
        verify(productRepository).findById(id);
        verify(productRepository, never()).deleteById(any());
    }
    
    @Test
    void shouldDeleteAllProducts() {
        // Given
        ProductId id1 = new ProductId(1L);
        ProductId id2 = new ProductId(2L);
        ProductId id3 = new ProductId(3L);
        List<ProductId> ids = List.of(id1, id2, id3);
        
        when(productRepository.findById(id1)).thenReturn(Optional.of(createTestProduct(1L, "Product1")));
        when(productRepository.findById(id2)).thenReturn(Optional.of(createTestProduct(2L, "Product2")));
        when(productRepository.findById(id3)).thenReturn(Optional.of(createTestProduct(3L, "Product3")));
        doNothing().when(productRepository).deleteAllById(ids);
        
        // When
        deleteProductService.deleteAll(ids);
        
        // Then
        verify(productRepository).findById(id1);
        verify(productRepository).findById(id2);
        verify(productRepository).findById(id3);
        verify(productRepository).deleteAllById(ids);
    }
    
    @Test
    void shouldThrowExceptionWhenAnyProductNotFoundInBatch() {
        // Given
        ProductId id1 = new ProductId(1L);
        ProductId id2 = new ProductId(999L);
        ProductId id3 = new ProductId(3L);
        List<ProductId> ids = List.of(id1, id2, id3);
        
        when(productRepository.findById(id1)).thenReturn(Optional.of(createTestProduct(1L, "Product1")));
        when(productRepository.findById(id2)).thenReturn(Optional.empty());
        
        // When/Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> deleteProductService.deleteAll(ids)
        );
        
        assertTrue(exception.getMessage().contains("999"));
        verify(productRepository).findById(id1);
        verify(productRepository).findById(id2);
        verify(productRepository, never()).findById(id3);
        verify(productRepository, never()).deleteAllById(any());
    }
    
    @Test
    void shouldDeleteAllWithEmptyList() {
        // Given
        List<ProductId> ids = List.of();
        doNothing().when(productRepository).deleteAllById(ids);
        
        // When
        deleteProductService.deleteAll(ids);
        
        // Then
        verify(productRepository, never()).findById(any());
        verify(productRepository).deleteAllById(ids);
    }
}
