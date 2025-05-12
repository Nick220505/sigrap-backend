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
    Category category = Category.builder().id(1L).name("Test Category").build();

    Product product = Product.builder()
      .id(1)
      .name("Test Product")
      .description("Test Description")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .category(category)
      .build();

    ProductInfo productInfo = productMapper.toInfo(product);

    assertThat(productInfo).isNotNull();
    assertThat(productInfo.getId()).isEqualTo(1);
    assertThat(productInfo.getName()).isEqualTo("Test Product");
    assertThat(productInfo.getDescription()).isEqualTo("Test Description");
    assertThat(productInfo.getCostPrice()).isEqualByComparingTo(
      new BigDecimal("10.00")
    );
    assertThat(productInfo.getSalePrice()).isEqualByComparingTo(
      new BigDecimal("15.00")
    );
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
    Product product = Product.builder()
      .id(1)
      .name("Test Product")
      .description("Test Description")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .category(null)
      .build();

    ProductInfo productInfo = productMapper.toInfo(product);

    assertThat(productInfo).isNotNull();
    assertThat(productInfo.getId()).isEqualTo(1);
    assertThat(productInfo.getName()).isEqualTo("Test Product");
    assertThat(productInfo.getDescription()).isEqualTo("Test Description");
    assertThat(productInfo.getCostPrice()).isEqualByComparingTo(
      new BigDecimal("10.00")
    );
    assertThat(productInfo.getSalePrice()).isEqualByComparingTo(
      new BigDecimal("15.00")
    );
    assertThat(productInfo.getCategory()).isNull();
  }

  @Test
  void toEntity_shouldMapDataToEntity() {
    ProductData productData = ProductData.builder()
      .name("Test Product")
      .description("Test Description")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .categoryId(1)
      .build();

    Product product = productMapper.toEntity(productData);

    assertThat(product).isNotNull();
    assertThat(product.getId()).isNull();
    assertThat(product.getName()).isEqualTo("Test Product");
    assertThat(product.getDescription()).isEqualTo("Test Description");
    assertThat(product.getCostPrice()).isEqualByComparingTo(
      new BigDecimal("10.00")
    );
    assertThat(product.getSalePrice()).isEqualByComparingTo(
      new BigDecimal("15.00")
    );
    assertThat(product.getCategory()).isNull();
  }

  @Test
  void toEntity_shouldReturnNull_whenProductDataIsNull() {
    Product product = productMapper.toEntity(null);

    assertThat(product).isNull();
  }

  @Test
  void updateEntityFromData_shouldUpdateEntityWithDataValues() {
    Product product = Product.builder()
      .id(1)
      .name("Old Name")
      .description("Old Description")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .build();

    ProductData productData = ProductData.builder()
      .name("New Name")
      .description("New Description")
      .costPrice(new BigDecimal("20.00"))
      .salePrice(new BigDecimal("30.00"))
      .categoryId(1)
      .build();

    productMapper.updateEntityFromData(productData, product);

    assertThat(product.getId()).isEqualTo(1);
    assertThat(product.getName()).isEqualTo("New Name");
    assertThat(product.getDescription()).isEqualTo("New Description");
    assertThat(product.getCostPrice()).isEqualByComparingTo(
      new BigDecimal("20.00")
    );
    assertThat(product.getSalePrice()).isEqualByComparingTo(
      new BigDecimal("30.00")
    );
    assertThat(product.getCategory()).isNull();
  }

  @Test
  void updateEntityFromData_shouldSetNullValues_whenDataFieldsAreNull() {
    Product product = Product.builder()
      .id(1)
      .name("Original Name")
      .description("Original Description")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .build();

    ProductData productData = ProductData.builder()
      .name("New Name")
      .costPrice(new BigDecimal("20.00"))
      .salePrice(new BigDecimal("30.00"))
      .build();

    productMapper.updateEntityFromData(productData, product);

    assertThat(product.getId()).isEqualTo(1);
    assertThat(product.getName()).isEqualTo("New Name");
    assertThat(product.getDescription()).isNull();
    assertThat(product.getCostPrice()).isEqualByComparingTo(
      new BigDecimal("20.00")
    );
    assertThat(product.getSalePrice()).isEqualByComparingTo(
      new BigDecimal("30.00")
    );
  }

  @Test
  void updateEntityFromData_shouldDoNothing_whenProductDataIsNull() {
    Product product = Product.builder()
      .id(1)
      .name("Original Name")
      .description("Original Description")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .build();

    Product originalProduct = Product.builder()
      .id(1)
      .name("Original Name")
      .description("Original Description")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .build();

    productMapper.updateEntityFromData(null, product);

    assertThat(product.getId()).isEqualTo(originalProduct.getId());
    assertThat(product.getName()).isEqualTo(originalProduct.getName());
    assertThat(product.getDescription()).isEqualTo(
      originalProduct.getDescription()
    );
    assertThat(product.getCostPrice()).isEqualByComparingTo(
      originalProduct.getCostPrice()
    );
    assertThat(product.getSalePrice()).isEqualByComparingTo(
      originalProduct.getSalePrice()
    );
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
