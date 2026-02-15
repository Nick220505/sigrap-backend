package com.sigrap.product.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.product.application.port.in.CreateProductUseCase;
import com.sigrap.product.application.port.in.DeleteProductUseCase;
import com.sigrap.product.application.port.in.GetProductUseCase;
import com.sigrap.product.application.port.in.UpdateProductUseCase;
import com.sigrap.product.application.port.in.command.CreateProductCommand;
import com.sigrap.product.application.port.in.command.UpdateProductCommand;
import com.sigrap.product.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Integration tests for ProductController.
 * Tests the REST adapter with mocked use cases to verify HTTP handling,
 * request/response mapping, and error handling.
 */
class ProductControllerTest {

    private MockMvc mockMvc;
    private CreateProductUseCase createProductUseCase;
    private GetProductUseCase getProductUseCase;
    private UpdateProductUseCase updateProductUseCase;
    private DeleteProductUseCase deleteProductUseCase;
    private ProductResponseMapper responseMapper;
    private ObjectMapper objectMapper;

    @ControllerAdvice
    public static class TestExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @BeforeEach
    void setup() {
        createProductUseCase = mock(CreateProductUseCase.class);
        getProductUseCase = mock(GetProductUseCase.class);
        updateProductUseCase = mock(UpdateProductUseCase.class);
        deleteProductUseCase = mock(DeleteProductUseCase.class);
        responseMapper = new ProductResponseMapper();
        objectMapper = new ObjectMapper();

        ProductController productController = new ProductController(
            createProductUseCase,
            getProductUseCase,
            updateProductUseCase,
            deleteProductUseCase,
            responseMapper
        );

        mockMvc = standaloneSetup(productController)
            .setControllerAdvice(new TestExceptionHandler())
            .build();
    }

    @Test
    void create_shouldCreateProduct() throws Exception {
        // Given
        ProductRequest request = new ProductRequest(
            "Laptop",
            "High-performance laptop",
            new BigDecimal("800.00"),
            new BigDecimal("1200.00"),
            50,
            10,
            1L
        );

        Product product = new Product(
            new ProductId(1L),
            new ProductName("Laptop"),
            "High-performance laptop",
            new ProductPrice(new BigDecimal("800.00")),
            new ProductPrice(new BigDecimal("1200.00")),
            new ProductStock(50),
            new ProductStock(10),
            new CategoryId(1L),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(createProductUseCase.create(any(CreateProductCommand.class))).thenReturn(product);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("Laptop"))
            .andExpect(jsonPath("$.description").value("High-performance laptop"))
            .andExpect(jsonPath("$.costPrice").value(800.00))
            .andExpect(jsonPath("$.salePrice").value(1200.00))
            .andExpect(jsonPath("$.stock").value(50))
            .andExpect(jsonPath("$.minimumStockThreshold").value(10))
            .andExpect(jsonPath("$.categoryId").value(1L));

        verify(createProductUseCase).create(any(CreateProductCommand.class));
    }

    @Test
    void create_shouldCreateProductWithoutDescription() throws Exception {
        // Given
        ProductRequest request = new ProductRequest(
            "Mouse",
            null,
            new BigDecimal("15.00"),
            new BigDecimal("25.00"),
            100,
            20,
            1L
        );

        Product product = new Product(
            new ProductId(2L),
            new ProductName("Mouse"),
            null,
            new ProductPrice(new BigDecimal("15.00")),
            new ProductPrice(new BigDecimal("25.00")),
            new ProductStock(100),
            new ProductStock(20),
            new CategoryId(1L),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(createProductUseCase.create(any(CreateProductCommand.class))).thenReturn(product);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(2L))
            .andExpect(jsonPath("$.name").value("Mouse"))
            .andExpect(jsonPath("$.description").isEmpty());

        verify(createProductUseCase).create(any(CreateProductCommand.class));
    }

    @Test
    void getById_shouldReturnProduct_whenExists() throws Exception {
        // Given
        Long id = 1L;
        Product product = new Product(
            new ProductId(id),
            new ProductName("Keyboard"),
            "Mechanical keyboard",
            new ProductPrice(new BigDecimal("50.00")),
            new ProductPrice(new BigDecimal("80.00")),
            new ProductStock(30),
            new ProductStock(5),
            new CategoryId(1L),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(getProductUseCase.getById(any(ProductId.class))).thenReturn(product);

        // When & Then
        mockMvc.perform(get("/api/products/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.name").value("Keyboard"))
            .andExpect(jsonPath("$.description").value("Mechanical keyboard"))
            .andExpect(jsonPath("$.costPrice").value(50.00))
            .andExpect(jsonPath("$.salePrice").value(80.00))
            .andExpect(jsonPath("$.stock").value(30))
            .andExpect(jsonPath("$.minimumStockThreshold").value(5))
            .andExpect(jsonPath("$.categoryId").value(1L));

        verify(getProductUseCase).getById(any(ProductId.class));
    }

    @Test
    void getById_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
        // Given
        Long id = 999L;
        when(getProductUseCase.getById(any(ProductId.class)))
            .thenThrow(new IllegalArgumentException("Product not found"));

        // When & Then
        mockMvc.perform(get("/api/products/{id}", id))
            .andExpect(status().isNotFound());

        verify(getProductUseCase).getById(any(ProductId.class));
    }

