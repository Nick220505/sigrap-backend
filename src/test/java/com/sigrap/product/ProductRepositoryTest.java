package com.sigrap.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

  @Test
  void shouldSaveProduct() {
    Product product = Product.builder()
        .name("Test Product")
        .description("Test Description")
        .costPrice(new BigDecimal("10.00"))
        .salePrice(new BigDecimal("15.00"))
        .build();

    Product savedProduct = productRepository.save(product);

    assertThat(savedProduct.getId()).isNotNull();
    assertThat(savedProduct.getName()).isEqualTo("Test Product");
    assertThat(savedProduct.getDescription()).isEqualTo("Test Description");
    assertThat(savedProduct.getCostPrice()).isEqualByComparingTo(new BigDecimal("10.00"));
    assertThat(savedProduct.getSalePrice()).isEqualByComparingTo(new BigDecimal("15.00"));
  }

  @Test
  void shouldFindProductById() {
    Product product = Product.builder()
        .name("Test Product")
        .description("Test Description")
        .costPrice(new BigDecimal("10.00"))
        .salePrice(new BigDecimal("15.00"))
        .build();
    Product savedProduct = productRepository.save(product);

    Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

    assertThat(foundProduct).isPresent();
    assertThat(foundProduct.get().getName()).isEqualTo("Test Product");
    assertThat(foundProduct.get().getCostPrice()).isEqualByComparingTo(new BigDecimal("10.00"));
  }

  @Test
  void shouldFindAllProducts() {
    Product product1 = Product.builder()
        .name("Product 1")
        .description("Description 1")
        .costPrice(new BigDecimal("10.00"))
        .salePrice(new BigDecimal("15.00"))
        .build();

    Product product2 = Product.builder()
        .name("Product 2")
        .description("Description 2")
        .costPrice(new BigDecimal("20.00"))
        .salePrice(new BigDecimal("30.00"))
        .build();

    productRepository.save(product1);
    productRepository.save(product2);

    List<Product> products = productRepository.findAll();

    assertThat(products).hasSize(2);
    assertThat(products).extracting(Product::getName).contains("Product 1", "Product 2");
  }

  @Test
  void shouldDeleteProduct() {
    Product product = Product.builder()
        .name("Test Product")
        .costPrice(new BigDecimal("10.00"))
        .salePrice(new BigDecimal("15.00"))
        .build();
    Product savedProduct = productRepository.save(product);

    productRepository.deleteById(savedProduct.getId());
    Optional<Product> deletedProduct = productRepository.findById(savedProduct.getId());

    assertThat(deletedProduct).isEmpty();
  }
}