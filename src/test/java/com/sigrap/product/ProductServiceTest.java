package com.sigrap.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sigrap.category.Category;
import com.sigrap.category.CategoryInfo;
import com.sigrap.category.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> productService.findById(id)
    );
    assertThat(exception).hasMessage("Product not found with id: " + id);
    verify(productMapper, never()).toInfo(any());
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
    Long categoryId = 1L;
    Integer productId = 1;

    ProductData productData = ProductData.builder()
      .name("New Product")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .categoryId(categoryId.intValue())
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
      .id(productId)
      .name("New Product")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .category(category)
      .build();

    ProductInfo productInfo = ProductInfo.builder()
      .id(productId)
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
    assertThat(createdProductInfo.getId()).isEqualTo(productId);
    assertThat(createdProductInfo.getName()).isEqualTo("New Product");
    verify(productRepository).save(product);
  }

  @Test
  void create_shouldThrowException_whenCategoryDoesNotExist() {
    Long categoryId = 1L;

    ProductData productData = ProductData.builder()
      .name("New Product")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .categoryId(categoryId.intValue())
      .build();

    Product product = Product.builder()
      .name("New Product")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .build();

    when(productMapper.toEntity(productData)).thenReturn(product);
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> productService.create(productData)
    );
    assertThat(exception).hasMessage("Category not found: " + categoryId);
    verify(productRepository, never()).save(any());
  }

  @Test
  void update_shouldUpdateProduct_whenProductExists() {
    Integer id = 1;
    ProductData productData = ProductData.builder()
      .name("Updated Product")
      .description("Updated Description")
      .costPrice(new BigDecimal("20.00"))
      .salePrice(new BigDecimal("30.00"))
      .build();

    Product existingProduct = Product.builder()
      .id(id)
      .name("Original Product")
      .description("Original Description")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .build();

    Product updatedProduct = Product.builder()
      .id(id)
      .name("Updated Product")
      .description("Updated Description")
      .costPrice(new BigDecimal("20.00"))
      .salePrice(new BigDecimal("30.00"))
      .build();

    ProductInfo productInfo = ProductInfo.builder()
      .id(id)
      .name("Updated Product")
      .description("Updated Description")
      .costPrice(new BigDecimal("20.00"))
      .salePrice(new BigDecimal("30.00"))
      .build();

    when(productRepository.findById(id)).thenReturn(
      Optional.of(existingProduct)
    );
    when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
    when(productMapper.toInfo(updatedProduct)).thenReturn(productInfo);

    ProductInfo updatedProductInfo = productService.update(id, productData);

    assertThat(updatedProductInfo).isNotNull();
    assertThat(updatedProductInfo.getId()).isEqualTo(id);
    assertThat(updatedProductInfo.getName()).isEqualTo("Updated Product");
    assertThat(updatedProductInfo.getDescription()).isEqualTo(
      "Updated Description"
    );
    assertThat(updatedProductInfo.getCostPrice()).isEqualByComparingTo(
      new BigDecimal("20.00")
    );
    assertThat(updatedProductInfo.getSalePrice()).isEqualByComparingTo(
      new BigDecimal("30.00")
    );
    verify(productRepository).save(existingProduct);
  }

  @Test
  void update_shouldUpdateProductWithCategory_whenProductExists() {
    Integer id = 1;
    Long categoryId = 1L;

    ProductData productData = ProductData.builder()
      .name("Updated Product")
      .costPrice(new BigDecimal("20.00"))
      .salePrice(new BigDecimal("30.00"))
      .categoryId(categoryId.intValue())
      .build();

    Product existingProduct = Product.builder()
      .id(id)
      .name("Original Product")
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
      .costPrice(new BigDecimal("20.00"))
      .salePrice(new BigDecimal("30.00"))
      .category(category)
      .build();

    CategoryInfo categoryInfo = CategoryInfo.builder()
      .id(categoryId)
      .name("Test Category")
      .build();

    ProductInfo productInfo = ProductInfo.builder()
      .id(id)
      .name("Updated Product")
      .costPrice(new BigDecimal("20.00"))
      .salePrice(new BigDecimal("30.00"))
      .category(categoryInfo)
      .build();

    when(productRepository.findById(id)).thenReturn(
      Optional.of(existingProduct)
    );
    when(categoryRepository.findById(categoryId)).thenReturn(
      Optional.of(category)
    );
    when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
    when(productMapper.toInfo(updatedProduct)).thenReturn(productInfo);

    ProductInfo updatedProductInfo = productService.update(id, productData);

    assertThat(updatedProductInfo).isNotNull();
    assertThat(updatedProductInfo.getId()).isEqualTo(id);
    assertThat(updatedProductInfo.getName()).isEqualTo("Updated Product");
    assertThat(updatedProductInfo.getCategory().getId()).isEqualTo(categoryId);
    assertThat(updatedProductInfo.getCategory().getName()).isEqualTo(
      "Test Category"
    );
    verify(productRepository).save(existingProduct);
  }

  @Test
  void update_shouldRemoveCategory_whenCategoryIdIsNull() {
    Integer id = 1;
    Long categoryId = 1L;

    ProductData productData = ProductData.builder()
      .name("Updated Product")
      .costPrice(new BigDecimal("20.00"))
      .salePrice(new BigDecimal("30.00"))
      .categoryId(null)
      .build();

    Category category = Category.builder()
      .id(categoryId)
      .name("Test Category")
      .build();

    Product existingProduct = Product.builder()
      .id(id)
      .name("Original Product")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .category(category)
      .build();

    Product updatedProduct = Product.builder()
      .id(id)
      .name("Updated Product")
      .costPrice(new BigDecimal("20.00"))
      .salePrice(new BigDecimal("30.00"))
      .category(null)
      .build();

    ProductInfo productInfo = ProductInfo.builder()
      .id(id)
      .name("Updated Product")
      .costPrice(new BigDecimal("20.00"))
      .salePrice(new BigDecimal("30.00"))
      .category(null)
      .build();

    when(productRepository.findById(id)).thenReturn(
      Optional.of(existingProduct)
    );
    when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
    when(productMapper.toInfo(updatedProduct)).thenReturn(productInfo);

    ProductInfo updatedProductInfo = productService.update(id, productData);

    assertThat(updatedProductInfo).isNotNull();
    assertThat(updatedProductInfo.getId()).isEqualTo(id);
    assertThat(updatedProductInfo.getName()).isEqualTo("Updated Product");
    assertThat(updatedProductInfo.getCategory()).isNull();
    verify(productRepository).save(existingProduct);
  }

  @Test
  void update_shouldThrowException_whenProductDoesNotExist() {
    Integer id = 1;
    ProductData productData = ProductData.builder()
      .name("Updated Product")
      .costPrice(new BigDecimal("20.00"))
      .salePrice(new BigDecimal("30.00"))
      .build();

    when(productRepository.findById(id)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> productService.update(id, productData)
    );
    assertThat(exception).hasMessage("Product not found with id: " + id);
    verify(productRepository, never()).save(any());
  }

  @Test
  void update_shouldThrowException_whenCategoryDoesNotExist() {
    Integer id = 1;
    Long categoryId = 1L;

    ProductData productData = ProductData.builder()
      .name("Updated Product")
      .costPrice(new BigDecimal("20.00"))
      .salePrice(new BigDecimal("30.00"))
      .categoryId(categoryId.intValue())
      .build();

    Product existingProduct = Product.builder()
      .id(id)
      .name("Original Product")
      .costPrice(new BigDecimal("10.00"))
      .salePrice(new BigDecimal("15.00"))
      .build();

    when(productRepository.findById(id)).thenReturn(
      Optional.of(existingProduct)
    );
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> productService.update(id, productData)
    );
    assertThat(exception).hasMessage("Category not found: " + categoryId);
    verify(productRepository, never()).save(any());
  }

  @Test
  void delete_shouldDeleteProduct_whenProductExists() {
    Integer id = 1;
    Product product = Product.builder().id(id).build();

    when(productRepository.findById(id)).thenReturn(Optional.of(product));
    doNothing().when(productRepository).delete(product);

    productService.delete(id);

    verify(productRepository).delete(product);
  }

  @Test
  void delete_shouldThrowException_whenProductDoesNotExist() {
    Integer id = 1;
    when(productRepository.findById(id)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> productService.delete(id)
    );
    assertThat(exception).hasMessage("Product not found with id: " + id);
    verify(productRepository, never()).delete(any());
  }

  @Test
  void deleteAllById_shouldDeleteAllProducts_whenAllExist() {
    List<Integer> ids = List.of(1, 2);
    Product product1 = Product.builder().id(1).build();
    Product product2 = Product.builder().id(2).build();

    when(productRepository.findById(1)).thenReturn(Optional.of(product1));
    when(productRepository.findById(2)).thenReturn(Optional.of(product2));
    doNothing().when(productRepository).deleteAll(List.of(product1, product2));

    productService.deleteAllById(ids);

    verify(productRepository).deleteAll(List.of(product1, product2));
  }

  @Test
  void deleteAllById_shouldThrowException_whenAnyProductDoesNotExist() {
    List<Integer> ids = List.of(1, 2);
    Product product1 = Product.builder().id(1).build();

    when(productRepository.findById(1)).thenReturn(Optional.of(product1));
    when(productRepository.findById(2)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> productService.deleteAllById(ids)
    );
    assertThat(exception).hasMessage("Product not found with id: 2");
    verify(productRepository, never()).deleteAll(any());
  }
}
