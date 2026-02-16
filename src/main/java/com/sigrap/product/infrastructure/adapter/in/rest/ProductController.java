package com.sigrap.product.infrastructure.adapter.in.rest;

import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.product.application.port.in.CreateProductUseCase;
import com.sigrap.product.application.port.in.DeleteProductUseCase;
import com.sigrap.product.application.port.in.GetProductUseCase;
import com.sigrap.product.application.port.in.UpdateProductUseCase;
import com.sigrap.product.application.port.in.command.CreateProductCommand;
import com.sigrap.product.application.port.in.command.UpdateProductCommand;
import com.sigrap.product.domain.model.Product;
import com.sigrap.product.domain.model.ProductId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for product operations (Hexagonal Architecture).
 * This is an input adapter that translates HTTP requests to use case calls.
 * It handles HTTP concerns (validation, status codes, request/response mapping)
 * and delegates business logic to use cases.
 */
@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Management", description = "APIs for managing products including inventory, pricing, and categorization")
public class ProductController {
    
    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final ProductResponseMapper responseMapper;
    
    /**
     * Constructor injection of use cases and mapper.
     *
     * @param createProductUseCase use case for creating products
     * @param getProductUseCase use case for retrieving products
     * @param updateProductUseCase use case for updating products
     * @param deleteProductUseCase use case for deleting products
     * @param responseMapper mapper for converting domain entities to response DTOs
     */
    public ProductController(
            CreateProductUseCase createProductUseCase,
            GetProductUseCase getProductUseCase,
            UpdateProductUseCase updateProductUseCase,
            DeleteProductUseCase deleteProductUseCase,
            ProductResponseMapper responseMapper) {
        this.createProductUseCase = createProductUseCase;
        this.getProductUseCase = getProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.responseMapper = responseMapper;
    }
    
