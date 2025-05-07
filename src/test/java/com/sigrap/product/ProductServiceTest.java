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
    Product product = new Product();
    product.setId(id);
    product.setName("Test Product");
    product.setCostPrice(new BigDecimal("10.00"));
    product.setSalePrice(new BigDecimal("15.00"));

    ProductInfo productInfo = new ProductInfo();
    productInfo.setId(id);
    productInfo.setName("Test Product");
    productInfo.setCostPrice(new BigDecimal("10.00"));
    productInfo.setSalePrice(new BigDecimal("15.00"));

    when(productRepository.findById(id)).thenReturn(Optional.of(product));
    when(productMapper.toInfo(product)).thenReturn(productInfo);

    ProductInfo foundProductInfo = productService.findById(id);

    assertThat(foundProductInfo).isNotNull();
    assertThat(foundProductInfo.getId()).isEqualTo(id);
    assertThat(foundProductInfo.getName()).isEqualTo("Test Product");
    assertThat(foundProductInfo.getCostPrice()).isEqualByComparingTo(new BigDecimal("10.00"));
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
    List<Product> products = List.of(
        createProduct(1, "Product 1", new BigDecimal("10.00"), new BigDecimal("15.00")),
        createProduct(2, "Product 2", new BigDecimal("20.00"), new BigDecimal("30.00")));

    List<ProductInfo> productInfos = List.of(
        createProductInfo(1, "Product 1", new BigDecimal("10.00"), new BigDecimal("15.00")),
        createProductInfo(2, "Product 2", new BigDecimal("20.00"), new BigDecimal("30.00")));

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
    ProductData productData = new ProductData();
    productData.setName("New Product");
    productData.setDescription("New Description");
    productData.setCostPrice(new BigDecimal("10.00"));
    productData.setSalePrice(new BigDecimal("15.00"));

    Product product = new Product();
    product.setName("New Product");
    product.setDescription("New Description");
    product.setCostPrice(new BigDecimal("10.00"));
    product.setSalePrice(new BigDecimal("15.00"));

    Product savedProduct = new Product();
    savedProduct.setId(1);
    savedProduct.setName("New Product");
    savedProduct.setDescription("New Description");
    savedProduct.setCostPrice(new BigDecimal("10.00"));
    savedProduct.setSalePrice(new BigDecimal("15.00"));

    ProductInfo productInfo = new ProductInfo();
    productInfo.setId(1);
    productInfo.setName("New Product");
    productInfo.setDescription("New Description");
    productInfo.setCostPrice(new BigDecimal("10.00"));
    productInfo.setSalePrice(new BigDecimal("15.00"));

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

    ProductData productData = new ProductData();
    productData.setName("New Product");
    productData.setCostPrice(new BigDecimal("10.00"));
    productData.setSalePrice(new BigDecimal("15.00"));
    productData.setCategoryId(categoryId);

    Product product = new Product();
    product.setName("New Product");
    product.setCostPrice(new BigDecimal("10.00"));
    product.setSalePrice(new BigDecimal("15.00"));

    Category category = new Category();
    category.setId(categoryId);
    category.setName("Test Category");

    Product savedProduct = new Product();
    savedProduct.setId(1);
    savedProduct.setName("New Product");
    savedProduct.setCostPrice(new BigDecimal("10.00"));
    savedProduct.setSalePrice(new BigDecimal("15.00"));
    savedProduct.setCategory(category);

    ProductInfo productInfo = new ProductInfo();
    productInfo.setId(1);
    productInfo.setName("New Product");
    productInfo.setCostPrice(new BigDecimal("10.00"));
    productInfo.setSalePrice(new BigDecimal("15.00"));

    when(productMapper.toEntity(productData)).thenReturn(product);
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
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

    ProductData productData = new ProductData();
    productData.setName("New Product");
    productData.setCostPrice(new BigDecimal("10.00"));
    productData.setSalePrice(new BigDecimal("15.00"));
    productData.setCategoryId(categoryId);

    Product product = new Product();
    product.setName("New Product");
    product.setCostPrice(new BigDecimal("10.00"));
    product.setSalePrice(new BigDecimal("15.00"));

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

    ProductData productData = new ProductData();
    productData.setName("Updated Product");
    productData.setDescription("Updated Description");
    productData.setCostPrice(new BigDecimal("15.00"));
    productData.setSalePrice(new BigDecimal("25.00"));
    productData.setCategoryId(categoryId);

    Product existingProduct = new Product();
    existingProduct.setId(id);
    existingProduct.setName("Old Name");
    existingProduct.setCostPrice(new BigDecimal("10.00"));
    existingProduct.setSalePrice(new BigDecimal("15.00"));

    Category category = new Category();
    category.setId(categoryId);
    category.setName("Test Category");

    Product updatedProduct = new Product();
    updatedProduct.setId(id);
    updatedProduct.setName("Updated Product");
    updatedProduct.setDescription("Updated Description");
    updatedProduct.setCostPrice(new BigDecimal("15.00"));
    updatedProduct.setSalePrice(new BigDecimal("25.00"));
    updatedProduct.setCategory(category);

    ProductInfo productInfo = new ProductInfo();
    productInfo.setId(id);
    productInfo.setName("Updated Product");
    productInfo.setDescription("Updated Description");
    productInfo.setCostPrice(new BigDecimal("15.00"));
    productInfo.setSalePrice(new BigDecimal("25.00"));

    when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
    doNothing().when(productMapper).updateEntityFromData(productData, existingProduct);
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
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

    ProductData productData = new ProductData();
    productData.setName("Updated Product");
    productData.setCostPrice(new BigDecimal("15.00"));
    productData.setSalePrice(new BigDecimal("25.00"));
    productData.setCategoryId(null);

    Product existingProduct = new Product();
    existingProduct.setId(id);
    existingProduct.setName("Old Name");
    existingProduct.setCostPrice(new BigDecimal("10.00"));
    existingProduct.setSalePrice(new BigDecimal("15.00"));
    existingProduct.setCategory(new Category());

    Product updatedProduct = new Product();
    updatedProduct.setId(id);
    updatedProduct.setName("Updated Product");
    updatedProduct.setCostPrice(new BigDecimal("15.00"));
    updatedProduct.setSalePrice(new BigDecimal("25.00"));
    updatedProduct.setCategory(null);

    ProductInfo productInfo = new ProductInfo();
    productInfo.setId(id);
    productInfo.setName("Updated Product");
    productInfo.setCostPrice(new BigDecimal("15.00"));
    productInfo.setSalePrice(new BigDecimal("25.00"));

    when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
    doNothing().when(productMapper).updateEntityFromData(productData, existingProduct);
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
    ProductData productData = new ProductData();
    productData.setName("Updated Product");

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

    ProductData productData = new ProductData();
    productData.setName("Updated Product");
    productData.setCategoryId(categoryId);

    Product existingProduct = new Product();
    existingProduct.setId(id);
    existingProduct.setName("Old Name");

    when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
    doNothing().when(productMapper).updateEntityFromData(productData, existingProduct);
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      productService.update(id, productData);
    });

    verify(productRepository, never()).save(any());
  }

  @Test
  void delete_shouldDeleteProduct_whenProductExists() {
    Integer id = 1;
    Product product = new Product();
    product.setId(id);

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

  private Product createProduct(Integer id, String name, BigDecimal costPrice, BigDecimal salePrice) {
    Product product = new Product();
    product.setId(id);
    product.setName(name);
    product.setCostPrice(costPrice);
    product.setSalePrice(salePrice);
    return product;
  }

  private ProductInfo createProductInfo(Integer id, String name, BigDecimal costPrice, BigDecimal salePrice) {
    ProductInfo productInfo = new ProductInfo();
    productInfo.setId(id);
    productInfo.setName(name);
    productInfo.setCostPrice(costPrice);
    productInfo.setSalePrice(salePrice);
    return productInfo;
  }
}