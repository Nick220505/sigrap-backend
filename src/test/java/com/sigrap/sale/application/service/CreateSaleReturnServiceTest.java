package com.sigrap.sale.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.sale.application.port.in.command.CreateSaleReturnCommand;
import com.sigrap.sale.domain.model.*;
import com.sigrap.sale.domain.port.SaleRepositoryPort;
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
class CreateSaleReturnServiceTest {
    
    @Mock
    private SaleReturnRepositoryPort saleReturnRepository;
    
    @Mock
    private SaleRepositoryPort saleRepository;
    
    @InjectMocks
    private CreateSaleReturnService createSaleReturnService;
    
    private Sale testSale;
    
    @BeforeEach
    void setUp() {
        reset(saleReturnRepository, saleRepository);
        testSale = new Sale(
            new SaleId(1L),
            new SaleNumber("SALE-001"),
            1L,
            2L,
            LocalDateTime.now(),
            BigDecimal.valueOf(100),
            PaymentMethod.CASH,
            SaleStatus.COMPLETED,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
    
    @Test
    void shouldCreateSaleReturnSuccessfully() {
        // Given
        LocalDateTime returnDate = LocalDateTime.now();
        CreateSaleReturnCommand command = new CreateSaleReturnCommand(
            "RET-001",
            1L,
            returnDate,
            "Defective product",
            BigDecimal.valueOf(100),
            "Customer complaint"
        );
        
        when(saleRepository.findById(any(SaleId.class))).thenReturn(Optional.of(testSale));
        when(saleReturnRepository.save(any(SaleReturn.class))).thenAnswer(invocation -> {
            SaleReturn ret = invocation.getArgument(0);
            return new SaleReturn(
                new SaleReturnId(1L),
                ret.getReturnNumber(),
                ret.getSaleId(),
                ret.getReturnDate(),
                ret.getReason(),
                ret.getStatus(),
                ret.getRefundAmount(),
                ret.getNotes(),
                ret.getCreatedAt(),
                ret.getUpdatedAt()
            );
        });
        
        // When
        SaleReturn result = createSaleReturnService.create(command);
        
        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("RET-001", result.getReturnNumber().value());
        assertEquals(1L, result.getSaleId().value());
        assertEquals("Defective product", result.getReason());
        assertEquals(BigDecimal.valueOf(100), result.getRefundAmount());
        assertEquals(SaleReturnStatus.PENDING, result.getStatus());
        
        verify(saleRepository).findById(any(SaleId.class));
        verify(saleReturnRepository).save(any(SaleReturn.class));
    }
    
    @Test
    void shouldThrowExceptionWhenSaleNotFound() {
        // Given
        CreateSaleReturnCommand command = new CreateSaleReturnCommand(
            "RET-001",
            999L,
            LocalDateTime.now(),
            "Reason",
            BigDecimal.valueOf(100),
            null
        );
        
        when(saleRepository.findById(any(SaleId.class))).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> createSaleReturnService.create(command));
        verify(saleReturnRepository, never()).save(any(SaleReturn.class));
    }
    
    @Test
    void shouldThrowExceptionWhenReturnNumberIsNull() {
        // Given
        CreateSaleReturnCommand command = new CreateSaleReturnCommand(
            null,
            1L,
            LocalDateTime.now(),
            "Reason",
            BigDecimal.valueOf(100),
            null
        );
        
        when(saleRepository.findById(any(SaleId.class))).thenReturn(Optional.of(testSale));
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> createSaleReturnService.create(command));
        verify(saleReturnRepository, never()).save(any(SaleReturn.class));
    }
}
