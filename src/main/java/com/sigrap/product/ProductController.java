package com.sigrap.product;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Operations for managing products")
public class ProductController {

  private final ProductService productService;

  @Operation(summary = "Get all products", description = "Retrieves a list of all products")
  @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
  @GetMapping
  public List<ProductInfo> findAll() {
    return productService.findAll();
  }

  @Operation(summary = "Get product by ID", description = "Retrieves a product by its ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Product found"),
      @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
  })
  @GetMapping("/{id}")
  public ProductInfo findById(
      @Parameter(description = "ID of the product to retrieve") @PathVariable Integer id) {
    return productService.findById(id);
  }

  @Operation(summary = "Create a new product", description = "Creates a new product")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Product created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProductInfo create(
      @Parameter(description = "Product data to create", required = true) @Valid @RequestBody ProductData productData) {
    return productService.create(productData);
  }

  @Operation(summary = "Update a product", description = "Updates an existing product by its ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Product updated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
  })
  @PutMapping("/{id}")
  public ProductInfo update(
      @Parameter(description = "ID of the product to update") @PathVariable Integer id,
      @Parameter(description = "Updated product data", required = true) @Valid @RequestBody ProductData productData) {
    return productService.update(id, productData);
  }

  @Operation(summary = "Delete a product", description = "Deletes a product by its ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
  })
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
      @Parameter(description = "ID of the product to delete") @PathVariable Integer id) {
    productService.delete(id);
  }

  @Operation(summary = "Delete multiple products", description = "Deletes multiple products by their IDs")
  @ApiResponse(responseCode = "204", description = "Products deleted successfully")
  @DeleteMapping("/delete-many")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAllById(
      @Parameter(description = "List of product IDs to delete", required = true) @RequestBody List<Integer> ids) {
    productService.deleteAllById(ids);
  }
}