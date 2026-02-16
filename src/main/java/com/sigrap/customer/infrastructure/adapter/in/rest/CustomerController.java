package com.sigrap.customer.infrastructure.adapter.in.rest;

import com.sigrap.customer.application.port.in.CreateCustomerUseCase;
import com.sigrap.customer.application.port.in.DeleteCustomerUseCase;
import com.sigrap.customer.application.port.in.GetCustomerUseCase;
import com.sigrap.customer.application.port.in.UpdateCustomerUseCase;
import com.sigrap.customer.application.port.in.command.CreateCustomerCommand;
import com.sigrap.customer.application.port.in.command.UpdateCustomerCommand;
import com.sigrap.customer.domain.model.Customer;
import com.sigrap.customer.domain.model.CustomerId;
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
 * REST controller for customer operations (Hexagonal Architecture).
 * This is an input adapter that translates HTTP requests to use case calls.
 * It handles HTTP concerns (validation, status codes, request/response mapping)
 * and delegates business logic to use cases.
 * 
 * <p>Mapped to /api/customers for coexistence with legacy endpoints.
 */
@RestController("customerController")
@RequestMapping("/api/customers")
@Tag(name = "Customer Management", description = "APIs for managing customer information including contact details and addresses")
public class CustomerController {
    
    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;
    private final CustomerResponseMapper responseMapper;
    
    /**
     * Constructor injection of use cases and mapper.
     *
     * @param createCustomerUseCase use case for creating customers
     * @param getCustomerUseCase use case for retrieving customers
     * @param updateCustomerUseCase use case for updating customers
     * @param deleteCustomerUseCase use case for deleting customers
     * @param responseMapper mapper for converting domain entities to response DTOs
     */
    public CustomerController(
            CreateCustomerUseCase createCustomerUseCase,
            GetCustomerUseCase getCustomerUseCase,
            UpdateCustomerUseCase updateCustomerUseCase,
            DeleteCustomerUseCase deleteCustomerUseCase,
            CustomerResponseMapper responseMapper) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.getCustomerUseCase = getCustomerUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
        this.deleteCustomerUseCase = deleteCustomerUseCase;
        this.responseMapper = responseMapper;
    }
    
    /**
     * Creates a new customer.
     * POST /api/customers
     *
     * @param request the customer creation request
     * @return the created customer response with HTTP 201 status
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a new customer",
        description = "Creates a new customer with full name, document ID, email, phone number, and address. " +
                      "The email must be unique across the system."
    )
    @ApiResponse(
        responseCode = "201",
        description = "Customer created successfully",
        content = @Content(schema = @Schema(implementation = CustomerResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request - validation errors or duplicate email"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public CustomerResponse create(@Valid @RequestBody CustomerRequest request) {
        CreateCustomerCommand command = new CreateCustomerCommand(
            request.fullName(),
            request.documentId(),
            request.email(),
            request.phoneNumber(),
            request.address()
        );
        Customer customer = createCustomerUseCase.create(command);
        return responseMapper.toResponse(customer);
    }
    
    /**
     * Retrieves a customer by their ID.
     * GET /api/customers/{id}
     *
     * @param id the customer identifier
     * @return the customer response with HTTP 200 status
     * @throws com.sigrap.exception.ResourceNotFoundException if the customer is not found
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get customer by ID",
        description = "Retrieves a single customer by their unique identifier including all contact details"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Customer found successfully",
        content = @Content(schema = @Schema(implementation = CustomerResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Customer not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public CustomerResponse getById(
        @Parameter(description = "Customer unique identifier", required = true, example = "1")
        @PathVariable Long id
    ) {
        CustomerId customerId = new CustomerId(id);
        Customer customer = getCustomerUseCase.getById(customerId);
        return responseMapper.toResponse(customer);
    }
    
    /**
     * Retrieves all customers.
     * GET /api/customers
     *
     * @return a list of all customer responses with HTTP 200 status
     */
    @GetMapping
    @Operation(
        summary = "Get all customers",
        description = "Retrieves a list of all customers in the system with their complete information"
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of customers retrieved successfully",
        content = @Content(schema = @Schema(implementation = CustomerResponse.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public List<CustomerResponse> getAll() {
        return getCustomerUseCase.getAll().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Updates an existing customer.
     * PUT /api/customers/{id}
     *
     * @param id the customer identifier
     * @param request the customer update request
     * @return the updated customer response with HTTP 200 status
     * @throws com.sigrap.exception.ResourceNotFoundException if the customer is not found
     * @throws IllegalArgumentException if the new email conflicts with an existing customer
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing customer",
        description = "Updates a customer's information including name, document ID, email, phone, and address. " +
                      "The email must remain unique."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Customer updated successfully",
        content = @Content(schema = @Schema(implementation = CustomerResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request - validation errors or duplicate email"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Customer not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public CustomerResponse update(
            @Parameter(description = "Customer unique identifier", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request) {
        CustomerId customerId = new CustomerId(id);
        UpdateCustomerCommand command = new UpdateCustomerCommand(
            request.fullName(),
            request.documentId(),
            request.email(),
            request.phoneNumber(),
            request.address()
        );
        Customer customer = updateCustomerUseCase.update(customerId, command);
        return responseMapper.toResponse(customer);
    }
    
    /**
     * Deletes a customer by their ID.
     * DELETE /api/customers/{id}
     *
     * @param id the customer identifier
     * @return HTTP 204 No Content status
     * @throws com.sigrap.exception.ResourceNotFoundException if the customer is not found
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a customer",
        description = "Deletes a customer by their unique identifier. This operation cannot be undone."
    )
    @ApiResponse(
        responseCode = "204",
        description = "Customer deleted successfully"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Customer not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public void delete(
        @Parameter(description = "Customer unique identifier", required = true, example = "1")
        @PathVariable Long id
    ) {
        CustomerId customerId = new CustomerId(id);
        deleteCustomerUseCase.delete(customerId);
    }
    
    /**
     * Deletes multiple customers by their IDs (batch delete).
     * DELETE /api/customers/batch
     *
     * @param ids the list of customer identifiers to delete
     * @return HTTP 204 No Content status
     * @throws com.sigrap.exception.ResourceNotFoundException if any of the customers are not found
     */
    @DeleteMapping("/batch")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete multiple customers",
        description = "Deletes multiple customers in a single operation. All specified customers must exist."
    )
    @ApiResponse(
        responseCode = "204",
        description = "Customers deleted successfully"
    )
    @ApiResponse(
        responseCode = "404",
        description = "One or more customers not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public void deleteAll(
        @Parameter(description = "List of customer IDs to delete", required = true)
        @RequestBody List<Long> ids
    ) {
        List<CustomerId> customerIds = ids.stream()
            .map(CustomerId::new)
            .toList();
        deleteCustomerUseCase.deleteAll(customerIds);
    }
}
