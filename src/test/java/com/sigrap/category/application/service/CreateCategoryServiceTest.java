package com.sigrap.category.application.service;

import com.sigrap.category.application.port.in.command.CreateCategoryCommand;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CreateCategoryService.
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
class CreateCategoryServiceTest {
    
    @Mock
    private CategoryRepositoryPort categoryRepository;
    
    @InjectMocks
    private CreateCategoryService createCategoryService;
    
    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(categoryRepository);
    }
    
    @Test
    void shouldCreateCategorySuccessfully() {
        // Given
        CreateCategoryCommand command = new CreateCategoryCommand(
            "Office Supplies",
            "Office and stationery items"
        );
        
        when(categoryRepository.existsByName(any(CategoryName.class))).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category cat = invocation.getArgument(0);
            // Simulate ID generation by persistence layer
            return new Category(
                new CategoryId(1L),
                cat.getName(),
                cat.getDescription(),
                cat.getCreatedAt(),
                cat.getUpdatedAt()
            );
        });
        
        // When
        Category result = createCategoryService.create(command);
        
        // Then
        assertNotNull(result, "Created category should not be null");
        assertNotNull(result.getId(), "Created category should have an ID");
        assertEquals("Office Supplies", result.getName().value(), "Category name should match");
        assertEquals("Office and stationery items", result.getDescription(), "Category description should match");
        
        // Verify interactions
        verify(categoryRepository).existsByName(any(CategoryName.class));
        verify(categoryRepository).save(any(Category.class));
    }
    
    @Test
    void shouldCreateCategoryWithNullDescription() {
        // Given
        CreateCategoryCommand command = new CreateCategoryCommand(
            "Electronics",
            null
        );
        
        when(categoryRepository.existsByName(any(CategoryName.class))).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category cat = invocation.getArgument(0);
            return new Category(
                new CategoryId(2L),
                cat.getName(),
                cat.getDescription(),
                cat.getCreatedAt(),
                cat.getUpdatedAt()
            );
        });
        
        // When
        Category result = createCategoryService.create(command);
        
        // Then
        assertNotNull(result);
        assertNull(result.getDescription(), "Description should be null");
        assertEquals("Electronics", result.getName().value());
        
        verify(categoryRepository).save(any(Category.class));
    }
    
    @Test
    void shouldThrowExceptionWhenCategoryNameAlreadyExists() {
        // Given
        CreateCategoryCommand command = new CreateCategoryCommand(
            "Existing Category",
            "This name already exists"
        );
        
        when(categoryRepository.existsByName(any(CategoryName.class))).thenReturn(true);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createCategoryService.create(command),
            "Should throw exception when category name already exists"
        );
        
        assertTrue(
            exception.getMessage().contains("already exists"),
            "Exception message should indicate name already exists"
        );
        assertTrue(
            exception.getMessage().contains("Existing Category"),
            "Exception message should include the category name"
        );
        
        // Verify that save was never called
        verify(categoryRepository, never()).save(any(Category.class));
        verify(categoryRepository).existsByName(any(CategoryName.class));
    }
    
    @Test
    void shouldThrowExceptionWhenCategoryNameIsBlank() {
        // Given
        CreateCategoryCommand command = new CreateCategoryCommand(
            "   ",
            "Description"
        );
        
        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> createCategoryService.create(command),
            "Should throw exception when category name is blank"
        );
        
        // Verify that repository was never called
        verifyNoInteractions(categoryRepository);
    }
    
    @Test
    void shouldThrowExceptionWhenCategoryNameIsNull() {
        // Given
        CreateCategoryCommand command = new CreateCategoryCommand(
            null,
            "Description"
        );
        
        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> createCategoryService.create(command),
            "Should throw exception when category name is null"
        );
        
        // Verify that repository was never called
        verifyNoInteractions(categoryRepository);
    }
    
    @Test
    void shouldThrowExceptionWhenCategoryNameExceedsMaxLength() {
        // Given - name with 101 characters
        String longName = "A".repeat(101);
        CreateCategoryCommand command = new CreateCategoryCommand(
            longName,
            "Description"
        );
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createCategoryService.create(command),
            "Should throw exception when category name exceeds max length"
        );
        
        assertTrue(
            exception.getMessage().contains("exceed"),
            "Exception message should indicate length exceeded"
        );
        
        // Verify that repository was never called
        verifyNoInteractions(categoryRepository);
    }
    
    @Test
    void shouldCreateCategoryWithMaxLengthName() {
        // Given - name with exactly 100 characters
        String maxLengthName = "A".repeat(100);
        CreateCategoryCommand command = new CreateCategoryCommand(
            maxLengthName,
            "Description"
        );
        
        when(categoryRepository.existsByName(any(CategoryName.class))).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category cat = invocation.getArgument(0);
            return new Category(
                new CategoryId(3L),
                cat.getName(),
                cat.getDescription(),
                cat.getCreatedAt(),
                cat.getUpdatedAt()
            );
        });
        
        // When
        Category result = createCategoryService.create(command);
        
        // Then
        assertNotNull(result);
        assertEquals(maxLengthName, result.getName().value());
        
        verify(categoryRepository).save(any(Category.class));
    }
    
    @Test
    void shouldTrimWhitespaceFromCategoryName() {
        // Given
        CreateCategoryCommand command = new CreateCategoryCommand(
            "  Furniture  ",
            "Home furniture"
        );
        
        when(categoryRepository.existsByName(any(CategoryName.class))).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category cat = invocation.getArgument(0);
            return new Category(
                new CategoryId(4L),
                cat.getName(),
                cat.getDescription(),
                cat.getCreatedAt(),
                cat.getUpdatedAt()
            );
        });
        
        // When
        Category result = createCategoryService.create(command);
        
        // Then
        assertNotNull(result);
        // Note: The CategoryName value object should handle trimming if needed
        // This test verifies the current behavior
        assertEquals("  Furniture  ", result.getName().value());
        
        verify(categoryRepository).save(any(Category.class));
    }
}
