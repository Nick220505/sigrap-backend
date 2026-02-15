package com.sigrap.category.application.service;

import com.sigrap.category.domain.model.Category;
import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.category.domain.model.CategoryName;
import com.sigrap.category.domain.port.CategoryRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DeleteCategoryService.
 * Tests the use case implementation with mocked repository port.
 * 
 * <p>Following hexagonal architecture testing principles:
 * <ul>
 *   <li>Tests use case logic in isolation</li>
 *   <li>Mocks output ports (repository)</li>
 *   <li>No Spring context required</li>
 *   <li>Fast execution</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class DeleteCategoryServiceTest {
    
    @Mock
    private CategoryRepositoryPort categoryRepository;
    
    @InjectMocks
    private DeleteCategoryService deleteCategoryService;
    
    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(categoryRepository);
    }
    
    @Test
    void shouldDeleteCategorySuccessfully() {
        // Given
        CategoryId id = new CategoryId(1L);
        Category existingCategory = new Category(
            id,
            new CategoryName("Office Supplies"),
            "Office and stationery items",
            java.time.LocalDateTime.now(),
            java.time.LocalDateTime.now()
        );
        
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        doNothing().when(categoryRepository).deleteById(id);
        
        // When
        assertDoesNotThrow(() -> deleteCategoryService.delete(id));
        
        // Then
        verify(categoryRepository).findById(id);
        verify(categoryRepository).deleteById(id);
    }
    
    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {
        // Given
        CategoryId id = new CategoryId(999L);
        
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> deleteCategoryService.delete(id),
            "Should throw exception when category is not found"
        );
        
        assertTrue(
            exception.getMessage().contains("not found"),
            "Exception message should indicate category not found"
        );
        assertTrue(
            exception.getMessage().contains("999"),
            "Exception message should include the category ID"
        );
        
        // Verify that delete was never called
        verify(categoryRepository).findById(id);
        verify(categoryRepository, never()).deleteById(any(CategoryId.class));
    }
    
    @Test
    void shouldDeleteMultipleCategoriesSuccessfully() {
        // Given
        CategoryId id1 = new CategoryId(1L);
        CategoryId id2 = new CategoryId(2L);
        CategoryId id3 = new CategoryId(3L);
        List<CategoryId> ids = Arrays.asList(id1, id2, id3);
        
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        Category category1 = new Category(id1, new CategoryName("Category 1"), "Description 1", now, now);
        Category category2 = new Category(id2, new CategoryName("Category 2"), "Description 2", now, now);
        Category category3 = new Category(id3, new CategoryName("Category 3"), "Description 3", now, now);
        
        when(categoryRepository.findById(id1)).thenReturn(Optional.of(category1));
        when(categoryRepository.findById(id2)).thenReturn(Optional.of(category2));
        when(categoryRepository.findById(id3)).thenReturn(Optional.of(category3));
        doNothing().when(categoryRepository).deleteAllById(ids);
        
        // When
        assertDoesNotThrow(() -> deleteCategoryService.deleteAll(ids));
        
        // Then
        verify(categoryRepository).findById(id1);
        verify(categoryRepository).findById(id2);
        verify(categoryRepository).findById(id3);
        verify(categoryRepository).deleteAllById(ids);
    }
    
    @Test
    void shouldThrowExceptionWhenAnyCategoryNotFoundInBatchDelete() {
        // Given
        CategoryId id1 = new CategoryId(1L);
        CategoryId id2 = new CategoryId(999L);
        CategoryId id3 = new CategoryId(3L);
        List<CategoryId> ids = Arrays.asList(id1, id2, id3);
        
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        Category category1 = new Category(id1, new CategoryName("Category 1"), "Description 1", now, now);
        
        when(categoryRepository.findById(id1)).thenReturn(Optional.of(category1));
        when(categoryRepository.findById(id2)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> deleteCategoryService.deleteAll(ids),
            "Should throw exception when any category is not found"
        );
        
        assertTrue(
            exception.getMessage().contains("not found"),
            "Exception message should indicate category not found"
        );
        assertTrue(
            exception.getMessage().contains("999"),
            "Exception message should include the missing category ID"
        );
        
        // Verify that deleteAllById was never called
        verify(categoryRepository).findById(id1);
        verify(categoryRepository).findById(id2);
        verify(categoryRepository, never()).findById(id3);
        verify(categoryRepository, never()).deleteAllById(any());
    }
    
    @Test
    void shouldDeleteEmptyListSuccessfully() {
        // Given
        List<CategoryId> emptyList = List.of();
        
        // When
        assertDoesNotThrow(() -> deleteCategoryService.deleteAll(emptyList));
        
        // Then
        verify(categoryRepository, never()).findById(any(CategoryId.class));
        verify(categoryRepository).deleteAllById(emptyList);
    }
    
    @Test
    void shouldDeleteSingleCategoryInBatch() {
        // Given
        CategoryId id = new CategoryId(1L);
        List<CategoryId> ids = List.of(id);
        
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        Category category = new Category(id, new CategoryName("Single Category"), "Description", now, now);
        
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).deleteAllById(ids);
        
        // When
        assertDoesNotThrow(() -> deleteCategoryService.deleteAll(ids));
        
        // Then
        verify(categoryRepository).findById(id);
        verify(categoryRepository).deleteAllById(ids);
    }
}
