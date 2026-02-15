package com.sigrap.category.infrastructure.adapter.out.persistence;

import com.sigrap.category.domain.model.Category;
import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.category.domain.model.CategoryName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CategoryPersistenceAdapter.
 * Tests the adapter with a real database (H2) using Spring Boot test context.
 * 
 * These tests verify:
 * - Correct mapping between domain and JPA entities
 * - Database operations work as expected
 * - The adapter properly implements the repository port
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CategoryPersistenceAdapterTest {

    @Autowired
    private CategoryPersistenceAdapter adapter;

    @Autowired
    private CategoryJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        jpaRepository.deleteAll();
    }

    @Test
    void shouldSaveNewCategory() {
        // Given
        Category category = new Category(
            new CategoryName("Office Supplies"),
            "Pens, papers, and office items"
        );

        // When
        Category saved = adapter.save(category);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Office Supplies", saved.getName().value());
        assertEquals("Pens, papers, and office items", saved.getDescription());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldUpdateExistingCategory() {
        // Given - save initial category
        Category category = new Category(
            new CategoryName("Electronics"),
            "Electronic devices"
        );
        Category saved = adapter.save(category);
        
        // When - update the category
        Category updated = new Category(
            saved.getId(),
            new CategoryName("Electronics & Gadgets"),
            "Electronic devices and gadgets",
            saved.getCreatedAt(),
            saved.getUpdatedAt()
        );
        Category result = adapter.save(updated);

        // Then
        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("Electronics & Gadgets", result.getName().value());
        assertEquals("Electronic devices and gadgets", result.getDescription());
    }

    @Test
    void shouldFindCategoryById() {
        // Given
        Category category = new Category(
            new CategoryName("Books"),
            "Books and magazines"
        );
        Category saved = adapter.save(category);

        // When
        Optional<Category> found = adapter.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("Books", found.get().getName().value());
        assertEquals("Books and magazines", found.get().getDescription());
    }

    @Test
    void shouldReturnEmptyWhenCategoryNotFound() {
        // Given
        CategoryId nonExistentId = new CategoryId(999L);

        // When
        Optional<Category> found = adapter.findById(nonExistentId);

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindAllCategories() {
        // Given
        adapter.save(new Category(new CategoryName("Category 1"), "Description 1"));
        adapter.save(new Category(new CategoryName("Category 2"), "Description 2"));
        adapter.save(new Category(new CategoryName("Category 3"), "Description 3"));

        // When
        List<Category> categories = adapter.findAll();

        // Then
        assertNotNull(categories);
        assertEquals(3, categories.size());
        assertTrue(categories.stream().anyMatch(c -> c.getName().value().equals("Category 1")));
        assertTrue(categories.stream().anyMatch(c -> c.getName().value().equals("Category 2")));
        assertTrue(categories.stream().anyMatch(c -> c.getName().value().equals("Category 3")));
    }

    @Test
    void shouldReturnEmptyListWhenNoCategories() {
        // When
        List<Category> categories = adapter.findAll();

        // Then
        assertNotNull(categories);
        assertTrue(categories.isEmpty());
    }

    @Test
    void shouldReturnTrueWhenCategoryNameExists() {
        // Given
        adapter.save(new Category(new CategoryName("Furniture"), "Office furniture"));

        // When
        boolean exists = adapter.existsByName(new CategoryName("Furniture"));

        // Then
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenCategoryNameDoesNotExist() {
        // Given
        CategoryName nonExistentName = new CategoryName("Non-existent Category");

        // When
        boolean exists = adapter.existsByName(nonExistentName);

        // Then
        assertFalse(exists);
    }

    @Test
    void shouldDeleteCategoryById() {
        // Given
        Category category = new Category(
            new CategoryName("Temporary Category"),
            "To be deleted"
        );
        Category saved = adapter.save(category);
        CategoryId savedId = saved.getId();

        // When
        adapter.deleteById(savedId);

        // Then
        Optional<Category> found = adapter.findById(savedId);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldDeleteMultipleCategoriesById() {
        // Given
        Category cat1 = adapter.save(new Category(new CategoryName("Category A"), "Description A"));
        Category cat2 = adapter.save(new Category(new CategoryName("Category B"), "Description B"));
        Category cat3 = adapter.save(new Category(new CategoryName("Category C"), "Description C"));
        
        List<CategoryId> idsToDelete = List.of(cat1.getId(), cat2.getId());

        // When
        adapter.deleteAllById(idsToDelete);

        // Then
        assertTrue(adapter.findById(cat1.getId()).isEmpty());
        assertTrue(adapter.findById(cat2.getId()).isEmpty());
        assertTrue(adapter.findById(cat3.getId()).isPresent());
    }

    @Test
    void shouldPreserveTimestampsWhenSaving() {
        // Given
        Category category = new Category(
            new CategoryName("Test Category"),
            "Test description"
        );

        // When
        Category saved = adapter.save(category);

        // Then
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldHandleCategoryWithNullDescription() {
        // Given
        Category category = new Category(
            new CategoryName("No Description Category"),
            null
        );

        // When
        Category saved = adapter.save(category);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("No Description Category", saved.getName().value());
        assertNull(saved.getDescription());
    }

    @Test
    void shouldMapAllFieldsCorrectlyBetweenDomainAndJpa() {
        // Given
        Category category = new Category(
            new CategoryName("Complete Category"),
            "Full description"
        );

        // When
        Category saved = adapter.save(category);
        Optional<Category> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        Category result = retrieved.get();
        assertEquals(saved.getId().value(), result.getId().value());
        assertEquals(saved.getName().value(), result.getName().value());
        assertEquals(saved.getDescription(), result.getDescription());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void shouldEnforceUniqueNameConstraint() {
        // Given
        adapter.save(new Category(new CategoryName("Unique Name"), "First"));

        // When & Then
        assertThrows(Exception.class, () -> {
            adapter.save(new Category(new CategoryName("Unique Name"), "Second"));
        });
    }
}
