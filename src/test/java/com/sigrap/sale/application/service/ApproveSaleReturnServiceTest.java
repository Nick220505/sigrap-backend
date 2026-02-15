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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApproveSaleReturnServiceTest {
    
    @Mock
    private SaleReturnRepositoryPort saleReturnRepository;
    
    @InjectMocks
    private ApproveSaleReturnService approveSaleReturnService;
    
    @BeforeEach
    void setUp() {
        reset(saleReturnRepository);
    }
    
    @Test
    void shouldApproveSaleReturnSuccessfully() {
        // Given
        SaleReturnId id = new SaleReturnId(1L);
        SaleReturn testReturn = new SaleReturn(
            id,
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
        
        when(saleReturnRepository.findById(id)).thenReturn(Optional.of(testReturn));
        when(saleReturnRepository.save(any(SaleReturn.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        SaleReturn result = approveSaleReturnService.approve(id);
        
        // Then
        assertEquals(SaleReturnStatus.APPROVED, result.getStatus());
        verify(saleReturnRepository).findById(id);
        verify(saleReturnRepository).save(any(SaleReturn.class));
    }
    
    @Test
    void shouldThrowExceptionWhenSaleReturnNotFound() {
        // Given
        SaleReturnId id = new SaleReturnId(999L);
        when(saleReturnRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> approveSaleReturnService.approve(id));
        verify(saleReturnRepository, never()).save(any(SaleReturn.class));
    }
    
    @Test
    void shouldThrowExceptionWhenApprovingNonPendingReturn() {
        // Given
        SaleReturnId id = new SaleReturnId(1L);
        SaleReturn approvedReturn = new SaleReturn(
            id,
            new SaleReturnNumber("RET-001"),
            new SaleId(1L),
            LocalDateTime.now(),
            "Reason",
            SaleReturnStatus.APPROVED,
            BigDecimal.valueOf(100),
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(saleReturnRepository.findById(id)).thenReturn(Optional.of(approvedReturn));
        
        // When & Then
        assertThrows(IllegalStateException.class, () -> approveSaleReturnService.approve(id));
        verify(saleReturnRepository, never()).save(any(SaleReturn.class));
    }
}
