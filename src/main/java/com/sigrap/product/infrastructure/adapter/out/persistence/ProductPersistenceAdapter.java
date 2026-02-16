package com.sigrap.product.infrastructure.adapter.out.persistence;

import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.product.domain.model.Product;
import com.sigrap.product.domain.model.ProductId;
import com.sigrap.product.domain.model.ProductName;
import com.sigrap.product.domain.port.ProductRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementation for Product repository port.
 * This adapter bridges the domain layer with the JPA persistence infrastructure.
 * 
 * <p>Responsibilities:
 * <ul>
 *   <li>Implements the ProductRepositoryPort interface from the domain layer</li>
 *   <li>Translates between domain entities and JPA entities using the mapper</li>
 *   <li>Delegates actual persistence operations to the JPA repository</li>
 *   <li>Handles the impedance mismatch between domain and persistence models</li>
 * </ul>
 * 
 * <p>This is an output adapter in hexagonal architecture terminology.
 */
@Component
public class ProductPersistenceAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository jpaRepository;
    private final ProductPersistenceMapper mapper;

    /**
     * Constructor for dependency injection.
     *
     * @param jpaRepository the Spring Data JPA repository
     * @param mapper the MapStruct mapper for entity conversion
     */
    public ProductPersistenceAdapter(
            ProductJpaRepository jpaRepository,
            ProductPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * Saves a product to the database.
     * Converts the domain entity to a JPA entity, persists it, and converts back.
     *
     * @param product the domain product to save
     * @return the saved product with generated ID if it was new
     */
    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = mapper.toJpaEntity(product);
        ProductJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    /**
     * Finds a product by its identifier.
     *
     * @param id the product identifier
     * @return an Optional containing the domain product if found, empty otherwise
     */
    @Override
    public Optional<Product> findById(ProductId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    /**
     * Retrieves all products from the database.
     *
     * @return a list of all domain products
     */
    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Finds all products belonging to a specific category.
     *
     * @param categoryId the category identifier
     * @return a list of products in the category
     */
    @Override
    public List<Product> findByCategoryId(CategoryId categoryId) {
        return jpaRepository.findByCategoryId(categoryId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Checks if a product with the given name exists.
     *
     * @param name the product name to check
     * @return true if a product with this name exists, false otherwise
     */
    @Override
    public boolean existsByName(ProductName name) {
        return jpaRepository.existsByName(name.value());
    }

    /**
     * Checks if a product with the given name exists, excluding a specific product ID.
     * Useful for update operations to check name uniqueness.
     *
     * @param name the product name to check
     * @param excludeId the product ID to exclude from the check
     * @return true if another product with the name exists, false otherwise
     */
    @Override
    public boolean existsByNameAndIdNot(ProductName name, ProductId excludeId) {
        return jpaRepository.existsByNameAndIdNot(name.value(), excludeId.value());
    }

    /**
     * Deletes a product by its identifier.
     *
     * @param id the product identifier
     */
    @Override
    public void deleteById(ProductId id) {
        jpaRepository.deleteById(id.value());
    }

    /**
     * Deletes multiple products by their identifiers.
     *
     * @param ids the list of product identifiers to delete
     */
    @Override
    public void deleteAllById(List<ProductId> ids) {
        List<Long> longIds = ids.stream()
                .map(ProductId::value)
                .toList();
        // Delete one by one to ensure proper transaction handling
        longIds.forEach(jpaRepository::deleteById);
    }

    /**
     * Counts the total number of products.
     *
     * @return the total count of products
     */
    @Override
    public long count() {
        return jpaRepository.count();
    }

    /**
     * Saves multiple products at once.
     *
     * @param products the list of products to save
     * @return the list of saved products
     */
    @Override
    public List<Product> saveAll(List<Product> products) {
        List<ProductJpaEntity> entities = products.stream()
                .map(mapper::toJpaEntity)
                .toList();
        List<ProductJpaEntity> savedEntities = jpaRepository.saveAll(entities);
        return savedEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }
}
