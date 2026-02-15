package com.sigrap.sale.application.service;

import com.sigrap.sale.application.port.in.command.CreateSaleCommand;
import com.sigrap.sale.domain.model.*;
import com.sigrap.sale.domain.port.SaleRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(MockitoExtension.class)
class CreateSaleServiceTest {
    
    @Mock
    private SaleRepositoryPort saleRepository;
    
    @InjectMocks
    private CreateSaleService createSaleService;
    
    @BeforeEach
    void setUp() {
        reset(saleRepository);
    }
    
    @Test
    void shouldCreateSaleSuccessfully() {
        // Given
        LocalDateTime saleDate = LocalDateTime.now();
        CreateSaleCommand command = new CreateSaleCommand(
            "SALE-001",
            1L,
            2L,
            saleDate,
            PaymentMethod.CASH,
            "Test sale"
        );
        
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> {
            Sale sale = invocation.getArgument(0);
            return new Sale(
                new SaleId(1L),
                sale.getSaleNumber(),
                sale.getCustomerId(),
                sale.getEmployeeId(),
                sale.getSaleDate(),
                sale.getTotalAmount(),
                sale.getPaymentMethod(),
                sale.getStatus(),
                sale.getNotes(),
                sale.getCreatedAt(),
                sale.getUpdatedAt()
            );
        });
        
        // When
        Sale result = createSaleService.create(command);
        
        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("SALE-001", result.getSaleNumber().value());
        assertEquals(1L, result.getCustomerId());
        assertEquals(2L, result.getEmployeeId());
        assertEquals(saleDate, result.getSaleDate());
        assertEquals(PaymentMethod.CASH, result.getPaymentMethod());
        assertEquals(SaleStatus.PENDING, result.getStatus());
        assertEquals("Test sale", result.getNotes());
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
        
        verify(saleRepository).save(any(Sale.class));
    }
    
    @Test
    void shouldCreateSaleWithMinimalData() {
        // Given
        LocalDateTime saleDate = LocalDateTime.now();
        CreateSaleCommand command = new CreateSaleCommand(
            "SALE-002",
            1L,
            2L,
            saleDate,
            PaymentMethod.CREDIT_CARD,
            null
        );
        
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> {
            Sale sale = invocation.getArgument(0);
            return new Sale(
                new SaleId(2L),
                sale.getSaleNumber(),
                sale.getCustomerId(),
                sale.getEmployeeId(),
                sale.getSaleDate(),
                sale.getTotalAmount(),
                sale.getPaymentMethod(),
                sale.getStatus(),
                sale.getNotes(),
                sale.getCreatedAt(),
                sale.getUpdatedAt()
            );
        });
        
        // When
        Sale result = createSaleService.create(command);
        
        // Then
        assertNotNull(result);
        assertNull(result.getNotes());
        assertEquals(PaymentMethod.CREDIT_CARD, result.getPaymentMethod());
        
        verify(saleRepository).save(any(Sale.class));
    }
    
    @Test
    void shouldThrowExceptionWhenSaleNumberIsNull() {
        // Given
        CreateSaleCommand command = new CreateSaleCommand(
            null,
            1L,
            2L,
            LocalDateTime.now(),
            PaymentMethod.CASH,
            "Test"
        );
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> createSaleService.create(command));
        verifyNoInteractions(saleRepository);
    }
    
    @Test
    void shouldThrowExceptionWhenSaleNumberIsBlank() {
        // Given
        CreateSaleCommand command = new CreateSaleCommand(
            "   ",
            1L,
            2L,
            LocalDateTime.now(),
            PaymentMethod.CASH,
            "Test"
        );
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> createSaleService.create(command));
        verifyNoInteractions(saleRepository);
    }
}
