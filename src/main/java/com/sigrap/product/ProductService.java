package com.sigrap.product;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigrap.category.CategoryService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final CategoryService categoryService;

  @Transactional(readOnly = true)
  public List<Product> getAll() {
    return productRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Product getById(Integer id) {
    return productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
  }

  @Transactional
  public Product create(Product product) {
    if (product.getCategory() != null && product.getCategory().getId() != null) {
      product.setCategory(categoryService.getById(product.getCategory().getId()));
    }
    return productRepository.save(product);
  }

  @Transactional
  public Product update(Integer id, Product productDetails) {
    var product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    product.setName(productDetails.getName());
    product.setDescription(productDetails.getDescription());
    product.setCostPrice(productDetails.getCostPrice());
    product.setSalePrice(productDetails.getSalePrice());

    if (productDetails.getCategory() != null && productDetails.getCategory().getId() != null) {
      product.setCategory(categoryService.getById(productDetails.getCategory().getId()));
    } else {
      product.setCategory(null);
    }

    return productRepository.save(product);
  }

  @Transactional
  public void delete(Integer id) {
    var product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    productRepository.delete(product);
  }
}