    @Test
    void getAll_shouldReturnAllProducts() throws Exception {
        // Given
        Product product1 = new Product(
            new ProductId(1L),
            new ProductName("Product 1"),
            "Description 1",
            new ProductPrice(new BigDecimal("10.00")),
            new ProductPrice(new BigDecimal("15.00")),
            new ProductStock(100),
            new ProductStock(10),
            new CategoryId(1L),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        Product product2 = new Product(
            new ProductId(2L),
            new ProductName("Product 2"),
            "Description 2",
            new ProductPrice(new BigDecimal("20.00")),
            new ProductPrice(new BigDecimal("30.00")),
            new ProductStock(50),
            new ProductStock(5),
            new CategoryId(1L),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        List<Product> products = List.of(product1, product2);
        when(getProductUseCase.getAll()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].name").value("Product 1"))
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$[1].name").value("Product 2"));

        verify(getProductUseCase).getAll();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoProducts() throws Exception {
        // Given
        when(getProductUseCase.getAll()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        verify(getProductUseCase).getAll();
    }

    @Test
    void getByCategoryId_shouldReturnProductsInCategory() throws Exception {
        // Given
        Long categoryId = 1L;
        Product product1 = new Product(
            new ProductId(1L),
            new ProductName("Product A"),
            "Category 1 product",
            new ProductPrice(new BigDecimal("10.00")),
            new ProductPrice(new BigDecimal("15.00")),
            new ProductStock(100),
            new ProductStock(10),
            new CategoryId(categoryId),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        Product product2 = new Product(
            new ProductId(2L),
            new ProductName("Product B"),
            "Category 1 product",
            new ProductPrice(new BigDecimal("20.00")),
            new ProductPrice(new BigDecimal("30.00")),
            new ProductStock(50),
            new ProductStock(5),
            new CategoryId(categoryId),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        List<Product> products = List.of(product1, product2);
        when(getProductUseCase.getByCategoryId(any(CategoryId.class))).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/products/category/{categoryId}", categoryId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].categoryId").value(categoryId))
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$[1].categoryId").value(categoryId));

        verify(getProductUseCase).getByCategoryId(any(CategoryId.class));
    }

    @Test
    void getByCategoryId_shouldReturnEmptyList_whenNoCategoryProducts() throws Exception {
        // Given
        Long categoryId = 999L;
        when(getProductUseCase.getByCategoryId(any(CategoryId.class))).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/products/category/{categoryId}", categoryId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        verify(getProductUseCase).getByCategoryId(any(CategoryId.class));
    }

    @Test
    void update_shouldUpdateProduct_whenExists() throws Exception {
        // Given
        Long id = 1L;
        ProductRequest request = new ProductRequest(
            "Updated Product",
            "Updated description",
            new BigDecimal("100.00"),
            new BigDecimal("150.00"),
            75,
            15,
            2L
        );

        Product updatedProduct = new Product(
            new ProductId(id),
            new ProductName("Updated Product"),
            "Updated description",
            new ProductPrice(new BigDecimal("100.00")),
            new ProductPrice(new BigDecimal("150.00")),
            new ProductStock(75),
            new ProductStock(15),
            new CategoryId(2L),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(updateProductUseCase.update(any(ProductId.class), any(UpdateProductCommand.class)))
            .thenReturn(updatedProduct);

        // When & Then
        mockMvc.perform(put("/api/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.name").value("Updated Product"))
            .andExpect(jsonPath("$.description").value("Updated description"))
            .andExpect(jsonPath("$.costPrice").value(100.00))
            .andExpect(jsonPath("$.salePrice").value(150.00))
            .andExpect(jsonPath("$.stock").value(75))
            .andExpect(jsonPath("$.minimumStockThreshold").value(15))
            .andExpect(jsonPath("$.categoryId").value(2L));

        verify(updateProductUseCase).update(any(ProductId.class), any(UpdateProductCommand.class));
    }

    @Test
    void update_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
        // Given
        Long id = 999L;
        ProductRequest request = new ProductRequest(
            "Updated Product",
            "Updated description",
            new BigDecimal("100.00"),
            new BigDecimal("150.00"),
            75,
            15,
            2L
        );

        when(updateProductUseCase.update(any(ProductId.class), any(UpdateProductCommand.class)))
            .thenThrow(new IllegalArgumentException("Product not found"));

        // When & Then
        mockMvc.perform(put("/api/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());

        verify(updateProductUseCase).update(any(ProductId.class), any(UpdateProductCommand.class));
    }

    @Test
    void delete_shouldDeleteProduct_whenExists() throws Exception {
        // Given
        Long id = 1L;
        doNothing().when(deleteProductUseCase).delete(any(ProductId.class));

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", id))
            .andExpect(status().isNoContent());

        verify(deleteProductUseCase).delete(any(ProductId.class));
    }

    @Test
    void delete_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
        // Given
        Long id = 999L;
        doThrow(new IllegalArgumentException("Product not found"))
            .when(deleteProductUseCase).delete(any(ProductId.class));

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", id))
            .andExpect(status().isNotFound());

        verify(deleteProductUseCase).delete(any(ProductId.class));
    }

    @Test
    void deleteAll_shouldDeleteMultipleProducts() throws Exception {
        // Given
        List<Long> ids = List.of(1L, 2L, 3L);
        doNothing().when(deleteProductUseCase).deleteAll(anyList());

        // When & Then
        mockMvc.perform(delete("/api/products/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
            .andExpect(status().isNoContent());

        verify(deleteProductUseCase).deleteAll(anyList());
    }

    @Test
    void deleteAll_shouldReturnNotFound_whenAnyProductDoesNotExist() throws Exception {
        // Given
        List<Long> ids = List.of(1L, 999L);
        doThrow(new IllegalArgumentException("Product not found"))
            .when(deleteProductUseCase).deleteAll(anyList());

        // When & Then
        mockMvc.perform(delete("/api/products/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
            .andExpect(status().isNotFound());

        verify(deleteProductUseCase).deleteAll(anyList());
    }

    @Test
    void deleteAllByIdLegacy_shouldDeleteMultipleProducts() throws Exception {
        // Given
        List<Long> ids = List.of(1L, 2L);
        doNothing().when(deleteProductUseCase).deleteAll(anyList());

        // When & Then
        mockMvc.perform(delete("/api/products/delete-many")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
            .andExpect(status().isNoContent());

        verify(deleteProductUseCase).deleteAll(anyList());
    }

    @Test
    void create_shouldHandleProductWithZeroStock() throws Exception {
        // Given
        ProductRequest request = new ProductRequest(
            "Out of Stock Product",
            "No stock available",
            new BigDecimal("10.00"),
            new BigDecimal("15.00"),
            0,
            10,
            1L
        );

        Product product = new Product(
            new ProductId(1L),
            new ProductName("Out of Stock Product"),
            "No stock available",
            new ProductPrice(new BigDecimal("10.00")),
            new ProductPrice(new BigDecimal("15.00")),
            new ProductStock(0),
            new ProductStock(10),
            new CategoryId(1L),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(createProductUseCase.create(any(CreateProductCommand.class))).thenReturn(product);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.stock").value(0));

        verify(createProductUseCase).create(any(CreateProductCommand.class));
    }

    @Test
    void create_shouldHandleProductWithoutCategory() throws Exception {
        // Given
        ProductRequest request = new ProductRequest(
            "Uncategorized Product",
            "No category",
            new BigDecimal("10.00"),
            new BigDecimal("15.00"),
            100,
            10,
            null
        );

        Product product = new Product(
            new ProductId(1L),
            new ProductName("Uncategorized Product"),
            "No category",
            new ProductPrice(new BigDecimal("10.00")),
            new ProductPrice(new BigDecimal("15.00")),
            new ProductStock(100),
            new ProductStock(10),
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(createProductUseCase.create(any(CreateProductCommand.class))).thenReturn(product);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.categoryId").isEmpty());

        verify(createProductUseCase).create(any(CreateProductCommand.class));
    }

    @Test
    void create_shouldHandleLargePriceValues() throws Exception {
        // Given
        ProductRequest request = new ProductRequest(
            "Expensive Product",
            "High-value item",
            new BigDecimal("99999.99"),
            new BigDecimal("149999.99"),
            1,
            1,
            1L
        );

        Product product = new Product(
            new ProductId(1L),
            new ProductName("Expensive Product"),
            "High-value item",
            new ProductPrice(new BigDecimal("99999.99")),
            new ProductPrice(new BigDecimal("149999.99")),
            new ProductStock(1),
            new ProductStock(1),
            new CategoryId(1L),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(createProductUseCase.create(any(CreateProductCommand.class))).thenReturn(product);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.costPrice").value(99999.99))
            .andExpect(jsonPath("$.salePrice").value(149999.99));

        verify(createProductUseCase).create(any(CreateProductCommand.class));
    }
}
