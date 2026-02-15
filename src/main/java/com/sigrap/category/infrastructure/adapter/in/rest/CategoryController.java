package com.sigrap.category.infrastructure.adapter.in.rest;

import com.sigrap.category.application.port.in.CreateCategoryUseCase;
import com.sigrap.category.application.port.in.DeleteCategoryUseCase;
import com.sigrap.category.application.port.in.GetCategoryUseCase;
import com.sigrap.category.application.port.in.UpdateCategoryUseCase;
import com.sigrap.category.application.port.in.command.CreateCategoryCommand;
import com.sigrap.category.application.port.in.command.UpdateCategoryCommand;
import com.sigrap.category.domain.model.Category;
import com.sigrap.category.domain.model.CategoryId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for category operations.
 * This is an input adapter that translates HTTP requests to use case calls.
 * It handles HTTP concerns (validation, status codes, request/response mapping)
 * and delegates business logic to use cases.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    
    private final CreateCategoryUseCase createCategoryUseCase;
    private final GetCategoryUseCase getCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final CategoryResponseMapper responseMapper;
    
    /**
     * Constructor injection of use cases and mapper.
     *
     * @param createCategoryUseCase use case for creating categories
     * @param getCategoryUseCase use case for retrieving categories
     * @param updateCategoryUseCase use case for updating categories
     * @param deleteCategoryUseCase use case for deleting categories
     * @param responseMapper mapper for converting domain entities to response DTOs
     */
    public CategoryController(
            CreateCategoryUseCase createCategoryUseCase,
            GetCategoryUseCase getCategoryUseCase,
            UpdateCategoryUseCase updateCategoryUseCase,
            DeleteCategoryUseCase deleteCategoryUseCase,
            CategoryResponseMapper responseMapper) {
        this.createCategoryUseCase = createCategoryUseCase;
        this.getCategoryUseCase = getCategoryUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
        this.responseMapper = responseMapper;
    }
    
    /**
     * Creates a new category.
     * POST /api/categories
     *
     * @param request the category creation request
     * @return the created category response with HTTP 201 status
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@Valid @RequestBody CategoryRequest request) {
        CreateCategoryCommand command = new CreateCategoryCommand(
            request.name(),
            request.description()
        );
        Category category = createCategoryUseCase.create(command);
        return responseMapper.toResponse(category);
    }
    
    /**
     * Retrieves a category by its ID.
     * GET /api/categories/{id}
     *
     * @param id the category identifier
     * @return the category response with HTTP 200 status
     * @throws IllegalArgumentException if the category is not found
     */
    @GetMapping("/{id}")
    public CategoryResponse getById(@PathVariable Long id) {
        CategoryId categoryId = new CategoryId(id);
        Category category = getCategoryUseCase.getById(categoryId);
        return responseMapper.toResponse(category);
    }
    
    /**
     * Retrieves all categories.
     * GET /api/categories
     *
     * @return a list of all category responses with HTTP 200 status
     */
    @GetMapping
    public List<CategoryResponse> getAll() {
        return getCategoryUseCase.getAll().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Updates an existing category.
     * PUT /api/categories/{id}
     *
     * @param id the category identifier
     * @param request the category update request
     * @return the updated category response with HTTP 200 status
     * @throws IllegalArgumentException if the category is not found or name conflicts
     */
    @PutMapping("/{id}")
    public CategoryResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryId categoryId = new CategoryId(id);
        UpdateCategoryCommand command = new UpdateCategoryCommand(
            request.name(),
            request.description()
        );
        Category category = updateCategoryUseCase.update(categoryId, command);
        return responseMapper.toResponse(category);
    }
    
    /**
     * Deletes a category by its ID.
     * DELETE /api/categories/{id}
     *
     * @param id the category identifier
     * @return HTTP 204 No Content status
     * @throws IllegalArgumentException if the category is not found
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        CategoryId categoryId = new CategoryId(id);
        deleteCategoryUseCase.delete(categoryId);
    }
    
    /**
     * Deletes multiple categories by their IDs (batch delete).
     * DELETE /api/categories/batch
     *
     * @param ids the list of category identifiers to delete
     * @return HTTP 204 No Content status
     * @throws IllegalArgumentException if any of the categories are not found
     */
    @DeleteMapping("/batch")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll(@RequestBody List<Long> ids) {
        List<CategoryId> categoryIds = ids.stream()
            .map(CategoryId::new)
            .toList();
        deleteCategoryUseCase.deleteAll(categoryIds);
    }
    
    /**
     * Deletes multiple categories by their IDs (legacy endpoint for backward compatibility).
     * DELETE /api/categories/delete-many
     * 
     * @deprecated Use {@link #deleteAll(List)} with /batch endpoint instead.
     *             This endpoint is maintained for backward compatibility and will be removed in a future version.
     *
     * @param ids the list of category identifiers to delete
     * @return HTTP 204 No Content status
     * @throws IllegalArgumentException if any of the categories are not found
     */
    @Deprecated(since = "Hexagonal architecture migration", forRemoval = true)
    @DeleteMapping("/delete-many")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllByIdLegacy(@RequestBody List<Long> ids) {
        // Delegate to the new endpoint implementation
        deleteAll(ids);
    }
}
