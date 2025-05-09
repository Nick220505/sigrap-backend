package com.sigrap.product;

import com.sigrap.category.Category;
import com.sigrap.category.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProductMapper productMapper;

  @Transactional(readOnly = true)
  public List<ProductInfo> findAll() {
    return productRepository
      .findAll()
      .stream()
      .map(productMapper::toInfo)
      .toList();
  }

  @Transactional(readOnly = true)
  public ProductInfo findById(Integer id) {
    Product product = productRepository
      .findById(id)
      .orElseThrow(EntityNotFoundException::new);
    return productMapper.toInfo(product);
  }

  @Transactional
  public ProductInfo create(ProductData productData) {
    Product product = productMapper.toEntity(productData);

    if (productData.getCategoryId() != null) {
      Category category = categoryRepository
        .findById(productData.getCategoryId())
        .orElseThrow(() -> new EntityNotFoundException("Category not found"));
      product.setCategory(category);
    }

    Product savedProduct = productRepository.save(product);
    return productMapper.toInfo(savedProduct);
  }

  @Transactional
  public ProductInfo update(Integer id, ProductData productData) {
    Product product = productRepository
      .findById(id)
      .orElseThrow(EntityNotFoundException::new);
    productMapper.updateEntityFromData(productData, product);

    if (productData.getCategoryId() != null) {
      Category category = categoryRepository
        .findById(productData.getCategoryId())
        .orElseThrow(() -> new EntityNotFoundException("Category not found"));
      product.setCategory(category);
    } else {
      product.setCategory(null);
    }

    Product updatedProduct = productRepository.save(product);
    return productMapper.toInfo(updatedProduct);
  }

  @Transactional
  public void delete(Integer id) {
    Product product = productRepository
      .findById(id)
      .orElseThrow(EntityNotFoundException::new);
    productRepository.delete(product);
  }

  @Transactional
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
