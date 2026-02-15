package com.sigrap.sale.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.sale.domain.model.*;
import com.sigrap.sale.domain.port.SaleReturnRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSaleReturnServiceTest {
    
    @Mock
    private SaleReturnRepositoryPort saleReturnRepository;
    
    @InjectMocks
    private GetSaleReturnService getSaleReturnService;
    
    private SaleReturn testReturn;
    
    @BeforeEach
    void setUp() {
        reset(saleReturnRepository);
        testReturn = new SaleReturn(
            new SaleReturnId(1L),
            new SaleReturnNumber("RET-001"),
            new SaleId(1L),
            LocalDateTime.now(),
            "Defective product",
            SaleReturnStatus.PENDING,
            BigDecimal.valueOf(100),
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
    
    @Test
    void shouldGetSaleReturnById() {
        // Given
        SaleReturnId id = new SaleReturnId(1L);
        when(saleReturnRepository.findById(id)).thenReturn(Optional.of(testReturn));
        
        // When
        SaleReturn result = getSaleReturnService.getById(id);
        
        // Then
        assertNotNull(result);
        assertEquals(testReturn.getId(), result.getId());
        verify(saleReturnRepository).findById(id);
    }
    
    @Test
    void shouldThrowExceptionWhenSaleReturnNotFound() {
        // Given
        SaleReturnId id = new SaleReturnId(999L);
        when(saleReturnRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> getSaleReturnService.getById(id));
        verify(saleReturnRepository).findById(id);
    }
    
    @Test
    void shouldFindSaleReturnById() {
        // Given
        SaleReturnId id = new SaleReturnId(1L);
        when(saleReturnRepository.findById(id)).thenReturn(Optional.of(testReturn));
        
        // When
        Optional<SaleReturn> result = getSaleReturnService.findById(id);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(testReturn.getId(), result.get().getId());
        verify(saleReturnRepository).findById(id);
    }
    
    @Test
    void shouldGetAllSaleReturns() {
        // Given
        SaleReturn return2 = new SaleReturn(
            new SaleReturnId(2L),
            new SaleReturnNumber("RET-002"),
            new SaleId(2L),
            LocalDateTime.now(),
            "Wrong item",
            SaleReturnStatus.APPROVED,
            BigDecimal.valueOf(50),
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        when(saleReturnRepository.findAll()).thenReturn(Arrays.asList(testReturn, return2));
        
        // When
        List<SaleReturn> result = getSaleReturnService.getAll();
        
        // Then
        assertEquals(2, result.size());
        verify(saleReturnRepository).findAll();
    }
    
    @Test
    void shouldGetSaleReturnsBySaleId() {
        // Given
        SaleId saleId = new SaleId(1L);
        when(saleReturnRepository.findBySaleId(saleId)).thenReturn(List.of(testReturn));
        
        // When
        List<SaleReturn> result = getSaleReturnService.getBySaleId(saleId);
        
        // Then
        assertEquals(1, result.size());
        assertEquals(saleId, result.get(0).getSaleId());
        verify(saleReturnRepository).findBySaleId(saleId);
    }
    
    @Test
    void shouldGetSaleReturnsByStatus() {
        // Given
        SaleReturnStatus status = SaleReturnStatus.PENDING;
        when(saleReturnRepository.findByStatus(status)).thenReturn(List.of(testReturn));
        
        // When
        List<SaleReturn> result = getSaleReturnService.getByStatus(status);
        
        // Then
        assertEquals(1, result.size());
        assertEquals(status, result.get(0).getStatus());
        verify(saleReturnRepository).findByStatus(status);
    }
}
