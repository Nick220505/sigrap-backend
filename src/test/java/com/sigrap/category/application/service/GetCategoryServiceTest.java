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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GetCategoryService.
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
class GetCategoryServiceTest {
    
    @Mock
    private CategoryRepositoryPort categoryRepository;
    
    @InjectMocks
    private GetCategoryService getCategoryService;
    
    private Category testCategory;
    private CategoryId testCategoryId;
    
    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(categoryRepository);
        
        // Create test data
        testCategoryId = new CategoryId(1L);
        testCategory = new Category(
            testCategoryId,
            new CategoryName("Office Supplies"),
            "Office and stationery items",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now()
        );
    }
    
    @Test
    void shouldGetCategoryByIdSuccessfully() {
        // Given
        when(categoryRepository.findById(testCategoryId)).thenReturn(Optional.of(testCategory));
        
        // When
        Category result = getCategoryService.getById(testCategoryId);
        
        // Then
        assertNotNull(result, "Retrieved category should not be null");
        assertEquals(testCategoryId, result.getId(), "Category ID should match");
        assertEquals("Office Supplies", result.getName().value(), "Category name should match");
        assertEquals("Office and stationery items", result.getDescription(), "Category description should match");
        
        // Verify interactions
        verify(categoryRepository).findById(testCategoryId);
    }
    
    @Test
    void shouldThrowExceptionWhenCategoryNotFoundById() {
        // Given
        CategoryId nonExistentId = new CategoryId(999L);
        when(categoryRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> getCategoryService.getById(nonExistentId),
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
        
        verify(categoryRepository).findById(nonExistentId);
    }
    
    @Test
    void shouldFindCategoryByIdSuccessfully() {
        // Given
        when(categoryRepository.findById(testCategoryId)).thenReturn(Optional.of(testCategory));
        
        // When
        Optional<Category> result = getCategoryService.findById(testCategoryId);
        
        // Then
        assertTrue(result.isPresent(), "Optional should contain a category");
        assertEquals(testCategoryId, result.get().getId(), "Category ID should match");
        assertEquals("Office Supplies", result.get().getName().value(), "Category name should match");
        
        verify(categoryRepository).findById(testCategoryId);
    }
    
    @Test
    void shouldReturnEmptyOptionalWhenCategoryNotFoundById() {
        // Given
        CategoryId nonExistentId = new CategoryId(999L);
        when(categoryRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        
        // When
        Optional<Category> result = getCategoryService.findById(nonExistentId);
        
        // Then
        assertFalse(result.isPresent(), "Optional should be empty when category not found");
        
        verify(categoryRepository).findById(nonExistentId);
    }
    
    @Test
    void shouldGetAllCategoriesSuccessfully() {
        // Given
        Category category1 = new Category(
            new CategoryId(1L),
            new CategoryName("Office Supplies"),
            "Office items",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        Category category2 = new Category(
            new CategoryId(2L),
            new CategoryName("Electronics"),
            "Electronic devices",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        Category category3 = new Category(
            new CategoryId(3L),
            new CategoryName("Furniture"),
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        List<Category> categories = Arrays.asList(category1, category2, category3);
        when(categoryRepository.findAll()).thenReturn(categories);
        
        // When
        List<Category> result = getCategoryService.getAll();
        
        // Then
        assertNotNull(result, "Result list should not be null");
        assertEquals(3, result.size(), "Should return all categories");
        assertEquals("Office Supplies", result.get(0).getName().value());
        assertEquals("Electronics", result.get(1).getName().value());
        assertEquals("Furniture", result.get(2).getName().value());
        assertNull(result.get(2).getDescription(), "Third category should have null description");
        
        verify(categoryRepository).findAll();
    }
    
    @Test
    void shouldReturnEmptyListWhenNoCategoriesExist() {
        // Given
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());
        
        // When
        List<Category> result = getCategoryService.getAll();
        
        // Then
        assertNotNull(result, "Result list should not be null");
        assertTrue(result.isEmpty(), "Result list should be empty");
        
        verify(categoryRepository).findAll();
    }
    
    @Test
    void shouldGetSingleCategoryInList() {
        // Given
        List<Category> categories = Collections.singletonList(testCategory);
        when(categoryRepository.findAll()).thenReturn(categories);
        
        // When
        List<Category> result = getCategoryService.getAll();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size(), "Should return single category");
        assertEquals(testCategoryId, result.get(0).getId());
        
        verify(categoryRepository).findAll();
    }
    
    @Test
    void shouldHandleMultipleCallsToGetById() {
        // Given
        when(categoryRepository.findById(testCategoryId)).thenReturn(Optional.of(testCategory));
        
        // When
        Category result1 = getCategoryService.getById(testCategoryId);
        Category result2 = getCategoryService.getById(testCategoryId);
        
        // Then
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.getId(), result2.getId());
        
        // Verify repository was called twice
        verify(categoryRepository, times(2)).findById(testCategoryId);
    }
    
    @Test
    void shouldHandleMultipleCallsToGetAll() {
        // Given
        List<Category> categories = Collections.singletonList(testCategory);
        when(categoryRepository.findAll()).thenReturn(categories);
        
        // When
        List<Category> result1 = getCategoryService.getAll();
        List<Category> result2 = getCategoryService.getAll();
        
        // Then
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.size(), result2.size());
        
        // Verify repository was called twice
        verify(categoryRepository, times(2)).findAll();
    }
}