    /**
     * Creates a new product.
     * POST /api/products
     *
     * @param request the product creation request
     * @return the created product response with HTTP 201 status
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a new product",
        description = "Creates a new product with name, description, pricing, stock levels, and optional category assignment. " +
                      "The product name must be unique across the system."
    )
    @ApiResponse(
        responseCode = "201",
        description = "Product created successfully",
        content = @Content(schema = @Schema(implementation = ProductResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request - validation errors or duplicate product name"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        CreateProductCommand command = new CreateProductCommand(
            request.name(),
            request.description(),
            request.costPrice(),
            request.salePrice(),
            request.stock(),
            request.minimumStockThreshold(),
            request.categoryId()
        );
        Product product = createProductUseCase.create(command);
        return responseMapper.toResponse(product);
    }
    
    /**
     * Retrieves a product by its ID.
     * GET /api/products/{id}
     *
     * @param id the product identifier
     * @return the product response with HTTP 200 status
     * @throws IllegalArgumentException if the product is not found
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get product by ID",
        description = "Retrieves a single product by its unique identifier including all details like pricing, stock, and category"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Product found successfully",
        content = @Content(schema = @Schema(implementation = ProductResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Product not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public ProductResponse getById(
        @Parameter(description = "Product unique identifier", required = true, example = "1")
        @PathVariable Long id
    ) {
        ProductId productId = new ProductId(id);
        Product product = getProductUseCase.getById(productId);
        return responseMapper.toResponse(product);
    }
    
    /**
     * Retrieves all products.
     * GET /api/products
     *
     * @return a list of all product responses with HTTP 200 status
     */
    @GetMapping
    @Operation(
        summary = "Get all products",
        description = "Retrieves a list of all products in the system with their complete details"
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of products retrieved successfully",
        content = @Content(schema = @Schema(implementation = ProductResponse.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public List<ProductResponse> getAll() {
        return getProductUseCase.getAll().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Retrieves all products belonging to a specific category.
     * GET /api/products/category/{categoryId}
     *
     * @param categoryId the category identifier
     * @return a list of product responses in the category with HTTP 200 status
     */
    @GetMapping("/category/{categoryId}")
    @Operation(
        summary = "Get products by category",
        description = "Retrieves all products that belong to a specific category"
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of products in category retrieved successfully",
        content = @Content(schema = @Schema(implementation = ProductResponse.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public List<ProductResponse> getByCategoryId(
        @Parameter(description = "Category unique identifier", required = true, example = "1")
        @PathVariable Long categoryId
    ) {
        CategoryId catId = new CategoryId(categoryId);
        return getProductUseCase.getByCategoryId(catId).stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Updates an existing product.
     * PUT /api/products/{id}
     *
     * @param id the product identifier
     * @param request the product update request
     * @return the updated product response with HTTP 200 status
     * @throws IllegalArgumentException if the product is not found or name conflicts
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing product",
        description = "Updates a product's details including name, description, pricing, stock levels, and category. " +
                      "The product name must remain unique."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Product updated successfully",
        content = @Content(schema = @Schema(implementation = ProductResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request - validation errors or duplicate product name"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Product not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public ProductResponse update(
            @Parameter(description = "Product unique identifier", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductId productId = new ProductId(id);
        UpdateProductCommand command = new UpdateProductCommand(
            request.name(),
            request.description(),
            request.costPrice(),
            request.salePrice(),
            request.stock(),
            request.minimumStockThreshold(),
            request.categoryId()
        );
        Product product = updateProductUseCase.update(productId, command);
        return responseMapper.toResponse(product);
    }
    
    /**
     * Deletes a product by its ID.
     * DELETE /api/products/{id}
     *
     * @param id the product identifier
     * @return HTTP 204 No Content status
     * @throws IllegalArgumentException if the product is not found
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a product",
        description = "Deletes a product by its unique identifier. This operation cannot be undone."
    )
    @ApiResponse(
        responseCode = "204",
        description = "Product deleted successfully"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Product not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public void delete(
        @Parameter(description = "Product unique identifier", required = true, example = "1")
        @PathVariable Long id
    ) {
        ProductId productId = new ProductId(id);
        deleteProductUseCase.delete(productId);
    }
    
    /**
     * Deletes multiple products by their IDs (batch delete).
     * DELETE /api/products/batch
     *
     * @param ids the list of product identifiers to delete
     * @return HTTP 204 No Content status
     * @throws IllegalArgumentException if any of the products are not found
     */
    @DeleteMapping("/batch")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete multiple products",
        description = "Deletes multiple products in a single operation. All specified products must exist."
    )
    @ApiResponse(
        responseCode = "204",
        description = "Products deleted successfully"
    )
    @ApiResponse(
        responseCode = "404",
        description = "One or more products not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public void deleteAll(
        @Parameter(description = "List of product IDs to delete", required = true)
        @RequestBody List<Long> ids
    ) {
        List<ProductId> productIds = ids.stream()
            .map(ProductId::new)
            .toList();
        deleteProductUseCase.deleteAll(productIds);
    }
    
    /**
     * Deletes multiple products by their IDs (legacy endpoint for backward compatibility).
     * DELETE /api/products/delete-many
     * 
     * @deprecated Use {@link #deleteAll(List)} with /batch endpoint instead.
     *             This endpoint is maintained for backward compatibility and will be removed in a future version.
     *
     * @param ids the list of product identifiers to delete
     * @return HTTP 204 No Content status
     * @throws IllegalArgumentException if any of the products are not found
     */
    @Deprecated(since = "Hexagonal architecture migration", forRemoval = true)
    @DeleteMapping("/delete-many")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete multiple products (deprecated)",
        description = "Legacy endpoint for batch deletion. Use /batch endpoint instead.",
        deprecated = true
    )
    @ApiResponse(
        responseCode = "204",
        description = "Products deleted successfully"
    )
    @ApiResponse(
        responseCode = "404",
        description = "One or more products not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public void deleteAllByIdLegacy(
        @Parameter(description = "List of product IDs to delete", required = true)
        @RequestBody List<Long> ids
    ) {
        // Delegate to the new endpoint implementation
        deleteAll(ids);
    }
}
