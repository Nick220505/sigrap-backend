package com.sigrap.product;

import com.sigrap.category.CategoryController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

/**
 * REST controller for managing products in the inventory system.
 * Provides endpoints for CRUD operations on products.
 *
 * <p>This controller manages:
 * <ul>
 *   <li>Product creation and validation</li>
 *   <li>Product retrieval (single and bulk)</li>
 *   <li>Product updates</li>
 *   <li>Product deletion (single and bulk)</li>
 *   <li>Inventory tracking</li>
 * </ul></p>
 *
 * <p>Product Management Features:
 * <ul>
 *   <li>Category-based organization</li>
 *   <li>Stock level tracking</li>
 *   <li>Price management</li>
 *   <li>Product details maintenance</li>
 * </ul></p>
 *
 * <p>Usage Examples:
 * <pre>
 * // Create product
 * POST /api/products
 * {
 *   "name": "Notebook",
 *   "description": "100-page lined notebook",
 *   "price": 5.99,
 *   "stock": 100,
 *   "categoryId": 1
 * }
 *
 * // Update product
 * PUT /api/products/1
 * {
 *   "name": "Premium Notebook",
 *   "description": "100-page premium lined notebook",
 *   "price": 7.99,
 *   "stock": 150,
 *   "categoryId": 1
 * }
 * </pre></p>
 *
 * @see ProductService
 * @see Product
 * @see CategoryController
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(
  name = "Product Management",
  description = "Operations for managing products"
)
public class ProductController {

  private final ProductService productService;

  /**
   * Retrieves all products in the system.
   *
   * <p>This endpoint provides a complete list of all available products,
   * which can be used for:
   * <ul>
   *   <li>Inventory management</li>
   *   <li>Product catalogs</li>
   *   <li>Stock level monitoring</li>
   * </ul></p>
   *
   * @return List of all products with their details
   */
  @Operation(
    summary = "Get all products",
    description = "Retrieves a list of all products"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Products retrieved successfully"
  )
  @GetMapping
  public List<ProductInfo> findAll() {
    return productService.findAll();
  }

  /**
   * Retrieves a specific product by its ID.
   *
   * <p>This endpoint is useful for:
   * <ul>
   *   <li>Viewing product details</li>
   *   <li>Pre-populating edit forms</li>
   *   <li>Stock level checking</li>
   * </ul></p>
   *
   * @param id The unique identifier of the product
   * @return The product information
   * @throws EntityNotFoundException if product not found
   */
  @Operation(
    summary = "Get product by ID",
    description = "Retrieves a product by its ID"
  )
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "Product found"),
      @ApiResponse(
        responseCode = "404",
        description = "Product not found",
        content = @Content
      ),
    }
  )
  @GetMapping("/{id}")
  public ProductInfo findById(
    @Parameter(
      description = "ID of the product to retrieve"
    ) @PathVariable Integer id
  ) {
    return productService.findById(id);
  }

  /**
   * Creates a new product.
   *
   * <p>This endpoint:
   * <ul>
   *   <li>Validates product data</li>
   *   <li>Verifies category existence</li>
   *   <li>Creates new product record</li>
   *   <li>Initializes inventory tracking</li>
   * </ul></p>
   *
   * <p>Required fields:
   * <ul>
   *   <li>name - Product name (must be unique)</li>
   *   <li>description - Product description</li>
   *   <li>price - Product price (must be positive)</li>
   *   <li>stock - Initial stock level (must be non-negative)</li>
   *   <li>categoryId - Valid category identifier</li>
   * </ul></p>
   *
   * @param productData The product data to create
   * @return The created product information
   * @throws IllegalArgumentException if product name already exists
   * @throws EntityNotFoundException if category not found
   */
  @Operation(
    summary = "Create a new product",
    description = "Creates a new product"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "201",
        description = "Product created successfully"
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Invalid input data",
        content = @Content
      ),
    }
  )
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProductInfo create(
    @Parameter(
      description = "Product data to create",
      required = true
    ) @Valid @RequestBody ProductData productData
  ) {
    return productService.create(productData);
  }

  /**
   * Updates an existing product.
   *
   * <p>This endpoint:
   * <ul>
   *   <li>Validates update data</li>
   *   <li>Verifies category if changed</li>
   *   <li>Updates product record</li>
   *   <li>Updates inventory tracking</li>
   * </ul></p>
   *
   * <p>Updatable fields:
   * <ul>
   *   <li>name - New product name (must be unique)</li>
   *   <li>description - New product description</li>
   *   <li>price - New price (must be positive)</li>
   *   <li>stock - New stock level (must be non-negative)</li>
   *   <li>categoryId - New category identifier</li>
   * </ul></p>
   *
   * @param id The ID of the product to update
   * @param productData The updated product data
   * @return The updated product information
   * @throws EntityNotFoundException if product or new category not found
   * @throws IllegalArgumentException if new name conflicts with existing product
   */
  @Operation(
    summary = "Update a product",
    description = "Updates an existing product by its ID"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Product updated successfully"
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Invalid input data",
        content = @Content
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Product not found",
        content = @Content
      ),
    }
  )
  @PutMapping("/{id}")
  public ProductInfo update(
    @Parameter(
      description = "ID of the product to update"
    ) @PathVariable Integer id,
    @Parameter(
      description = "Updated product data",
      required = true
    ) @Valid @RequestBody ProductData productData
  ) {
    return productService.update(id, productData);
  }

  /**
   * Deletes a product by its ID.
   *
   * <p>This endpoint:
   * <ul>
   *   <li>Verifies product existence</li>
   *   <li>Removes the product record</li>
   *   <li>Updates inventory records</li>
   * </ul></p>
   *
   * <p>Note: This operation is irreversible. Consider archiving
   * products instead of deletion if historical data is needed.</p>
   *
   * @param id The ID of the product to delete
   * @throws EntityNotFoundException if product not found
   */
  @Operation(
    summary = "Delete a product",
    description = "Deletes a product by its ID"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "204",
        description = "Product deleted successfully"
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Product not found",
        content = @Content
      ),
    }
  )
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
    @Parameter(
      description = "ID of the product to delete"
    ) @PathVariable Integer id
  ) {
    productService.delete(id);
  }

  /**
   * Deletes multiple products by their IDs.
   *
   * <p>This endpoint:
   * <ul>
   *   <li>Validates all product IDs</li>
   *   <li>Performs bulk deletion</li>
   *   <li>Updates inventory records</li>
   * </ul></p>
   *
   * <p>Note: This operation:
   * <ul>
   *   <li>Is atomic - all products are deleted or none</li>
   *   <li>Is irreversible - consider archiving if historical data is needed</li>
   *   <li>Will fail if any product doesn't exist</li>
   * </ul></p>
   *
   * @param ids List of product IDs to delete
   * @throws EntityNotFoundException if any product not found
   */
  @Operation(
    summary = "Delete multiple products",
    description = "Deletes multiple products by their IDs"
  )
  @ApiResponse(
    responseCode = "204",
    description = "Products deleted successfully"
  )
  @DeleteMapping("/delete-many")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAllById(
    @Parameter(
      description = "List of product IDs to delete",
      required = true
    ) @RequestBody List<Integer> ids
  ) {
    productService.deleteAllById(ids);
  }
}
