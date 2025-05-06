package com.sigrap.product;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sigrap.category.Category;

@SpringBootTest
class ProductMapperTest {

  @Autowired
  private ProductMapper productMapper;

  @Test
  void toInfo_shouldMapEntityToInfo() {
    Category category = new Category();
    category.setId(1);
    category.setName("Test Category");

    Product product = new Product();
    product.setId(1);
    product.setName("Test Product");
    product.setDescription("Test Description");
    product.setCostPrice(new BigDecimal("10.00"));
    product.setSalePrice(new BigDecimal("15.00"));
    product.setCategory(category);

    ProductInfo productInfo = productMapper.toInfo(product);

    assertThat(productInfo).isNotNull();
    assertThat(productInfo.getId()).isEqualTo(1);
    assertThat(productInfo.getName()).isEqualTo("Test Product");
    assertThat(productInfo.getDescription()).isEqualTo("Test Description");
    assertThat(productInfo.getCostPrice()).isEqualByComparingTo(new BigDecimal("10.00"));
    assertThat(productInfo.getSalePrice()).isEqualByComparingTo(new BigDecimal("15.00"));
    assertThat(productInfo.getCategory()).isNotNull();
    assertThat(productInfo.getCategory().getId()).isEqualTo(1);
    assertThat(productInfo.getCategory().getName()).isEqualTo("Test Category");
  }

  @Test
  void toInfo_shouldReturnNull_whenProductIsNull() {
    ProductInfo productInfo = productMapper.toInfo(null);

    assertThat(productInfo).isNull();
  }

  @Test
  void toInfo_shouldHandleNullCategory_whenMappingProduct() {
    Product product = new Product();
    product.setId(1);
    product.setName("Test Product");
    product.setDescription("Test Description");
    product.setCostPrice(new BigDecimal("10.00"));
    product.setSalePrice(new BigDecimal("15.00"));
    product.setCategory(null);

    ProductInfo productInfo = productMapper.toInfo(product);

    assertThat(productInfo).isNotNull();
    assertThat(productInfo.getId()).isEqualTo(1);
    assertThat(productInfo.getName()).isEqualTo("Test Product");
    assertThat(productInfo.getDescription()).isEqualTo("Test Description");
    assertThat(productInfo.getCostPrice()).isEqualByComparingTo(new BigDecimal("10.00"));
    assertThat(productInfo.getSalePrice()).isEqualByComparingTo(new BigDecimal("15.00"));
    assertThat(productInfo.getCategory()).isNull();
  }

  @Test
  void toEntity_shouldMapDataToEntity() {
    ProductData productData = new ProductData();
    productData.setName("Test Product");
    productData.setDescription("Test Description");
    productData.setCostPrice(new BigDecimal("10.00"));
    productData.setSalePrice(new BigDecimal("15.00"));
    productData.setCategoryId(1);

    Product product = productMapper.toEntity(productData);

    assertThat(product).isNotNull();
    assertThat(product.getId()).isNull();
    assertThat(product.getName()).isEqualTo("Test Product");
    assertThat(product.getDescription()).isEqualTo("Test Description");
    assertThat(product.getCostPrice()).isEqualByComparingTo(new BigDecimal("10.00"));
    assertThat(product.getSalePrice()).isEqualByComparingTo(new BigDecimal("15.00"));
    assertThat(product.getCategory()).isNull();
  }

  @Test
  void toEntity_shouldReturnNull_whenProductDataIsNull() {
    Product product = productMapper.toEntity(null);

    assertThat(product).isNull();
  }

  @Test
  void updateEntityFromData_shouldUpdateEntityWithDataValues() {
    Product product = new Product();
    product.setId(1);
    product.setName("Old Name");
    product.setDescription("Old Description");
    product.setCostPrice(new BigDecimal("10.00"));
    product.setSalePrice(new BigDecimal("15.00"));

    ProductData productData = new ProductData();
    productData.setName("New Name");
    productData.setDescription("New Description");
    productData.setCostPrice(new BigDecimal("20.00"));
    productData.setSalePrice(new BigDecimal("30.00"));
    productData.setCategoryId(1);

    productMapper.updateEntityFromData(productData, product);

    assertThat(product.getId()).isEqualTo(1);
    assertThat(product.getName()).isEqualTo("New Name");
    assertThat(product.getDescription()).isEqualTo("New Description");
    assertThat(product.getCostPrice()).isEqualByComparingTo(new BigDecimal("20.00"));
    assertThat(product.getSalePrice()).isEqualByComparingTo(new BigDecimal("30.00"));
    assertThat(product.getCategory()).isNull();
  }

  @Test
  void updateEntityFromData_shouldSetNullValues_whenDataFieldsAreNull() {
    Product product = new Product();
    product.setId(1);
    product.setName("Original Name");
    product.setDescription("Original Description");
    product.setCostPrice(new BigDecimal("10.00"));
    product.setSalePrice(new BigDecimal("15.00"));

    ProductData productData = new ProductData();
    productData.setName("New Name");
    productData.setCostPrice(new BigDecimal("20.00"));
    productData.setSalePrice(new BigDecimal("30.00"));

    productMapper.updateEntityFromData(productData, product);

    assertThat(product.getId()).isEqualTo(1);
    assertThat(product.getName()).isEqualTo("New Name");
    assertThat(product.getDescription()).isNull();
    assertThat(product.getCostPrice()).isEqualByComparingTo(new BigDecimal("20.00"));
    assertThat(product.getSalePrice()).isEqualByComparingTo(new BigDecimal("30.00"));
  }

  @Test
  void updateEntityFromData_shouldDoNothing_whenProductDataIsNull() {
    Product product = new Product();
    product.setId(1);
    product.setName("Original Name");
    product.setDescription("Original Description");
    product.setCostPrice(new BigDecimal("10.00"));
    product.setSalePrice(new BigDecimal("15.00"));

    Product originalProduct = new Product();
    originalProduct.setId(1);
    originalProduct.setName("Original Name");
    originalProduct.setDescription("Original Description");
    originalProduct.setCostPrice(new BigDecimal("10.00"));
    originalProduct.setSalePrice(new BigDecimal("15.00"));

    productMapper.updateEntityFromData(null, product);

    assertThat(product.getId()).isEqualTo(originalProduct.getId());
    assertThat(product.getName()).isEqualTo(originalProduct.getName());
    assertThat(product.getDescription()).isEqualTo(originalProduct.getDescription());
    assertThat(product.getCostPrice()).isEqualByComparingTo(originalProduct.getCostPrice());
    assertThat(product.getSalePrice()).isEqualByComparingTo(originalProduct.getSalePrice());
  }

  @Test
  void mapCategory_shouldReturnNull_whenCategoryIdIsNull() {
    Category category = productMapper.mapCategory(null);
    assertThat(category).isNull();
  }

  @Test
  void mapCategory_shouldCreateCategoryWithId_whenCategoryIdIsNotNull() {
    Integer categoryId = 1;
    Category category = productMapper.mapCategory(categoryId);

    assertThat(category).isNotNull();
    assertThat(category.getId()).isEqualTo(categoryId);
    assertThat(category.getName()).isNull();
  }
}