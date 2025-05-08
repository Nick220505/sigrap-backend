package com.sigrap.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

class ProductControllerTest {

  private MockMvc mockMvc;
  private ProductService productService;
  private ObjectMapper objectMapper;

  @ControllerAdvice
  public static class TestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
      return ResponseEntity.notFound().build();
    }
  }

  @BeforeEach
  void setup() {
    productService = mock(ProductService.class);
    objectMapper = new ObjectMapper();
    ProductController controller = new ProductController(productService);

    mockMvc = standaloneSetup(controller)
        .setControllerAdvice(new TestExceptionHandler())
        .build();
  }

  @Test
  void findAll_shouldReturnAllProducts() throws Exception {
    ProductInfo product1 = ProductInfo.builder()
        .id(1)
        .name("Product 1")
        .costPrice(new BigDecimal("10.00"))
        .salePrice(new BigDecimal("15.00"))
        .build();

    ProductInfo product2 = ProductInfo.builder()
        .id(2)
        .name("Product 2")
        .costPrice(new BigDecimal("20.00"))
        .salePrice(new BigDecimal("30.00"))
        .build();

    List<ProductInfo> products = List.of(product1, product2);
    when(productService.findAll()).thenReturn(products);

    mockMvc.perform(get("/api/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("Product 1"))
        .andExpect(jsonPath("$[0].costPrice").value(10.00))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].name").value("Product 2"))
        .andExpect(jsonPath("$[1].costPrice").value(20.00));
  }

  @Test
  void findById_shouldReturnProduct_whenExists() throws Exception {
    Integer id = 1;
    ProductInfo product = ProductInfo.builder()
        .id(id)
        .name("Test Product")
        .costPrice(new BigDecimal("10.00"))
        .salePrice(new BigDecimal("15.00"))
        .build();

    when(productService.findById(id)).thenReturn(product);

    mockMvc.perform(get("/api/products/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.name").value("Test Product"))
        .andExpect(jsonPath("$.costPrice").value(10.00))
        .andExpect(jsonPath("$.salePrice").value(15.00));
  }

  @Test
  void findById_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
    Integer id = 1;
    when(productService.findById(id)).thenThrow(new EntityNotFoundException());

    mockMvc.perform(get("/api/products/{id}", id))
        .andExpect(status().isNotFound());
  }

  @Test
  void create_shouldCreateProduct() throws Exception {
    ProductData productData = ProductData.builder()
        .name("New Product")
        .description("Test Description")
        .costPrice(new BigDecimal("10.00"))
        .salePrice(new BigDecimal("15.00"))
        .build();

    ProductInfo createdProduct = ProductInfo.builder()
        .id(1)
        .name("New Product")
        .description("Test Description")
        .costPrice(new BigDecimal("10.00"))
        .salePrice(new BigDecimal("15.00"))
        .build();

    when(productService.create(any(ProductData.class))).thenReturn(createdProduct);

    mockMvc.perform(post("/api/products")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(productData)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("New Product"))
        .andExpect(jsonPath("$.description").value("Test Description"))
        .andExpect(jsonPath("$.costPrice").value(10.00))
        .andExpect(jsonPath("$.salePrice").value(15.00));

    verify(productService).create(any(ProductData.class));
  }

  @Test
  void update_shouldUpdateProduct_whenExists() throws Exception {
    Integer id = 1;
    ProductData productData = ProductData.builder()
        .name("Updated Product")
        .description("Updated Description")
        .costPrice(new BigDecimal("15.00"))
        .salePrice(new BigDecimal("25.00"))
        .build();

    ProductInfo updatedProduct = ProductInfo.builder()
        .id(id)
        .name("Updated Product")
        .description("Updated Description")
        .costPrice(new BigDecimal("15.00"))
        .salePrice(new BigDecimal("25.00"))
        .build();

    when(productService.update(eq(id), any(ProductData.class))).thenReturn(updatedProduct);

    mockMvc.perform(put("/api/products/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(productData)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.name").value("Updated Product"))
        .andExpect(jsonPath("$.description").value("Updated Description"))
        .andExpect(jsonPath("$.costPrice").value(15.00))
        .andExpect(jsonPath("$.salePrice").value(25.00));

    verify(productService).update(eq(id), any(ProductData.class));
  }

  @Test
  void update_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
    Integer id = 1;
    ProductData productData = ProductData.builder()
        .name("Updated Product")
        .costPrice(new BigDecimal("15.00"))
        .salePrice(new BigDecimal("25.00"))
        .build();

    when(productService.update(eq(id), any(ProductData.class))).thenThrow(new EntityNotFoundException());

    mockMvc.perform(put("/api/products/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(productData)))
        .andExpect(status().isNotFound());
  }

  @Test
  void delete_shouldDeleteProduct_whenExists() throws Exception {
    Integer id = 1;
    doNothing().when(productService).delete(id);

    mockMvc.perform(delete("/api/products/{id}", id))
        .andExpect(status().isNoContent());

    verify(productService).delete(id);
  }

  @Test
  void delete_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
    Integer id = 1;
    doThrow(new EntityNotFoundException()).when(productService).delete(id);

    mockMvc.perform(delete("/api/products/{id}", id))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteAllById_shouldDeleteProducts() throws Exception {
    List<Integer> ids = List.of(1, 2);
    doNothing().when(productService).deleteAllById(ids);

    mockMvc.perform(delete("/api/products/delete-many")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(ids)))
        .andExpect(status().isNoContent());

    verify(productService).deleteAllById(ids);
  }

  @Test
  void deleteAllById_shouldReturnNotFound_whenAnyProductDoesNotExist() throws Exception {
    List<Integer> ids = List.of(1, 2);
    doThrow(new EntityNotFoundException()).when(productService).deleteAllById(ids);

    mockMvc.perform(delete("/api/products/delete-many")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(ids)))
        .andExpect(status().isNotFound());
  }
}