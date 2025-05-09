package com.sigrap.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sigrap.category.Category;
import com.sigrap.category.CategoryRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private ProductMapper productMapper;

  @InjectMocks
  private ProductService productService;

  @Test
  void findById_shouldReturnProductInfo_whenProductExists() {
    Integer id = 1;
    Product product = Product.builder()
      .id(id)
      .name("Test Product")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .build();

    ProductInfo productInfo = ProductInfo.builder()
      .id(id)
      .name("Test Product")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .build();

    when(productRepository.findById(id)).thenReturn(Optional.of(product));
    when(productMapper.toInfo(product)).thenReturn(productInfo);

    ProductInfo foundProductInfo = productService.findById(id);

    assertThat(foundProductInfo).isNotNull();
    assertThat(foundProductInfo.getId()).isEqualTo(id);
    assertThat(foundProductInfo.getName()).isEqualTo("Test Product");
    assertThat(foundProductInfo.getCostPrice()).isEqualByComparingTo(
      new BigDecimal("10.00")
    );
  }

  @Test
  void findById_shouldThrowException_whenProductDoesNotExist() {
    Integer id = 1;
    when(productRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      productService.findById(id);
    });
  }

  @Test
  void findAll_shouldReturnAllProductInfos() {
    Product product1 = Product.builder()
      .id(1)
      .name("Product 1")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .build();

    Product product2 = Product.builder()
      .id(2)
      .name("Product 2")
      .costPrice(new BigDecimal("20.00"))
      .salePrice(new BigDecimal("30.00"))
      .build();

    List<Product> products = List.of(product1, product2);

    ProductInfo productInfo1 = ProductInfo.builder()
      .id(1)
      .name("Product 1")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .build();

    ProductInfo productInfo2 = ProductInfo.builder()
      .id(2)
      .name("Product 2")
      .costPrice(new BigDecimal("20.00"))
      .salePrice(new BigDecimal("30.00"))
      .build();

    List<ProductInfo> productInfos = List.of(productInfo1, productInfo2);

    when(productRepository.findAll()).thenReturn(products);
    when(productMapper.toInfo(products.get(0))).thenReturn(productInfos.get(0));
    when(productMapper.toInfo(products.get(1))).thenReturn(productInfos.get(1));

    List<ProductInfo> allProductInfos = productService.findAll();

    assertThat(allProductInfos).hasSize(2);
    assertThat(allProductInfos.get(0).getName()).isEqualTo("Product 1");
    assertThat(allProductInfos.get(1).getName()).isEqualTo("Product 2");
  }

  @Test
  void create_shouldCreateProduct_withoutCategory() {
    ProductData productData = ProductData.builder()
      .name("New Product")
      .description("New Description")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .build();

    Product product = Product.builder()
      .name("New Product")
      .description("New Description")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .build();

    Product savedProduct = Product.builder()
      .id(1)
      .name("New Product")
      .description("New Description")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .build();

    ProductInfo productInfo = ProductInfo.builder()
      .id(1)
      .name("New Product")
      .description("New Description")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .build();

    when(productMapper.toEntity(productData)).thenReturn(product);
    when(productRepository.save(product)).thenReturn(savedProduct);
    when(productMapper.toInfo(savedProduct)).thenReturn(productInfo);

    ProductInfo createdProductInfo = productService.create(productData);

    assertThat(createdProductInfo).isNotNull();
    assertThat(createdProductInfo.getId()).isEqualTo(1);
    assertThat(createdProductInfo.getName()).isEqualTo("New Product");
    verify(productRepository).save(product);
  }

  @Test
  void create_shouldCreateProduct_withCategory() {
    Integer categoryId = 1;

    ProductData productData = ProductData.builder()
      .name("New Product")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .categoryId(categoryId)
      .build();

    Product product = Product.builder()
      .name("New Product")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .build();

    Category category = Category.builder()
      .id(categoryId)
      .name("Test Category")
      .build();

    Product savedProduct = Product.builder()
      .id(1)
      .name("New Product")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .category(category)
      .build();

    ProductInfo productInfo = ProductInfo.builder()
      .id(1)
      .name("New Product")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .build();

    when(productMapper.toEntity(productData)).thenReturn(product);
    when(categoryRepository.findById(categoryId)).thenReturn(
      Optional.of(category)
    );
    when(productRepository.save(product)).thenReturn(savedProduct);
    when(productMapper.toInfo(savedProduct)).thenReturn(productInfo);

    ProductInfo createdProductInfo = productService.create(productData);

    assertThat(createdProductInfo).isNotNull();
    assertThat(createdProductInfo.getId()).isEqualTo(1);
    assertThat(createdProductInfo.getName()).isEqualTo("New Product");
    verify(productRepository).save(product);
  }

  @Test
  void create_shouldThrowException_whenCategoryDoesNotExist() {
    Integer categoryId = 1;

    ProductData productData = ProductData.builder()
      .name("New Product")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .categoryId(categoryId)
      .build();

    Product product = Product.builder()
      .name("New Product")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .build();

    when(productMapper.toEntity(productData)).thenReturn(product);
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      productService.create(productData);
    });

    verify(productRepository, never()).save(any());
  }

  @Test
  void update_shouldUpdateProduct_whenProductExists() {
    Integer id = 1;
    Integer categoryId = 2;

    ProductData productData = ProductData.builder()
      .name("Updated Product")
      .description("Updated Description")
      .costPrice(new BigDecimal("15.00"))
      .salePrice(new BigDecimal("25.00"))
      .categoryId(categoryId)
      .build();

    Product existingProduct = Product.builder()
      .id(id)
      .name("Old Name")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .build();

    Category category = Category.builder()
      .id(categoryId)
      .name("Test Category")
      .build();

    Product updatedProduct = Product.builder()
      .id(id)
      .name("Updated Product")
      .description("Updated Description")
      .costPrice(new BigDecimal("15.00"))
      .salePrice(new BigDecimal("25.00"))
      .category(category)
      .build();

    ProductInfo productInfo = ProductInfo.builder()
      .id(id)
      .name("Updated Product")
      .description("Updated Description")
      .costPrice(new BigDecimal("15.00"))
      .salePrice(new BigDecimal("25.00"))
      .build();

    when(productRepository.findById(id)).thenReturn(
      Optional.of(existingProduct)
    );
    doNothing()
      .when(productMapper)
      .updateEntityFromData(productData, existingProduct);
    when(categoryRepository.findById(categoryId)).thenReturn(
      Optional.of(category)
    );
    when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
    when(productMapper.toInfo(updatedProduct)).thenReturn(productInfo);

    ProductInfo updatedProductInfo = productService.update(id, productData);

    assertThat(updatedProductInfo).isNotNull();
    assertThat(updatedProductInfo.getId()).isEqualTo(id);
    assertThat(updatedProductInfo.getName()).isEqualTo("Updated Product");
    verify(productRepository).save(existingProduct);
  }

  @Test
  void update_shouldRemoveCategory_whenCategoryIdIsNull() {
    Integer id = 1;

    ProductData productData = ProductData.builder()
      .name("Updated Product")
      .costPrice(new BigDecimal("15.00"))
      .salePrice(new BigDecimal("25.00"))
      .categoryId(null)
      .build();

    Product existingProduct = Product.builder()
      .id(id)
      .name("Old Name")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .category(Category.builder().id(1).build())
      .build();

    Product updatedProduct = Product.builder()
      .id(id)
      .name("Updated Product")
      .costPrice(new BigDecimal("15.00"))
      .salePrice(new BigDecimal("25.00"))
      .category(null)
      .build();

    ProductInfo productInfo = ProductInfo.builder()
      .id(id)
      .name("Updated Product")
      .costPrice(new BigDecimal("15.00"))
      .salePrice(new BigDecimal("25.00"))
      .build();

    when(productRepository.findById(id)).thenReturn(
      Optional.of(existingProduct)
    );
    doNothing()
      .when(productMapper)
      .updateEntityFromData(productData, existingProduct);
    when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
    when(productMapper.toInfo(updatedProduct)).thenReturn(productInfo);

    ProductInfo updatedProductInfo = productService.update(id, productData);

    assertThat(updatedProductInfo).isNotNull();
    assertThat(updatedProductInfo.getId()).isEqualTo(id);
    assertThat(updatedProductInfo.getName()).isEqualTo("Updated Product");
    assertThat(existingProduct.getCategory()).isNull();
    verify(productRepository).save(existingProduct);
  }

  @Test
  void update_shouldThrowException_whenProductDoesNotExist() {
    Integer id = 1;
    ProductData productData = ProductData.builder()
      .name("Updated Product")
      .build();

    when(productRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      productService.update(id, productData);
    });

    verify(productRepository, never()).save(any());
  }

  @Test
  void update_shouldThrowException_whenCategoryDoesNotExist() {
    Integer id = 1;
    Integer categoryId = 2;

    ProductData productData = ProductData.builder()
      .name("Updated Product")
      .categoryId(categoryId)
      .build();

    Product existingProduct = Product.builder().id(id).name("Old Name").build();

    when(productRepository.findById(id)).thenReturn(
      Optional.of(existingProduct)
    );
    doNothing()
      .when(productMapper)
      .updateEntityFromData(productData, existingProduct);
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      productService.update(id, productData);
    });

    verify(productRepository, never()).save(any());
  }

  @Test
  void delete_shouldDeleteProduct_whenProductExists() {
    Integer id = 1;
    Product product = Product.builder().id(id).build();

    when(productRepository.findById(id)).thenReturn(Optional.of(product));

    productService.delete(id);

    verify(productRepository).delete(product);
  }

  @Test
  void delete_shouldThrowException_whenProductDoesNotExist() {
    Integer id = 1;
    when(productRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      productService.delete(id);
    });

    verify(productRepository, never()).delete(any());
  }

  @Test
  void deleteAllById_shouldDeleteAllProducts_whenAllExist() {
    List<Integer> ids = List.of(1, 2);

    when(productRepository.existsById(1)).thenReturn(true);
    when(productRepository.existsById(2)).thenReturn(true);
    doNothing().when(productRepository).deleteAllById(ids);

    productService.deleteAllById(ids);

    verify(productRepository).deleteAllById(ids);
  }

  @Test
  void deleteAllById_shouldThrowException_whenAnyProductDoesNotExist() {
    List<Integer> ids = List.of(1, 2);

    when(productRepository.existsById(1)).thenReturn(true);
    when(productRepository.existsById(2)).thenReturn(false);

    assertThrows(EntityNotFoundException.class, () -> {
      productService.deleteAllById(ids);
    });

    verify(productRepository, never()).deleteAllById(any());
  }
}
