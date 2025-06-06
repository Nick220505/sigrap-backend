package com.sigrap.product;

import com.sigrap.audit.Auditable;
import com.sigrap.category.Category;
import com.sigrap.category.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing product operations.
 * Handles business logic for creating, reading, updating, and deleting products.
 * Also manages product-category relationships.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

  /**
   * Repository for database operations on products.
   * Provides CRUD functionality and custom queries for product entities.
   */
  private final ProductRepository productRepository;

  /**
   * Repository for accessing category data.
   * Used for verifying category existence when creating or updating products.
   */
  private final CategoryRepository categoryRepository;

  /**
   * Mapper for converting between Product entities and DTOs.
   * Handles object transformation for API responses and database operations.
   */
  private final ProductMapper productMapper;

  /**
   * Retrieves all products from the database.
   *
   * @return List of all products mapped to ProductInfo objects
   */
  @Transactional(readOnly = true)
  public List<ProductInfo> findAll() {
    return productRepository
      .findAll()
      .stream()
      .map(productMapper::toInfo)
      .toList();
  }

  /**
   * Finds a product by its ID.
   *
   * @param id The ID of the product to find
   * @return The found product mapped to ProductInfo
   * @throws EntityNotFoundException if the product is not found
   */
  @Transactional(readOnly = true)
  public ProductInfo findById(Integer id) {
    Product product = productRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Product not found with id: " + id)
      );
    return productMapper.toInfo(product);
  }

  /**
   * Creates a new product.
   *
   * @param productData The data for creating the product
   * @return The created product mapped to ProductInfo
   * @throws EntityNotFoundException if the specified category is not found
   */
  @Transactional
  @Auditable(action = "CREAR", entity = "PRODUCTO", captureDetails = true)
  public ProductInfo create(ProductData productData) {
    Product product = productMapper.toEntity(productData);

    if (productData.getCategoryId() != null) {
      Long categoryId = Long.valueOf(productData.getCategoryId());
      Category category = categoryRepository
        .findById(categoryId)
        .orElseThrow(() ->
          new EntityNotFoundException("Category not found: " + categoryId)
        );
      product.setCategory(category);
    }

    Product savedProduct = productRepository.save(product);
    return productMapper.toInfo(savedProduct);
  }

  /**
   * Updates an existing product.
   *
   * @param id The ID of the product to update
   * @param productData The new data for the product
   * @return The updated product mapped to ProductInfo
   * @throws EntityNotFoundException if the product or specified category is not found
   */
  @Transactional
  @Auditable(
    action = "ACTUALIZAR",
    entity = "PRODUCTO",
    entityIdParam = "id",
    captureDetails = true
  )
  public ProductInfo update(Integer id, ProductData productData) {
    Product product = productRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Product not found with id: " + id)
      );
    productMapper.updateEntityFromData(productData, product);

    if (productData.getCategoryId() != null) {
      Long categoryId = Long.valueOf(productData.getCategoryId());
      Category category = categoryRepository
        .findById(categoryId)
        .orElseThrow(() ->
          new EntityNotFoundException("Category not found: " + categoryId)
        );
      product.setCategory(category);
    } else {
      product.setCategory(null);
    }

    Product updatedProduct = productRepository.save(product);
    return productMapper.toInfo(updatedProduct);
  }

  /**
   * Deletes a product by its ID.
   *
   * @param id The ID of the product to delete
   * @throws EntityNotFoundException if the product is not found
   */
  @Transactional
  @Auditable(action = "ELIMINAR", entity = "PRODUCTO", entityIdParam = "id")
  public void delete(Integer id) {
    Product product = productRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Product not found with id: " + id)
      );
    productRepository.delete(product);
  }

  /**
   * Deletes multiple products by their IDs.
   * Validates all IDs exist before performing the deletion.
   *
   * @param ids List of product IDs to delete
   * @throws EntityNotFoundException if any of the products is not found
   */
  @Transactional
  @Auditable(
    action = "ELIMINAR_LOTE",
    entity = "PRODUCTO",
    captureDetails = true
  )
  public void deleteAllById(List<Integer> ids) {
    ids.forEach(id -> {
      if (!productRepository.existsById(id)) {
        throw new EntityNotFoundException(
          "Product with id " + id + " not found"
        );
      }
    });
    productRepository.deleteAllById(ids);
  }
}
