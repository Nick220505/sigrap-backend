package com.sigrap.category.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.category.application.port.in.command.UpdateCategoryCommand;
import com.sigrap.category.domain.model.Category;
import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.category.domain.model.CategoryName;
import com.sigrap.category.domain.port.CategoryRepositoryPort;
import com.sigrap.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UpdateCategoryService.
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
class UpdateCategoryServiceTest {
    
    @Mock
    private CategoryRepositoryPort categoryRepository;
    
    @Mock
    private EventPublisherPort eventPublisher;
    
    @InjectMocks
    private UpdateCategoryService updateCategoryService;
    
    private Category existingCategory;
    private CategoryId categoryId;
    
    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(categoryRepository);
        
        // Create a sample existing category
        categoryId = new CategoryId(1L);
        existingCategory = new Category(
            categoryId,
            new CategoryName("Office Supplies"),
            "Office and stationery items",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(1)
        );
    }
    
    @Test
    void shouldUpdateCategorySuccessfully() {
        // Given
        UpdateCategoryCommand command = new UpdateCategoryCommand(
            "Updated Office Supplies",
            "Updated description"
        );
        
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName(any(CategoryName.class))).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Category result = updateCategoryService.update(categoryId, command);
        
        // Then
        assertNotNull(result, "Updated category should not be null");
        assertEquals("Updated Office Supplies", result.getName().value(), "Category name should be updated");
        assertEquals("Updated description", result.getDescription(), "Category description should be updated");
        
        // Verify interactions
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).existsByName(any(CategoryName.class));
        verify(categoryRepository).save(any(Category.class));
    }
    
    @Test
    void shouldUpdateOnlyDescriptionWhenNameUnchanged() {
        // Given
        UpdateCategoryCommand command = new UpdateCategoryCommand(
            "Office Supplies", // Same name
            "New description"
        );
        
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Category result = updateCategoryService.update(categoryId, command);
        
        // Then
        assertNotNull(result);
        assertEquals("Office Supplies", result.getName().value(), "Category name should remain unchanged");
        assertEquals("New description", result.getDescription(), "Category description should be updated");
        
        // Verify that existsByName was not called since name didn't change
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).existsByName(any(CategoryName.class));
        verify(categoryRepository).save(any(Category.class));
    }
    
    @Test
    void shouldUpdateCategoryWithNullDescription() {
        // Given
        UpdateCategoryCommand command = new UpdateCategoryCommand(
            "Office Supplies",
            null
        );
        
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Category result = updateCategoryService.update(categoryId, command);
        
        // Then
        assertNotNull(result);
        assertNull(result.getDescription(), "Description should be null");
        assertEquals("Office Supplies", result.getName().value());
        
        verify(categoryRepository).save(any(Category.class));
    }
    
    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {
        // Given
        CategoryId nonExistentId = new CategoryId(999L);
        UpdateCategoryCommand command = new UpdateCategoryCommand(
            "New Name",
            "New Description"
        );
        
        when(categoryRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        
        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> updateCategoryService.update(nonExistentId, command),
            "Should throw exception when category not found"
        );
        
        assertTrue(
            exception.getMessage().contains("not found"),
            "Exception message should indicate category not found"
        );
        assertTrue(
            exception.getMessage().contains("999"),
            "Exception message should include the category ID"
        );
        
        // Verify that save was never called
        verify(categoryRepository, never()).save(any(Category.class));
        verify(categoryRepository).findById(nonExistentId);
    }
    
    @Test
    void shouldThrowExceptionWhenNewNameAlreadyExists() {
        // Given
        UpdateCategoryCommand command = new UpdateCategoryCommand(
            "Electronics", // Different name that already exists
            "Updated description"
        );
        
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName(any(CategoryName.class))).thenReturn(true);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> updateCategoryService.update(categoryId, command),
            "Should throw exception when new name already exists"
        );
        
        assertTrue(
            exception.getMessage().contains("already exists"),
            "Exception message should indicate name already exists"
        );
        assertTrue(
            exception.getMessage().contains("Electronics"),
            "Exception message should include the category name"
        );
        
        // Verify that save was never called
        verify(categoryRepository, never()).save(any(Category.class));
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).existsByName(any(CategoryName.class));
    }
    
    @Test
    void shouldThrowExceptionWhenNewNameIsBlank() {
        // Given
        UpdateCategoryCommand command = new UpdateCategoryCommand(
            "   ",
            "Description"
        );
        
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        
        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> updateCategoryService.update(categoryId, command),
            "Should throw exception when new name is blank"
        );
        
        // Verify that save was never called
        verify(categoryRepository, never()).save(any(Category.class));
    }
    
    @Test
    void shouldThrowExceptionWhenNewNameIsNull() {
        // Given
        UpdateCategoryCommand command = new UpdateCategoryCommand(
            null,
            "Description"
        );
        
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        
        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> updateCategoryService.update(categoryId, command),
            "Should throw exception when new name is null"
        );
        
        // Verify that save was never called
        verify(categoryRepository, never()).save(any(Category.class));
    }
    
    @Test
    void shouldThrowExceptionWhenNewNameExceedsMaxLength() {
        // Given - name with 101 characters
        String longName = "A".repeat(101);
        UpdateCategoryCommand command = new UpdateCategoryCommand(
            longName,
            "Description"
        );
        
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> updateCategoryService.update(categoryId, command),
            "Should throw exception when new name exceeds max length"
        );
        
        assertTrue(
            exception.getMessage().contains("exceed"),
            "Exception message should indicate length exceeded"
        );
        
        // Verify that save was never called
        verify(categoryRepository, never()).save(any(Category.class));
    }
    
    @Test
    void shouldUpdateCategoryWithMaxLengthName() {
        // Given - name with exactly 100 characters
        String maxLengthName = "A".repeat(100);
        UpdateCategoryCommand command = new UpdateCategoryCommand(
            maxLengthName,
            "Description"
        );
        
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName(any(CategoryName.class))).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Category result = updateCategoryService.update(categoryId, command);
        
        // Then
        assertNotNull(result);
        assertEquals(maxLengthName, result.getName().value());
        
        verify(categoryRepository).save(any(Category.class));
    }
    
    @Test
    void shouldUpdateTimestampWhenCategoryIsUpdated() {
        // Given
        UpdateCategoryCommand command = new UpdateCategoryCommand(
            "New Name",
            "New Description"
        );
        
        LocalDateTime originalUpdatedAt = existingCategory.getUpdatedAt();
        
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName(any(CategoryName.class))).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Category result = updateCategoryService.update(categoryId, command);
        
        // Then
        assertNotNull(result);
        assertTrue(
            result.getUpdatedAt().isAfter(originalUpdatedAt) || result.getUpdatedAt().isEqual(LocalDateTime.now()),
            "Updated timestamp should be updated"
        );
        
        verify(categoryRepository).save(any(Category.class));
    }
    
    @Test
    void shouldNotChangeCreatedAtTimestamp() {
        // Given
        UpdateCategoryCommand command = new UpdateCategoryCommand(
            "New Name",
            "New Description"
        );
        
        LocalDateTime originalCreatedAt = existingCategory.getCreatedAt();
        
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName(any(CategoryName.class))).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Category result = updateCategoryService.update(categoryId, command);
        
        // Then
        assertNotNull(result);
        assertEquals(originalCreatedAt, result.getCreatedAt(), "Created timestamp should not change");
        
        verify(categoryRepository).save(any(Category.class));
    }
}
