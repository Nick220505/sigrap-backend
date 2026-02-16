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
    public ProductResponse getById(@PathVariable Long id) {
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
    public List<ProductResponse> getByCategoryId(@PathVariable Long categoryId) {
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
    public ProductResponse update(
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
    public void delete(@PathVariable Long id) {
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
    public void deleteAll(@RequestBody List<Long> ids) {
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
    public void deleteAllByIdLegacy(@RequestBody List<Long> ids) {
        // Delegate to the new endpoint implementation
        deleteAll(ids);
    }
}
