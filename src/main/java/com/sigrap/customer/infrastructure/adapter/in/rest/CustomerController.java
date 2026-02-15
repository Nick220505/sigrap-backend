package com.sigrap.customer.infrastructure.adapter.in.rest;

import com.sigrap.customer.application.port.in.CreateCustomerUseCase;
import com.sigrap.customer.application.port.in.DeleteCustomerUseCase;
import com.sigrap.customer.application.port.in.GetCustomerUseCase;
import com.sigrap.customer.application.port.in.UpdateCustomerUseCase;
import com.sigrap.customer.application.port.in.command.CreateCustomerCommand;
import com.sigrap.customer.application.port.in.command.UpdateCustomerCommand;
import com.sigrap.customer.domain.model.Customer;
import com.sigrap.customer.domain.model.CustomerId;
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
 * <p>Mapped to /api/v2/customers for coexistence with legacy endpoints.
 */
@RestController("customerControllerV2")
@RequestMapping("/api/v2/customers")
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
     * POST /api/v2/customers
     *
     * @param request the customer creation request
     * @return the created customer response with HTTP 201 status
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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
     * GET /api/v2/customers/{id}
     *
     * @param id the customer identifier
     * @return the customer response with HTTP 200 status
     * @throws com.sigrap.exception.ResourceNotFoundException if the customer is not found
     */
    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable Long id) {
        CustomerId customerId = new CustomerId(id);
        Customer customer = getCustomerUseCase.getById(customerId);
        return responseMapper.toResponse(customer);
    }
    
    /**
     * Retrieves all customers.
     * GET /api/v2/customers
     *
     * @return a list of all customer responses with HTTP 200 status
     */
    @GetMapping
    public List<CustomerResponse> getAll() {
        return getCustomerUseCase.getAll().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Updates an existing customer.
     * PUT /api/v2/customers/{id}
     *
     * @param id the customer identifier
     * @param request the customer update request
     * @return the updated customer response with HTTP 200 status
     * @throws com.sigrap.exception.ResourceNotFoundException if the customer is not found
     * @throws IllegalArgumentException if the new email conflicts with an existing customer
     */
    @PutMapping("/{id}")
    public CustomerResponse update(
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
     * DELETE /api/v2/customers/{id}
     *
     * @param id the customer identifier
     * @return HTTP 204 No Content status
     * @throws com.sigrap.exception.ResourceNotFoundException if the customer is not found
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        CustomerId customerId = new CustomerId(id);
        deleteCustomerUseCase.delete(customerId);
    }
    
    /**
     * Deletes multiple customers by their IDs (batch delete).
     * DELETE /api/v2/customers/batch
     *
     * @param ids the list of customer identifiers to delete
     * @return HTTP 204 No Content status
     * @throws com.sigrap.exception.ResourceNotFoundException if any of the customers are not found
     */
    @DeleteMapping("/batch")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll(@RequestBody List<Long> ids) {
        List<CustomerId> customerIds = ids.stream()
            .map(CustomerId::new)
            .toList();
        deleteCustomerUseCase.deleteAll(customerIds);
    }
}
