package com.sigrap.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

  @Test
  void shouldSaveProduct() {
    Product product = new Product();
    product.setName("Test Product");
    product.setDescription("Test Description");
    product.setCostPrice(new BigDecimal("10.00"));
    product.setSalePrice(new BigDecimal("15.00"));

    Product savedProduct = productRepository.save(product);

    assertThat(savedProduct.getId()).isNotNull();
    assertThat(savedProduct.getName()).isEqualTo("Test Product");
    assertThat(savedProduct.getDescription()).isEqualTo("Test Description");
    assertThat(savedProduct.getCostPrice()).isEqualByComparingTo(new BigDecimal("10.00"));
    assertThat(savedProduct.getSalePrice()).isEqualByComparingTo(new BigDecimal("15.00"));
  }

  @Test
  void shouldFindProductById() {
    Product product = new Product();
    product.setName("Test Product");
    product.setDescription("Test Description");
    product.setCostPrice(new BigDecimal("10.00"));
    product.setSalePrice(new BigDecimal("15.00"));
    Product savedProduct = productRepository.save(product);

    Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

    assertThat(foundProduct).isPresent();
    assertThat(foundProduct.get().getName()).isEqualTo("Test Product");
    assertThat(foundProduct.get().getCostPrice()).isEqualByComparingTo(new BigDecimal("10.00"));
  }

  @Test
  void shouldFindAllProducts() {
    Product product1 = new Product();
    product1.setName("Product 1");
    product1.setDescription("Description 1");
    product1.setCostPrice(new BigDecimal("10.00"));
    product1.setSalePrice(new BigDecimal("15.00"));

    Product product2 = new Product();
    product2.setName("Product 2");
    product2.setDescription("Description 2");
    product2.setCostPrice(new BigDecimal("20.00"));
    product2.setSalePrice(new BigDecimal("30.00"));

    productRepository.save(product1);
    productRepository.save(product2);

    List<Product> products = productRepository.findAll();

    assertThat(products).hasSize(2);
    assertThat(products).extracting(Product::getName).contains("Product 1", "Product 2");
  }

  @Test
  void shouldDeleteProduct() {
    Product product = new Product();
    product.setName("Test Product");
    product.setCostPrice(new BigDecimal("10.00"));
    product.setSalePrice(new BigDecimal("15.00"));
    Product savedProduct = productRepository.save(product);

    productRepository.deleteById(savedProduct.getId());
    Optional<Product> deletedProduct = productRepository.findById(savedProduct.getId());

    assertThat(deletedProduct).isEmpty();
  }
}