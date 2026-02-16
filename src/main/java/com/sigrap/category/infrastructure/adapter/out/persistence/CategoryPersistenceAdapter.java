package com.sigrap.category.infrastructure.adapter.out.persistence;

import com.sigrap.category.domain.model.Category;
import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.category.domain.model.CategoryName;
import com.sigrap.category.domain.port.CategoryRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementation for Category repository port.
 * This adapter bridges the domain layer with the JPA persistence infrastructure.
 * 
 * <p>Responsibilities:
 * <ul>
 *   <li>Implements the CategoryRepositoryPort interface from the domain layer</li>
 *   <li>Translates between domain entities and JPA entities using the mapper</li>
 *   <li>Delegates actual persistence operations to the JPA repository</li>
 *   <li>Handles the impedance mismatch between domain and persistence models</li>
 * </ul>
 * 
 * <p>This is an output adapter in hexagonal architecture terminology.
 */
@Component
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {

    private final CategoryJpaRepository jpaRepository;
    private final CategoryPersistenceMapper mapper;

    /**
     * Constructor for dependency injection.
     *
     * @param jpaRepository the Spring Data JPA repository
     * @param mapper the MapStruct mapper for entity conversion
     */
    public CategoryPersistenceAdapter(
            CategoryJpaRepository jpaRepository,
            CategoryPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * Saves a category to the database.
     * Converts the domain entity to a JPA entity, persists it, and converts back.
     *
     * @param category the domain category to save
     * @return the saved category with generated ID if it was new
     */
    @Override
    public Category save(Category category) {
        CategoryJpaEntity entity = mapper.toJpaEntity(category);
        CategoryJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    /**
     * Finds a category by its identifier.
     *
     * @param id the category identifier
     * @return an Optional containing the domain category if found, empty otherwise
     */
    @Override
    public Optional<Category> findById(CategoryId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    /**
     * Retrieves all categories from the database.
     *
     * @return a list of all domain categories
     */
    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Checks if a category with the given name exists.
     *
     * @param name the category name to check
     * @return true if a category with this name exists, false otherwise
     */
    @Override
    public boolean existsByName(CategoryName name) {
        return jpaRepository.existsByName(name.value());
    }

    /**
     * Deletes a category by its identifier.
     *
     * @param id the category identifier
     */
    @Override
    public void deleteById(CategoryId id) {
        jpaRepository.deleteById(id.value());
    }

    /**
     * Deletes multiple categories by their identifiers.
     *
     * @param ids the list of category identifiers to delete
     */
    @Override
    public void deleteAllById(List<CategoryId> ids) {
        List<Long> longIds = ids.stream()
                .map(CategoryId::value)
                .toList();
        jpaRepository.deleteAllById(longIds);
    }

    /**
     * Counts the total number of categories.
     *
     * @return the total count of categories
     */
    @Override
    public long count() {
        return jpaRepository.count();
    }

    /**
     * Saves multiple categories at once.
     *
     * @param categories the list of categories to save
     * @return the list of saved categories
     */
    @Override
    public List<Category> saveAll(List<Category> categories) {
        List<CategoryJpaEntity> entities = categories.stream()
                .map(mapper::toJpaEntity)
                .toList();
        List<CategoryJpaEntity> savedEntities = jpaRepository.saveAll(entities);
        return savedEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }
}
